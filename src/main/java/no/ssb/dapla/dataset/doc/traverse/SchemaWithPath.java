package no.ssb.dapla.dataset.doc.traverse;

import no.ssb.dapla.dataset.doc.builder.LineageBuilder;
import no.ssb.dapla.dataset.doc.model.lineage.Source;
import org.apache.avro.Schema;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SchemaWithPath {
    static final float REQUIRED_MATCH_SCORE = 0.5F;// 1.0F is full match

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
            return findSource(fieldFinder.findNearMatches(name));
        }
        return findSource(fields);
    }

    private Source findSource(List<FieldFinder.Field> fields) {
        if (fields.isEmpty()) {
            return null;
        }
        List<FieldFinder.Field> validMatches = fields.stream()
                .filter(field -> field.getMatchScore() > REQUIRED_MATCH_SCORE)
                .collect(Collectors.toList());
        if (validMatches.isEmpty()) {
            return null;
        }

        int fieldCount = validMatches.size();
        String paths = validMatches.stream().map(FieldFinder.Field::getPath).collect(Collectors.joining(","));
        float matchScore = validMatches.stream().map(FieldFinder.Field::getMatchScore).reduce((a, b) -> a * b).orElse(0F);
        float confidence = (0.9F / fieldCount) * matchScore; // TODO: calculate confidence based on if we have one field or more matches
        List<String> fieldCandidates = getFieldCandidates(validMatches, fieldCount, matchScore);
        return LineageBuilder.crateSourceBuilder()
                .field(fieldCount == 1 && matchScore == 1.0F ? paths : "")
                .fieldCandidates(fieldCandidates)
                .path(path)
                .version(version)
                .confidence(confidence)
                .type(matchScore == 1.0f ? "inherited" : "derived/created")
                .build();
    }

    private List<String> getFieldCandidates(List<FieldFinder.Field> fields, int fieldCount, Float matchScore) {
        if (fieldCount > 1 || matchScore < 1.0F) {
            return fields.stream().map(FieldFinder.Field::getPath).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
