import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class BarcodeNutrition {
    // Unit conversion
    private static Map<String, Integer> unitConversions = Map.of("g", 1, "mg", 1000, "mcg", 1000000);

    // Nutrition data
    private String servingSize;
    private double calories;
    private double fat;
    private double carbohydrates;
    private double fiber;
    private double protein;
    private double sodium;

    public BarcodeNutrition(String barcode) throws IOException {
        String page = getPage(getURL(barcode));
        JSONObject product = new JSONObject(page).getJSONObject("product");

        servingSize = product.getString("serving_size");

        JSONObject nutriments = product.getJSONObject("nutriments");
        calories = nutriments.getDouble("energy-kcal_serving");

        fat = getStandardField(nutriments, "fat");
        carbohydrates = getStandardField(nutriments, "carbohydrates");
        fiber = getStandardField(nutriments, "fiber");
        protein = getStandardField(nutriments, "proteins");
        sodium = getStandardField(nutriments, "sodium");
    }

    private static double getStandardField(JSONObject nutrients, String field) {
        double value = nutrients.getDouble(field + "_serving");
        value /= unitConversions.get(nutrients.getString(field + "_unit"));

        return value;
    }

    private static String getURL(String barcode) {
        return "https://world.openfoodfacts.org/api/v0/product/" + barcode + ".json";
    }

    private static String getPage(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder builder = new StringBuilder();

        while (reader.ready()) {
            builder.append(reader.readLine());
        }

        connection.disconnect();

        return builder.toString();
    }

    @Override
    public String toString() {
        return "BarcodeNutrition{" +
                "servingSize='" + servingSize + '\'' +
                ", calories=" + calories +
                ", fat=" + fat +
                ", carbohydrates=" + carbohydrates +
                ", fiber=" + fiber +
                ", protein=" + protein +
                ", sodium=" + sodium +
                '}';
    }

    public double getCalories() {
        return calories;
    }

    public double getCarbohydrates() {
        return carbohydrates;
    }

    public double getFat() {
        return fat;
    }

    public double getFiber() {
        return fiber;
    }

    public double getProtein() {
        return protein;
    }

    public double getSodium() {
        return sodium;
    }

    public String getServingSize() {
        return servingSize;
    }
}
