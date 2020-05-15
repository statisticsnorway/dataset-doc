package no.ssb.dapla.dataset.doc;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatasetUri {

    static final Pattern parentUriPattern = Pattern.compile("(?<scheme>[^:]+):(?://(?<authority>[^/]*))?(?<pathPrefix>/[^/].*)?");
    static final Pattern pathPattern = Pattern.compile("/*(?<path>/[^/].*)");
    static final Pattern versionPattern = Pattern.compile("/*(?<version>[^/]+)/*");

    public static DatasetUri of(String parentUri, String path, String version) {
        Matcher parentUriMatcher = parentUriPattern.matcher(parentUri);
        if (!parentUriMatcher.matches()) {
            throw new IllegalArgumentException("parentUri does not match required pattern");
        }
        Matcher pathMatcher = pathPattern.matcher("/" + path);
        if (!pathMatcher.matches()) {
            throw new IllegalArgumentException("path does not match required pattern");
        }
        Matcher versionMatcher = versionPattern.matcher(version);
        if (!versionMatcher.matches()) {
            throw new IllegalArgumentException("version does not match required pattern");
        }
        String normalizedScheme = parentUriMatcher.group("scheme");
        String normalizedAuthority = parentUriMatcher.group("authority");
        String normalizedPathPrefix = parentUriMatcher.group("pathPrefix") == null ? "" : stripTrailingSlashes(parentUriMatcher.group("pathPrefix"));
        String normalizedPath = stripTrailingSlashes(pathMatcher.group("path"));
        String normalizedVersion = stripLeadingSlashes(versionMatcher.group("version"));
        return new DatasetUri(normalizedScheme, normalizedAuthority, normalizedPathPrefix, normalizedPath, normalizedVersion);
    }

    private static String stripTrailingSlashes(String input) {
        return input.endsWith("/") ? stripTrailingSlashes(input.substring(0, input.length() - 1)) : input;
    }

    private static String stripLeadingSlashes(String input) {
        return input.startsWith("/") ? stripLeadingSlashes(input.substring(1)) : input;
    }

    public static DatasetUri of(String parentUri, String path, long version) {
        return of(parentUri, path, String.valueOf(version));
    }

    public static DatasetUri of(String scheme, String authority, String path, String version) {
        return of(scheme, authority, "", path, version);
    }

    public static DatasetUri of(String scheme, String authority, String pathPrefix, String path, String version) {
        return of(scheme + ":" + (authority == null ? "" : "//" + authority) + pathPrefix, path, version);
    }

    final String pathPrefix;
    final String path;
    final String version;
    final URI uri;

    DatasetUri(String scheme, String authority, String pathPrefix, String path, String version) {
        this.pathPrefix = pathPrefix;
        this.path = path;
        this.version = version;
        try {
            uri = new URI(scheme, authority, pathPrefix + path + "/" + version, null, null);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public String getPathPrefix() {
        return pathPrefix;
    }

    public String getParentUri() {
        return uri.getScheme() + ":" + (uri.getAuthority() != null ? "//" + uri.getAuthority() : "") + pathPrefix;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }

    public URI toURI() {
        return uri;
    }

    public String toVersionedDatasetId() {
        return path + "/" + version;
    }

    @Override
    public String toString() {
        return uri.toString();
    }
}
