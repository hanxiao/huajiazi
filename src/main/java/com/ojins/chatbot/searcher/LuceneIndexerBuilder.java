package com.ojins.chatbot.searcher;

import com.ojins.chatbot.dialog.QAState;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by han on 12/5/16.
 */
public class LuceneIndexerBuilder {
    private Directory index = new RAMDirectory();
    private Set<QAState> qaStates = new HashSet<>();

    public LuceneIndexerBuilder setIndex(Directory index) {
        this.index = index;
        return this;
    }

    public LuceneIndexerBuilder setFilePath(String fp) {
        try {
            this.index = FSDirectory.open(Paths.get(fp));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return this;
    }

    public LuceneIndexerBuilder setQAStates(Set<QAState> qaStates) {
        this.qaStates = qaStates;
        return this;
    }

    public LuceneIndexer build() {
        return new LuceneIndexer(index, qaStates);
    }

}
