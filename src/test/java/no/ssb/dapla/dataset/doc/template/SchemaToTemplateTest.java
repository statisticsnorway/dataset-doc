package no.ssb.dapla.dataset.doc.template;


import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.junit.jupiter.api.Test;

class SchemaToTemplateTest {

    @Test
    void testWithTwoLevels() {
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

        SchemaToTemplate schemaToTemplate =
                new SchemaToTemplate(schema, "/path/to/dataset")
                        .withLogicalRecordFilterFilter("unitType")
                        .withInstanceVariableFilter(
                                "dataStructureComponentRole",
                                "dataStructureComponentType",
                                "identifierComponentIsComposite",
                                "identifierComponentIsUnique",
                                "representedVariable",
                                "sentinelValueDomain",
                                "population");

        String jsonString = schemaToTemplate.generateSimpleTemplateAsJsonString();
        System.out.println(jsonString);
    }

    @Test
    void testOneLevel() {
        Schema schema = SchemaBuilder
                .record("konto").namespace("no.ssb.dataset")
                .fields()
                .name("kontonummer").prop("description", "vilkårlig lang sekvens av tegn inkludert aksenter og spesielle tegn fra standardiserte tegnsett").type().stringType().noDefault()
                .name("innskudd").prop("description", "9 sifret nummer gitt de som er registrert i Enhetsregisteret.").type().stringType().noDefault()
                .name("gjeld").prop("description", "en sum av penger i hele kroner brukt i en kontekst. Dette kan være en transaksjon, saldo o.l."). type().optional().stringType()
                .endRecord();

        SchemaToTemplate schemaToTemplate =
                new SchemaToTemplate(schema, "/path/to/dataset")
                        .withLogicalRecordFilterFilter("unitType")
                        .withInstanceVariableFilter(
                                "dataStructureComponentRole",
                                "dataStructureComponentType",
                                "identifierComponentIsComposite",
                                "identifierComponentIsUnique",
                                "representedVariable",
                                "sentinelValueDomain",
                                "population");

        String jsonString = schemaToTemplate.generateSimpleTemplateAsJsonString();
        System.out.println(jsonString);
    }
}