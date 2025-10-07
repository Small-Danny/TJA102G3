package com.tibafit.service.article;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.tibafit.dto.article.MessageDTO;
import com.tibafit.dto.article.MessagePostDTO;
import com.tibafit.model.article.Message;
import com.tibafit.model.user.User;
import com.tibafit.repository.article.MessageRepository;
import com.tibafit.repository.user.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class MessageService {

	@Autowired
	private MessageRepository messageRepository;
	@Autowired
	private UserRepository userRepository;

	public void sendMessage(MessagePostDTO dto) {
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

		Message message = new Message();
		message.setSenderUserId(currentUser.getUserId());
		message.setReceiverUserId(dto.getReceiverUserId());
		message.setRelatedArticleId(dto.getRelatedArticleId());
		message.setTitle(dto.getTitle());
		message.setContent(dto.getContent());
		if (message.getMessageType() == null) {
			message.setMessageType("會員訊息");
		}
		messageRepository.save(message);
	}

	/**
	 * 收件匣：撈出接收者是該使用者的訊息
	 */
	public List<MessageDTO> getSent(Integer userId) {
		List<Object[]> rows = messageRepository.findInboxMessages(userId);
		List<MessageDTO> result = new ArrayList<MessageDTO>();
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
		for (Object[] obj : rows) {
			MessageDTO dto = new MessageDTO();
			dto.setId((Integer) obj[0]);
			dto.setSenderId((Integer) obj[1]);
			dto.setSenderName((String) obj[2]);

			// 處理 senderAvatar 路徑
			String avatar = (String) obj[3];
			if (avatar == null || avatar.isBlank()) {
				dto.setSenderAvatar("/frontend-template/assets/images/profile-picture-default.jpg");
			} else if (!avatar.startsWith("http://") && !avatar.startsWith("https://")
					&& !avatar.startsWith("/")) {
				dto.setSenderAvatar("/" + avatar);
			}
			dto.setType((String) obj[4]);
			dto.setTitle((String) obj[5]);
			dto.setContent((String) obj[6]);
			dto.setIsRead((Boolean) obj[7]);
			dto.setCreateTime((java.time.LocalDateTime) obj[8]); 
			dto.setReceiverId(currentUser.getUserId());
			result.add(dto);
		}
		return result;
	}
	
	/**
	 * 以發送訊息
	 */
	public List<MessageDTO> getSentMessages(Integer userId) {
	    List<Object[]> rows = messageRepository.findSentMessages(userId);
	    List<MessageDTO> result = new ArrayList<>();
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
	    for (Object[] obj : rows) {
	        MessageDTO dto = new MessageDTO();
	        dto.setId((Integer) obj[0]);
	        dto.setReceiverId((Integer) obj[1]);
	        dto.setReceiverName((String) obj[2]);

	        String avatar = currentUser.getProfilePicture();
	        if (avatar == null || avatar.isBlank()) {
	            dto.setSenderAvatar("/frontend-template/assets/images/profile-picture-default.jpg");
	        } else if (!avatar.startsWith("http://") && !avatar.startsWith("https://") && !avatar.startsWith("/")) {
	            dto.setSenderAvatar("/" + avatar);
	        }

	        dto.setType((String) obj[4]);
	        dto.setTitle((String) obj[5]);
	        dto.setContent((String) obj[6]);
	        dto.setIsRead((Boolean) obj[7]);
	        dto.setCreateTime((java.time.LocalDateTime) obj[8]); 

	        dto.setSenderId(userId);  // 自己就是發送者
	        result.add(dto);
	    }

	    return result;
	}
	public List<MessageDTO> getSystemNotifications(Integer userId) {
		List<Object[]> rows = messageRepository.findSystemMessages(userId);
		List<MessageDTO> result = new ArrayList<MessageDTO>();

		for (Object[] obj : rows) {
			MessageDTO dto = new MessageDTO();
			dto.setId((Integer) obj[0]);
			dto.setSenderId(null);
			dto.setSenderName((String) obj[2]);
			dto.setSenderAvatar((String) obj[3]);
			dto.setType((String) obj[4]);
			dto.setTitle((String) obj[5]);
			dto.setContent((String) obj[6]);
			dto.setIsRead((Boolean) obj[7]);
			dto.setCreateTime((java.time.LocalDateTime) obj[8]); 
			result.add(dto);
		}

		return result;
	}
	public void markMessageRead(Integer id) {
	    messageRepository.findById(id).ifPresent(message -> {
	        message.setIsRead(true);
	        messageRepository.save(message);
	    });
	}

	public void markMessagesRead(List<Integer> ids) {
	    ids.forEach(this::markMessageRead);
	}
	@Transactional
	public void deleteMessages(List<Integer> ids, String tab){
	    ids.forEach(id -> messageRepository.findById(id).ifPresent(message -> {
	        if("已發訊息".equals(tab)){
	            message.setIsDeletedBySender(true);
	        } else {
	            message.setIsDeletedByReceiver(true);
	        }
	    }));
	}


}
