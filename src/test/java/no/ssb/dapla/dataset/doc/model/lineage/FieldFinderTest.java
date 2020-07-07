package no.ssb.dapla.dataset.doc.model.lineage;

import no.ssb.dapla.dataset.doc.template.TestUtils;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class FieldFinderTest {

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

    @Test
    void find() {
        FieldFinder fieldFinder = new FieldFinder(inputSchemaSkatt);

        List<String> paths = fieldFinder.getPaths("innskudd");
        assertThat(paths).isEqualTo(Collections.singletonList("konto.innskudd"));
    }

    @Test
    void find2FindFnr() {
        FieldFinder fieldFinder = new FieldFinder(inputSchemaSkatt);

        List<String> paths = fieldFinder.getPaths("fnr");
        assertThat(paths).isEqualTo(Collections.singletonList("fnr"));
    }

    @Test
    void findInSkattSchema() {
        Schema schema = TestUtils.loadSchema("testdata/skatt-v0.68.avsc");

        FieldFinder fieldFinder = new FieldFinder(schema);

        List<FieldFinder.Field> instances = fieldFinder.find("organisasjonsnummer");
        List<String> paths = instances.stream().map(FieldFinder.Field::getPath).collect(Collectors.toList());
        assertThat(paths.size()).isEqualTo(12);

        fieldFinder.find("organisasjonsnummer").stream().map(FieldFinder.Field::getPath).forEach(System.out::println);
        System.out.println("----------------------");
        fieldFinder.find("personidentifikator").stream().map(FieldFinder.Field::getPath).forEach(System.out::println);
    }

}