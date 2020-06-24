package no.ssb.dapla.dataset.doc.model.lineage;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class LogicalRecord extends Field {

    public LogicalRecord() {
        type = "structure"; // always structure for LogicalRecord
    }

    @JsonIgnore
    private final List<LogicalRecord> children = new ArrayList<>();

    @JsonIgnore
    private final List<InstanceVariable> instanceVariables = new ArrayList<>();

    @JsonIgnore
    private LogicalRecord parent;

    @JsonIgnore
    public List<LogicalRecord> getChildren() {
        return children;
    }

    @JsonIgnore
    public String getPath() {
        StringJoiner joiner = new StringJoiner(".");
        for (ListIterator<String> iter = getParents().listIterator(getParents().size()); iter.hasPrevious(); ) {
            joiner.add(iter.previous());
        }
        return joiner.add(getName()).toString();
    }

    public void addLogicalRecord(LogicalRecord logicalRecord) {
        logicalRecord.parent = this;
        children.add(logicalRecord);
        fields.add(logicalRecord);
    }

    public List<InstanceVariable> find(String name) {
        return instanceVariables.stream()
                .filter(i -> i.getName().equals(name))
                .collect(Collectors.toList());
    }

    public void addInstanceVariable(InstanceVariable instanceVariable) {
        fields.add(instanceVariable);
        instanceVariables.add(instanceVariable);
    }

    private List<String> getParents() {
        LogicalRecord currentParent = parent;
        List<String> parentList = new ArrayList<>();
        while (currentParent != null) {
            // don't add spark_schema
            // TODO: find a better way to check this
            if (!currentParent.getName().equals("spark_schema")) {
                parentList.add(currentParent.name);
            }
            currentParent = currentParent.parent;
        }
        return parentList;
    }
}
