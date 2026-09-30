package com.example.smartpantry.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "smart_pantry.db";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) { super(context, DB_NAME, null, DB_VERSION); }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE pantry_items (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, quantity REAL NOT NULL, unit TEXT NOT NULL, expiry_date TEXT)");
        db.execSQL("CREATE TABLE recipes (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, description TEXT NOT NULL, method TEXT NOT NULL)");
        db.execSQL("CREATE TABLE recipe_ingredients (id INTEGER PRIMARY KEY AUTOINCREMENT, recipe_id INTEGER NOT NULL, name TEXT NOT NULL, quantity REAL NOT NULL, unit TEXT NOT NULL, FOREIGN KEY(recipe_id) REFERENCES recipes(id) ON DELETE CASCADE)");
        seedRecipes(db);
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS recipe_ingredients");
        db.execSQL("DROP TABLE IF EXISTS recipes");
        db.execSQL("DROP TABLE IF EXISTS pantry_items");
        onCreate(db);
    }

    public long insertPantryItem(String name, double quantity, String unit, String expiry) {
        ContentValues v = new ContentValues();
        v.put("name", name); v.put("quantity", quantity); v.put("unit", unit); v.put("expiry_date", expiry);
        return getWritableDatabase().insert("pantry_items", null, v);
    }

    public int updatePantryItem(long id, String name, double quantity, String unit, String expiry) {
        ContentValues v = new ContentValues();
        v.put("name", name); v.put("quantity", quantity); v.put("unit", unit); v.put("expiry_date", expiry);
        return getWritableDatabase().update("pantry_items", v, "id=?", new String[]{String.valueOf(id)});
    }

    public int deletePantryItem(long id) {
        return getWritableDatabase().delete("pantry_items", "id=?", new String[]{String.valueOf(id)});
    }
    public int clearAllPantryItems() {
        return getWritableDatabase().delete(
                "pantry_items",
                null,
                null
        );
    }
    public List<PantryItem> getPantryItems() {
        List<PantryItem> result = new ArrayList<>();
        Cursor c = getReadableDatabase().query("pantry_items", null, null, null, null, null, "name COLLATE NOCASE ASC");
        try {
            while (c.moveToNext()) {
                result.add(new PantryItem(
                    c.getLong(c.getColumnIndexOrThrow("id")),
                    c.getString(c.getColumnIndexOrThrow("name")),
                    c.getDouble(c.getColumnIndexOrThrow("quantity")),
                    c.getString(c.getColumnIndexOrThrow("unit")),
                    c.getString(c.getColumnIndexOrThrow("expiry_date"))
                ));
            }
        } finally { c.close(); }
        return result;
    }

    public PantryItem getPantryItem(long id) {
        Cursor c = getReadableDatabase().query("pantry_items", null, "id=?", new String[]{String.valueOf(id)}, null, null, null);
        try {
            if (c.moveToFirst()) {
                return new PantryItem(id, c.getString(c.getColumnIndexOrThrow("name")),
                    c.getDouble(c.getColumnIndexOrThrow("quantity")),
                    c.getString(c.getColumnIndexOrThrow("unit")),
                    c.getString(c.getColumnIndexOrThrow("expiry_date")));
            }
        } finally { c.close(); }
        return null;
    }

    public List<Recipe> getRecipes() {
        List<Recipe> result = new ArrayList<>();
        Cursor c = getReadableDatabase().query("recipes", null, null, null, null, null, "name COLLATE NOCASE ASC");
        try {
            while (c.moveToNext()) {
                long id = c.getLong(c.getColumnIndexOrThrow("id"));
                result.add(new Recipe(id,
                    c.getString(c.getColumnIndexOrThrow("name")),
                    c.getString(c.getColumnIndexOrThrow("description")),
                    getRecipeIngredients(id),
                    c.getString(c.getColumnIndexOrThrow("method"))));
            }
        } finally { c.close(); }
        return result;
    }

    private List<RecipeIngredient> getRecipeIngredients(long recipeId) {
        List<RecipeIngredient> result = new ArrayList<>();
        Cursor c = getReadableDatabase().query("recipe_ingredients", null, "recipe_id=?", new String[]{String.valueOf(recipeId)}, null, null, "id ASC");
        try {
            while (c.moveToNext()) {
                result.add(new RecipeIngredient(
                    c.getString(c.getColumnIndexOrThrow("name")),
                    c.getDouble(c.getColumnIndexOrThrow("quantity")),
                    c.getString(c.getColumnIndexOrThrow("unit"))));
            }
        } finally { c.close(); }
        return result;
    }

    public Recipe getRecipe(long recipeId) {
        for (Recipe r : getRecipes()) if (r.id == recipeId) return r;
        return null;
    }

    private void seedRecipes(SQLiteDatabase db) {
        insertRecipe(db, "Tomato Omelette", "A quick protein-rich omelette using pantry basics.",
            new String[]{"egg","tomato","onion","salt"}, new double[]{2,1,0.5,1}, new String[]{"pieces","pieces","pieces","g"},
            "Beat the eggs. Dice tomato and onion. Cook onion and tomato, add eggs and season with salt. Fold and serve.");
        insertRecipe(db, "Garlic Tomato Pasta", "Simple pasta with tomato and garlic.",
            new String[]{"pasta","tomato","garlic","olive oil","salt"}, new double[]{200,2,2,15,1}, new String[]{"g","pieces","cloves","ml","g"},
            "Boil pasta. Sauté garlic in olive oil, add tomato and salt, then toss with drained pasta.");
        insertRecipe(db, "Vegetable Fried Rice", "Fast fried rice for leftover vegetables.",
            new String[]{"rice","carrot","peas","onion","egg","soy sauce"}, new double[]{250,1,80,0.5,1,15}, new String[]{"g","pieces","g","pieces","pieces","ml"},
            "Cook vegetables in a hot pan. Add rice and soy sauce. Push aside, scramble the egg, then combine.");
        insertRecipe(db, "Chicken Rice Bowl", "A filling rice bowl with chicken and vegetables.",
            new String[]{"rice","chicken","carrot","soy sauce"}, new double[]{200,150,1,15}, new String[]{"g","g","pieces","ml"},
            "Cook chicken through. Add sliced carrot. Serve over cooked rice and drizzle with soy sauce.");
        insertRecipe(db, "Tuna Sandwich", "A simple tuna sandwich.",
            new String[]{"bread","tuna","mayonnaise","lettuce"}, new double[]{2,1,20,2}, new String[]{"slices","can","g","leaves"},
            "Mix tuna with mayonnaise. Layer lettuce and tuna between bread slices.");
        insertRecipe(db, "Bean Toast", "Warm beans on toast.",
            new String[]{"bread","beans","tomato","salt"}, new double[]{2,150,1,1}, new String[]{"slices","g","pieces","g"},
            "Warm the beans with diced tomato and salt. Spoon over toasted bread.");
        insertRecipe(db, "Potato Egg Hash", "Crispy potatoes with egg.",
            new String[]{"potato","egg","onion","salt","olive oil"}, new double[]{2,2,0.5,1,15}, new String[]{"pieces","pieces","pieces","g","ml"},
            "Dice and pan-fry potato and onion. Add beaten egg and season with salt. Cook until set.");
        insertRecipe(db, "Chicken Wrap", "Quick chicken and lettuce wrap.",
            new String[]{"tortilla","chicken","lettuce","tomato"}, new double[]{2,150,2,1}, new String[]{"pieces","g","leaves","pieces"},
            "Cook chicken. Fill tortillas with chicken, lettuce and sliced tomato, then roll.");
        insertRecipe(db, "Creamy Garlic Pasta", "Creamy pasta with garlic.",
            new String[]{"pasta","cream","garlic","parmesan","salt"}, new double[]{200,100,2,30,1}, new String[]{"g","ml","cloves","g","g"},
            "Cook pasta. Sauté garlic, add cream and parmesan, season, then toss with pasta.");
        insertRecipe(db, "Greek Salad", "Fresh salad with a simple dressing.",
            new String[]{"cucumber","tomato","feta","olive oil","salt"}, new double[]{1,2,80,15,1}, new String[]{"pieces","pieces","g","ml","g"},
            "Chop cucumber and tomato. Crumble feta over the vegetables and dress with olive oil and salt.");
        insertRecipe(db, "Pancakes", "Basic fluffy pancakes.",
            new String[]{"flour","milk","egg","sugar"}, new double[]{150,200,1,20}, new String[]{"g","ml","pieces","g"},
            "Whisk flour, milk, egg and sugar into a batter. Cook spoonfuls on a lightly oiled pan.");
        insertRecipe(db, "Banana Oat Bowl", "Quick breakfast bowl.",
            new String[]{"oats","banana","milk","honey"}, new double[]{60,1,200,10}, new String[]{"g","pieces","ml","g"},
            "Combine oats and milk. Top with sliced banana and honey.");
        insertRecipe(db, "Avocado Toast", "Creamy avocado on toast.",
            new String[]{"bread","avocado","salt","lemon"}, new double[]{2,1,1,0.5}, new String[]{"slices","pieces","g","pieces"},
            "Toast bread. Mash avocado with salt and lemon juice. Spread over toast.");
        insertRecipe(db, "Peanut Banana Toast", "Sweet pantry toast.",
            new String[]{"bread","banana","peanut butter"}, new double[]{2,1,30}, new String[]{"slices","pieces","g"},
            "Toast bread. Spread with peanut butter and top with sliced banana.");
        insertRecipe(db, "Lentil Tomato Soup", "Hearty lentil and tomato soup.",
            new String[]{"lentils","tomato","onion","carrot","salt"}, new double[]{180,2,0.5,1,1}, new String[]{"g","pieces","pieces","pieces","g"},
            "Sauté onion and carrot. Add lentils and tomato with water. Simmer until lentils are tender and season.");
        insertRecipe(db, "Cheesy Scrambled Eggs", "Soft scrambled eggs with cheese.",
            new String[]{"egg","cheese","milk","salt"}, new double[]{2,40,30,1}, new String[]{"pieces","g","ml","g"},
            "Whisk eggs with milk and salt. Cook gently, add cheese and fold until creamy.");
        insertRecipe(db, "Chicken Tomato Pasta", "Comforting chicken pasta.",
            new String[]{"pasta","chicken","tomato","garlic"}, new double[]{200,150,2,2}, new String[]{"g","g","pieces","cloves"},
            "Cook pasta and chicken. Sauté garlic and tomato, then combine everything.");
        insertRecipe(db, "Veggie Omelette", "Flexible omelette using common vegetables.",
            new String[]{"egg","pepper","onion","cheese"}, new double[]{2,1,0.5,30}, new String[]{"pieces","pieces","pieces","g"},
            "Sauté diced pepper and onion. Add beaten eggs and cheese. Fold when set.");
        insertRecipe(db, "Simple Hummus Bowl", "A simple chickpea bowl.",
            new String[]{"chickpeas","lemon","olive oil","salt"}, new double[]{200,0.5,15,1}, new String[]{"g","pieces","ml","g"},
            "Mash chickpeas with lemon, olive oil and salt. Serve as a bowl or spread.");
        insertRecipe(db, "Apple Cinnamon Oats", "Warm apple breakfast oats.",
            new String[]{"oats","apple","milk","cinnamon"}, new double[]{60,1,200,1}, new String[]{"g","pieces","ml","g"},
            "Cook oats in milk. Stir in diced apple and cinnamon and cook until soft.");
    }

    private void insertRecipe(SQLiteDatabase db, String name, String description, String[] names, double[] quantities, String[] units, String method) {
        ContentValues r = new ContentValues();
        r.put("name", name); r.put("description", description); r.put("method", method);
        long id = db.insert("recipes", null, r);
        for (int i = 0; i < names.length; i++) {
            ContentValues ing = new ContentValues();
            ing.put("recipe_id", id); ing.put("name", names[i]); ing.put("quantity", quantities[i]); ing.put("unit", units[i]);
            db.insert("recipe_ingredients", null, ing);
        }
    }
}
