package com.ojins.chatbot.dialog;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

/**
 * Created by hxiao on 2016/12/5.
 */

@Data
@AllArgsConstructor
public class QAPair {
    String question, answer;
    Set<String> didYouMean;
    Set<String> followUp;
    double score;
    int hits;

    public void incrementScore(double delta) {
        this.score += delta;
    }

    public void incrementHits(int delta) {
        this.hits += delta;
    }

    public void incrementHitsByOne() {
        incrementHits(1);
    }

    public boolean isValid() {
        return question != null && answer != null;
    }
}
