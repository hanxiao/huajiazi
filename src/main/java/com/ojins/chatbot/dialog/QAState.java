package com.ojins.chatbot.dialog;

import org.apache.commons.lang3.builder.EqualsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Created by han on 11/12/16.
 */
public class QAState {
    // this is a QA-State
    // questions: what user will ask
    // answers: what bot should reply
    // questions store all possible form of questions that corresponds to the homogeneous answer(s)
    // for the sake of generalization ability, we use a list of answers rather than one answer
    // but all answers should mean more or less the same
    // a good example would be the following， as both answers lead to the same state "不能帮你翻译德语"
    // q: ["能帮我翻译下么"， “我不懂德语”]
    // a: ["我也不懂", "不能"]
    // a bad example would be the following， as two answers lead to a positive AND a negative states "吃了"，“没吃”
    // q: ["你吃了么"，“你吃饭了么”，“去吃饭吧”]
    // a: ["我不去吃了"， “我吃了”]

    private List<String> questions = new ArrayList<String>();
    private List<String> answers = new ArrayList<String>();
    private transient static Random rnd = new Random();



    public List<String> getAnswers() {
        return answers;
    }

    public List<String> getQuestions() {
        return questions;
    }

    // two qastates are considered as same if their answers are same
    @Override
    public int hashCode() {
        return Math.abs(String.join("", answers).hashCode());
    }


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof QAState))
            return false;
        if (obj == this)
            return true;

        QAState rhs = (QAState) obj;
        return new EqualsBuilder().
                append(hashCode(), rhs.hashCode()).
                isEquals();
    }


    public Optional<String> popRandomAnswer(boolean isRandom) {
        int idx = isRandom ? rnd.nextInt(answers.size()) : 0;
        return Optional.ofNullable(answers.get(idx));
    }

    public void addAnswer(String answer) {
        answers.add(answer);
    }

    public void addQuestion(String question) {
        questions.add(question);
    }
}
