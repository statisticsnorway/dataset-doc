package no.ssb.dapla.dataset.doc.template;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import no.ssb.avro.convert.core.SchemaBuddy;
import no.ssb.dapla.dataset.doc.builder.LineageBuilder;
import no.ssb.dapla.dataset.doc.model.lineage.Dataset;
import no.ssb.dapla.dataset.doc.model.lineage.InstanceVariable;
import no.ssb.dapla.dataset.doc.model.lineage.LogicalRecord;
import no.ssb.dapla.dataset.doc.model.lineage.Source;
import org.apache.avro.Schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SchemaToLineageTemplate extends SchemaTraverse<LogicalRecord> {
    private final Schema schema;
    private final List<SchemaWithPath> schemaWithPaths;

    public SchemaToLineageTemplate(List<SchemaWithPath> inputs, Schema outputSchema) {
        this.schemaWithPaths = inputs;
        this.schema = outputSchema;
    }

    public String generateTemplateAsJsonString() {
        try {
            Dataset dataset = generateTemplate();
            return new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
                    .writeValueAsString(dataset);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private Dataset generateTemplate() {
        SchemaBuddy schemaBuddy = SchemaBuddy.parse(schema);

        LogicalRecord root = traverse(schemaBuddy);
        return LineageBuilder.createDatasetBuilder()
                .root(root)
                .build();
    }

    private LogicalRecord traverse(SchemaBuddy schemaBuddy) {
        LogicalRecord root = LineageBuilder.createLogicalRecordBuilder()
                .build();

        traverse(schemaBuddy, root);
        return root.getRoot(); // We don't need the first witch always is the spark_schema root
    }

    @Override
    protected LogicalRecord processStruct(SchemaBuddy schemaBuddy, LogicalRecord parent) {
        LogicalRecord childLogicalRecord = getLogicalRecord(schemaBuddy.getName());
        parent.addLogicalRecord(childLogicalRecord);
        return childLogicalRecord;
    }

    @Override
    protected void processField(SchemaBuddy schemaBuddy, LogicalRecord parent) {
        parent.addInstanceVariable(getInstanceVariable(schemaBuddy.getName()));
    }

    private InstanceVariable getInstanceVariable(String name) {
        // TODO: calculate confidence when doing findSources
        Collection<Source> sources = findSources(name);

        return LineageBuilder.createInstanceVariableBuilder()
                .name(name)
                .confidence("0.9") // TODO calculate when finding from source schema
                .type("inherited") // For now we can't find what is derived
                .addSources(sources)
                .build();
    }

    private Collection<Source> findSources(String name) {
        return schemaWithPaths.stream()
                .map(schemaWithPath -> schemaWithPath.getSource(name))
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private LogicalRecord getLogicalRecord(String name) {
        return LineageBuilder.createLogicalRecordBuilder()
                .name(name)
                .build();
    }
}
