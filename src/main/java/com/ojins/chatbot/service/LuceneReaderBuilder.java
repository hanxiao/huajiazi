package com.ojins.chatbot.service;

import com.ojins.chatbot.analyzer.AnalyzerManager;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;
import java.nio.file.Paths;

@Accessors(chain = true)
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
class LuceneReaderBuilder {
    Analyzer analyzer = AnalyzerManager.chineseIKSmartAnalyzer;
    Directory index = new RAMDirectory();
    int numAnswer = 5;

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