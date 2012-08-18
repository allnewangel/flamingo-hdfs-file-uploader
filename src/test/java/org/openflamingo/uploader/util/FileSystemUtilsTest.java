package org.openflamingo.uploader.util;

import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.Test;

/**
 * FileSystemUtils의 단위 테스트 케이스.
 *
 * @author Edward KIM
 * @since 0.2
 */
public class FileSystemUtilsTest {

    @Test
    public void correctPath() throws Exception {
        Assert.assertTrue(FileSystemUtils.correctPath("") == null);
        Assert.assertEquals("file:/local", FileSystemUtils.correctPath("/local"));
        Assert.assertEquals("file:/local", FileSystemUtils.correctPath("file:/local"));
        Assert.assertEquals("hdfs://localhost:9000/local", FileSystemUtils.correctPath("hdfs://localhost:9000/local"));
    }

    @Test
    public void checkScheme() throws Exception {
        FileSystemUtils.checkScheme("file:/local", FileSystemScheme.LOCAL);
        FileSystemUtils.checkScheme("hdfs://localhost:9000/local", FileSystemScheme.HDFS);

        try {
            FileSystemUtils.checkScheme("hdfs://localhost:9000/local", FileSystemScheme.LOCAL);
            Assert.assertTrue(false);
        } catch (Exception ex) {
            Assert.assertTrue(true);
        }

        try {
            FileSystemUtils.checkScheme("file:/local", FileSystemScheme.HDFS);
            Assert.assertTrue(false);
        } catch (Exception ex) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void validateSameFileSystem() throws Exception {
        FileSystemUtils.validateSameFileSystem("file:/local", "/local1");
    }

}
