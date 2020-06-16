package no.ssb.dapla.dataset.doc.template;


import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.json.JSONException;
import org.junit.jupiter.api.Test;

class SchemaToLineageTemplateTest {

    @Test
    void testOneLevel() {
        Schema schema = SchemaBuilder
                .record("konto").namespace("no.ssb.dataset")
                .fields()
                .name("kontonummer").prop("description", "vilkårlig lang sekvens av tegn inkludert aksenter og spesielle tegn fra standardiserte tegnsett").type().stringType().noDefault()
                .name("innskudd").prop("description", "9 sifret nummer gitt de som er registrert i Enhetsregisteret.").type().stringType().noDefault()
                .name("gjeld").prop("description", "en sum av penger i hele kroner brukt i en kontekst. Dette kan være en transaksjon, saldo o.l.").type().optional().stringType()
                .endRecord();

        SchemaToLineageTemplate schemaToTemplate =
                new SchemaToLineageTemplate(schema, "/kilde/konto");

        String jsonString = schemaToTemplate.generateTemplateAsJsonString();
        System.out.println(jsonString);
    }

    @Test
    void testWithTwoLevels() throws JSONException {
        Schema schema = SchemaBuilder
                .record("root").namespace("no.ssb.dataset")
                .fields()
                .name("group").type().stringType().noDefault()
                .name("person").type().optional().type(
                        SchemaBuilder.record("person")
                                .fields()
                                .name("name").type().stringType().noDefault()
                                .name("sex").type().optional().stringType()
                                .name("address").type().optional().type(
                                SchemaBuilder.record("address")
                                        .fields()
                                        .name("street").type().stringType().noDefault()
                                        .name("postcode").type().stringType().noDefault()
                                        .endRecord())
                                .endRecord())
                .endRecord();

        SchemaToLineageTemplate schemaToTemplate =
                new SchemaToLineageTemplate(schema, "/kilde/ske/freg/person/rådata/v123");

        String jsonString = schemaToTemplate.generateTemplateAsJsonString();
        System.out.println(jsonString);
    }
}