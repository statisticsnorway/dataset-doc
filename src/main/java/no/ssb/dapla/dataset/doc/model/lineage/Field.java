package no.ssb.dapla.dataset.doc.model.lineage;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Field {

    @JsonProperty
    String name;

    @JsonProperty
    protected String type;

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected String confidence;

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    protected List<Source> sources = new ArrayList<>();

    @JsonProperty("fields")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    protected final List<Field> fields = new ArrayList<>();

    public void setName(String name) {
        this.name = name;
    }

    public void addSource(Source source) {
        sources.add(source);
    }

    public void addSources(Collection<Source> sources) {
        this.sources.addAll(sources);
    }

    public String getConfidence() {
        return confidence;
    }

    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}