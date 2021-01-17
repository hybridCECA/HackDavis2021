import java.util.Map;

public abstract class Nutrition {
    // Unit conversion
    protected static Map<String, Integer> unitConversions = Map.of("g", 1, "mg", 1000, "mcg", 1000000, "kcal", 1);

    protected static String fields = "calories|total fat|total carbohydrate|dietary fiber|protein|sodium";

    protected String servingSize;
    protected Map<String, Double> info;

    @Override
    public String toString() {
        return "Nutrition{" +
                "servingSize='" + servingSize + '\'' +
                ", info=" + info +
                '}';
    }

    public String getFields() {
        return fields;
    }

    public boolean hasField(String fieldName) {
        return info.containsKey(fieldName);
    }

    public Double getField(String fieldName) {
        return info.get(fieldName);
    }

    public String getServingSize() {
        return servingSize;
    }
}
