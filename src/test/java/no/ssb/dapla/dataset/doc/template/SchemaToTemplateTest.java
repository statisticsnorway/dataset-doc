package no.ssb.dapla.dataset.doc.template;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import no.ssb.avro.convert.core.SchemaBuddy;
import no.ssb.dapla.dataset.doc.model.simple.Dataset;
import no.ssb.dapla.dataset.doc.model.simple.LogicalRecord;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

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

//        System.out.println(SchemaBuddy.parse(schema).toString(true));

        SchemaToTemplate schemaToTemplate = new SchemaToTemplate(schema)
                .withDoSimpleFiltering(true);


        System.out.println(schemaToTemplate.generateTemplateAsJsonString());
//
//
//        ObjectNode rootNode = new ObjectMapper().createObjectNode();
//        ObjectNode logicalRecordRoot = rootNode.putObject("logical-record-root");
//        logicalRecordRoot.put("name", "root");
//        ArrayNode ivs = logicalRecordRoot.putArray("instanceVariables");
//        ivs.addObject().put("name", "id");
//        ArrayNode lrs = logicalRecordRoot.putArray("logicalRecords");
//        ObjectNode personLR = lrs.addObject();
//        personLR.put("name", "person");
//        {
//            ArrayNode personIVs = personLR.putArray("instanceVariables");
//            personIVs.addObject().put("name", "name");
//            personIVs.addObject().put("name", "sex");
//        }

//        JSONAssert.assertEquals(jsonString, rootNode.toPrettyString(), false);
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

        SchemaToTemplate schemaToTemplate =
                new SchemaToTemplate(schema).withDoSimpleFiltering(true);

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

        JSONAssert.assertEquals(jsonString, rootNode.toPrettyString(), false);
    }

    @Test
    void testDataset() throws JsonProcessingException {
        SchemaToTemplate schemaToTemplate = new SchemaToTemplate(null);

        Dataset dataset = new Dataset();
        LogicalRecord root = new LogicalRecord();
        root.setName("Name");
        dataset.setRoot(root);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map = objectMapper.setFilterProvider(schemaToTemplate.getFilterProvider())
                .convertValue(dataset, new TypeReference<>() {
                });

        String json = objectMapper.enable(SerializationFeature.INDENT_OUTPUT)
                .writer(schemaToTemplate.getFilterProvider())
                .writeValueAsString(map);

        System.out.println(json);
//
//        System.out.println(json);
    }
}