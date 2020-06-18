package no.ssb.dapla.dataset.doc.model.lineage;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class LogicalRecord extends Field {

    @JsonProperty
    private String type = "structure";

    @JsonIgnore
    private final List<LogicalRecord> logicalRecords = new ArrayList<>();

    public void addLogicalRecord(LogicalRecord logicalRecord) {
        logicalRecords.add(logicalRecord);
        fields.add(logicalRecord);
    }

    @JsonIgnore
    public LogicalRecord getRoot() {
        if (logicalRecords.size() == 1) {
            return logicalRecords.get(0);
        }
        throw new IllegalStateException("Can only have one root, was:" + logicalRecords);
    }

    public void addInstanceVariable(InstanceVariable instanceVariable) {
        fields.add(instanceVariable);
    }
}
