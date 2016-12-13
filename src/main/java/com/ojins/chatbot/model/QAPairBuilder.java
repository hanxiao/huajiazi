package com.ojins.chatbot.model;

import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Accessors(chain = true)
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QAPairBuilder {
    String answer, question, topic = "default";
    Set<String> didYouMean, followUp;
    double score = 0;
    int hits = 1;

    public QAPair build() {
        return new QAPair(question, answer, topic, didYouMean, followUp, score, hits);
    }
}