package com.ojins.chatbot.service;

import com.ojins.chatbot.model.QAPair;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
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
@FieldDefaults(level = AccessLevel.PRIVATE)
class LuceneIndexerBuilder {
    Directory index = new RAMDirectory();
    Set<QAPair> qaStates = new HashSet<>();
    boolean overwrite = false;

    LuceneIndexerBuilder setFilePath(String fp) {
        try {
            this.index = FSDirectory.open(Paths.get(fp));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return this;
    }

    LuceneIndexer createLuceneIndexer() {
        return new LuceneIndexer(index, qaStates, overwrite);
    }

}
