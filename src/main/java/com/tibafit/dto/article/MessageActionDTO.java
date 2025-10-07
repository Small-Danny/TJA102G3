package com.tibafit.dto.article;

import java.util.List;

public class MessageActionDTO {
    private List<Integer> messageIds;

    public MessageActionDTO() {}

    public MessageActionDTO(List<Integer> messageIds) {
        this.messageIds = messageIds;
    }

    public List<Integer> getMessageIds() {
        return messageIds;
    }

    public void setMessageIds(List<Integer> messageIds) {
        this.messageIds = messageIds;
    }
}
