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

public class SchemaToLineageTemplate {
    private final Schema schema;
    private final List<SchemaWithPath> schemaWithPaths;
    private final long sourceTimeStamp = 123456789L;

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
                .name("datasetName")
                .build();

        traverse(schemaBuddy, root, 0);
        return root.getRoot(); // We don't need the first witch always is the spark_schema root
    }

    private void traverse(SchemaBuddy schemaBuddy, LogicalRecord parentLogicalRecord, int level) {
        if (schemaBuddy.isArrayType()) {
            List<SchemaBuddy> children = schemaBuddy.getChildren();
            if (children.size() != 1) {
                throw new IllegalStateException("Avro Array can only have 1 child: was:" + schemaBuddy.toString(true) + "‰n");
            }
            traverse(children.get(0), parentLogicalRecord, level);
            return;
        }

        if (schemaBuddy.isBranch()) {
            LogicalRecord childLogicalRecord = getLogicalRecord(schemaBuddy.getName());
            parentLogicalRecord.addLogicalRecord(childLogicalRecord);
            for (SchemaBuddy child : schemaBuddy.getChildren()) {
                traverse(child, childLogicalRecord, level + 1);
            }
        } else {
            parentLogicalRecord.addInstanceVariable(getInstanceVariable(schemaBuddy.getName()));
        }
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
