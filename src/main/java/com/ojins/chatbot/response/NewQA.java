package com.ojins.chatbot.response;

import lombok.Data;

/**
 * Created by hxiao on 2016/12/7.
 */
@Data
public class NewQA {
    private String question;
    private String answer;

    public boolean isValid() {
        return question != null && answer != null;
    }
}
