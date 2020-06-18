package no.ssb.dapla.dataset.doc.model.lineage;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InstanceVariable extends Field {

    @JsonProperty
    private String type= "inherited";

    @JsonProperty
    private String confidence= "0.9";

}
