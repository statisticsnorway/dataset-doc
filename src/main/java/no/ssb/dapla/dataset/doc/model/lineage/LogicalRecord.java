package no.ssb.dapla.dataset.doc.model.lineage;

import com.fasterxml.jackson.annotation.JsonIgnore;
import no.ssb.dapla.dataset.doc.traverse.ParentAware;
import no.ssb.dapla.dataset.doc.traverse.PathTraverse;
import no.ssb.dapla.dataset.doc.traverse.TraverseField;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LogicalRecord extends Field implements TraverseField<LogicalRecord>, ParentAware {

    public LogicalRecord() {
        super();
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

    @Override
    public ParentAware getParent() {
        return parent;
    }

    @JsonIgnore
    public String getPath() {
        PathTraverse<LogicalRecord> pathTraverse = new PathTraverse<>(this);
        return pathTraverse.getPath("spark_schema");
    }

    @Override
    public void addChild(LogicalRecord logicalRecord) {
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

    public void setParent(LogicalRecord parent) {
        this.parent = parent;
    }
}
