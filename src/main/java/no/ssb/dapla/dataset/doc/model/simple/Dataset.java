package no.ssb.dapla.dataset.doc.model.simple;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"name", "description", "root"})
public class Dataset {
    @JsonProperty("logical-record-root")
    Record root;

    @JsonProperty
    String name = "dataset name";

    @JsonProperty
    String description = "add description";

    public Record getRoot() {
        return root;
    }

    public void setRoot(Record root) {
        this.root = root;
    }
}
