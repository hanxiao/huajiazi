package com.ojins.chatbot.searcher;

import com.ojins.chatbot.dialog.QAState;

import java.util.HashSet;
import java.util.Set;

public class QAServiceBuilder {
    private Set<QAState> qaStates = new HashSet<>();
    private String topic = "german-phd";

    public QAServiceBuilder setQAStates(Set<QAState> qaStates) {
        this.qaStates = qaStates;
        return this;
    }

    public QAServiceBuilder setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public QAService createQAService() {
        return new QAService(qaStates, topic);
    }
}