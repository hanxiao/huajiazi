package com.ojins.chatbot.service;

import com.google.common.collect.Sets;
import com.ojins.chatbot.model.QAPair;
import com.ojins.chatbot.model.QAPairBuilder;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Accessors(chain = true)
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QAServiceBuilder {
    Set<QAPair> qaStates = Sets.newHashSet(
            new QAPairBuilder().setQuestion("你的作者是谁啊?").setAnswer("肖涵").build());
    String topic = "default";
    boolean overwrite = false;
    String indexDir = "tmp-index/";

    public QAService createQAService() {
        return new QAService(qaStates, topic, overwrite, indexDir);
    }
}