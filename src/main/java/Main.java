import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    private static List<String> readFile(File file) {
        List<String> output = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line;
            while ((line = reader.readLine()) != null) {
                output.add(line);
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return output;
    }

    public static void main(String[] args) throws IOException {
        // Info parsing
        File dataDirectory = new File("./data");

        List<List<String>> strings = Arrays.stream(dataDirectory.listFiles()).map(Main::readFile).collect(Collectors.toList());
        strings.stream().map(TextNutrition::new).forEach(System.out::println);

        /*
        // Barcode test
        String[] barcodes = {"044000072742", "073141152334", "024100106851"};
        for (String barcode : barcodes) {
            System.out.println(new BarcodeNutrition(barcode));
        }
         */
    }
}
