package fr.gdd;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class TEST {
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
    public void groupby_classTEST() throws IOException {

        String outputfileGroupBy_CDs = "/GDD/Thi/count-distinct-sampling/watdiv/sample/groupby_class/CDs/SACSPO_CDs.csv";
        SACSPO.getSampleSaCSPO_CDs_GROUPBY_CLASS("/GDD/WATDIV", outputfileGroupBy_CDs, 1100000,"<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>");
        String outputfileGroupBy_CDo = "/GDD/Thi/count-distinct-sampling/watdiv/sample/groupby_class/CDo/SACSPO_CDo.csv";
        SACSPO.getSampleSaCSPO_CDo_GROUPBY_CLASS("/GDD/WATDIV", outputfileGroupBy_CDo, 1100000,"<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>");

    }
    @Disabled
    @Test
    public void groupby_class_predicateTEST() throws IOException {

        String outputfileGroupBy_CDs = "/GDD/Thi/count-distinct-sampling/watdiv/sample/groupby_class_predicate/CDs/SACSPO_CDs.csv";
        SACSPO.getSampleSaCSPO_CDs_GROUPBY_CLASS_PREDICATE("/GDD/WATDIV", outputfileGroupBy_CDs, 10000,"<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>");
        String outputfileGroupBy_CDo = "/GDD/Thi/count-distinct-sampling/watdiv/sample/groupby_class_predicate/CDo/SACSPO_CDo.csv";
        SACSPO.getSampleSaCSPO_CDo_GROUPBY_CLASS_PREDICATE("/GDD/WATDIV", outputfileGroupBy_CDo, 10000,"<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>");

    }
}
