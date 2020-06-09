package no.ssb.dapla.dataset.doc.model.simple;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@JsonFilter("LogicalRecord_MinimumFilter")
public class LogicalRecord {
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

    @JsonIgnore
    private String parentId;

    @JsonIgnore
    private String id;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUnitType() {
        return unitType;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
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
