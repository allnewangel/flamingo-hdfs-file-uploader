package org.openflamingo.uploader.util;

/**
 * File System Enumeration.
 *
 * @author Edward KIM
 * @since 0.1
 */
public enum FileSystemScheme {

    LOCAL("file:"), HDFS("hdfs://");

    private String scheme;

    private FileSystemScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getScheme() {
        return scheme;
    }
}
