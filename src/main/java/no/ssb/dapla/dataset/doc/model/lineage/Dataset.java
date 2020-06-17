package no.ssb.dapla.dataset.doc.model.lineage;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Dataset {
    @JsonProperty("lineage")
    LogicalRecord root;

    public LogicalRecord getRoot() {
        return root;
    }

    public void setRoot(LogicalRecord root) {
        this.root = root;
    }
}
