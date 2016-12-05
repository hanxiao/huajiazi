package com.ojins.chatbot.service;

import com.ojins.chatbot.analyzer.ChineseSynonymAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;
import java.nio.file.Paths;

public class LuceneReaderBuilder {
    private Analyzer chineseAnalyzer = new ChineseSynonymAnalyzer();
    private Directory index = new RAMDirectory();
    private int numAnswer = 5;

    public LuceneReaderBuilder setChineseAnalyzer(Analyzer chineseAnalyzer) {
        this.chineseAnalyzer = chineseAnalyzer;
        return this;
    }

    public LuceneReaderBuilder setIndex(Directory index) {
        this.index = index;
        return this;
    }

    public LuceneReaderBuilder setFilePath(String fp) {
        try {
            this.index = FSDirectory.open(Paths.get(fp));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return this;
    }

    public LuceneReaderBuilder setNumAnswer(int numAnswer) {
        this.numAnswer = numAnswer;
        return this;
    }

    public LuceneReaderBuilder setIndexer(LuceneIndexer indexer) {
        this.index = indexer.getIndex();
        this.chineseAnalyzer = indexer.getChineseAnalyzer();
        return this;
    }

    public LuceneReader createLuceneReader() {
        return new LuceneReader(chineseAnalyzer, index, numAnswer);
    }
}