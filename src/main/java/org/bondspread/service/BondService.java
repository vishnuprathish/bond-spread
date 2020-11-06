package org.bondspread.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bondspread.domain.Bond;
import org.bondspread.domain.InvalidSpread;
import org.bondspread.domain.Spread;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

public class BondService {

    private final List<Bond> govtBonds = new ArrayList<>();
    private final List<Bond> corpBonds = new ArrayList<>();
    private final TreeMap<Float, Bond> lookupMap = new TreeMap<>(); // reduces lookuptime at the cost of one time build op

    public BondService(String bonds) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode node = mapper.readTree(bonds).get("data");
            List<Bond> bondList = new ArrayList<>();
            for (final JsonNode objNode : node) {
                bondList.add(new Bond(objNode.toString()));
            }
            buildBondsLists(bondList);
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid Bonds input file");
        }
    }

    public BondService(List<Bond> bonds) {
        // build a one-time lookup map for binary lookup on tenor->Bond
        buildBondsLists(bonds);
    }

    public List<Bond> getCorpBonds() {
        return corpBonds;
    }

    public List<Bond> getGovtBonds() {
        return govtBonds;
    }

    /**
     * init govt bond list using treemap to do easy lookup
     * @param bonds
     */
    private void buildBondsLists(List<Bond> bonds) {
        bonds.forEach(bond -> {
            if(bond.isValid()) {
            if (bond.getType().equals("government")) {
                govtBonds.add(bond);
                if (lookupMap.get(bond.getTenorFloat()) != null) {
                    Bond currBond = lookupMap.get(bond.getTenorFloat());
                    // break the tie. if both bonds are of same tenor, pick the earliest in list
                    if (currBond.getAmount_outstanding() < bond.getAmount_outstanding()) {
                        lookupMap.put(bond.getTenorFloat(), bond);
                    }
                } else {
                    lookupMap.put(bond.getTenorFloat(), bond);
                }
            } else if (bond.getType().equals("corporate")) {
                corpBonds.add(bond);
            }
        }
        });
    }

    /**
     * Calculate spread between two bonds
     * @param privateBond
     * @param govtBond
     * @return spread
     */
    public static Spread calculateSpread(Bond privateBond, Bond govtBond) {
        NumberFormat defaultFormat = NumberFormat.getPercentInstance();
        Float spread_pct = null;
        try {
            spread_pct = Math.abs(defaultFormat.parse(privateBond.getYield()).floatValue() - defaultFormat.parse(govtBond.getYield()).floatValue());
        } catch (ParseException e) {
            return new InvalidSpread();
        }
        return new Spread(privateBond.getId(), govtBond.getId(), Spread.toBps(spread_pct * 100));
    }

    /**
     * find the closest govt bond to the given bond
     * @param corpBond
     * @return closest bond
     */
    public Bond findClosestGovtBond(Bond corpBond) {
        float tenor = Float.parseFloat(corpBond.getTenor().split(" ")[0]);
        Map.Entry<Float, Bond> floorEntry = this.lookupMap.floorEntry(corpBond.getTenorFloat());
        Map.Entry<Float, Bond> ceilingEntry = this.lookupMap.ceilingEntry(corpBond.getTenorFloat());

        if(floorEntry == null && ceilingEntry != null) {
            return ceilingEntry.getValue();
        } else if(ceilingEntry == null && floorEntry != null) {
            return floorEntry.getValue();
        } else if(ceilingEntry != null && floorEntry != null) {
            if((Math.abs(ceilingEntry.getKey() - corpBond.getTenorFloat())) >=
                    (Math.abs(floorEntry.getKey() - corpBond.getTenorFloat()))) {
                return floorEntry.getValue();
            } else {
                return ceilingEntry.getValue();
            }
        }
        throw new IllegalArgumentException("Govt Bond not found");
    }

    public List<Spread> calculateSpreadForAll() {
        List<Spread> results = new ArrayList<>();
        this.corpBonds.forEach(corpBond -> {
            Bond govtBond = this.findClosestGovtBond(corpBond);
            results.add(calculateSpread(corpBond, govtBond));
        });
        return results;
    }
}
