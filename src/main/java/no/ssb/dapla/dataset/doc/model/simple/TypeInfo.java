package no.ssb.dapla.dataset.doc.model.simple;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Map;

@JsonPropertyOrder({"value", "type"})
public class TypeInfo {
    @JsonProperty("selected-id")
    private String id;

    @JsonProperty("concept-type")
    private String type;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("candidates")
    private Map<String, String> candidatesNameToId;

    public TypeInfo() {
    }

    public TypeInfo(String id, String type, Map<String, String> candidatesNameToId) {
        this.id = id;
        this.type = type;
        this.candidatesNameToId = candidatesNameToId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
