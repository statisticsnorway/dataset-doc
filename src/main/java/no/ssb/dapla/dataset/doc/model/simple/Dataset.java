package no.ssb.dapla.dataset.doc.model.simple;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Dataset {
    @JsonProperty("dataset-path")
    String path;

    @JsonProperty("logical-record-root")
    LogicalRecord root;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public LogicalRecord getRoot() {
        return root;
    }

    public void setRoot(LogicalRecord root) {
        this.root = root;
    }
}
