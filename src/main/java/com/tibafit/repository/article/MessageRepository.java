package com.tibafit.repository.article;


import com.tibafit.model.article.Message;

import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
	// 收件匣查詢：JOIN sender user 拿頭像與名稱，只要會員訊息
	@Query("""
	    SELECT 
	        m.messageId,
	        m.senderUserId,
	        u.nickName,
	        u.profilePicture,
	        m.messageType,
	        m.title,
	        m.content,
	        m.isRead,
	        m.createTime
	    FROM Message m
	    LEFT JOIN User u ON m.senderUserId = u.userId
	    WHERE m.receiverUserId = :receiverId
	    AND m.isDeletedByReceiver = false
	    AND m.senderUserId IS NOT NULL
	    ORDER BY m.createTime DESC
	""")
	List<Object[]> findInboxMessages(@Param("receiverId") Integer receiverId);

    
 // 已發訊息查詢：JOIN 接收者 user 拿接收者名稱與頭像
    @Query("""
        SELECT 
            m.messageId,
            m.receiverUserId,
            u.nickName,
            u.profilePicture,
            m.messageType,
            m.title,
            m.content,
            m.isRead,
            m.createTime
        FROM Message m
        LEFT JOIN User u ON m.receiverUserId = u.userId
        WHERE m.senderUserId = :senderId
        AND m.isDeletedBySender = false
        ORDER BY m.createTime DESC
    """)
    List<Object[]> findSentMessages(@Param("senderId") Integer senderId);
    
 // 系統通知（senderUserId 為 null）
    @Query("""
        SELECT 
            m.messageId,
            null,
            '系統' AS senderName,
            null,
            m.messageType,
            m.title,
            m.content,
            m.isRead,
            m.createTime
        FROM Message m
        WHERE m.receiverUserId = :receiverId
        AND m.senderUserId IS NULL
        AND m.isDeletedByReceiver = false
        ORDER BY m.createTime DESC
    """)
    List<Object[]> findSystemMessages(@Param("receiverId") Integer receiverId);
}

