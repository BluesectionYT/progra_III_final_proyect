package co.edu.uptc.controller;

import co.edu.uptc.enums.ErrorMessages;
import co.edu.uptc.model.Category;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class BodyReader {
    private JsonObject body;

    private BodyReader(JsonObject body) {
        this.body = body;
    }

    public static BodyReader parse(String text) {
        try {
            return new BodyReader(JsonParser.parseString(text).getAsJsonObject());
        } catch (JsonParseException | IllegalStateException | NullPointerException e) {
            throw new IllegalArgumentException(ErrorMessages.INVALID_REQUEST.getText());
        }
    }

    public static boolean isBlank(String text) {
        return text == null || text.isBlank();
    }

    public static boolean isInvalidAmount(double amount) {
        return Double.isNaN(amount) || Double.isInfinite(amount) || amount <= 0;
    }

    public String readField(String key) {
        JsonElement element = body.get(key);
        return (element == null || element.isJsonNull()) ? null : element.getAsString();
    }

    public double readAmount(String key) {
        try {
            JsonElement element = body.get(key);
            return (element == null || element.isJsonNull()) ? Double.NaN : element.getAsDouble();
        } catch (NumberFormatException | UnsupportedOperationException | IllegalStateException e) {
            return Double.NaN;
        }
    }

    public <E extends Enum<E>> E readEnum(String key, Class<E> type, ErrorMessages error) {
        String value = readField(key);
        if (isBlank(value)) {
            return null;
        }
        try {
            return Enum.valueOf(type, value.trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(error.getText());
        }
    }

    public LocalDate readDate(String key) {
        String value = readField(key);
        if (isBlank(value)) {
            return null;
        }
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(ErrorMessages.INVALID_DATE.getText());
        }
    }

    public ArrayList<Category> readCategories(double budget) {
        JsonArray items = requireCategoryArray();
        ArrayList<Category> categories = new ArrayList<>();
        for (JsonElement item : items) {
            categories.add(toCategory(item, categories));
        }
        validateTotal(categories, budget);
        return categories;
    }

    private JsonArray requireCategoryArray() {
        JsonElement raw = body.get("categories");
        if (raw == null || !raw.isJsonArray() || raw.getAsJsonArray().isEmpty()) {
            throw new IllegalArgumentException(ErrorMessages.CATEGORIES_REQUIRED.getText());
        }
        return raw.getAsJsonArray();
    }

    private Category toCategory(JsonElement item, List<Category> existing) {
        if (!item.isJsonObject()) {
            throw new IllegalArgumentException(ErrorMessages.INVALID_CATEGORY.getText());
        }
        BodyReader entry = new BodyReader(item.getAsJsonObject());
        String name = entry.readField("name");
        double amount = entry.readAmount("budget");
        if (isBlank(name) || isInvalidAmount(amount)) {
            throw new IllegalArgumentException(ErrorMessages.CATEGORY_DATA_REQUIRED.getText());
        }
        requireUniqueName(name.trim(), existing);
        return new Category(name.trim(), amount);
    }

    private void requireUniqueName(String name, List<Category> existing) {
        for (Category category : existing) {
            if (category.getName().toLowerCase().equals(name.toLowerCase())) {
                throw new IllegalArgumentException(ErrorMessages.CATEGORY_REPEATED.format(name));
            }
        }
    }

    private void validateTotal(List<Category> categories, double budget) {
        double total = 0;
        for (Category category : categories) {
            total += category.getBudget();
        }
        if (total > budget) {
            throw new IllegalArgumentException(ErrorMessages.CATEGORIES_EXCEED_BUDGET.getText());
        }
    }
}
