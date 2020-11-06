package org.bondspread.domain;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 *
 */
public class Bond {
    private String id;
    private String type;
    // specified in years
    private String tenor;
    private String yield;
    private Long amount_outstanding;

    Bond()  {}

    public Bond(String input) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Bond bond = mapper.readValue(input, Bond.class);
        this.id = bond.id;
        this.type = bond.type;
        this.tenor = bond.tenor;
        this.yield = bond.yield;
        this.amount_outstanding = bond.amount_outstanding;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getTenor() {
        return tenor;
    }

    public Float getTenorFloat() {
        return Float.parseFloat(tenor.split(" ")[0]);
    }

    public String getYield() {
        return yield;
    }

    public Long getAmount_outstanding() {
        return amount_outstanding;
    }

    public boolean isValid() {
        // validation for bond parsing
        return this.id != null
                && this.type != null && (this.type.equals("government") || this.type.equals("corporate"))
                && this.tenor != null && this.tenor.endsWith(" years")
                && this.yield != null && this.yield.endsWith("%")
                && this.amount_outstanding != null;
    }

    @Override
    public String toString() {
        return "Bond{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", tenor='" + tenor + '\'' +
                ", yield='" + yield + '\'' +
                ", amount_outstanding=" + amount_outstanding +
                '}';
    }
}
