package com.ojins.chatbot.dialog;

import lombok.Data;

/**
 * Created by hxiao on 2016/12/5.
 */

@Data
public class QAResult {
    String question, answer;
    String[] didYouMean;
    String[] followUp;
    double score;

    public QAResult(String answer, String question, String[] didYouMean, String[] followUp, double score) {
        this.answer = answer;
        this.question = question;
        this.didYouMean = didYouMean;
        this.followUp = followUp;
        this.score = score;
    }
}
