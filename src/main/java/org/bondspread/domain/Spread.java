package org.bondspread.domain;

public class Spread {
    // no camel case to help with easy serialization
    private final String corporate_bond_id;
    private final String government_bond_id;
    private final String spread_to_benchmark;

    public Spread(String corporate_bond_id, String government_bond_id, String spread_to_benchmark) {
        this.corporate_bond_id = corporate_bond_id;
        this.government_bond_id = government_bond_id;
        this.spread_to_benchmark = spread_to_benchmark;
    }

    public String getCorporate_bond_id() {
        return corporate_bond_id;
    }

    public String getGovernment_bond_id() {
        return government_bond_id;
    }

    public String getSpread_to_benchmark() {
        return spread_to_benchmark;
    }

    public static String toBps(float percent) {
        // get the closest int from fp representation
        return String.valueOf((Math.round(percent * 100))) + " bps";
    }

    @Override
    public String toString() {
        return "Spread{" +
                "corporate_bond_id='" + corporate_bond_id + '\'' +
                ", government_bond_id='" + government_bond_id + '\'' +
                ", spread_to_benchmark='" + spread_to_benchmark + '\'' +
                '}';
    }
}
