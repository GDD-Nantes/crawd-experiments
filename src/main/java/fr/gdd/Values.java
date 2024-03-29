package fr.gdd;

public class Values {
    private double sum_one_over_p_i_for_N;
    private double sum_one_over_p_i_for_fix_value;
    private double S;
    private double sum_ratio; // Sum of (1/p_i)/F_i

    // Constructor
    public Values(double sum_one_over_p_i_for_N, double sum_one_over_p_i_for_fix_value, double S, double sum_ratio) {
        this.sum_one_over_p_i_for_N = sum_one_over_p_i_for_N;
        this.sum_one_over_p_i_for_fix_value = sum_one_over_p_i_for_fix_value;
        this.S = S;
        this.sum_ratio = sum_ratio;
    }

    public double getS() {
        return S;
    }
    public double getSum_one_over_p_i_for_fix_value() {
        return sum_one_over_p_i_for_fix_value;
    }
    public double getSum_one_over_p_i_for_N() {
        return sum_one_over_p_i_for_N;
    }

    public double getSum_ratio() {
        return sum_ratio;
    }


    // Update method
    public void update(double sum_one_over_p_i_for_N, double sum_one_over_p_i_for_fix_value,double new_S, double new_ratio) {
        this.sum_one_over_p_i_for_N += sum_one_over_p_i_for_N;
        this.sum_one_over_p_i_for_fix_value += sum_one_over_p_i_for_fix_value;
        this.S += 1;
        this.sum_ratio += new_ratio;
    }

}

