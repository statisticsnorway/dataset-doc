package no.ssb.dapla.dataset.doc.model.lds;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LineageField extends LineageObject {

    @JsonProperty
    private String name;

    @JsonProperty
    private String lineageDataSet;

    @JsonProperty
    private String relationType;

    @JsonProperty
    private List<String> lineage;

    @JsonProperty
    private float confidence;

    @JsonProperty
    private String instanceVariable;
}
