package com.example.smartpantry.model;

import java.util.List;

public class Recipe {
    public final long id;
    public final String name;
    public final String description;
    public final List<RecipeIngredient> ingredients;
    public final String method;

    public Recipe(long id, String name, String description, List<RecipeIngredient> ingredients, String method) {
        this.id = id; this.name = name; this.description = description; this.ingredients = ingredients; this.method = method;
    }
}
