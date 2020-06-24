package no.ssb.dapla.dataset.doc.template;

import no.ssb.dapla.dataset.doc.builder.LineageBuilder;
import no.ssb.dapla.dataset.doc.model.lineage.FieldFinder;
import no.ssb.dapla.dataset.doc.model.lineage.InstanceVariable;
import no.ssb.dapla.dataset.doc.model.lineage.Source;
import org.apache.avro.Schema;

import java.util.List;

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
        // for now, just use the first match
        // TODO: Try to find the correct one in hierarchy if more matches and (Can look at other fields to do this)
        String field = instanceVariables.get(0).getPath();
        float confidence = 0.9F; // TODO: calculate confidence based on if we have one field or more matches
        return LineageBuilder.crateSourceBuilder()
                .field(field)
                .path(path)
                .version(version)
                .confidence(confidence)
                .build();
    }
}
