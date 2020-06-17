package no.ssb.dapla.dataset.doc.model.lineage;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class LogicalRecord {
    @JsonProperty
    private String name;

    @JsonProperty
    private String type = "structure";

    @JsonProperty("fields")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final List<InstanceVariable> instanceVariables = new ArrayList<>();

    @JsonProperty("catalogs")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final List<LogicalRecord> logicalRecords = new ArrayList<>();

    public void setName(String name) {
        this.name = name;
    }

    public void addLogicalRecord(LogicalRecord logicalRecord) {
        logicalRecords.add(logicalRecord);
    }

    @JsonIgnore
    public LogicalRecord getRoot() {
        if (logicalRecords.size() == 1) {
            return logicalRecords.get(0);
        }
        throw new IllegalStateException("Can only have one root, was:" + logicalRecords);
    }


    public void addInstanceVariable(InstanceVariable instanceVariable) {
        instanceVariables.add(instanceVariable);
    }
}
