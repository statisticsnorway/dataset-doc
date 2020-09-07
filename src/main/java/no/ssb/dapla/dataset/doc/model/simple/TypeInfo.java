package no.ssb.dapla.dataset.doc.model.simple;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@JsonPropertyOrder({"value", "type"})
public class TypeInfo {
    @JsonProperty("selected-id")
    private String id;

    @JsonProperty("concept-type")
    private String type;

    @JsonProperty("candidates")
    private List<Candidate> candidates;

    public TypeInfo() {
    }

    public TypeInfo(String id, String type, Map<String, String> candidatesNameToId) {
        this.id = id;
        this.type = type;
        if (candidatesNameToId == null) {
            this.candidates = List.of();
        } else {
            this.candidates = candidatesNameToId.entrySet().stream()
                    .map(e -> new Candidate(e.getKey(), e.getValue())).collect(Collectors.toList());
        }
        makeSureWeHaveOneElement();
    }

    // Fix to avoid gui crash on emptyListForNow
    private void makeSureWeHaveOneElement() {
        if (candidates.isEmpty()) {
            candidates.add(new Candidate(type + "_DUMMY", type + "_generated"));
        }
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
