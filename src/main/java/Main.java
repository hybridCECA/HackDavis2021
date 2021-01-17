import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        String[] barcodes = {"044000072742", "073141152334", "024100106851"};
        Arrays.stream(barcodes).map(BarcodeNutrition::new).forEach(System.out::println);
    }
}
