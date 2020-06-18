package no.ssb.dapla.dataset.doc.model.lineage;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Source {

    public Source(String field, String path, String version) {
        this.field = field;
        this.path = path;
        this.version = version;
    }

    public Source() {
    }

    @JsonProperty
    private String field;

    @JsonProperty
    private String path;

    @JsonProperty
    private String version;
}
