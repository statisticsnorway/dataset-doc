package no.ssb.dapla.dataset.doc.model.simple;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Dataset {
    @JsonProperty("logical-record-root")
    Record root;

    public Record getRoot() {
        return root;
    }

    public void setRoot(Record root) {
        this.root = root;
    }
}
