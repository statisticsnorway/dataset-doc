package no.ssb.dapla.dataset.doc.template;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import no.ssb.dapla.dataset.doc.model.gsim.IdentifiableArtefact;
import org.apache.commons.io.IOUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

class TestUtils {

    static ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    private static ClassLoader classloader = Thread.currentThread().getContextClassLoader();

    public static String load(String fileName) {
        try {
            return IOUtils.toString(getInputStream(fileName), StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void compare(String fileName, IdentifiableArtefact identifiableArtefact) {

    }

    private static InputStream getInputStream(String fileName) throws FileNotFoundException {
        InputStream stream = classloader.getResourceAsStream(fileName);
        if (stream == null) {
            throw new FileNotFoundException(fileName);
        }
        return stream;
    }

}

