package fr.gdd;

import org.apache.jena.dboe.trans.bplustree.ProgressJenaIterator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

public class SPOTest {
    public static String watdiv_path = "/WATDIV";
    public static String dbpedia_path = "/LargeRDFBench";
    public static String wdbench_path = "/WDBench";

    @Disabled
    @Test
    public void SPO_watdiv() throws IOException {

        String watdivPath = "/count-distinct-sampling/watdiv/sample/";
        int iterations = 5;
        int dataSize = 1_100_000;

        for (int i = 1; i <= iterations; i++) {
            String iterationDirectory = watdivPath + "Run_" + i + "/";
            Files.createDirectories(Paths.get(iterationDirectory));
            ProgressJenaIterator.rng = new Random(i);
            String outputCDo = iterationDirectory + "SPO_CDo.csv";
            String outputCDs = iterationDirectory + "SPO_CDs.csv";
            String outputCDp = iterationDirectory + "SPO_CDp.csv";

            SPO.CDo(watdiv_path, outputCDo, dataSize);
            SPO.CDs(watdiv_path, outputCDs, dataSize);
            SPO.CDp(watdiv_path, outputCDp, dataSize);

            System.out.println("WATDIV: Done iteration " + i);
        }

    }
    @Disabled
    @Test
    public void SPO_dbpedia() throws IOException {
        String dbpediaPath = "/count-distinct-sampling/largerdf_dbpedia/sample/";
        int iterations = 5;
        int dataSize = 4_500_000;

        for (int i = 1; i <= iterations; i++) {
            String iterationDirectory = dbpediaPath + "Run_" + i + "/";
            Files.createDirectories(Paths.get(iterationDirectory));
            ProgressJenaIterator.rng = new Random(i);
            String outputCDo = iterationDirectory + "SPO_CDo.csv";
            String outputCDs = iterationDirectory + "SPO_CDs.csv";
            String outputCDp = iterationDirectory + "SPO_CDp.csv";

            SPO.CDo_dbpedia(dbpedia_path, outputCDo, dataSize);
            SPO.CDs_dbpedia(dbpedia_path, outputCDs, dataSize);
            SPO.CDp_dbpedia(dbpedia_path, outputCDp, dataSize);

            System.out.println("DBPEDIA: Done iteration " + i);
        }
    }
    @Disabled
    @Test
    public void SPO_wdbench() throws IOException {
        String wdbenchPath = "/count-distinct-sampling/wdbench/sample/";
        int iterations = 5;
        int dataSize = 13_000_000;

        for (int i = 1; i <= iterations; i++) {
            String iterationDirectory = wdbenchPath + "Run_" + i + "/";
            Files.createDirectories(Paths.get(iterationDirectory));
            ProgressJenaIterator.rng = new Random(i);
            String outputCDo = iterationDirectory + "SPO_CDo.csv";
            String outputCDs = iterationDirectory + "SPO_CDs.csv";
            String outputCDp = iterationDirectory + "SPO_CDp.csv";

            SPO.CDo(wdbench_path, outputCDo, dataSize);
            SPO.CDs_wdbench(wdbench_path, outputCDs, dataSize);
            SPO.CDp_wdbench(wdbench_path, outputCDp, dataSize);

            System.out.println("WDBENCH: Done iteration " + i);
        }
    }


}
