package no.ssb.dapla.dataset.doc.model.simple;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import no.ssb.dapla.dataset.doc.traverse.TraverseField;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@JsonFilter("LogicalRecord_MinimumFilter")
public class LogicalRecord implements TraverseField<LogicalRecord> {
    public interface CreateIdHandler {
        String createId(InstanceVariable name);
    }

    @JsonProperty
    private String name;

    @JsonProperty
    private String unitType;

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final List<InstanceVariable> instanceVariables = new ArrayList<>();

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final List<LogicalRecord> logicalRecords = new ArrayList<>();

    @Override
    public void addChild(LogicalRecord child) {
        logicalRecords.add(child);
    }

    public String getName() {
        return name;
    }

    @JsonIgnore
    public String getPath() {
        // TODO: make this create path with parents
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnitType() {
        return unitType;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }


    public void addLogicalRecord(LogicalRecord logicalRecord) {
        logicalRecords.add(logicalRecord);
    }

    public List<LogicalRecord> getLogicalRecords() {
        return logicalRecords;
    }

    public void addInstanceVariable(InstanceVariable instanceVariable) {
        instanceVariables.add(instanceVariable);
    }

    public List<InstanceVariable> getInstanceVariables() {
        return instanceVariables;
    }

    public List<String> getInstanceVariableIds(CreateIdHandler createIdHandler) {
        return instanceVariables.stream().map(i -> "/" + createIdHandler.createId(i)).collect(Collectors.toList());
    }
}
