package no.ssb.dapla.dataset.doc.model.lineage;

import no.ssb.avro.convert.core.SchemaBuddy;
import no.ssb.dapla.dataset.doc.template.TestUtils;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.junit.jupiter.api.Test;

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

        List<InstanceVariable> instanceVariables = fieldFinder.find("innskudd");
        List<String> paths = instanceVariables.stream().map(InstanceVariable::getPath).collect(Collectors.toList());
        assertThat(paths).isEqualTo(List.of("konto.innskudd"));
    }

    @Test
    void findInSkattSchema() {
        Schema schema = TestUtils.loadSchema("testdata/skatt-v0.68.avsc");
//        System.out.println(SchemaBuddy.parse(schema).toString(true));

        FieldFinder fieldFinder = new FieldFinder(schema);

        List<InstanceVariable> instanceVariables = fieldFinder.find("organisasjonsnummer");
        List<String> paths = instanceVariables.stream().map(InstanceVariable::getPath).collect(Collectors.toList());
        assertThat(paths.size()).isEqualTo(12);
        System.out.println(paths);
    }

}