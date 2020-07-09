package no.ssb.dapla.dataset.doc.template;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import no.ssb.avro.convert.core.SchemaBuddy;
import no.ssb.dapla.dataset.doc.builder.LineageBuilder;
import no.ssb.dapla.dataset.doc.model.lineage.Dataset;
import no.ssb.dapla.dataset.doc.model.lineage.Instance;
import no.ssb.dapla.dataset.doc.model.lineage.Record;
import no.ssb.dapla.dataset.doc.model.lineage.SourceConfidence;
import no.ssb.dapla.dataset.doc.traverse.SchemaWithPath;
import no.ssb.dapla.dataset.doc.model.lineage.Source;
import no.ssb.dapla.dataset.doc.traverse.SchemaTraverse;
import org.apache.avro.Schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
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
            String jacskonJson = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
                    .writeValueAsString(dataset);
            // Need to use gson to format arrays with line break (Did not find out how to do this with Jackson)
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.toJson(new JsonParser().parse(jacskonJson));
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
        return LineageBuilder.createLogicalRecordBuilder()
                .name(schemaBuddy.getName())
                .build();
    }

    @Override
    protected void processField(SchemaBuddy schemaBuddy, Record parent) {
        parent.addInstanceVariable(getInstanceVariable(schemaBuddy.getName()));
    }

    private Instance getInstanceVariable(String name) {
        Collection<Source> sources = findSources(name);

        SourceConfidence sourceConfidence = new SourceConfidence(sources);

        return LineageBuilder.createInstanceVariableBuilder()
                .name(name)
                .confidence(sourceConfidence.getAverageConfidenceOfSources())
                .type(sourceConfidence.getFieldType())
                .addTypeCandidates(sourceConfidence.getTypeCandidates())
                .addSources(sources)
                .build();
    }

    private Collection<Source> findSources(String name) {
        return schemaWithPaths.stream()
                .map(schemaWithPath -> schemaWithPath.getSource(name))
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
