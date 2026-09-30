package com.example.smartpantry;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.adapter.PantryAdapter;
import com.example.smartpantry.db.DatabaseHelper;
import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.util.IngredientMatcher;
import com.example.smartpantry.util.MotionFeedback;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PantryActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private PantryAdapter adapter;
    private TextView count;

    private SharedPreferences preferences;

    private static final String PREFS_NAME =
            "smart_pantry_notifications";

    private static final String KEY_SUGGESTED_RECIPES =
            "suggested_recipe_ids";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pantry);

        db = new DatabaseHelper(this);

        preferences = getSharedPreferences(
                PREFS_NAME,
                MODE_PRIVATE
        );

        count = findViewById(R.id.txtCount);

        RecyclerView rv =
                findViewById(R.id.recyclerPantry);

        rv.setLayoutManager(
                new LinearLayoutManager(this)
        );

        adapter = new PantryAdapter(
                new PantryAdapter.Listener() {

                    @Override
                    public void onEdit(PantryItem item) {

                        Intent i = new Intent(
                                PantryActivity.this,
                                AddEditIngredientActivity.class
                        );

                        i.putExtra(
                                "id",
                                item.getId()
                        );

                        startActivity(i);
                    }

                    @Override
                    public void onDelete(PantryItem item) {

                        new AlertDialog.Builder(
                                PantryActivity.this
                        )
                                .setTitle("Delete ingredient?")
                                .setMessage(
                                        "Remove "
                                                + item.getName()
                                                + " from your pantry?"
                                )
                                .setPositiveButton(
                                        "Delete",
                                        (d, w) -> {

                                            db.deletePantryItem(
                                                    item.getId()
                                            );

                                            load();

                                            Toast.makeText(
                                                    PantryActivity.this,
                                                    "Ingredient deleted",
                                                    Toast.LENGTH_SHORT
                                            ).show();
                                        }
                                )
                                .setNegativeButton(
                                        "Cancel",
                                        null
                                )
                                .show();
                    }
                }
        );

        rv.setAdapter(adapter);

        Button add =
                findViewById(R.id.btnAdd);

        Button clearAll =
                findViewById(R.id.btnClearAll);

        ImageButton settings =
                findViewById(R.id.btnSettings);

        Button navPantry =
                findViewById(R.id.navPantry);

        Button navSuggested =
                findViewById(R.id.navSuggested);

        Button navAlmost =
                findViewById(R.id.navAlmost);

        add.setOnClickListener(
                v -> startActivity(
                        new Intent(
                                this,
                                AddEditIngredientActivity.class
                        )
                )
        );

        clearAll.setOnClickListener(
                v -> confirmClearAll()
        );

        settings.setOnClickListener(
                v -> startActivity(
                        new Intent(
                                this,
                                SettingsActivity.class
                        )
                )
        );

        navPantry.setOnClickListener(
                v -> {
                }
        );

        navSuggested.setOnClickListener(
                v -> startActivity(
                        new Intent(
                                this,
                                SuggestionsActivity.class
                        )
                )
        );

        navAlmost.setOnClickListener(
                v -> startActivity(
                        new Intent(
                                this,
                                AlmostThereActivity.class
                        )
                )
        );

        MotionFeedback.apply(add);
        MotionFeedback.apply(clearAll);
        MotionFeedback.apply(settings);
        MotionFeedback.apply(navPantry);
        MotionFeedback.apply(navSuggested);
        MotionFeedback.apply(navAlmost);
    }

    @Override
    protected void onResume() {

        super.onResume();

        load();

        checkForNewRecipeSuggestions();
    }

    private void load() {

        List<PantryItem> items =
                db.getPantryItems();

        adapter.submitList(items);

        count.setText(
                items.size()
                        + (items.size() == 1
                        ? " ingredient"
                        : " ingredients")
        );
    }


    private void confirmClearAll() {

        List<PantryItem> items =
                db.getPantryItems();

        if (items.isEmpty()) {

            Toast.makeText(
                    this,
                    "Your pantry is empty.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("This clears all items, clear your pantry?")
                .setMessage(
                        "This will remove all "
                                + items.size()
                                + " ingredients from your pantry.\n\n"
                                + "Your saved recipes will not be affected."
                )
                .setPositiveButton(
                        "Clear All",
                        (dialog, which) -> {

                            db.clearAllPantryItems();

                            preferences.edit()
                                    .remove(
                                            KEY_SUGGESTED_RECIPES
                                    )
                                    .apply();

                            load();

                            Toast.makeText(
                                    PantryActivity.this,
                                    "Pantry cleared",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .show();
    }

    private void checkForNewRecipeSuggestions() {

        List<PantryItem> pantry =
                db.getPantryItems();

        List<Recipe> recipes =
                db.getRecipes();

        Set<String> previousSuggested =
                new HashSet<>(
                        preferences.getStringSet(
                                KEY_SUGGESTED_RECIPES,
                                new HashSet<>()
                        )
                );

        Set<String> currentSuggested =
                new HashSet<>();

        Recipe newRecipe = null;

        for (Recipe recipe : recipes) {

            IngredientMatcher.MatchResult result =
                    IngredientMatcher.getMatchInfo(
                            recipe,
                            pantry
                    );

            if (result.hasMatch()) {

                String id =
                        String.valueOf(recipe.id);

                currentSuggested.add(id);

                if (!previousSuggested.contains(id)
                        && newRecipe == null) {

                    newRecipe = recipe;
                }
            }
        }

        preferences.edit()
                .putStringSet(
                        KEY_SUGGESTED_RECIPES,
                        currentSuggested
                )
                .apply();

        if (newRecipe != null) {

            showNewRecipePopup(newRecipe);
        }
    }

    private void showNewRecipePopup(
            Recipe recipe
    ) {

            LinearLayout container = new LinearLayout(this);
            container.setOrientation(LinearLayout.VERTICAL);
            container.setPadding(24, 20, 24, 20);

            GradientDrawable background = new GradientDrawable();
            background.setColor(Color.WHITE);
            background.setCornerRadius(28);
            background.setStroke(2, Color.rgb(46, 125, 50));

            container.setBackground(background);
            container.setElevation(10);

            TextView title = new TextView(this);
            title.setText("🍳 New Recipe Suggested");
            title.setTextSize(16);
            title.setTextColor(Color.rgb(27, 94, 32));

            TextView message = new TextView(this);
            message.setText(
                    recipe.name
                            + "\n"
                            + recipe.description
            );
            message.setTextSize(13);
            message.setTextColor(Color.DKGRAY);
            message.setPadding(0, 8, 0, 12);

            LinearLayout buttons = new LinearLayout(this);
            buttons.setOrientation(LinearLayout.HORIZONTAL);
            buttons.setGravity(Gravity.END);

            Button viewRecipe = new Button(this);
            viewRecipe.setText("View Recipe");

            Button later = new Button(this);
            later.setText("Later");

            buttons.addView(
                    later,
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    )
            );

            buttons.addView(
                    viewRecipe,
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    )
            );

            container.addView(title);
            container.addView(message);
            container.addView(buttons);

            PopupWindow popup = new PopupWindow(
                    container,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    true
            );

            popup.setBackgroundDrawable(background);
            popup.setOutsideTouchable(true);
            popup.setElevation(12);

            later.setOnClickListener(
                    v -> popup.dismiss()
            );

            viewRecipe.setOnClickListener(
                    v -> {

                        popup.dismiss();

                        Intent i = new Intent(
                                PantryActivity.this,
                                RecipeDetailActivity.class
                        );

                        i.putExtra(
                                "recipe_id",
                                recipe.id
                        );

                        startActivity(i);
                    }
            );

            popup.showAtLocation(
                    findViewById(android.R.id.content),
                    Gravity.BOTTOM | Gravity.END,
                    16,
                    90
            );
        }
    }