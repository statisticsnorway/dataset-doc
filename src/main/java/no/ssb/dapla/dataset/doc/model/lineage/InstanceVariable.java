package no.ssb.dapla.dataset.doc.model.lineage;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class InstanceVariable extends Field {

    @JsonProperty
    String name;

    @JsonProperty
    private String type= "inherited";

    @JsonProperty
    private String confidence= "0.9";

    @JsonProperty
    private List<Source> sources = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Source> getSources() {
        return sources;
    }

    public void addSource(Source source) {
        sources.add(source);
    }

    public String getConfidence() {
        return confidence;
    }

    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }

}
