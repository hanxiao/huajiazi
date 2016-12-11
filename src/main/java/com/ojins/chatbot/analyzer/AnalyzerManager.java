package com.ojins.chatbot.analyzer;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.charfilter.HTMLStripCharFilterFactory;
import org.apache.lucene.analysis.cn.smart.HMMChineseTokenizerFactory;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.synonym.SynonymFilterFactory;
import org.wltea.analyzer.lucene.IKTokenizerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

@Slf4j
public class AnalyzerManager {
    private static final Map<String, String> stopwordConfig = ImmutableMap.of("words", "analyzer/stopwords.txt");
    private static final Map<String, String> iktokenizerSmartConfig = ImmutableMap.of("useSmart", "true");
    private static final Map<String, String> synonymConfig = ImmutableMap.of("synonyms", "analyzer/synonyms.txt");

    public static Analyzer chineseIKSmartAnalyzer, chineseHMMAnalyzer, chineseIKAnalyzer, chinesePlainAnalyzer;

    static {
        try {
            chineseIKSmartAnalyzer = CustomAnalyzer.builder()
                    .addCharFilter(HTMLStripCharFilterFactory.class)
                    .withTokenizer(IKTokenizerFactory.class, new HashMap<>(iktokenizerSmartConfig))
                    .addTokenFilter(StopFilterFactory.class, new HashMap<>(stopwordConfig))
                    .addTokenFilter(SynonymFilterFactory.class, new HashMap<>(synonymConfig))
                    .build();

            chineseIKAnalyzer = CustomAnalyzer.builder()
                    .addCharFilter(HTMLStripCharFilterFactory.class)
                    .withTokenizer(IKTokenizerFactory.class)
                    .addTokenFilter(StopFilterFactory.class, new HashMap<>(stopwordConfig))
                    .addTokenFilter(SynonymFilterFactory.class, new HashMap<>(synonymConfig))
                    .build();

            chineseHMMAnalyzer = CustomAnalyzer.builder()
                    .addCharFilter(HTMLStripCharFilterFactory.class)
                    .withTokenizer(HMMChineseTokenizerFactory.class)
                    .addTokenFilter(StopFilterFactory.class, new HashMap<>(stopwordConfig))
                    .addTokenFilter(SynonymFilterFactory.class, new HashMap<>(synonymConfig))
                    .build();

            chinesePlainAnalyzer = CustomAnalyzer.builder()
                    .addCharFilter(HTMLStripCharFilterFactory.class)
                    .withTokenizer(HMMChineseTokenizerFactory.class)
                    .build();
        } catch (IOException ex) {
            log.error("init analyzer error", ex);
        }
    }

}
