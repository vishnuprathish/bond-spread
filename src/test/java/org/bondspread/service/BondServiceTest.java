package org.bondspread.service;

import junit.framework.TestCase;
import org.bondspread.domain.Bond;
import org.junit.Assert;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class BondServiceTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
    }

    // happy path
    public void testCalculateSpread() throws IOException {
        Bond corp = new Bond(String.join("", Files.readAllLines(Path.of("src/test/resource/single.json"))));
        Bond govt = new Bond(String.join("", Files.readAllLines(Path.of("src/test/resource/single_govt.json"))));
        Assert.assertEquals(BondService.calculateSpread(corp, govt).toString(),
                "Spread{corporate_bond_id='c1', government_bond_id='g1', spread_to_benchmark='160 bps'}");

    }

    public void testCalculateSpreadCeil() throws IOException {
        Bond corp = new Bond(String.join("", Files.readAllLines(Path.of("src/test/resource/single.json"))));
        Bond govt = new Bond(String.join("", Files.readAllLines(Path.of("src/test/resource/single_govt_2.json"))));
        Assert.assertEquals(BondService.calculateSpread(corp, govt).toString(),
                "Spread{corporate_bond_id='c1', government_bond_id='g1', spread_to_benchmark='30 bps'}");
    }

    // rounding to bps
    public void testCalculateSpreadRounding() throws IOException {
        Bond corp = new Bond(String.join("", Files.readAllLines(Path.of("src/test/resource/single_r.json"))));
        Bond govt = new Bond(String.join("", Files.readAllLines(Path.of("src/test/resource/single_r1.json"))));
        Assert.assertEquals(BondService.calculateSpread(corp, govt).toString(),
                "Spread{corporate_bond_id='c1', government_bond_id='g1', spread_to_benchmark='236 bps'}");
    }

    // happy path
    public void testFindClosestGovtBond() throws IOException {
        BondService service = new BondService(String.join("", Files.readAllLines(Path.of("src/test/resource/input.json"))));
        Bond corp = new Bond(String.join("", Files.readAllLines(Path.of("src/test/resource/single.json"))));
        Bond govtBond = service.findClosestGovtBond(corp);
        Assert.assertEquals(govtBond.toString(),
                "Bond{id='g1', type='government', tenor='9.4 years', yield='3.70%', amount_outstanding=2500000}");
    }

    // upper round, abs
    public void testFindClosestGovtBondCiel() throws IOException {
        BondService service = new BondService(String.join("", Files.readAllLines(Path.of("src/test/resource/input_2.json"))));
        Bond corp = new Bond(String.join("", Files.readAllLines(Path.of("src/test/resource/single.json"))));
        Bond govtBond = service.findClosestGovtBond(corp);
        Assert.assertEquals(govtBond.toString(),
                "Bond{id='g2', type='government', tenor='10.2 years', yield='4.80%', amount_outstanding=1750000}");
    }

    // happy path for complete list
    public void testCalculateSpreadForAll() throws IOException {
        BondService service = new BondService(String.join("", Files.readAllLines(Path.of("src/test/resource/input.json"))));
        Assert.assertEquals(service.calculateSpreadForAll().toString(),
                "[Spread{corporate_bond_id='c1', government_bond_id='g1', spread_to_benchmark='160 bps'}]");
    }

    public void testCalculateSpreadForAllCeil() throws IOException {
        BondService service = new BondService(String.join("", Files.readAllLines(Path.of("src/test/resource/input_s.json"))));
        Assert.assertEquals(service.calculateSpreadForAll().toString(),
                "[Spread{corporate_bond_id='c1', government_bond_id='g1', spread_to_benchmark='60 bps'}]");
    }

    // tests multiple bonds, same tenor for govt bonds, higher amount_outstanding upon tenor conflict
    public void testCalculateSpreadForAllMultiple() throws IOException {
        BondService service = new BondService(String.join("", Files.readAllLines(Path.of("src/test/resource/input_multiple_bonds.json"))));
        Assert.assertEquals(service.calculateSpreadForAll().toString(),
                "[Spread{corporate_bond_id='c1', government_bond_id='g1', spread_to_benchmark='60 bps'}, Spread{corporate_bond_id='c3', government_bond_id='g6', spread_to_benchmark='330 bps'}, Spread{corporate_bond_id='c4', government_bond_id='g1', spread_to_benchmark='320 bps'}, Spread{corporate_bond_id='c5', government_bond_id='g6', spread_to_benchmark='330 bps'}]");
    }

    public void testBondInvalidation() throws IOException {
        BondService service = new BondService(String.join("", Files.readAllLines(Path.of("src/test/resource/input_several_invalid.json"))));
        Assert.assertEquals(service.getCorpBonds().toString(),
                "[Bond{id='c6', type='corporate', tenor='12.0 years', yield='4.80%', amount_outstanding=1750000}]");
        Assert.assertEquals(service.getGovtBonds().toString(),
                "[Bond{id='g2', type='government', tenor='12.0 years', yield='4.80%', amount_outstanding=1750000}]");
    }
}