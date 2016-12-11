package com.ojins.chatbot.model;

import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Set;

@Accessors(chain = true)
@Setter
public class QAPairBuilder {
    private String answer, question, topic = "default";
    private Set<String> didYouMean, followUp;
    private double score = 0;
    private int hits = 1;

    public QAPair build() {
        return new QAPair(question, answer, topic, didYouMean, followUp, score, hits);
    }
}