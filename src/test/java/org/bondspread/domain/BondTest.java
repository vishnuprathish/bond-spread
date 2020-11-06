package org.bondspread.domain;

import junit.framework.TestCase;
import org.junit.Assert;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

public class BondTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
    }

    public void testParse() throws IOException {
        Bond b = new Bond(String.join("", Files.readAllLines(Path.of("src/test/resource/single.json"))));
        Assert.assertEquals(b.toString(), "Bond{id='c1', type='corporate', tenor='10.3 years', yield='5.30%', amount_outstanding=1200000}");
    }

}