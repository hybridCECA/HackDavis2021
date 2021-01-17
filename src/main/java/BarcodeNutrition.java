import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;

public class BarcodeNutrition extends Nutrition {
    public BarcodeNutrition(String barcode) throws IOException {
        info = new TreeMap<>();

        String page = getPage(getURL(barcode));
        JSONObject product = new JSONObject(page).getJSONObject("product");

        servingSize = product.getString("serving_size");

        // BarcodeField -> Field
        Map<String, String> fieldMap = new TreeMap<>();
        for (String field : fields.split("\\|")) {
            String barcodeField = field.replace("calories", "energy-kcal")
                    .replace("protein", "proteins")
                    .replace("total ", "")
                    .replace("carbohydrate", "carbohydrates")
                    .replace("dietary ", "");
            fieldMap.put(barcodeField, field);
        }

        JSONObject nutriments = product.getJSONObject("nutriments");

        for (Map.Entry<String, String> entry : fieldMap.entrySet()) {
            double value = getStandardField(nutriments, entry.getKey());

            info.put(entry.getValue(), value);
        }
    }

    private double getStandardField(JSONObject nutrients, String field) {
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
}
