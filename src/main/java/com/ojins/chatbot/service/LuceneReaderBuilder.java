package com.ojins.chatbot.service;

import com.ojins.chatbot.analyzer.AnalyzerManager;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;
import java.nio.file.Paths;

@Accessors(chain = true)
@Setter
public class LuceneReaderBuilder {
    private Analyzer analyzer = AnalyzerManager.chineseIKSmartAnalyzer;
    private Directory index = new RAMDirectory();
    private int numAnswer = 5;

    public LuceneReaderBuilder setFilePath(String fp) {
        try {
            this.index = FSDirectory.open(Paths.get(fp));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return this;
    }

    LuceneReaderBuilder setIndexer(LuceneIndexer indexer) {
        this.index = indexer.getIndex();
        this.analyzer = indexer.getAnalyzer();
        return this;
    }

    LuceneReader createLuceneReader() {
        return new LuceneReader(analyzer, index, numAnswer);
    }
}