import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextNutrition extends Nutrition {
    private static final String FIELD_GROUP = "field";
    private static final String VALUE_GROUP = "value";
    private static final String UNITS_GROUP = "units";
    private static final Pattern pattern = Pattern.compile("^(?<" + FIELD_GROUP + ">[a-zA-Z\\s]+)(?<" + VALUE_GROUP + ">(\\d|\\.)+|O)(?<" + UNITS_GROUP + ">(m|mc)*?([g9]))");

    private boolean putCaloriesIfNum(String line) {
        if (line.matches("^(\\d|\\.)+$")) {
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
            if (lines.get(i).trim().toLowerCase().contains("calories")) {
                for (int j = 1; ; j++) {
                    boolean firstException = false;
                    try {
                        if (putCaloriesIfNum(lines.get(i - j))) {
                            break;
                        }
                    } catch (IndexOutOfBoundsException e) {
                        firstException = true;
                    }
                    try {
                        if (putCaloriesIfNum(lines.get(i + j))) {
                            break;
                        }
                    } catch (IndexOutOfBoundsException e) {
                        if (firstException) {
                            break;
                        }
                    }
                }
                break;
            }
        }

        // Search for other fields
        for (String line : lines) {
            if (line.toLowerCase().startsWith("serving size")) {
                servingSize = line.replaceAll("(?i)serving size(?-i)\\s*", "");
                continue;
            }

            Matcher matcher = pattern.matcher(line);

            if (matcher.find()) {
                String valueString = matcher.group(VALUE_GROUP);
                double value = (valueString.equalsIgnoreCase("O")) ? 0 : Double.parseDouble(matcher.group(VALUE_GROUP));

                String units = matcher.group(UNITS_GROUP);
                if (units.endsWith("9")) {
                    units = units.replaceAll("9$", "g");
                }

                value /= unitConversions.get(units);

                String parsedField = matcher.group(FIELD_GROUP).trim().toLowerCase();

                for (String field : fieldArray) {
                    if (parsedField.equals(field)) {
                        info.put(field, value);
                        break;
                    }
                }
            }
        }
    }
}
