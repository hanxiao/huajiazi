package com.ojins.chatbot.dialog;

/**
 * Created by hxiao on 2016/12/5.
 */
public class QAScoreTuple {
    public String question, answer;

    public double getScore() {
        return score;
    }

    public double score;
    public int hits;

    public QAScoreTuple(String question, String answer, double score, int hits) {
        this.question = question;
        this.answer = answer;
        this.score = score;
        this.hits = hits;
    }
}
