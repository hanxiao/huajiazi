package com.ojins.chatbot.dialog;

/**
 * Created by hxiao on 2016/12/5.
 */
public class QAResult {
    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public String[] getDidYouMean() {
        return didYouMean;
    }

    public String[] getFollowUp() {
        return followUp;
    }

    public double getScore() {
        return score;
    }

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
