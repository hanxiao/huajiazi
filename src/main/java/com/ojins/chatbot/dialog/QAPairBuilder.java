package com.ojins.chatbot.dialog;

import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Set;

@Accessors(chain = true)
@Setter
public class QAPairBuilder {
    private String answer;
    private String question;
    private Set<String> didYouMean;
    private Set<String> followUp;
    private double score = 0;
    private int hits = 1;

    public QAPair build() {
        return new QAPair(question, answer, didYouMean, followUp, score, hits);
    }
}