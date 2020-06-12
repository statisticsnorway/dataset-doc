package no.ssb.dapla.dataset.doc.template;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import no.ssb.dapla.dataset.doc.model.gsim.IdentifiableArtefact;
import no.ssb.dapla.dataset.doc.model.gsim.LogicalRecord;
import no.ssb.dapla.dataset.doc.model.simple.Dataset;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

class SimpleToGsimTest {
    final private static String TEST_DATA_FOLDER = "testdata/gsim_files";

    String json = "{\n" +
            "  \"logical-record-root\" : {\n" +
            "    \"name\" : \"root\",\n" +
            "    \"logicalRecords\" : [ {\n" +
            "      \"name\" : \"person\",\n" +
            "      \"logicalRecords\" : [ {\n" +
            "        \"name\" : \"address\",\n" +
            "        \"logicalRecords\" : [ ],\n" +
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
            "}\n";

    @Test
    void createGsimObjectsFor2Levels_AndWriteToFiles() throws JsonProcessingException {
        Dataset root = new ObjectMapper().readValue(json, Dataset.class);

        // to generate files
        //new File(TEST_DATA_FOLDER).mkdirs();
        //new SimpleToGsim(root, new JsonToFileProvider(TEST_DATA_FOLDER)).createGsimObjects();

        new SimpleToGsim(root, "/path/to/dataset", identifiableArtefact -> {
            String fileName = String.format("testdata/gsim_2_levels/%s_%s.json", identifiableArtefact.getGsimName(), identifiableArtefact.getName());
            String expected = TestUtils.load(fileName);
            assertThat(getJson(identifiableArtefact)).isEqualTo(expected);
        }).createGsimObjects();
    }

    @Test
    void createGsimObjectsForZeroLevels() throws JsonProcessingException {
        String json = "{\n" +
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
                "}\n";

        Dataset root = new ObjectMapper().readValue(json, Dataset.class);
        List<String> list = Arrays.asList(
                "/InstanceVariable/path.to.dataset.konto.kontonummer",
                "/InstanceVariable/path.to.dataset.konto.innskudd",
                "/InstanceVariable/path.to.dataset.konto.gjeld");

        Queue<String> paths = new LinkedList<>();
        paths.add("path.to.dataset.konto");
        paths.add("path.to.dataset.konto");
        paths.add("path.to.dataset.konto");
        paths.add("path.to.dataset.konto.kontonummer");
        paths.add("path.to.dataset.konto.innskudd");
        paths.add("path.to.dataset.konto.gjeld");

        Queue<String> gsimNames = new LinkedList<>();
        gsimNames.add("UnitDataStructure");
        gsimNames.add("UnitDataSet");
        gsimNames.add("LogicalRecord");
        gsimNames.add("InstanceVariable");
        gsimNames.add("InstanceVariable");
        gsimNames.add("InstanceVariable");

        new SimpleToGsim(root, "/path/to/dataset", identifiableArtefact -> {
            String fileName = String.format("testdata/gsim_1_level/%s_%s.json", identifiableArtefact.getGsimName(), identifiableArtefact.getName());
            String expected = TestUtils.load(fileName);
            assertThat(getJson(identifiableArtefact)).isEqualTo(expected);

            if (identifiableArtefact instanceof LogicalRecord) {
                List<String> instanceVariables = ((LogicalRecord) identifiableArtefact).getInstanceVariables();
                assertThat(instanceVariables).isEqualTo(list);
            }
            assertThat(identifiableArtefact.getId()).isEqualTo(paths.remove());
            assertThat(identifiableArtefact.getGsimName()).isEqualTo(gsimNames.remove());
        }).createGsimObjects();

        assertThat(paths).isEmpty();
        assertThat(gsimNames).isEmpty();
    }


    String getJson(IdentifiableArtefact identifiableArtefact) {
        try {
            return new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
                    .writeValueAsString(identifiableArtefact);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}