package no.ssb.dapla.dataset.doc.model.lineage;

import no.ssb.dapla.dataset.doc.builder.LineageBuilder;
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
        List<FieldFinder.Field> fields = fieldFinder.find(name);
        if (fields.isEmpty()) {
            return getSource(fieldFinder.findNearMatches(name));
        }
        return getSource(fields);
    }

    private Source getSource(List<FieldFinder.Field> fields) {
        if (fields.isEmpty()) {
            return null;
        }
        int fieldCount = fields.size();
        String paths = fields.stream().map(FieldFinder.Field::getPath).collect(Collectors.joining(","));
        Float matchScore = fields.stream().map(FieldFinder.Field::getMatchScore).reduce((aFloat, aFloat2) -> aFloat * aFloat2).orElse(0F);
        float confidence = (0.9F / fieldCount) * matchScore; // TODO: calculate confidence based on if we have one field or more matches
        List<String> fieldCandidates = getFieldCandidates(fields, fieldCount, matchScore);
        return LineageBuilder.crateSourceBuilder()
                .field(fieldCount == 1 && matchScore == 1.0F ? paths : "")
                .fieldCandidates(fieldCandidates)
                .path(path)
                .version(version)
                .confidence(confidence)
                .build();
    }

    private List<String> getFieldCandidates(List<FieldFinder.Field> fields, int fieldCount, Float matchScore) {
        if (fieldCount > 1 || matchScore < 1.0F ) {
            return fields.stream().map(FieldFinder.Field::getPath).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
