package com.ojins.chatbot.analyzer;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.cn.smart.HMMChineseTokenizer;
import org.apache.lucene.analysis.en.EnglishMinimalStemFilter;
import org.apache.lucene.analysis.synonym.SynonymFilterFactory;
import org.apache.lucene.analysis.util.FilesystemResourceLoader;
import org.apache.lucene.util.Version;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by han on 11/14/16.
 */
public class ChineseSynonymAnalyzer extends Analyzer {
    private final CharArraySet stopWords;
    private static final String DEFAULT_STOPWORD_FILE = "data/stopwords.txt";
    private static final String STOPWORD_FILE_COMMENT = "//";
    private Map<String, String> filterArgs = new HashMap<>();
    private SynonymFilterFactory factory;

    public static CharArraySet getDefaultStopSet() {
        return ChineseSynonymAnalyzer.DefaultSetHolder.DEFAULT_STOP_SET;
    }

    public ChineseSynonymAnalyzer() {
        this(true, true);
    }

    public ChineseSynonymAnalyzer(boolean useDefaultStopWords, boolean useSynonym) {
        this.stopWords = useDefaultStopWords ? ChineseSynonymAnalyzer.DefaultSetHolder.DEFAULT_STOP_SET : CharArraySet.EMPTY_SET;
        if (useSynonym) initSynonymFilter();
    }

    private void initSynonymFilter() {
        filterArgs.put("luceneMatchVersion", Version.LATEST.toString());
        filterArgs.put("synonyms", "synonyms.txt");
        filterArgs.put("expand", "true");
        factory = new SynonymFilterFactory(filterArgs);
        try {
            factory.inform(new FilesystemResourceLoader(Paths.get("data/")));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public ChineseSynonymAnalyzer(CharArraySet stopWords) {
        this.stopWords = stopWords == null ? CharArraySet.EMPTY_SET : stopWords;
    }

    public Analyzer.TokenStreamComponents createComponents(String fieldName) {
        Tokenizer tokenizer = new HMMChineseTokenizer();// IKTokenizer(false);
        TokenStream result = new EnglishMinimalStemFilter(tokenizer);
        if (!this.stopWords.isEmpty()) {
            result = new StopFilter(result, this.stopWords);
        }
        if (this.factory != null) {
            result = factory.create(result);
        }

        return new Analyzer.TokenStreamComponents(tokenizer, result);
    }

    protected TokenStream normalize(String fieldName, TokenStream in) {
        return new LowerCaseFilter(in);
    }

    private static class DefaultSetHolder {
        static final CharArraySet DEFAULT_STOP_SET;

        private DefaultSetHolder() {
        }

        static CharArraySet loadDefaultStopWordSet() throws IOException {
            return CharArraySet.unmodifiableSet(
                    WordlistLoader.getWordSet(new FileReader(DEFAULT_STOPWORD_FILE), STOPWORD_FILE_COMMENT));
        }

        static {
            try {
                DEFAULT_STOP_SET = loadDefaultStopWordSet();
            } catch (IOException var1) {
                throw new RuntimeException("Unable to load default stopword set");
            }
        }
    }
}
