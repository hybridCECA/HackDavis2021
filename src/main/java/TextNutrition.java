import java.util.List;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextNutrition extends Nutrition {
    private static final String FIELD_GROUP = "field";
    private static final String VALUE_GROUP = "value";
    private static final String UNITS_GROUP = "units";
    private static final Pattern pattern = Pattern.compile("^(?<" + FIELD_GROUP + ">[a-zA-Z\\s]+)(?<" + VALUE_GROUP + ">(\\d|\\.)+|O)(?<" + UNITS_GROUP + ">(m|mc)*?([g9]))");

    private boolean fuzzyEquals(String s1, String s2) {
        String s1c = s1.toLowerCase();
        String s2c = s2.toLowerCase();

        return s1c.contains(s2c) || s2c.contains(s1c);
    }

    private boolean putCaloriesIfNum(String line) {
        if (line.trim().matches("^(\\d|\\.)+$")) {
            info.put("calories", Double.parseDouble(line));
            return true;
        }

        return false;
    }

    public TextNutrition(List<String> lines) {
        info = new TreeMap<>();

        String[] fieldArray = fields.split("\\|");

        // Search for calories
        for (int i = 0; i < lines.size(); i++) {
            // String found, search for num in both directions
            if (fuzzyEquals(lines.get(i), "calories")) {
                int numExceptions = 0;
                for (int j = 1; numExceptions < 2; j++) {
                    numExceptions = 0;
                    try {
                        if (putCaloriesIfNum(lines.get(i - j))) {
                            break;
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        numExceptions++;
                    }
                    try {
                        if (putCaloriesIfNum(lines.get(i + j))) {
                            break;
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        numExceptions++;
                    }
                }
                break;
            }
        }

        // Search for other fields
        for (String line : lines) {
            // If is the serving size
            if (line.trim().toLowerCase().startsWith("serving size")) {
                servingSize = line.replaceAll("(?i)serving size(?-i)\\s*", "");
                continue;
            }

            Matcher matcher = pattern.matcher(line);

            if (matcher.find()) {
                // Replace 9 with g
                String units = matcher.group(UNITS_GROUP).replaceAll("9$", "g");

                // Replace O with 0
                String valueString = matcher.group(VALUE_GROUP).replaceAll("O", "0");
                double value = Double.parseDouble(valueString);
                value /= unitConversions.get(units);

                // Match field
                String parsedField = matcher.group(FIELD_GROUP).trim().toLowerCase();
                for (String field : fieldArray) {
                    if (fuzzyEquals(parsedField, field)) {
                        info.put(field, value);
                        break;
                    }
                }
            }
        }
    }
}
