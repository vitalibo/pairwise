package ua.edu.lp.asu.pairwise;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static ua.edu.lp.asu.pairwise.TestHelper.parameter;

public class PairwiseTest {

    @DataProvider
    public Object[][] samplesParameters() {
        return new Object[][]{{
            Arrays.asList(
                parameter("A", "a1", "a2", "a3"),
                parameter("B", "b1", "b2", "b3"),
                parameter("C", "c1", "c2", "c3"))}, {

            Arrays.asList(
                parameter("Browser", "Chrome", "Firefox", "Internet Explorer", "Safari"),
                parameter("Page", "Link", "Image", "Description"),
                parameter("Product", "Phone", "Movie", "Computer", "Blender", "Microwave", "Book", "Sweater"),
                parameter("Click", "Link", "Image", "Description"))}, {

            Arrays.asList(
                parameter("Type", "CD", "DVD"),
                parameter("Recording Speed", 2, 4, 8, 16, 24, 36, 52),
                parameter("File System", "ISO", "UDF", "HFS", "ISO9660"),
                parameter("Multisession", "No", "Start", "Continue"),
                parameter("Capacity", "100Mb", "700Mb", "4.7Gb"))}, {

            Arrays.asList(
                parameter("Type", "Primary", "Logical", "Single", "Span", "Stripe", "Mirror", "RAID-5"),
                parameter("Size", 10, 100, 500, 1000, 5000, 10000, 40000),
                parameter("Format method", "quick", "slow"),
                parameter("File system", "FAT", "FAT32", "NTFS"),
                parameter("Cluster size", 512, 1024, 2048, 4096, 8192, 16384, 32768, 65536),
                parameter("Compression", true, false))}};
    }

    @Test(dataProvider = "samplesParameters")
    public void testBuild(List<Parameter<?>> parameters) {
        final List<Case> pairs = InParameterOrderStrategy
            .generatePairs(parameters)
            .stream()
            .flatMap(Collection::stream)
            .collect(Collectors.toList());

        Pairwise pairwise = new Pairwise.Builder()
            .withParameters(parameters)
            .build();

        Assert.assertEquals(pairwise.getParameters(), parameters);
        Assert.assertFalse(pairwise.getCases().isEmpty());
        pairs.forEach(pair -> Assert.assertTrue(matches(pairwise, pair)));
    }

    private static boolean matches(Pairwise pairwise, Case pair) {
        return pairwise.getCases()
            .stream()
            .filter(pair::matches)
            .findFirst()
            .isPresent();
    }

}