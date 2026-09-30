package com.example.smartpantry.util;

import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class IngredientMatcher {

    public static class MatchResult {

        public final int matchedCount;
        public final int totalCount;
        public final List<String> missingIngredients;

        public MatchResult(
                int matchedCount,
                int totalCount,
                List<String> missingIngredients
        ) {
            this.matchedCount = matchedCount;
            this.totalCount = totalCount;
            this.missingIngredients = missingIngredients;
        }

        public boolean hasMatch() {
            return matchedCount > 0;
        }

        public boolean isComplete() {
            return matchedCount == totalCount;
        }
    }

    public static MatchResult getMatchInfo(
            Recipe recipe,
            List<PantryItem> pantryItems
    ) {

        int matched = 0;

        List<String> missing =
                new ArrayList<>();

        for (RecipeIngredient ingredient
                : recipe.ingredients) {

            boolean available =
                    isIngredientAvailable(
                            ingredient,
                            pantryItems
                    );

            if (available) {

                matched++;

            } else {

                missing.add(
                        ingredient.name
                );
            }
        }

        return new MatchResult(
                matched,
                recipe.ingredients.size(),
                missing
        );
    }

    public static boolean canMake(
            Recipe recipe,
            List<PantryItem> pantryItems
    ) {

        for (RecipeIngredient ingredient
                : recipe.ingredients) {

            if (!isIngredientAvailable(
                    ingredient,
                    pantryItems
            )) {

                return false;
            }
        }

        return true;
    }

    private static boolean isIngredientAvailable(
            RecipeIngredient recipeIngredient,
            List<PantryItem> pantryItems
    ) {

        double totalAvailable = 0.0;

        boolean foundMatchingIngredient =
                false;

        for (PantryItem pantryItem
                : pantryItems) {

            if (!ingredientsMatch(
                    recipeIngredient.name,
                    pantryItem.getName()
            )) {

                continue;
            }

            foundMatchingIngredient = true;

            if (areUnitsConvertible(
                    pantryItem.getUnit(),
                    recipeIngredient.unit
            )) {

                totalAvailable +=
                        convertQuantity(
                                pantryItem.getQuantity(),
                                pantryItem.getUnit(),
                                recipeIngredient.unit
                        );

            } else if (
                    normalizeUnit(
                            pantryItem.getUnit()
                    ).equals(
                            normalizeUnit(
                                    recipeIngredient.unit
                            )
                    )
            ) {

                totalAvailable +=
                        pantryItem.getQuantity();
            }
        }

        if (!foundMatchingIngredient) {
            return false;
        }

        return totalAvailable >=
                recipeIngredient.quantity;
    }

    private static boolean ingredientsMatch(
            String required,
            String available
    ) {

        String requiredBase =
                getBaseIngredient(required);

        String availableBase =
                getBaseIngredient(available);

        return requiredBase.equals(
                availableBase
        );
    }

    private static String getBaseIngredient(
            String value
    ) {

        String normalized =
                normalize(value);

        if (normalized.isEmpty()) {
            return "";
        }

        String[] words =
                normalized.split("\\s+");

        String[] knownIngredients = {

                "chicken",
                "beef",
                "pork",
                "fish",
                "tuna",

                "egg",
                "tomato",
                "onion",
                "garlic",
                "potato",
                "carrot",
                "peas",
                "beans",

                "rice",
                "pasta",
                "bread",
                "cheese",
                "milk",
                "cream",
                "flour",
                "oats",

                "banana",
                "apple",
                "avocado",
                "lemon",

                "lettuce",
                "cucumber",
                "pepper",

                "chickpeas",
                "lentils",
                "tortilla",

                "mayonnaise",
                "parmesan",
                "feta",

                "honey",
                "sugar",
                "cinnamon",

                "soy",
                "oil"
        };

        for (String word : words) {

            String singular =
                    singularize(word);

            for (String ingredient
                    : knownIngredients) {

                if (singular.equals(
                        ingredient
                )) {

                    return ingredient;
                }
            }
        }

        return normalized;
    }

    private static boolean areUnitsConvertible(
            String from,
            String to
    ) {

        String fromUnit =
                normalizeUnit(from);

        String toUnit =
                normalizeUnit(to);

        if (fromUnit.equals(toUnit)) {
            return true;
        }

        if (isWeightUnit(fromUnit)
                && isWeightUnit(toUnit)) {

            return true;
        }

        if (isVolumeUnit(fromUnit)
                && isVolumeUnit(toUnit)) {

            return true;
        }

        return false;
    }

    private static double convertQuantity(
            double quantity,
            String from,
            String to
    ) {

        String fromUnit =
                normalizeUnit(from);

        String toUnit =
                normalizeUnit(to);

        if (fromUnit.equals(toUnit)) {
            return quantity;
        }

        if (fromUnit.equals("kg")
                && toUnit.equals("g")) {

            return quantity * 1000.0;
        }

        if (fromUnit.equals("g")
                && toUnit.equals("kg")) {

            return quantity / 1000.0;
        }

        if (fromUnit.equals("mg")
                && toUnit.equals("g")) {

            return quantity / 1000.0;
        }

        if (fromUnit.equals("g")
                && toUnit.equals("mg")) {

            return quantity * 1000.0;
        }

        if (fromUnit.equals("kg")
                && toUnit.equals("mg")) {

            return quantity * 1_000_000.0;
        }

        if (fromUnit.equals("mg")
                && toUnit.equals("kg")) {

            return quantity / 1_000_000.0;
        }

        if (fromUnit.equals("l")
                && toUnit.equals("ml")) {

            return quantity * 1000.0;
        }

        if (fromUnit.equals("ml")
                && toUnit.equals("l")) {

            return quantity / 1000.0;
        }

        return quantity;
    }

    private static boolean isWeightUnit(
            String unit
    ) {

        return unit.equals("mg")
                || unit.equals("g")
                || unit.equals("kg");
    }

    private static boolean isVolumeUnit(
            String unit
    ) {

        return unit.equals("ml")
                || unit.equals("l");
    }

    private static String normalizeUnit(
            String unit
    ) {

        if (unit == null) {
            return "";
        }

        String value =
                unit.toLowerCase(Locale.US)
                        .trim();

        switch (value) {

            case "gram":
            case "grams":
                return "g";

            case "kilogram":
            case "kilograms":
                return "kg";

            case "milligram":
            case "milligrams":
                return "mg";

            case "millilitre":
            case "millilitres":
            case "milliliter":
            case "milliliters":
                return "ml";

            case "litre":
            case "litres":
            case "liter":
            case "liters":
                return "l";

            case "piece":
            case "pieces":
            case "pc":
            case "pcs":
                return "pieces";

            case "slice":
            case "slices":
                return "slices";

            case "clove":
            case "cloves":
                return "cloves";

            case "leaf":
            case "leaves":
                return "leaves";

            case "can":
            case "cans":
                return "cans";

            default:
                return value;
        }
    }

    private static String normalize(
            String value
    ) {

        if (value == null) {
            return "";
        }

        return value
                .toLowerCase(Locale.US)
                .trim()
                .replaceAll(
                        "[^a-z0-9\\s]",
                        " "
                )
                .replaceAll(
                        "\\s+",
                        " "
                );
    }

    private static String singularize(
            String word
    ) {

        if (word == null
                || word.isEmpty()) {

            return "";
        }

        String result =
                word.toLowerCase(Locale.US);

        if (result.endsWith("ies")
                && result.length() > 3) {

            return result.substring(
                    0,
                    result.length() - 3
            ) + "y";
        }

        if (result.endsWith("oes")
                && result.length() > 3) {

            return result.substring(
                    0,
                    result.length() - 2
            );
        }

        if (result.endsWith("ses")
                && result.length() > 3) {

            return result.substring(
                    0,
                    result.length() - 2
            );
        }

        if (result.endsWith("es")
                && result.length() > 3) {

            return result.substring(
                    0,
                    result.length() - 2
            );
        }

        if (result.endsWith("s")
                && result.length() > 2) {

            return result.substring(
                    0,
                    result.length() - 1
            );
        }

        return result;
    }
}