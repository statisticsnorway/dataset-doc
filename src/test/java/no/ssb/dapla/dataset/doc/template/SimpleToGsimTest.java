package no.ssb.dapla.dataset.doc.template;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import no.ssb.dapla.dataset.doc.model.gsim.IdentifiableArtefact;
import no.ssb.dapla.dataset.doc.model.simple.Dataset;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

class SimpleToGsimTest {
    final private static String TEST_DATA_FOLDER = "testdata/gsim_files";

    String json = "{\n" +
            "  \"dataset-path\" : \"/path/to/dataset\",\n" +
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


    @AfterEach
    void cleanup() throws IOException {
        FileUtils.deleteDirectory(new File(TEST_DATA_FOLDER));
    }

    @Test
    void createGsimObjectsFor2Levels_AndWriteToFiles() throws JsonProcessingException {
        Dataset root = new ObjectMapper().readValue(json, Dataset.class);

        new File(TEST_DATA_FOLDER).mkdirs();
        new SimpleToGsim(root, new JsonToFileProvider(TEST_DATA_FOLDER)).createGsimObjects();
    }

    @Test
    void createGsimObjectsForZeroLevels() throws JsonProcessingException {
        String json = "{\n" +
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
                "    } ],\n" +
                "    \"path\" : \"konto\"\n" +
                "  }\n" +
                "}\n";

        Dataset root = new ObjectMapper().readValue(json, Dataset.class);
        new SimpleToGsim(root, identifiableArtefact -> {
//            System.out.println(getJson(identifiableArtefact));
        }).createGsimObjects();
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