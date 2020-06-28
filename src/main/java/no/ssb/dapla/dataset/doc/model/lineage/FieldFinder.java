package no.ssb.dapla.dataset.doc.model.lineage;

import no.ssb.avro.convert.core.SchemaBuddy;
import no.ssb.dapla.dataset.doc.builder.LineageBuilder;
import no.ssb.dapla.dataset.doc.traverse.SchemaTraverse;
import org.apache.avro.Schema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FieldFinder extends SchemaTraverse<LogicalRecord> {
    final SchemaBuddy schemaBuddy;
    final LogicalRecord root;

    public FieldFinder(Schema schema) {
        this.schemaBuddy = SchemaBuddy.parse(schema);
        root = traverse(schemaBuddy);
    }

    public List<String> getPaths(String field) {
        return find(field).stream().map(InstanceVariable::getPath).collect(Collectors.toList());
    }

    public List<InstanceVariable> find(String field) {
        List<InstanceVariable> result = new ArrayList<>();
        search(field, root, result);
        return result;
    }

    private void search(String name, LogicalRecord parent, List<InstanceVariable> result) {
        result.addAll(parent.find(name));
        for (LogicalRecord child : parent.getChildren()) {
            search(name, child, result);
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
        // TODO: Find a better to deal with "spark_schema" root
        String path = parent.getPath().equals("spark_schema") ? "" : parent.getPath() + ".";
        instanceVariable.setPath(path + schemaBuddy.getName());
        parent.addInstanceVariable(instanceVariable);
    }
}
