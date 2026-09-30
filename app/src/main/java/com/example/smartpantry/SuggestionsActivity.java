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

public class SuggestionsActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private RecipeAdapter adapter;

    private TextView summary;
    private TextView empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_suggestions);

        db = new DatabaseHelper(this);

        summary = findViewById(R.id.txtSummary);
        empty = findViewById(R.id.txtEmpty);

        RecyclerView recycler =
                findViewById(R.id.recyclerReady);

        recycler.setLayoutManager(
                new LinearLayoutManager(this)
        );

        adapter = new RecipeAdapter(
                recipe -> openRecipe(recipe)
        );

        recycler.setAdapter(adapter);
        ImageButton settings =
                findViewById(R.id.btnSettings);
        settings.setOnClickListener(
                v -> startActivity(
                        new Intent(
                                SuggestionsActivity.this,
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
                v -> {
                }
        );

        navAlmost.setOnClickListener(
                v -> startActivity(
                        new Intent(
                                this,
                                AlmostThereActivity.class
                        )
                )
        );

        MotionFeedback.apply(navPantry);
        MotionFeedback.apply(navSuggested);
        MotionFeedback.apply(navAlmost);
    }

    @Override
    protected void onResume() {

        super.onResume();

        loadReadyRecipes();
    }

    private void loadReadyRecipes() {

        List<PantryItem> pantry =
                db.getPantryItems();

        List<Recipe> readyRecipes =
                new ArrayList<>();

        for (Recipe recipe : db.getRecipes()) {

            if (IngredientMatcher.canMake(
                    recipe,
                    pantry
            )) {

                readyRecipes.add(recipe);
            }
        }

        adapter.setPantryItems(pantry);

        adapter.submitList(readyRecipes);

        updateScreen(
                readyRecipes.size()
        );
    }

    private void updateScreen(int recipeCount) {

        if (recipeCount == 0) {

            summary.setText(
                    "No complete recipes are available yet."
            );

            empty.setVisibility(
                    TextView.VISIBLE
            );

        } else {

            summary.setText(
                    recipeCount
                            + (recipeCount == 1
                            ? " recipe ready to make"
                            : " recipes ready to make")
            );

            empty.setVisibility(
                    TextView.GONE
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

        ImageButton settings =
                findViewById(R.id.btnSettings);

        settings.setOnClickListener(
                v -> startActivity(
                        new Intent(
                                SuggestionsActivity.this,
                                SettingsActivity.class
                        )
                )
        );


    }
}