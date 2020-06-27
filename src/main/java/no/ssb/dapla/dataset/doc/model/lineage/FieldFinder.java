package no.ssb.dapla.dataset.doc.model.lineage;

import no.ssb.avro.convert.core.SchemaBuddy;
import no.ssb.dapla.dataset.doc.builder.LineageBuilder;
import no.ssb.dapla.dataset.doc.traverse.SchemaTraverse;
import org.apache.avro.Schema;

import java.util.ArrayList;
import java.util.List;

public class FieldFinder extends SchemaTraverse<LogicalRecord> {
    final SchemaBuddy schemaBuddy;
    final LogicalRecord root;

    public FieldFinder(Schema schema) {
        this.schemaBuddy = SchemaBuddy.parse(schema);
        root = traverse(schemaBuddy);
    }

    public List<InstanceVariable> find(String field) {
        List<InstanceVariable> instanceVariables = new ArrayList<>();
        search(field, root, instanceVariables);
        return instanceVariables;
    }

    private void search(String name, LogicalRecord parent, List<InstanceVariable> instanceVariables) {
        instanceVariables.addAll(parent.find(name));
        for (LogicalRecord child : parent.getChildren()) {
            search(name, child, instanceVariables);
        }
    }

    @Override
    protected LogicalRecord createChild(SchemaBuddy schemaBuddy, LogicalRecord parent) {
        return LineageBuilder.createLogicalRecordBuilder()
                .parent(parent)
                .name(schemaBuddy.getName())
                .build();
    }

    @Override
    protected void processField(SchemaBuddy schemaBuddy, LogicalRecord parent) {
        InstanceVariable instanceVariable = LineageBuilder.createInstanceVariableBuilder()
                .name(schemaBuddy.getName())
                .build();
        // TODO: Find a better to remove "spark_schema" root
        String path = parent.getPath().equals("spark_schema") ? "" : parent.getPath() + ".";
        instanceVariable.setPath(path + schemaBuddy.getName());
        parent.addInstanceVariable(instanceVariable);
    }
}
