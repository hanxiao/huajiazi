package com.ojins.chatbot.controller;

import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * ___   ___  ________  ___   __      __     __   ________ ________  ______
 * /__/\ /__/\/_______/\/__/\ /__/\   /__/\ /__/\ /_______//_______/\/_____/\
 * \::\ \\  \ \::: _  \ \::\_\\  \ \  \ \::\\:.\ \\__.::._\\::: _  \ \:::_ \ \
 * \::\/_\ .\ \::(_)  \ \:. `-\  \ \  \_\::_\:_\/   \::\ \ \::(_)  \ \:\ \ \ \
 * \:: ___::\ \:: __  \ \:. _    \ \   _\/__\_\_/\ _\::\ \_\:: __  \ \:\ \ \ \
 * \: \ \\::\ \:.\ \  \ \. \`-\  \ \  \ \ \ \::\ /__\::\__/\:.\ \  \ \:\_\ \ \
 * \__\/ \::\/\__\/\__\/\__\/ \__\/   \_\/  \__\\________\/\__\/\__\/\_____\/
 * <p>
 * <p>
 * <p>
 * Created on 2016/12/11.
 */
@Accessors(chain = true)
@Setter
public class QAControllerBuilder {
    private Set<String> newTopics;
    private boolean overwrite = true;
    private int serverPort = 9090;
    private int numThread = 10;

    public QAController build() {
        return new QAController(newTopics, overwrite, serverPort, numThread);
    }
}
