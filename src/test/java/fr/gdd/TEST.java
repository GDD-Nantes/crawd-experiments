package fr.gdd;

import org.apache.jena.dboe.trans.bplustree.ProgressJenaIterator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

public class TEST {
    public static String watdiv_path = "/GDD/WATDIV";
    public static String dbpedia_path = "/GDD/RSFB/engines/FedUP-experiments/backup/summaries/largerdfbench/fedup-id";
    public static String wdbench_path = "/GDD/wdbench/WDBench";

    @Disabled
    @Test
    public void SPO_CDs() throws IOException {
        String watdiv="/GDD/Thi/count-distinct-sampling/watdiv/sample/SPO_CDs.csv";
        SPO.CDs(watdiv_path, watdiv, 1_000_000);
        System.out.println("done watdiv");
        String dbpedia="/GDD/Thi/count-distinct-sampling/largerdf_dbpedia/sample/SPO_CDs.csv";
        SPO.CDs_dbpedia(dbpedia_path, dbpedia, 10_000_000);
        System.out.println("done dbpedia");
        String wdbench="/GDD/Thi/count-distinct-sampling/wdbench/sample/SPO_CDs.csv";
        SPO.CDs(wdbench_path, wdbench, 100_000_000);
        System.out.println("done wdbench");
    }
    @Disabled
    @Test
    public void SPO_watdiv() throws IOException {

        String watdivPath = "/GDD/Thi/count-distinct-sampling/watdiv/sample/";
        int iterations = 6;
        int dataSize = 1_100_000;

        for (int i = 6; i <= iterations; i++) {
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
        String dbpediaPath = "/GDD/Thi/count-distinct-sampling/largerdf_dbpedia/sample/";
        int iterations = 6;
        int dataSize = 4_500_000;

        for (int i = 6; i <= iterations; i++) {
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
        String wdbenchPath = "/GDD/Thi/count-distinct-sampling/wdbench/sample/";
        int iterations = 6;
        int dataSize = 13_000_000;

        for (int i = 6; i <= iterations; i++) {
            String iterationDirectory = wdbenchPath + "Run_" + i + "/";
            Files.createDirectories(Paths.get(iterationDirectory));
            ProgressJenaIterator.rng = new Random(i);
            //String outputCDo = iterationDirectory + "SPO_CDo.csv";
            String outputCDs = iterationDirectory + "SPO_CDs.csv";
            //String outputCDp = iterationDirectory + "SPO_CDp.csv";

            //SPO.CDo(wdbench_path, outputCDo, dataSize);
            SPO.CDs_wdbench(wdbench_path, outputCDs, dataSize);
            //SPO.CDp_wdbench(wdbench_path, outputCDp, dataSize);

            System.out.println("WDBENCH: Done iteration " + i);
        }
    }

    @Disabled
    @Test
    public void groupby_classTEST() throws IOException {

        String watdivPath = "/GDD/Thi/count-distinct-sampling/watdiv/sample/groupby_class/";
        int iterations = 1;
        int dataSize = 526_410;

        for (int i = 1; i <= iterations; i++) {
            String iterationDirectory = watdivPath + "Run_" + i + "/";
            Files.createDirectories(Paths.get(iterationDirectory));
            ProgressJenaIterator.rng = new Random(i);
            String outputCDo = iterationDirectory + "SACSPO_CDo.csv";
            SACSPO.getSampleSaCSPO_CDo_GROUPBY_CLASS(watdiv_path, outputCDo, dataSize, "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>");
            System.out.println("WATDIV: Done iteration " + i);
        }

    }

    @Disabled
    @Test
    public void accept_reject() throws IOException {
        String watdivPath = "/GDD/Thi/count-distinct-sampling/watdiv/sample/accept_reject/";
        int iterations = 1;
        int dataSize = 526_410;

        for (int i = 1; i <= iterations; i++) {
            String iterationDirectory = watdivPath + "Run_" + i + "/";
            Files.createDirectories(Paths.get(iterationDirectory));
            ProgressJenaIterator.rng = new Random(i);
            String outputCDo = iterationDirectory + "SACSPO_CDo_AR.csv";
            SACSPO.Accept_Reject_SaCSPO_CDo_Role0("/GDD/WATDIV2", outputCDo, dataSize);
        }
    }

    @Disabled
    @Test
    public void fixed_predicate() throws IOException {
        String watdivPath = "/GDD/Thi/count-distinct-sampling/watdiv/sample/fixed_predicate/";
        int iterations = 2;
        int dataSize = 1_100_000;

        for (int i = 2; i <= iterations; i++) {
            String iterationDirectory = watdivPath + "Run_" + i + "/";
            Files.createDirectories(Paths.get(iterationDirectory));
            ProgressJenaIterator.rng = new Random(i);
            String outputCDo = iterationDirectory + "CDo_fixed_pred.csv";
            SPO.fixed_predicate_Test(watdiv_path, outputCDo, dataSize);
        }
    }


}
