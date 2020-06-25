package no.ssb.dapla.dataset.doc.template;

import no.ssb.dapla.dataset.doc.builder.LineageBuilder;
import no.ssb.dapla.dataset.doc.model.lineage.FieldFinder;
import no.ssb.dapla.dataset.doc.model.lineage.InstanceVariable;
import no.ssb.dapla.dataset.doc.model.lineage.Source;
import org.apache.avro.Schema;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SchemaWithPath {
    final Schema schema;
    final String path;
    final long version;

    private final FieldFinder fieldFinder;

    public SchemaWithPath(Schema schema, String path, long version) {
        fieldFinder = new FieldFinder(schema);
        this.schema = schema;
        this.path = path;
        this.version = version;
    }

    public Source getSource(String name) {
        List<InstanceVariable> instanceVariables = fieldFinder.find(name);
        if (instanceVariables.isEmpty()) {
            return null;
        }
        int fieldCount = instanceVariables.size();
        String paths = instanceVariables.stream().map(InstanceVariable::getPath).collect(Collectors.joining(","));
        float confidence = 0.9F; // TODO: calculate confidence based on if we have one field or more matches
        List<String> fields = fieldCount > 1 ? instanceVariables.stream().map(InstanceVariable::getPath).collect(Collectors.toList()) : Collections.emptyList();
        return LineageBuilder.crateSourceBuilder()
                .field(fieldCount == 1 ? paths : "")
                .fieldCandidates(fields)
                .path(path)
                .version(version)
                .confidence(confidence)
                .build();
    }
}
