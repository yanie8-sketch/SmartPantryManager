package com.example.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.adapter.RecipeAdapter;
import com.example.smartpantry.db.DatabaseHelper;
import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.util.IngredientMatcher;
import com.example.smartpantry.util.MotionFeedback;

import java.util.ArrayList;
import java.util.List;

public class AlmostThereActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private RecipeAdapter adapter;
    private TextView summary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_almost_there
        );

        db = new DatabaseHelper(this);

        summary =
                findViewById(R.id.txtAlmostSummary);

        RecyclerView recycler =
                findViewById(R.id.recyclerAlmost);

        recycler.setLayoutManager(
                new LinearLayoutManager(this)
        );

        adapter =
                new RecipeAdapter(
                        recipe -> openRecipe(recipe)
                );

        recycler.setAdapter(adapter);
        ImageButton settings =
                findViewById(R.id.btnSettings);

        settings.setOnClickListener(
                v -> startActivity(
                        new Intent(
                                AlmostThereActivity.this,
                                SettingsActivity.class
                        )
                )
        );

        Button navPantry =
                findViewById(R.id.navPantry);

        Button navSuggested =
                findViewById(R.id.navSuggested);

        Button navAlmost =
                findViewById(R.id.navAlmost);

        navPantry.setOnClickListener(
                v -> startActivity(
                        new Intent(
                                this,
                                PantryActivity.class
                        )
                )
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
                v -> {
                }
        );

        MotionFeedback.apply(navPantry);
        MotionFeedback.apply(navSuggested);
        MotionFeedback.apply(navAlmost);
    }

    @Override
    protected void onResume() {

        super.onResume();

        loadAlmostThere();
    }

    private void loadAlmostThere() {

        List<PantryItem> pantry =
                db.getPantryItems();

        List<Recipe> almostRecipes =
                new ArrayList<>();

        for (Recipe recipe : db.getRecipes()) {

            if (IngredientMatcher.canMake(
                    recipe,
                    pantry
            )) {
                continue;
            }

            IngredientMatcher.MatchResult result =
                    IngredientMatcher.getMatchInfo(
                            recipe,
                            pantry
                    );

            if (result.hasMatch()) {

                almostRecipes.add(recipe);
            }
        }

        adapter.setPantryItems(pantry);

        adapter.submitList(almostRecipes);

        if (almostRecipes.isEmpty()) {

            summary.setText(
                    "No partial recipes yet. Add more pantry ingredients to discover recipes."
            );

        } else {

            summary.setText(
                    almostRecipes.size()
                            + (almostRecipes.size() == 1
                            ? " recipe you can almost make"
                            : " recipes you can almost make"
                              + " — missing ingredients shown below")
            );
        }
    }

    private void openRecipe(Recipe recipe) {

        Intent intent =
                new Intent(
                        this,
                        RecipeDetailActivity.class
                );

        intent.putExtra(
                "recipe_id",
                recipe.id
        );

        startActivity(intent);
    }
}