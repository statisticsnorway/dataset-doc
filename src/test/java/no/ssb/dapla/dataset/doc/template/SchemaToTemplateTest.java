package no.ssb.dapla.dataset.doc.template;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import no.ssb.dapla.dataset.doc.model.simple.EnumInfo;
import no.ssb.dapla.dataset.doc.model.simple.Instance;
import no.ssb.dapla.dataset.doc.model.simple.Record;
import no.ssb.dapla.dataset.doc.model.simple.TypeInfo;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

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

        ObjectNode logicalRecordRoot = new ObjectMapper().createObjectNode();
        logicalRecordRoot.put("name", "");
        logicalRecordRoot.put("description", "");
        ArrayNode ivs = logicalRecordRoot.putArray("instanceVariables");
        ivs.addObject().put("name", "group");
        ArrayNode lrs = logicalRecordRoot.putArray("logicalRecords");
        ObjectNode personLR = lrs.addObject();
        personLR.put("name", "person");
        personLR.put("description", "");
        {
            ArrayNode personIVs = personLR.putArray("instanceVariables");
            personIVs.addObject().put("name", "name");
            personIVs.addObject().put("name", "sex");

            ArrayNode addressLogicalRecords = personLR.putArray("logicalRecords");
            ObjectNode addressLR = addressLogicalRecords.addObject();
            addressLR.put("name", "address");
            addressLR.put("description", "");
            {
                ArrayNode addressIVs = addressLR.putArray("instanceVariables");
                addressIVs.addObject().put("name", "street");
                addressIVs.addObject().put("name", "postcode");
            }
        }
        String jsonString = schemaToTemplate.generateSimpleTemplateAsJsonString();

        JSONAssert.assertEquals(jsonString, logicalRecordRoot.toPrettyString(), false);
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
        ObjectNode logicalRecordRoot = new ObjectMapper().createObjectNode();
        logicalRecordRoot.put("name", "");
        logicalRecordRoot.put("description", "");
        ArrayNode ivs = logicalRecordRoot.putArray("instanceVariables");
        ivs.addObject().put("name", "id");
        ArrayNode lrs = logicalRecordRoot.putArray("logicalRecords");
        ObjectNode personLR = lrs.addObject();
        personLR.put("name", "person");
        personLR.put("description", "");
        {
            ArrayNode personIVs = personLR.putArray("instanceVariables");
            personIVs.addObject().put("name", "name");
            personIVs.addObject().put("name", "sex");
        }
        String jsonString = schemaToTemplate.generateSimpleTemplateAsJsonString();

        JSONAssert.assertEquals(jsonString, logicalRecordRoot.toPrettyString(), false);
    }

    @Test
    void testOneLevel() throws JSONException {
        Schema schema = SchemaBuilder
                .record("konto").namespace("no.ssb.dataset")
                .prop("description", "Inneholder kontoer av forskjellig art.")
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
                        return Map.of("some-id-could-be-guid", "All families 2018-01-01",
                                "Population_DUMMY", "Population_default");
                    case "RepresentedVariable":
                        return Map.of("some-id-could-be-guid", "NationalFamilyIdentifier",
                                "RepresentedVariable_DUMMY", "RepresentedVariable_default");
                    case "EnumeratedValueDomain":
                        return Map.of("some-id-could-be-guid", "Standard for gruppering av familier",
                                "EnumeratedValueDomain_DUMMY", "EnumeratedValueDomain_default");
                    case "DescribedValueDomain":
                        return Map.of("some-id-could-be-guid", "Heltall",
                                "DescribedValueDomain_DUMMY", "DescribedValueDomain_default",
                                "ValueDomain_DUMMY", "ValueDomain_default"
                        );
                    case "UnitType":
                        return Map.of("some-id-could-be-guid", "Heltall",
                                "UnitType_DUMMY", "UnitType_default");
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
        String json = TestUtils.load("testdata/template/default.json");

        JSONAssert.assertEquals(jsonString, json, false);
    }

    @Test
    void testConceptNameLookup() throws JSONException {
        Schema schema = SchemaBuilder
                .record("konto").namespace("no.ssb.dataset")
                .prop("description", "Inneholder kontoer av forskjellig art.")
                .fields()
                .name("kontonummer").prop("description", "vilkårlig lang sekvens av tegn inkludert aksenter og spesielle tegn fra standardiserte tegnsett").type().stringType().noDefault()
                .endRecord();


        ConceptNameLookup conceptNameLookup = new ConceptNameLookup() {
            @Override
            public Map<String, String> getNameToIds(String conceptType) {
                return Map.of();
            }

            @Override
            public List<String> getGsimSchemaEnum(String conceptType, String enumType) {
                return List.of();
            }
        };

        SchemaToTemplate schemaToTemplate =
                new SchemaToTemplate(schema, conceptNameLookup).withDoSimpleFiltering(false);

        String jsonString = schemaToTemplate.generateSimpleTemplateAsJsonString();
        // TODO: make proper test
        System.out.println(jsonString);
    }

    @Test
    void checkThatRootRecordNameIsEmptyString() throws JsonProcessingException {
        Schema schema = SchemaBuilder
                .record("spark_schema").namespace("no.ssb.dataset")
                .fields()
                .name("kontonummer").type().stringType().noDefault()
                .endRecord();

        SchemaToTemplate schemaToTemplate =
                new SchemaToTemplate(schema).withDoSimpleFiltering(false);

        String jsonString = schemaToTemplate.generateSimpleTemplateAsJsonString();

        Record root = new ObjectMapper().readValue(jsonString, Record.class);
        assertThat(root.getName()).isEmpty();
    }

    @Test
    void checkThatSuggestedValuesAreUsed() throws JsonProcessingException {
        Schema schema = SchemaBuilder
                .record("spark_schema").namespace("no.ssb.dataset")
                .fields()
                .name("kontonummer").type().stringType().noDefault()
                .endRecord();

        Suggester suggester = new Suggester() {
            @Override
            public Optional<Instance> suggestInstanceVariable(String name) {
                final Instance instance = new Instance();
                instance.setDescription("Suggested variable description");
                instance.setDataStructureComponentType(new EnumInfo("dataStructureComponentType", List.of("dataStructureComponentType")));
                instance.setIdentifierComponentIsComposite(false);
                instance.setIdentifierComponentIsUnique(true);
                instance.setDataStructureComponentRole(new EnumInfo("dataStructureComponentRole", List.of("dataStructureComponentRole")));
                instance.setPopulation(new TypeInfo("1", "type", Map.of("1", "population")));
                instance.setRepresentedVariable(new TypeInfo("2", "type", Map.of("2", "representedVariable")));
                instance.setSentinelValueDomain(new TypeInfo("3", "type", Map.of("3", "sentinelValueDomain")));
                return Optional.of(instance);
            }

            @Override
            public Optional<Record> suggestRecord(String name) {
                final Record record = new Record();
                record.setDescription("Suggested deataset description");
                record.setUnitType(new TypeInfo("00", "type", Map.of("00", "unitType")));
                return Optional.of(record);
            }
        };

        SchemaToTemplate schemaToTemplate =
                new SchemaToTemplate(schema, new DummyConceptNameLookup(), suggester).withDoSimpleFiltering(false);

        String jsonString = schemaToTemplate.generateSimpleTemplateAsJsonString();
        System.out.println(jsonString);
    }
}