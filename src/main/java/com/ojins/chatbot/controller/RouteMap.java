package com.ojins.chatbot.controller;

/**
 * Created by han on 12/16/16.
 */
public interface RouteMap {
    String ALL_TOPICS = "/topics";
    String TOPIC_PARAM = ":topic";
    String FILTER_PARAM = "filter";
    String FILTER_SOLVED = "solved";
    String FILTER_UNSOLVED = "unsolved";
    String QUEST_PARAM = "query";
    String TOPIC_SIZE = "/" + TOPIC_PARAM + "/size";
    String QA_LIST = "/" + TOPIC_PARAM + "/list";
    String ASK = "/" + TOPIC_PARAM;
    String TEACH = "/teach";
}