import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NutritionFactsParser {
    private static Map<String, Integer> unitConversions = Map.of("g", 1, "mg", 1000, "mcg", 1000000);

    // Regex pattern
    private static final String FIELD_GROUP = "field";
    private static final String VALUE_GROUP = "value";
    private static final String UNITS_GROUP = "units";
    private static final String CALORIES_GROUP = "calories";
    private static final String CALORIES_VALUE_GROUP = "cvalue";
    private static final Pattern pattern = Pattern.compile("(^(?<" + FIELD_GROUP + ">[a-zA-Z\\s]+)(?<" + VALUE_GROUP + ">\\d+)(?<" + UNITS_GROUP + ">(m|mc)*?g))|"
            + "(?<" + CALORIES_GROUP + ">(?i)calories(?-i))\\s*(?<" + CALORIES_VALUE_GROUP + ">\\d+)");

    // Array of Strings input
    private String[] lines;

    public NutritionFactsParser(String[] lines) {
        this.lines = lines;
    }

    public Map<String, Double> parse() {
        // Output
        Map<String, Double> fields = new TreeMap<>();

        // Loop for each line
        for (String line : lines) {
            Matcher matcher = pattern.matcher(line);

            if (matcher.find()) {
                if (matcher.group(CALORIES_GROUP) == null) {
                    // If other field found
                    double value = Double.parseDouble(matcher.group(VALUE_GROUP));
                    value /= unitConversions.get(matcher.group(UNITS_GROUP));

                    String field = matcher.group(FIELD_GROUP).trim().toLowerCase();

                    fields.put(field, value);
                } else {
                    // If calories found
                    double value = Double.parseDouble(matcher.group(CALORIES_VALUE_GROUP));
                    String field = matcher.group(CALORIES_GROUP).trim().toLowerCase();

                    fields.put(field, value);
                }
            }
        }

        return fields;
    }
}
