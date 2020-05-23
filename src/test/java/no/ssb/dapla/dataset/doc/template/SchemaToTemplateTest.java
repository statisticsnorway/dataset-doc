package no.ssb.dapla.dataset.doc.template;


import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
        String expected = "{\n" +
                "  \"dataset-path\" : \"/path/to/dataset\",\n" +
                "  \"logical-record-root\" : {\n" +
                "    \"name\" : \"root\",\n" +
                "    \"logicalRecords\" : [ {\n" +
                "      \"name\" : \"person\",\n" +
                "      \"logicalRecords\" : [ {\n" +
                "        \"name\" : \"address\",\n" +
                "        \"instanceVariables\" : [ {\n" +
                "          \"name\" : \"street\",\n" +
                "          \"description\" : \"street\"\n" +
                "        }, {\n" +
                "          \"name\" : \"postcode\",\n" +
                "          \"description\" : \"postcode\"\n" +
                "        } ]\n" +
                "      } ],\n" +
                "      \"instanceVariables\" : [ {\n" +
                "        \"name\" : \"name\",\n" +
                "        \"description\" : \"name\"\n" +
                "      }, {\n" +
                "        \"name\" : \"sex\",\n" +
                "        \"description\" : \"sex\"\n" +
                "      } ]\n" +
                "    } ],\n" +
                "    \"instanceVariables\" : [ {\n" +
                "      \"name\" : \"group\",\n" +
                "      \"description\" : \"group\"\n" +
                "    } ]\n" +
                "  }\n" +
                "}";
        assertThat(jsonString).isEqualTo(expected);
    }

    @Test
    void testOneLevel() {
        Schema schema = SchemaBuilder
                .record("konto").namespace("no.ssb.dataset")
                .fields()
                .name("kontonummer").prop("description", "vilkårlig lang sekvens av tegn inkludert aksenter og spesielle tegn fra standardiserte tegnsett").type().stringType().noDefault()
                .name("innskudd").prop("description", "9 sifret nummer gitt de som er registrert i Enhetsregisteret.").type().stringType().noDefault()
                .name("gjeld").prop("description", "en sum av penger i hele kroner brukt i en kontekst. Dette kan være en transaksjon, saldo o.l.").type().optional().stringType()
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
        String expected = "{\n" +
                "  \"dataset-path\" : \"/path/to/dataset\",\n" +
                "  \"logical-record-root\" : {\n" +
                "    \"name\" : \"konto\",\n" +
                "    \"instanceVariables\" : [ {\n" +
                "      \"name\" : \"kontonummer\",\n" +
                "      \"description\" : \"vilkårlig lang sekvens av tegn inkludert aksenter og spesielle tegn fra standardiserte tegnsett\"\n" +
                "    }, {\n" +
                "      \"name\" : \"innskudd\",\n" +
                "      \"description\" : \"9 sifret nummer gitt de som er registrert i Enhetsregisteret.\"\n" +
                "    }, {\n" +
                "      \"name\" : \"gjeld\",\n" +
                "      \"description\" : \"en sum av penger i hele kroner brukt i en kontekst. Dette kan være en transaksjon, saldo o.l.\"\n" +
                "    } ]\n" +
                "  }\n" +
                "}";

        assertThat(jsonString).isEqualTo(expected);
    }
}