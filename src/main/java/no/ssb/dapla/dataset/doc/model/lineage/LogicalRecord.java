package no.ssb.dapla.dataset.doc.model.lineage;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogicalRecord {
    @JsonProperty
    private String name;

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final List<LogicalRecord> logicalRecords = new ArrayList<>();

    @JsonProperty("instanceVariables")
    private final Map<String, InstanceVariable> mapOfInstanceVariables = new HashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addLogicalRecord(LogicalRecord logicalRecord) {
        logicalRecords.add(logicalRecord);
    }

    public List<LogicalRecord> getLogicalRecords() {
        return logicalRecords;
    }

    public void addInstanceVariable(InstanceVariable instanceVariable) {
        mapOfInstanceVariables.put(instanceVariable.getInherit(), instanceVariable);
    }
}
