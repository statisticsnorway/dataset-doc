package no.ssb.dapla.dataset.doc.template;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.ssb.dapla.dataset.doc.builder.LineageBuilder;
import no.ssb.dapla.dataset.doc.model.lineage.Dataset;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.junit.jupiter.api.Test;

class SchemaToLineageTemplateTest {


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

        SchemaToLineageTemplate schemaToTemplate =
                LineageBuilder.createSchemaToLineageBuilder()
                        .addInput(new SchemaWithPath(schema, // use output schema for input for test to run for now
                                "/kilde/freg",
                                123456789))
                        .outputSchema(schema)
                        .build();

        String jsonString = schemaToTemplate.generateTemplateAsJsonString();
        System.out.println(jsonString);
    }

    @Test
    void testWithOneLevel() throws JsonProcessingException {
        Schema schema = SchemaBuilder
                .record("root").namespace("no.ssb.dataset")
                .fields()
                .name("fnr").type().stringType().noDefault()
                .name("konto").type().optional().type(
                        SchemaBuilder.record("konto")
                                .fields()
                                .name("saldo").type().stringType().noDefault()
                                .endRecord())
                .endRecord();

        SchemaToLineageTemplate schemaToTemplate =
                LineageBuilder.createSchemaToLineageBuilder()
                        .addInput(new SchemaWithPath(schema, "/kilde/freg", 123456789))
                        .outputSchema(schema)
                        .build();

        String jsonString = schemaToTemplate.generateTemplateAsJsonString();

        // Check that we can parse json
        Dataset root = new ObjectMapper().readValue(jsonString, Dataset.class);
        String jsonStringForDataSet = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(root);
        System.out.println(jsonStringForDataSet);
    }

    @Test
    void testJoinTwoSources() throws JsonProcessingException {
        Schema inputSchemaSkatt = SchemaBuilder
                .record("spark_schema").namespace("no.ssb.dataset")
                .fields()
                .name("fnr").type().stringType().noDefault()
                .name("sum_innskudd").type().intType().noDefault()
                .endRecord();

        Schema inputSchemaFreg = SchemaBuilder
                .record("spark_schema").namespace("no.ssb.dataset")
                .fields()
                .name("fnr").type().stringType().noDefault()
                .name("alder").type().stringType().noDefault()
                .endRecord();

        Schema outputSchema = SchemaBuilder
                .record("spark_schema").namespace("no.ssb.dataset")
                .fields()
                .name("fnr").type().stringType().noDefault()
                .name("sum_innskudd").type().intType().noDefault()
                .name("alder").type().stringType().noDefault()
                .endRecord();

        SchemaToLineageTemplate schemaToTemplate =
                LineageBuilder.createSchemaToLineageBuilder()
                        .addInput(new SchemaWithPath(inputSchemaSkatt, "/kilde/skatt/konto/innskudd", 123456789))
                        .addInput(new SchemaWithPath(inputSchemaFreg, "/kilde/freg/alder", 123456789))
                        .outputSchema(outputSchema)
                        .build();

        String jsonString = schemaToTemplate.generateTemplateAsJsonString();

        // Check that we can parse json
        Dataset root = new ObjectMapper().readValue(jsonString, Dataset.class);
        String jsonStringForDataSet = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(root);
        System.out.println(jsonStringForDataSet);
    }

    @Test
    void testJoinTwoSourcesFromComplexSchema() throws JsonProcessingException {
        Schema inputSchemaSkatt = SchemaBuilder
                .record("spark_schema").namespace("no.ssb.dataset")
                .fields()
                .name("fnr").type().stringType().noDefault()
                .name("konto").type().optional().type(
                        SchemaBuilder.record("konto")
                                .fields()
                                .name("innskudd").type().stringType().noDefault()
                                .endRecord())
                .endRecord();

        Schema inputSchemaFreg = SchemaBuilder
                .record("spark_schema").namespace("no.ssb.dataset")
                .fields()
                .name("fnr").type().stringType().noDefault()
                .name("person").type().optional().type(
                        SchemaBuilder.record("person")
                                .fields()
                                .name("alder").type().stringType().noDefault()
                                .endRecord())
                .endRecord();

        Schema outputSchema = SchemaBuilder
                .record("spark_schema").namespace("no.ssb.dataset")
                .fields()
                .name("fnr").type().stringType().noDefault()
                .name("sum_innskudd").type().intType().noDefault()
                .name("alder").type().stringType().noDefault()
                .endRecord();

        SchemaToLineageTemplate schemaToTemplate =
                LineageBuilder.createSchemaToLineageBuilder()
                        .addInput(new SchemaWithPath(inputSchemaSkatt, "/kilde/skatt/konto/innskudd", 123456789))
                        .addInput(new SchemaWithPath(inputSchemaFreg, "/kilde/freg/alder", 123456789))
                        .outputSchema(outputSchema)
                        .build();

        String jsonString = schemaToTemplate.generateTemplateAsJsonString();

        // Check that we can parse json
        Dataset root = new ObjectMapper().readValue(jsonString, Dataset.class);
        String jsonStringForDataSet = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(root);
        System.out.println(jsonStringForDataSet);
    }
}