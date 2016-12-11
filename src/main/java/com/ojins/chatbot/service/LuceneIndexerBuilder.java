package com.ojins.chatbot.service;

import com.ojins.chatbot.model.QAPair;
import lombok.Setter;
import lombok.experimental.Accessors;
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

@Accessors(chain = true)
@Setter
public class LuceneIndexerBuilder {
    private Directory index = new RAMDirectory();
    private Set<QAPair> qaStates = new HashSet<>();
    private boolean overwrite = false;

    public LuceneIndexerBuilder setFilePath(String fp) {
        try {
            this.index = FSDirectory.open(Paths.get(fp));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return this;
    }

    public LuceneIndexer createLuceneIndexer() {
        return new LuceneIndexer(index, qaStates, overwrite);
    }

}
