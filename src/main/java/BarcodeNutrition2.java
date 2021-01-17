import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BarcodeNutrition2 extends Nutrition {
    public BarcodeNutrition2(String barcode) throws IOException {
        info = new TreeMap<>();

        // Get JSONObject from api
        String page = getPage(getURL(barcode));

        servingSize = getJSONField(page, "serving_size");

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

        // Get each field and put into map
        for (Map.Entry<String, String> entry : fieldMap.entrySet()) {
            double value = getStandardField(page, entry.getKey());

            info.put(entry.getValue(), value);
        }
    }

    private String getJSONField(String json, String field) {
        Pattern pattern = Pattern.compile("\"" + field + "\":\\s*\"?(?<value>.+?)\"?[,}]");
        Matcher matcher = pattern.matcher(json);

        if (matcher.find()) {
            return matcher.group("value");
        }

        return null;
    }

    private double getStandardField(String json, String field) {
        String fieldValue = getJSONField(json, field + "_serving");
        double value = Double.parseDouble(fieldValue);

        value /= unitConversions.get(getJSONField(json, field + "_unit"));

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
