package no.ssb.dapla.dataset.doc.model.simple;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;
import java.util.Map;

@JsonPropertyOrder({"value", "type"})
public class Info {
    @JsonProperty
    String value;

    @JsonProperty("concept-type")
    String type;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty
    List<String> candidates;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("candidates-name-to-id")
    Map<String, String> candidatesNameToId;

    public Info() {
    }

    public Info(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public Info(String value, String type, Map<String, String> candidatesNameToId) {
        this.value = value;
        this.type = type;
        this.candidatesNameToId = candidatesNameToId;
    }

    public Info(String value, String type, List<String> candidates) {
        this.value = value;
        this.type = type;
        this.candidates = candidates;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

//    public List<String> getCandidates() {
//        return candidates;
//    }
//
//    public void setCandidates(List<String> candidates) {
//        this.candidates = candidates;
//    }
//

}
