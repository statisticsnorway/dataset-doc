package no.ssb.dapla.dataset.doc.model.lineage;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InstanceVariable extends Field {

    @JsonProperty
    private String source;

    @JsonProperty
    private String inherit;

    @JsonProperty
    private String confidence= "0.9";

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getInherit() {
        return inherit;
    }

    public void setInherit(String inherit) {
        this.inherit = inherit;
    }

}
