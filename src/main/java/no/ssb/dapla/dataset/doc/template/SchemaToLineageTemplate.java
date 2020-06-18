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

import java.util.List;

public class SchemaToLineageTemplate {
    private final Schema schema;
    private final String source;
    private final long sourceTimeStamp = 123456789L ;

    public SchemaToLineageTemplate(Schema schema, String source) {
        this.schema = schema;
        this.source = source;
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
        return LineageBuilder.createInstanceVariableBuilder()
                .name(name)
                .confidence("0.9") // TODO calculate when finding from source schema
                .type("inherited") // TODO find based on source schema
                .addSource(new Source(name, source, sourceTimeStamp))
        .build();
    }

    private LogicalRecord getLogicalRecord(String name) {
        return LineageBuilder.createLogicalRecordBuilder()
                .name(name)
                .build();
    }
}
