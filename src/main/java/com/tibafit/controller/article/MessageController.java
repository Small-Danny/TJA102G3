package com.tibafit.controller.article;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.tibafit.dto.article.MessageDTO;
import com.tibafit.dto.article.MessagePostDTO;
import com.tibafit.model.article.Message;
import com.tibafit.model.user.User;
import com.tibafit.repository.user.UserRepository;
import com.tibafit.service.article.MessageService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserRepository userRepository;

    /**
     * 發送私訊
     */
    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendMessage(@RequestBody MessagePostDTO messagePostDTO) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (messagePostDTO.getSenderUserId() == null || messagePostDTO.getReceiverUserId() == null
                    || messagePostDTO.getTitle() == null || messagePostDTO.getTitle().trim().isEmpty()
                    || messagePostDTO.getContent() == null || messagePostDTO.getContent().trim().isEmpty()) {

                response.put("success", false);
                response.put("message", "請填寫完整資料");
                return ResponseEntity.badRequest().body(response);
            }

            if (messagePostDTO.getMessageType() == null || messagePostDTO.getMessageType().trim().isEmpty()) {
                messagePostDTO.setMessageType("會員訊息");
            }

            messageService.sendMessage(messagePostDTO);

            response.put("success", true);
            response.put("message", "訊息已發送成功");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "發送過程出錯：" + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 撈訊息（收件匣 / 已發訊息 / 系統通知）
     */
    @GetMapping
    public ResponseEntity<List<MessageDTO>> getMessages(@RequestParam String tab) {
        // 取得當前登入使用者
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = null;

        if (auth != null && auth.isAuthenticated()) {
            Object principal = auth.getPrincipal();

            if (principal instanceof UserDetails userDetails) {
                String email = userDetails.getUsername();
                currentUser = userRepository.findByEmail(email).orElse(null);
            } else if (principal instanceof String str && !"anonymousUser".equals(str)) {
                currentUser = userRepository.findByEmail(str).orElse(null);
            }
        }

        if (currentUser == null) {
            // 未登入回空清單
            return ResponseEntity.ok(new ArrayList<>());
        }

        Integer userId = currentUser.getUserId();
        List<MessageDTO> messages = new ArrayList<>();

        switch (tab) {
            case "收件匣":
                messages = messageService.getSent(userId);
                break;
            case "已發訊息":
            	 messages = messageService.getSentMessages(userId);
                break;
            case "系統通知":
                messages = messageService.getSystemNotifications(userId);
                break;
            default:
                break;
        }

        return ResponseEntity.ok(messages);
    }
    /**
     * 標為已讀（單筆或多筆）
     */
    @PostMapping("/markRead")
    public ResponseEntity<Map<String, Object>> markMessagesRead(@RequestBody Map<String, List<Integer>> body) {
        Map<String, Object> response = new HashMap<>();
        List<Integer> ids = body.get("ids");
        if (ids == null || ids.isEmpty()) {
            response.put("success", false);
            response.put("message", "請提供訊息ID");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            messageService.markMessagesRead(ids);
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 單筆標為已讀
     */
    @PostMapping("/markRead/{id}")
    public ResponseEntity<Map<String, Object>> markMessageRead(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        try {
            messageService.markMessageRead(id);
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    @PostMapping("/delete")
    public ResponseEntity<Map<String,Object>> deleteMessages(@RequestBody Map<String,Object> body){
        List<Integer> ids = (List<Integer>) body.get("ids");
        String tab = (String) body.get("tab"); // 前端傳 tab
        messageService.deleteMessages(ids, tab);
        return ResponseEntity.ok(Map.of("success", true));
    }


}
