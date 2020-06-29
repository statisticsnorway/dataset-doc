package no.ssb.dapla.dataset.doc.model.lineage;

import com.fasterxml.jackson.annotation.JsonIgnore;
import no.ssb.dapla.dataset.doc.traverse.ParentAware;
import no.ssb.dapla.dataset.doc.traverse.PathTraverse;
import no.ssb.dapla.dataset.doc.traverse.TraverseField;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Record extends Field implements TraverseField<Record>, ParentAware {

    public Record() {
        super();
        type = "structure"; // always structure for LogicalRecord
    }

    @JsonIgnore
    private final List<Record> children = new ArrayList<>();

    @JsonIgnore
    private final List<Instance> instances = new ArrayList<>();

    @JsonIgnore
    private Record parent;

    @JsonIgnore
    public List<Record> getChildren() {
        return children;
    }

    @Override
    public ParentAware getParent() {
        return parent;
    }

    @JsonIgnore
    public String getPath() {
        PathTraverse<Record> pathTraverse = new PathTraverse<>(this);
        return pathTraverse.getPath("spark_schema");
    }

    @Override
    public void addChild(Record record) {
        children.add(record);
        fields.add(record);
    }

    public List<Instance> find(String name) {
        return instances.stream()
                .filter(i -> i.getName().equals(name))
                .collect(Collectors.toList());
    }

    public void addInstanceVariable(Instance instance) {
        fields.add(instance);
        instances.add(instance);
    }

    public void setParent(Record parent) {
        this.parent = parent;
    }
}
