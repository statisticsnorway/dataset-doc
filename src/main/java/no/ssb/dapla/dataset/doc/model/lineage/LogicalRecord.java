package no.ssb.dapla.dataset.doc.model.lineage;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

public class LogicalRecord {
    @JsonIgnore
    private String name;

    @JsonProperty("instanceVariables")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final Map<String, InstanceVariable> instanceVariables = new HashMap<>();

    @JsonProperty("logicalRecords")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final Map<String, LogicalRecord> logicalRecords = new HashMap<>();

    public void setName(String name) {
        this.name = name;
    }

    public void addLogicalRecord(LogicalRecord logicalRecord) {
        logicalRecords.put(logicalRecord.name, logicalRecord);
    }

    @JsonIgnore
    public LogicalRecord getRoot() {
        if (logicalRecords.size() == 1) {
            return logicalRecords.values().iterator().next();
        }
        throw new IllegalStateException("Can only have one root, was:" + logicalRecords);
    }


    public void addInstanceVariable(InstanceVariable instanceVariable) {
        instanceVariables.put(instanceVariable.getInherit(), instanceVariable);
    }
}
