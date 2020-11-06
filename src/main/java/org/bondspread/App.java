package org.bondspread;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bondspread.domain.Spread;
import org.bondspread.service.BondService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) {
        if(args.length != 2) {
            System.out.println("Input and output file not specified or invalid.");
        }
        String input = args[0];
        String output = args[1];

        try {
            BondService service = new BondService(String.join("", Files.readAllLines(Path.of(input))));
            List<Spread> spreads = service.calculateSpreadForAll();
            String spread = serializeSpread(spreads);
            System.out.println(spread);
            Files.write(Path.of(output), spread.getBytes());
            System.out.println("Wrote to out file.");
        } catch (IOException e) {
            System.out.println("Unable to read input file. Detail: ");
            e.printStackTrace();
        }
    }

    private static String serializeSpread(List<Spread> spreads) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValueAsString(spreads);
        return "{\n" +
                "  \"data\":" + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(spreads) + "\n}";
    }
}
