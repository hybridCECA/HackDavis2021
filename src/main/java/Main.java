import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        String[] barcodes = {"044000072742", "073141152334", "024100106851"};
        for (String barcode : barcodes) {
            System.out.println(new BarcodeNutrition(barcode));
        }
    }
}
