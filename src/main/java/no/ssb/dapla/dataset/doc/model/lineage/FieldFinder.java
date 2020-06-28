package no.ssb.dapla.dataset.doc.model.lineage;

import no.ssb.avro.convert.core.SchemaBuddy;
import no.ssb.dapla.dataset.doc.builder.LineageBuilder;
import no.ssb.dapla.dataset.doc.traverse.SchemaTraverse;
import org.apache.avro.Schema;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FieldFinder extends SchemaTraverse<Record> {
    final SchemaBuddy schemaBuddy;
    final Record root;

    public FieldFinder(Schema schema) {
        this.schemaBuddy = SchemaBuddy.parse(schema);
        root = traverse(schemaBuddy);
    }

    public List<String> getPaths(String field) {
        return find(field).stream().map(Instance::getPath).collect(Collectors.toList());
    }

    public List<Instance> find(String field) {
        List<Instance> result = new ArrayList<>();
        search(field, root, result);
        return result;
    }

    private void search(String name, Record parent, List<Instance> result) {
        result.addAll(parent.find(name));
        for (Record child : parent.getChildren()) {
            search(name, child, result);
        }
    }

    @Override
    protected Record createChild(SchemaBuddy schemaBuddy, Record parent) {
        return LineageBuilder.createLogicalRecordBuilder()
                .parent(parent)
                .name(schemaBuddy.getName())
                .build();
    }

    @Override
    protected void processField(SchemaBuddy schemaBuddy, Record parent) {
        Instance instance = LineageBuilder.createInstanceVariableBuilder()
                .name(schemaBuddy.getName())
                .build();
        // TODO: Find a better to deal with "spark_schema" root
        String path = parent.getPath().equals("spark_schema") ? "" : parent.getPath() + ".";
        instance.setPath(path + schemaBuddy.getName());
        parent.addInstanceVariable(instance);
    }
}
