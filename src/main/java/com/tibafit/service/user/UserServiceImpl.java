package com.tibafit.service.user;

import com.tibafit.dto.user.*;
import com.tibafit.exception.ValidationException;
import com.tibafit.model.user.User;
import com.tibafit.repository.user.UserRepository;
import com.tibafit.service.cart.CartService;
import com.tibafit.service.file.FileService;
import com.tibafit.service.mail.MailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {
    // 宣告需要，使用final確保了我們的服務在整個生命週期中，都依賴於同一個，且一定會讓我們需要在建構子給他賦值
    private final UserRepository userRepository;
    private final StringRedisTemplate redisTemplate;// 新增Redis工具
    private final MailService mailService;
    private final FileService fileService;
    private final PasswordEncoder passwordEncoder; // 新增密碼比對工具
    private final ReCaptchaService reCaptchaService;// 新增Google reCAPTCHA
    private final AuthenticationManager userAuthenticationManager;
    private final SecurityContextRepository securityContextRepository;
    private final RememberMeServices rememberMeServices;
    private final CartService cartService;
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    // 建構子注入

    @Autowired
    public UserServiceImpl(UserRepository userRepository, StringRedisTemplate redisTemplate, MailService mailService,
                           FileService fileService, PasswordEncoder passwordEncoder, ReCaptchaService reCaptchaService,
                           RememberMeServices rememberMeServices,
                           @Qualifier("userAuthenticationManager") AuthenticationManager userAuthenticationManager,
                           CartService cartService) {
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
        this.mailService = mailService;
        this.fileService = fileService;
        this.passwordEncoder = passwordEncoder;
        this.reCaptchaService = reCaptchaService;
        this.rememberMeServices = rememberMeServices;
        this.userAuthenticationManager = userAuthenticationManager;
        this.securityContextRepository = new HttpSessionSecurityContextRepository();
        this.cartService = cartService;

    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User register(RegisterRequest req, HttpServletRequest request) { // 【增加參數】

        // 在所有業務邏輯的最前面，先進行 reCAPTCHA 驗證
        boolean isCaptchaValid = reCaptchaService.validateToken(req.getRecaptchaToken());
        if (!isCaptchaValid) {
            // 為了安全，不提示具體錯誤，模糊地回傳驗證碼錯誤
            throw new ValidationException("captcha", "驗證失敗，請重試");
        }

//		// 舊的kaptcha,不使用
//		String captchaInput = req.getCaptcha();
//		String correctCaptcha = (String) request.getSession().getAttribute("captchaCode");
//
//		if (correctCaptcha == null || captchaInput == null || !correctCaptcha.equalsIgnoreCase(captchaInput)) {
//			throw new ValidationException("captcha", "圖片驗證碼錯誤");
//		}
//
//		// 驗證成功後，立刻讓 Session 中的驗證碼失效，防止重複使用
//		request.getSession().removeAttribute("captchaCode");

        String userCode = req.getCode(); // 原本是在Map裡面取，現在改成我們的
        // 核心邏輯第一道，是否有獲取驗證碼
        if (userCode == null || userCode.trim().isEmpty()) {
            // 處理不同，是拋出例外，而非原本reponse寫法
            throw new ValidationException("emailCode", "請輸入電子郵件驗證碼");

        }
        String email = req.getEmail();
        // 核心邏輯第二道：把Email存在性檢查，這樣才不會導致驗證碼亂填且發信跑去按註冊跳驗證碼過期提示
        // 呼叫我們剛剛定義findBy
        // .isPresent() 用來檢查 Optional是否真的有東西
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ValidationException("email", "這個 Email 已經被註冊了");
        }
        // 從 req 取得 name，並呼叫我們的輔助方法，姓名欄位的後端驗證
        String name = req.getName();
        validateName(name);

        // 從Redis查詢正確的驗證碼並比對
        // 建立連線，這理由StringRedisTemplate來管理

        String correctCode = redisTemplate.opsForValue().get(email);

        // 比對驗證碼
        if (correctCode == null) {
            throw new ValidationException("emailCode", "驗證碼已過期，請重新發送");
        }

        if (!correctCode.equals(req.getCode())) {
            throw new ValidationException("emailCode", "驗證碼錯誤");
        }
        // 建立User物件並存入資料庫且補上後端才能決定的預設值
        User newUser = new User();

        newUser.setName(name);
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(req.getPassword())); // 加密密碼

        // 補上後端才能決定的預設值
        newUser.setAccountStatus(1); // 1 代表「啟用」
        // 存起來
        User savedUser = userRepository.save(newUser);
        // 刪除 Redis 的驗證碼，避免重複使用
        redisTemplate.delete(email);

        // 在所有事情都成功後，呼叫 MailService 發送通知信
        // 這裡我們用 try-catch 包起來，避免因為郵件系統問題導致整個註冊失敗
        try {
            mailService.sendMail(savedUser.getEmail(), "TibaFit 註冊成功通知",
                    "親愛的 " + savedUser.getName() + "，恭喜您成功註冊！歡迎加入 TibaFit！");
        } catch (Exception e) {
            // 實務上這裡會記錄錯誤日誌 (log)，但通常不會讓使用者看到註冊失敗
            // 因為註冊本身是成功的，只是郵件沒發出去
            e.printStackTrace();
        }
//		// 回傳成功註冊的 User 物件訊息
        return savedUser;
    }

    @Override
    public void sendVerificationCode(String email) {
        // 舊的寫法是先取得email，已經移到Controller邏輯了

        // 2. 檢查 Email 是否已被註冊，這邊一樣使用Optional
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ValidationException("email", "這個 Email 已經被註冊了");
        }
        // 3. 產生驗證碼，並存入 Redis
        int randomCode = (int) (Math.random() * 900000) + 100000;
        String authCode = String.valueOf(randomCode);
        // 將 'email:驗證碼' 存入 Redis，並設定 5 分鐘後自動過期
        redisTemplate.opsForValue().set(email, authCode, 5, TimeUnit.MINUTES);

        // 4. 寄送驗證信
        String subject = "TibaFit 會員註冊驗證信";
        String messageText = "您好！您的 TibaFit 註冊驗證碼是：" + authCode + "，有效時間為 5 分鐘。";
        mailService.sendMail(email, subject, messageText);

        // 5.不需要回傳true訊息了，現在是回傳完整的User物件

        // 6.檢查驗證碼

    }

    @Override
    public User login(LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
        boolean isCaptchaValid = reCaptchaService.validateToken(loginRequest.getRecaptchaToken());
        if (!isCaptchaValid) {
            throw new ValidationException("login", "驗證失敗，請重試");
        }
        String guestSessionId = request.getSession().getId();

        try {
            UsernamePasswordAuthenticationToken authRequest =
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());

            // 核心認證步驟
            Authentication authentication = userAuthenticationManager.authenticate(authRequest);

            var context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

            User loggedInUser = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("登入成功但找不到使用者資料"));

            // ▼▼▼【最終修正點：手動觸發 RememberMe 服務】▼▼▼
            // 這是因為我們的自訂登入流程不會自動觸發 RememberMeAuthenticationFilter
            // 所以我們必須在成功登入後，手動呼叫它。
            // ▼▼▼【 偵錯日誌開始 】▼▼▼
            if (loginRequest.isRememberMe()) {
                rememberMeServices.loginSuccess(request, response, authentication);

            }

            // 儲存安全上下文到 Session (這會更新 Session ID)
            securityContextRepository.saveContext(context, request, response);
            // 合併購物車
            cartService.mergeCart(guestSessionId, loggedInUser.getUserId());

            return loggedInUser;

        } catch (AuthenticationException e) {
            throw new ValidationException("login", "帳號或密碼錯誤");
        }
    }

    @Override
    @Transactional // 這個方法需要交易管理
    public User updateProfile(Integer userId, UpdateProfileRequest updateRequest) {

        User userToUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new ValidationException("uptateProfile", "找不到使用者"));

        // 姓名欄位的後端驗證
        validateName(updateRequest.getName());
        // 檢查並更新User的物件屬性
        userToUpdate.setName(updateRequest.getName());
        userToUpdate.setNickName(updateRequest.getNickName());
        userToUpdate.setGender(updateRequest.getGender());
        if (updateRequest.getProfilePicture() != null && !updateRequest.getProfilePicture().isEmpty()) {
            userToUpdate.setProfilePicture(updateRequest.getProfilePicture());
        }
        userToUpdate.setHeightCm(updateRequest.getHeightCm());
        userToUpdate.setWeightKg(updateRequest.getWeightKg());
        userToUpdate.setPhone(updateRequest.getPhone());

        // 【重要】在 Service 層重新計算 BMI
        BigDecimal height = updateRequest.getHeightCm();
        BigDecimal weight = updateRequest.getWeightKg();

        if (height != null && weight != null && height.compareTo(BigDecimal.ZERO) > 0) {
            // BMI = 體重(kg) / (身高(m))^2
            BigDecimal heightInMeters = height.divide(new BigDecimal("100"));
            BigDecimal bmi = weight.divide(heightInMeters.multiply(heightInMeters), 1, RoundingMode.HALF_UP);
            userToUpdate.setBmi(bmi);
        }

        // 有transactional可以不寫save變 return userToUpdate;主要語意清晰+避免物件不是這次交易查出來
        return userRepository.save(userToUpdate);

    }

    @Override
    @Transactional
    public String updateProfilePicture(Integer userId, MultipartFile profilePicture) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ValidationException("userToUpdatePictrue", "找不到使用者"));

        if (profilePicture == null || profilePicture.isEmpty()) {
            return user.getProfilePicture();
        }

        String contentType = profilePicture.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ValidationException("profilePicture", "僅支援圖片類型檔案");
        }

        String oldPicturePath = user.getProfilePicture();

        try {
            // 保存新圖片到文件系統，獲取相對路徑
            String newPicturePath = fileService.saveFile(profilePicture, "avatars");

            // 更新用戶頭像路徑
            user.setProfilePicture(newPicturePath);

            // 手動保存修改到資料庫（關鍵步驟）
            userRepository.save(user);
            userRepository.flush(); // 强制刷新，立即执行SQL

            // 異步刪除舊圖片
            if (oldPicturePath != null && !oldPicturePath.isEmpty()) {
                deleteOldFileAsync(oldPicturePath);
            }

            return newPicturePath;
        } catch (IOException e) {
            throw new RuntimeException("更新頭像失敗: " + e.getMessage(), e);
        }
    }

    @Async
    public void deleteOldFileAsync(String oldFileName) {
        try {
            fileService.deleteFile(oldFileName);
        } catch (Exception e) {
            System.err.println("刪除舊文件失敗（不影響新頭像生效）: " + e.getMessage());
            // 這裡可以記錄日誌，以便追蹤刪除失敗的原因
        }
    }

    @Override
    @Transactional
    public String changePassword(Integer userId, ChangePasswordRequest changepasswordrequest) {
        // 1. 查詢使用者是否存在
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ValidationException("changePassword", "找不到使用者"));

        // 2. 取得請求中的密碼
        String currentPassword = changepasswordrequest.getCurrentPassword();
        String newPassword = changepasswordrequest.getNewPassword();
        String confirmPassword = changepasswordrequest.getConfirmPassword();

        // 3. 呼叫輔助方法，進行非空驗證
        validateNotEmpty(currentPassword, "currentPassword", "請輸入當前密碼");
        validateNotEmpty(newPassword, "newPassword", "請輸入新密碼");
        validateNotEmpty(confirmPassword, "confirmPassword", "請確認輸入新密碼");

        // 4. 驗證新密碼和確認密碼是否一致
        if (!newPassword.equals(confirmPassword)) {
            throw new ValidationException("confirmPassword", "兩次輸入的密碼不一致");
        }

        // 5. 驗證當前密碼是否正確
        // 這裡需要用 passwordEncoder.matches() 來比對原始密碼與資料庫中的加密密碼
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new ValidationException("currentPassword", "當前密碼不正確");
        }

        // 6. 驗證新密碼強度和是否與舊密碼相同
        validatePasswordStrength(newPassword);

        // 7. 確保新密碼不能與原密碼相同
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new ValidationException("newPassword", "新密碼不能與原密碼相同");
        }

        // 8. 加密新密碼並保存
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return "密碼修改成功，請重新登錄";
    }

    /**
     * 【已更新】處理忘記密碼請求，使用 Google reCAPTCHA 進行驗證。
     *
     * @param req 包含 email 和 recaptchaToken 的請求物件
     */
    @Override
    @Transactional
    public void sendPasswordResetToken(PasswordResetRequest req) { // ★ 1. 參數改回 DTO

        // ★ 2. 安全驗證改回 reCAPTCHA
        boolean isCaptchaValid = reCaptchaService.validateToken(req.getRecaptchaToken());
        if (!isCaptchaValid) {
            throw new ValidationException("recaptchaToken", "人機驗證失敗，請重試");
        }

        // ★ 3. 從 DTO 中獲取 email
        String email = req.getEmail();

        // 檢查 是否找到使用者
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ValidationException("email", "如果該電子郵件已註冊，密碼重設信將很快寄出。"));

        if (user.getAccountStatus() != 1) {
            // 如果帳號被停權，我們不應該讓攻擊者知道這個帳號的狀態。
            // 假裝郵件已經寄出,這樣前端看到的提示訊息會和成功時一樣。
            System.out.println("一個被停權的帳號嘗試重設密碼: " + email); // 在後台留下紀錄
            return; // 直接結束，不執行後續發信邏輯
        }
        // 用UUID生成token
        String token = UUID.randomUUID().toString();
        // 設定token時間
        user.setResetPasswordToken(token);
        user.setTokenExpiryDate(LocalDateTime.now().plusMinutes(15));
        // 將帶有 Token 和過期時間的 User 物件存回資料庫
        userRepository.save(user);

        // 發送郵件,之後要改正是網址
        String resetUrl = "http://localhost:8080/frontend-template/reset-set-password.html?token=" + token;

        String subject = "TibaFit 密碼重設申請信";
        String messageText = "您好！\n\n我們收到了您的密碼重設請求。\n\n" + "請點擊以下連結以設定您的新密碼，此連結將在15分鐘後失效：\n" + resetUrl + "\n\n"
                + "如果您沒有提出此請求，請忽略此郵件。\n\n" + "TibaFit 團隊敬上";
        mailService.sendMail(email, subject, messageText);
    }

    /**
     * 根據使用者提供的 Token，執行密碼重設操作。
     *
     * @param req 包含 Token 和新密碼的請求物件
     * @return 成功訊息
     */
    @Override
    @Transactional
    public String resetPasswordWithToken(PerformResetRequest req) {

        // --- 第一步：使用 Token 驗證使用者 ---
        String token = req.getToken();
        User user = userRepository.findByResetPasswordToken(token)
                .orElseThrow(() -> new ValidationException("token", "無效的或已過期的重設連結"));

        // --- 第二步：檢查 Token 是否已過期 ---
        if (user.getTokenExpiryDate().isBefore(LocalDateTime.now())) {
            // 為安全起見，清除過期的 Token
            user.setResetPasswordToken(null);
            user.setTokenExpiryDate(null);
            userRepository.save(user);
            throw new ValidationException("token", "重設連結已過期，請重新申請");
        }

        // --- 第三步：驗證新密碼 (這部分邏輯是正確的) ---
        String newPassword = req.getNewPassword();
        String confirmPassword = req.getConfirmPassword();

        validateNotEmpty(newPassword, "newPassword", "請輸入新密碼");
        validateNotEmpty(confirmPassword, "confirmPassword", "請確認輸入新密碼");

        if (!newPassword.equals(confirmPassword)) {
            throw new ValidationException("confirmPassword", "兩次輸入的密碼不一致");
        }

        validatePasswordStrength(newPassword);

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new ValidationException("newPassword", "新密碼不能與原密碼相同");
        }

        // --- 第四步：更新密碼並讓 Token 失效 ---
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetPasswordToken(null);
        user.setTokenExpiryDate(null);
        userRepository.save(user);

        return "密碼重設成功，請使用新密碼登入。";
    }

    /**
     * @param name的驗證方法，有非空 長度跟中英文
     */
    private void validateName(String name) {
        // 非空驗證
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("name", "請輸入姓名");
        }

        // 長度驗證
        if (name.length() < 2 || name.length() > 20) {
            throw new ValidationException("name", "姓名長度必須介於 2 到 20 個字符之間");
        }

        // 字符類型驗證（中英文，去掉數字允許）
        String nameRegex = "^[a-zA-Z\u4e00-\u9fa5]+$";
        if (!name.matches(nameRegex)) {
            throw new ValidationException("name", "姓名只能包含中英文");
        }

    }

    /**
     * @param 非空驗證
     */
    private void validateNotEmpty(String value, String field, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(field, message);
        }
    }

    /**
     * @param passworrd密碼強度驗證 長度+大小寫+數字+特殊符號
     */
    private void validatePasswordStrength(String password) {
        if (password.length() < 8) {
            throw new ValidationException("newPassword", "密碼長度至少8位");
        }
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        if (!password.matches(passwordRegex)) {
            throw new ValidationException("newPassword", "密码必须包含大小写字母、数字和特殊符号");
        }
    }

    @Override
    public List<User> findAll() {
        // TODO Auto-generated method stub
        return userRepository.findAll();
    }

    @Override
    public User findById(Integer userId) {

        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public List<User> searchUser(String keyword) {

        return userRepository.searchUsers(keyword);
    }

    @Transactional
    @Override
    public void toggleAccountStatus(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ValidationException("userToUpdatePictrue", "找不到使用者"));
        Integer currentStatus = user.getAccountStatus();
        Integer newStatus;
        if (currentStatus == 1) {
            newStatus = 0; // 如果現在是 1 (啟用)，就把它變成 0 (停權)
        } else {
            newStatus = 1; // 否則 (不管是 0 還是其他值)，都把它變回 1 (啟用)
        }
        user.setAccountStatus(newStatus);

        userRepository.save(user);

    }
    @Override
    @Transactional
    public String subscribeNewsletter(String email) {
        // 1. 利用現有的 findByEmail 找出使用者
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ValidationException("email", "此 Email 未註冊為會員，無法訂閱"));

        // 2. 檢查是否已經訂閱過
        if (user.isSubscribed()) {
            return "您已訂閱過電子報，無需重複訂閱。";
        }

        // 3. 更新訂閱狀態並儲存
        user.setSubscribed(true);
        userRepository.save(user);

        // 4. 呼叫現有的 MailService 寄送確認信
        try {
            mailService.sendMail(email, "TibaFit | 成功訂閱電子報！", "感謝您的訂閱！我們將會定期為您帶來最新的健身資訊與活動消息。");
        } catch (Exception e) {
            // 即使信件沒寄出，訂閱本身還是成功的，所以只在後台記錄錯誤
            log.error("訂閱確認信發送失敗: " + email, e);
        }

        return "訂閱成功！確認信已發送至您的信箱。";
    }
}
