package no.ssb.dapla.dataset.doc.model.lineage;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Source {

    @JsonProperty
    private String field;

    @JsonProperty
    private String path;

    @JsonProperty
    private long version;

    @JsonIgnore
    private float confidence;

    @JsonIgnore
    private String type;

    public Source(String field, String path, long version) {
        this.field = field;
        this.path = path;
        this.version = version;
    }

    public Source() {
    }

    public float getConfidence() {
        return confidence;
    }

    public void setConfidence(float confidence) {
        this.confidence = confidence;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
