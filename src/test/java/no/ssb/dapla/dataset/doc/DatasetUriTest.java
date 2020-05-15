package no.ssb.dapla.dataset.doc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DatasetUriTest {

    @Test
    void testOf1a() {
        DatasetUri uri = DatasetUri.of("gs://my-bucket/data/prefix", "/path/to/dataset", "123456789");
        validate(uri, "gs://my-bucket/data/prefix/path/to/dataset/123456789", "gs://my-bucket/data/prefix",
                "/data/prefix", "/path/to/dataset", "123456789");
    }

    @Test
    void testOf1b() {
        DatasetUri uri = DatasetUri.of("gs://my-bucket/data/prefix/", "/path/to/dataset", "123456789");
        validate(uri, "gs://my-bucket/data/prefix/path/to/dataset/123456789", "gs://my-bucket/data/prefix",
                "/data/prefix", "/path/to/dataset", "123456789");
    }

    @Test
    void testOf1b2() {
        DatasetUri uri = DatasetUri.of("gs://my-bucket/data/prefix///", "/path/to/dataset", "123456789");
        validate(uri, "gs://my-bucket/data/prefix/path/to/dataset/123456789", "gs://my-bucket/data/prefix",
                "/data/prefix", "/path/to/dataset", "123456789");
    }

    @Test
    void testOf1b3() {
        DatasetUri uri = DatasetUri.of("gs://my-bucket/data/prefix//////", "/////path/to/dataset///////", "///////123456789/////");
        validate(uri, "gs://my-bucket/data/prefix/path/to/dataset/123456789", "gs://my-bucket/data/prefix",
                "/data/prefix", "/path/to/dataset", "123456789");
    }

    @Test
    void testOf1c() {
        DatasetUri uri = DatasetUri.of("gs://my-bucket/data/prefix", "path/to/dataset", "123456789");
        validate(uri, "gs://my-bucket/data/prefix/path/to/dataset/123456789", "gs://my-bucket/data/prefix",
                "/data/prefix", "/path/to/dataset", "123456789");
    }

    @Test
    void testOf1d() {
        DatasetUri uri = DatasetUri.of("gs://my-bucket/data/prefix/", "path/to/dataset", "123456789");
        validate(uri, "gs://my-bucket/data/prefix/path/to/dataset/123456789", "gs://my-bucket/data/prefix",
                "/data/prefix", "/path/to/dataset", "123456789");
    }

    @Test
    void testOf2() {
        DatasetUri uri = DatasetUri.of("gs://my-bucket/data/prefix", "/path/to/dataset", 123456789L);
        validate(uri, "gs://my-bucket/data/prefix/path/to/dataset/123456789", "gs://my-bucket/data/prefix",
                "/data/prefix", "/path/to/dataset", "123456789");
    }

    @Test
    void testOf3() {
        DatasetUri uri = DatasetUri.of("gs", "my-bucket", "/path/to/dataset", "123456789");
        validate(uri, "gs://my-bucket/path/to/dataset/123456789", "gs://my-bucket",
                "", "/path/to/dataset", "123456789");
    }

    @Test
    void testOf4() {
        DatasetUri uri = DatasetUri.of("gs", "my-bucket", "/data/prefix", "/path/to/dataset", "123456789");
        validate(uri, "gs://my-bucket/data/prefix/path/to/dataset/123456789", "gs://my-bucket/data/prefix",
                "/data/prefix", "/path/to/dataset", "123456789");
    }

    private void validate(DatasetUri uri, String full, String parentUri, String pathPrefix, String path, String version) {
        assertEquals(parentUri, uri.getParentUri());
        assertEquals(pathPrefix, uri.getPathPrefix());
        assertEquals(path, uri.getPath());
        assertEquals(version, uri.getVersion());
        assertEquals(full, uri.toString());
    }
}