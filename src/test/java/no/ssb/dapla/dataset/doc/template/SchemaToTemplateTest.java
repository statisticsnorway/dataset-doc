package no.ssb.dapla.dataset.doc.template;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.List;
import java.util.Map;

class SchemaToTemplateTest {

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

        SchemaToTemplate schemaToTemplate = new SchemaToTemplate(schema)
                .withDoSimpleFiltering(true)
                .addInstanceVariableFilter("description");

        ObjectNode rootNode = new ObjectMapper().createObjectNode();
        ObjectNode logicalRecordRoot = rootNode.putObject("logical-record-root");
        logicalRecordRoot.put("name", "root");
        ArrayNode ivs = logicalRecordRoot.putArray("instanceVariables");
        ivs.addObject().put("name", "group");
        ArrayNode lrs = logicalRecordRoot.putArray("logicalRecords");
        ObjectNode personLR = lrs.addObject();
        personLR.put("name", "person");
        {
            ArrayNode personIVs = personLR.putArray("instanceVariables");
            personIVs.addObject().put("name", "name");
            personIVs.addObject().put("name", "sex");

            ArrayNode addressLogicalRecords = personLR.putArray("logicalRecords");
            ObjectNode addressLR = addressLogicalRecords.addObject();
            addressLR.put("name", "address");
            {
                ArrayNode addressIVs = addressLR.putArray("instanceVariables");
                addressIVs.addObject().put("name", "street");
                addressIVs.addObject().put("name", "postcode");
            }
        }
        String jsonString = schemaToTemplate.generateSimpleTemplateAsJsonString();

        JSONAssert.assertEquals(jsonString, rootNode.toPrettyString(), false);
    }

    @Test
    void checkThatArrayWorks() throws JSONException {
        Schema schema = SchemaBuilder
                .record("root").namespace("no.ssb.dataset")
                .fields()
                .name("id").type().stringType().noDefault()
                .name("person").type().optional().type(
                        SchemaBuilder.array()
                                .items(SchemaBuilder.record("person")
                                        .fields()
                                        .name("name").type().stringType().noDefault()
                                        .name("sex").type().optional().stringType()
                                        .endRecord()
                                )
                )
                .endRecord();

        SchemaToTemplate schemaToTemplate = new SchemaToTemplate(schema)
                .withDoSimpleFiltering(true)
                .addInstanceVariableFilter("description");

        System.out.println(schemaToTemplate.generateSimpleTemplateAsJsonString());
        ObjectNode rootNode = new ObjectMapper().createObjectNode();
        ObjectNode logicalRecordRoot = rootNode.putObject("logical-record-root");
        logicalRecordRoot.put("name", "root");
        ArrayNode ivs = logicalRecordRoot.putArray("instanceVariables");
        ivs.addObject().put("name", "id");
        ArrayNode lrs = logicalRecordRoot.putArray("logicalRecords");
        ObjectNode personLR = lrs.addObject();
        personLR.put("name", "person");
        {
            ArrayNode personIVs = personLR.putArray("instanceVariables");
            personIVs.addObject().put("name", "name");
            personIVs.addObject().put("name", "sex");
        }
        String jsonString = schemaToTemplate.generateSimpleTemplateAsJsonString();

        JSONAssert.assertEquals(jsonString, rootNode.toPrettyString(), false);
    }

    @Test
    void testOneLevel() throws JSONException {
        Schema schema = SchemaBuilder
                .record("konto").namespace("no.ssb.dataset")
                .fields()
                .name("kontonummer").prop("description", "vilkårlig lang sekvens av tegn inkludert aksenter og spesielle tegn fra standardiserte tegnsett").type().stringType().noDefault()
                .name("innskudd").prop("description", "9 sifret nummer gitt de som er registrert i Enhetsregisteret.").type().stringType().noDefault()
                .name("gjeld").prop("description", "en sum av penger i hele kroner brukt i en kontekst. Dette kan være en transaksjon, saldo o.l.").type().optional().stringType()
                .endRecord();


        ConceptNameLookup conceptNameLookup = new ConceptNameLookup() {
            @Override
            public Map<String, String> getNameToIds(String conceptType) {
                switch (conceptType) {
                    case "Population":
                        return Map.of("All families 2018-01-01", "some-id-could-be-guid",
                                "Population_DUMMY", "Population_DUMMY-id");
                    case "RepresentedVariable":
                        return Map.of("NationalFamilyIdentifier", "some-id-could-be-guid",
                                "RepresentedVariable_DUMMY", "RepresentedVariable_DUMMY-id");
                    case "EnumeratedValueDomain":
                        return Map.of("Standard for gruppering av familier", "some-id-could-be-guid",
                                "EnumeratedValueDomain_DUMMY", "EnumeratedValueDomain_DUMMY-id");
                    case "DescribedValueDomain":
                        return Map.of("Heltall", "some-id-could-be-guid",
                                "DescribedValueDomain_DUMMY", "DescribedValueDomain_DUMMY-id");
                    case "UnitType":
                        return Map.of("Heltall", "some-id-could-be-guid",
                                "UnitType_DUMMY", "UnitType_DUMMY-id");
                    default:
                        throw new IllegalArgumentException("");
                }
            }

            @Override
            public List<String> getGsimSchemaEnum(String conceptType, String enumType) {
                switch (conceptType) {
                    case "InstanceVariable":
                        return processInstanceVariable(enumType);
                    default:
                        throw new IllegalArgumentException("");
                }
            }

            private List<String> processInstanceVariable(String enumType) {
                switch (enumType) {
                    case "dataStructureComponentType":
                        return List.of("IDENTIFIER", "MEASURE", "ATTRIBUTE");
                    case "dataStructureComponentRole":
                        return List.of("ENTITY", "IDENTITY", "COUNT", "TIME", "GEO");
                    default:
                        throw new IllegalArgumentException("");
                }
            }
        };

        SchemaToTemplate schemaToTemplate =
                new SchemaToTemplate(schema, conceptNameLookup).withDoSimpleFiltering(false);

        String jsonString = schemaToTemplate.generateSimpleTemplateAsJsonString();
        System.out.println(jsonString);

        ObjectNode rootNode = new ObjectMapper().createObjectNode();
        ObjectNode logicalRecordRoot = rootNode.putObject("logical-record-root");
        logicalRecordRoot.put("name", "konto");
        ArrayNode ivs = logicalRecordRoot.putArray("instanceVariables");
        ivs.addObject()
                .put("name", "kontonummer")
                .put("description", "vilkårlig lang sekvens av tegn inkludert aksenter og spesielle tegn fra standardiserte tegnsett");
        ivs.addObject()
                .put("name", "innskudd")
                .put("description", "9 sifret nummer gitt de som er registrert i Enhetsregisteret.");
        ivs.addObject()
                .put("name", "gjeld")
                .put("description", "en sum av penger i hele kroner brukt i en kontekst. Dette kan være en transaksjon, saldo o.l.");

//        JSONAssert.assertEquals(jsonString, rootNode.toPrettyString(), false);
    }
}