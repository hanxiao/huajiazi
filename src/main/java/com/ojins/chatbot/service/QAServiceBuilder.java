package com.ojins.chatbot.service;

import com.google.common.collect.Sets;
import com.ojins.chatbot.model.QAPair;
import com.ojins.chatbot.model.QAPairBuilder;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Set;

@Accessors(chain = true)
@Setter
public class QAServiceBuilder {
    private Set<QAPair> qaStates = Sets.newHashSet(
            new QAPairBuilder().setQuestion("你的作者是谁啊?").setAnswer("肖涵").build());
    private String topic = "default";
    private boolean overwrite = false;
    private String indexDir = "tmp-index/";

    public QAService createQAService() {
        return new QAService(qaStates, topic, overwrite, indexDir);
    }
}