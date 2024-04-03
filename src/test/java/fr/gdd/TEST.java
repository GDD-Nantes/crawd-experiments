package fr.gdd;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TEST {
    public static String watdiv_path = "/GDD/WATDIV";
    public static String dbpedia_path = "/GDD/RSFB/engines/FedUP-experiments/backup/summaries/largerdfbench/fedup-id";
    public static String wdbench_path = "/GDD/wdbench/WDBench";
    @Disabled
    @Test
    public void QBTEST() throws IOException {
        String outputfileQB2 = "/GDD/Thi/count-distinct-sampling/watdiv/sample/QB2.csv";
        String outputfileQB3 = "/GDD/Thi/count-distinct-sampling/watdiv/sample/QB3.csv";
        String outputfileQB4 = "/GDD/Thi/count-distinct-sampling/watdiv/sample/QB4.csv";
        String outputfileQB5 = "/GDD/Thi/count-distinct-sampling/watdiv/sample/QB5.csv";
        Integer sampleSize = 10916457;
        QB.QB2("/GDD/WATDIV", outputfileQB2, sampleSize);
        QB.QB3("/GDD/WATDIV", outputfileQB3, sampleSize);
        QB.QB4("/GDD/WATDIV", outputfileQB4, sampleSize);
        QB.QB5("/GDD/WATDIV", outputfileQB5, sampleSize);
    }

    @Disabled
    @Test
    public void QCTEST() throws IOException {
        String outputfileQC3 = "/GDD/Thi/count-distinct-sampling/watdiv/sample/QC3.csv";
        String outputfileQC4 = "/GDD/Thi/count-distinct-sampling/watdiv/sample/QC4.csv";
        String outputfileQC5 = "/GDD/Thi/count-distinct-sampling/watdiv/sample/QC5.csv";
        String outputfileQC6 = "/GDD/Thi/count-distinct-sampling/watdiv/sample/QC6.csv";
        QC.QC3("/GDD/WATDIV", outputfileQC3, 1000, "<http://db.uwaterloo.ca/~galuc/wsdbm/Role0>");
        QC.QC4("/GDD/WATDIV", outputfileQC4, 1000, "<http://db.uwaterloo.ca/~galuc/wsdbm/Role0>");
        QC.QC5("/GDD/WATDIV", outputfileQC5, 1000, "<http://db.uwaterloo.ca/~galuc/wsdbm/Role0>");
        QC.QC6("/GDD/WATDIV", outputfileQC6, 1000, "<http://db.uwaterloo.ca/~galuc/wsdbm/Role0>");
    }

    @Disabled
    @Test
    public void groupby_class_predicateTEST() throws IOException {

        String outputfileGroupBy_CDs = "/GDD/Thi/count-distinct-sampling/watdiv/sample/groupby_class_predicate/CDs/SACSPO_CDs.csv";
        SACSPO.getSampleSaCSPO_CDs_GROUPBY_CLASS_PREDICATE("/GDD/WATDIV", outputfileGroupBy_CDs, 100000, "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>");
        String outputfileGroupBy_CDo = "/GDD/Thi/count-distinct-sampling/watdiv/sample/groupby_class_predicate/CDo/SACSPO_CDo.csv";
        SACSPO.getSampleSaCSPO_CDo_GROUPBY_CLASS_PREDICATE("/GDD/WATDIV", outputfileGroupBy_CDo, 100000, "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>");

    }

    @Disabled
    @Test
    public void groupby_classTEST() throws IOException {

        String watdiv = "/GDD/Thi/count-distinct-sampling/watdiv/sample/groupby_class/CDo/SACSPO_CDo.csv";
        SACSPO.getSampleSaCSPO_CDo_GROUPBY_CLASS(watdiv_path, watdiv, 1_100_000, "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>");
        System.out.println("done watdiv");

        String dbpedia = "/GDD/Thi/count-distinct-sampling/largerdf_dbpedia/sample/groupby_class/CDo/SACSPO_CDo.csv";
        SACSPO.getSampleSaCSPO_CDo_GROUPBY_CLASS_dbpedia(dbpedia_path, dbpedia, 4_500_000, "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>");
        System.out.println("done dbpedia");

        String wdbench = "/GDD/Thi/count-distinct-sampling/wdbench/sample/groupby_class/CDo/SACSPO_CDo.csv";
        SACSPO.getSampleSaCSPO_CDo_GROUPBY_CLASS(wdbench_path, wdbench, 13_000_000, "<http://www.wikidata.org/prop/direct/P131>");


    }


    @Disabled
    @Test
    public void SAC_CDc() throws IOException {
         String watdiv="/GDD/Thi/count-distinct-sampling/watdiv/sample/SAC_CDc.csv";
         SAC.SAC_CDc(watdiv_path, watdiv, 100_000, "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>");
         System.out.println("done watdiv");
         String dbpedia="/GDD/Thi/count-distinct-sampling/largerdf_dbpedia/sample/SAC_CDc.csv";
         SAC.SAC_CDc_dbpedia(dbpedia_path, dbpedia, 1_000_000, "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>");
         System.out.println("done dbpedia");
         String wdbench="/GDD/Thi/count-distinct-sampling/wdbench/sample/SAC_CDc.csv";
         SAC.SAC_CDc(wdbench_path, wdbench, 10_000_000, "<http://www.wikidata.org/prop/direct/P131>");
         System.out.println("done wdbench");
    }

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
        int iterations = 5;
        int dataSize = 1_100_000;

        for (int i = 1; i <= iterations; i++) {
            String iterationDirectory = watdivPath + "Run_" + i + "/";
            Files.createDirectories(Paths.get(iterationDirectory));

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
        int iterations = 5;
        int dataSize = 4_500_000;

        for (int i = 1; i <= iterations; i++) {
            String iterationDirectory = dbpediaPath + "Run_" + i + "/";
            Files.createDirectories(Paths.get(iterationDirectory));

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
        int iterations = 5;
        int dataSize = 130_000_000;

        for (int i = 1; i <= iterations; i++) {
            String iterationDirectory = wdbenchPath + "Run_" + i + "/";
            Files.createDirectories(Paths.get(iterationDirectory));

            String outputCDo = iterationDirectory + "SPO_CDo.csv";
            String outputCDs = iterationDirectory + "SPO_CDs.csv";
            String outputCDp = iterationDirectory + "SPO_CDp.csv";

            SPO.CDo(wdbench_path, outputCDo, dataSize);
            SPO.CDs(wdbench_path, outputCDs, dataSize);
            SPO.CDp(wdbench_path, outputCDp, dataSize);

            System.out.println("WDBENCH: Done iteration " + i);
        }
    }

}
