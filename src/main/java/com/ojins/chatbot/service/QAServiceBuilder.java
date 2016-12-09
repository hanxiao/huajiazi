package com.ojins.chatbot.service;

import com.google.common.collect.Sets;
import com.ojins.chatbot.dialog.QAState;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Set;

@Accessors(chain = true)
@Setter
public class QAServiceBuilder {
    private Set<QAState> qaStates = Sets.newHashSet(new QAState("你的作者是谁?", "肖涵"));
    private String topic = "default";
    private boolean overwrite = false;

    public QAService createQAService() {
        return new QAService(qaStates, topic, overwrite);
    }
}