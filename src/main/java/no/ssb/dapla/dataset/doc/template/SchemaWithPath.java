package no.ssb.dapla.dataset.doc.template;

import no.ssb.avro.convert.core.SchemaBuddy;
import no.ssb.dapla.dataset.doc.model.lineage.Source;
import org.apache.avro.Schema;

import java.util.ArrayList;
import java.util.List;

public class SchemaWithPath {
    final Schema schema;
    final String path;
    final long version;

    public SchemaWithPath(Schema schema, String path, long version) {
        this.schema = schema;
        this.path = path;
        this.version = version;
    }


    public Source getSource(String name) {
        // Very simple implementation for POC
        // TODO: extend avro-buddy with recursive search to find matching fields in schema
        List<String> list= new ArrayList<>();
        SchemaBuddy.parse(schema, schemaBuddy -> {
            if(schemaBuddy.getName().equals(name)) {
                list.add(schemaBuddy.getName()); // TODO get path example:skatt.konto.innskudd
            }
        });
        if(list.isEmpty()) {
            return null;
        }
        // for now, just use the fist match
        return new Source(list.get(0), path, version);
    }
}
