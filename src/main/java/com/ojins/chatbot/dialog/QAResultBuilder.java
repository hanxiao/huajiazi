package com.ojins.chatbot.dialog;

public class QAResultBuilder {
    private String answer;
    private String question;
    private String[] didYouMean;
    private String[] followUp;
    private double score;

    public QAResultBuilder setAnswer(String answer) {
        this.answer = answer;
        return this;
    }

    public QAResultBuilder setQuestion(String question) {
        this.question = question;
        return this;
    }

    public QAResultBuilder setDidYouMean(String[] didYouMean) {
        this.didYouMean = didYouMean;
        return this;
    }

    public QAResultBuilder setFollowUp(String[] followUp) {
        this.followUp = followUp;
        return this;
    }

    public QAResultBuilder setScore(double score) {
        this.score = score;
        return this;
    }

    public QAResult createQAResult() {
        return new QAResult(answer, question, didYouMean, followUp, score);
    }
}