package no.ssb.dapla.dataset.doc.template;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import no.ssb.avro.convert.core.SchemaBuddy;
import no.ssb.dapla.dataset.doc.builder.LineageBuilder;
import no.ssb.dapla.dataset.doc.model.lineage.Dataset;
import no.ssb.dapla.dataset.doc.model.lineage.Instance;
import no.ssb.dapla.dataset.doc.model.lineage.Record;
import no.ssb.dapla.dataset.doc.model.lineage.SchemaWithPath;
import no.ssb.dapla.dataset.doc.model.lineage.Source;
import no.ssb.dapla.dataset.doc.traverse.SchemaTraverse;
import org.apache.avro.Schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class SchemaToLineageTemplate extends SchemaTraverse<Record> {
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

        Record root = traverse(schemaBuddy);
        return LineageBuilder.createDatasetBuilder()
                .root(root)
                .build();
    }

    @Override
    protected Record createChild(SchemaBuddy schemaBuddy, Record parent) {
        return getLogicalRecord(schemaBuddy.getName());
    }

    @Override
    protected void processField(SchemaBuddy schemaBuddy, Record parent) {
        parent.addInstanceVariable(getInstanceVariable(schemaBuddy.getName()));
    }

    private Instance getInstanceVariable(String name) {
        Collection<Source> sources = findSources(name);

        Optional<Float> confidence = sources.stream().map(Source::getConfidence).reduce(Float::sum);
        float sum = confidence.orElse(0F);
        float result = sum != 0F ? sum / sources.size() : 0F;
        String type = sum != 0F ? "inherited" : "derived/created";

        return LineageBuilder.createInstanceVariableBuilder()
                .name(name)
                .confidence(result) // TODO calculate when finding from source schema
                .type(type) // For now we can't find what is derived
                .addSources(sources)
                .build();
    }

    private Collection<Source> findSources(String name) {
        return schemaWithPaths.stream()
                .map(schemaWithPath -> schemaWithPath.getSource(name))
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private Record getLogicalRecord(String name) {
        return LineageBuilder.createLogicalRecordBuilder()
                .name(name)
                .build();
    }
}
