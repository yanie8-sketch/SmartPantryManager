package com.example.smartpantry.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.R;
import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.util.IngredientMatcher;
import com.example.smartpantry.util.MotionFeedback;

import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter
        extends RecyclerView.Adapter<RecipeAdapter.Holder> {

    public interface Listener {
        void onRecipeClick(Recipe recipe);
    }

    private final Listener listener;

    private final List<Recipe> recipes =
            new ArrayList<>();

    private List<PantryItem> pantryItems =
            new ArrayList<>();

    public RecipeAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submitList(List<Recipe> data) {

        recipes.clear();

        if (data != null) {
            recipes.addAll(data);
        }

        notifyDataSetChanged();
    }

    public void setPantryItems(
            List<PantryItem> pantry
    ) {

        if (pantry == null) {

            pantryItems = new ArrayList<>();

        } else {

            pantryItems = pantry;
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(
                        R.layout.item_recipe,
                        parent,
                        false
                );

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull Holder holder,
            int position
    ) {

        Recipe recipe =
                recipes.get(position);

        holder.name.setText(recipe.name);

        IngredientMatcher.MatchResult result =
                IngredientMatcher.getMatchInfo(
                        recipe,
                        pantryItems
                );

        boolean canMake =
                IngredientMatcher.canMake(
                        recipe,
                        pantryItems
                );

        StringBuilder text =
                new StringBuilder();

        /*
         * Ingredient availability.
         */
        text.append(result.matchedCount)
                .append("/")
                .append(result.totalCount)
                .append(" ingredients available");

        /*
         * Ready-to-make status.
         *
         * IMPORTANT:
         * This uses canMake(), which checks both
         * ingredient names AND quantities/units.
         */
        if (canMake) {

            text.append(" • Ready to make");

        } else if (!result.missingIngredients.isEmpty()) {

            text.append("\nMissing: ");

            for (
                    int i = 0;
                    i < result.missingIngredients.size();
                    i++
            ) {

                if (i > 0) {
                    text.append(", ");
                }

                text.append(
                        result.missingIngredients.get(i)
                );
            }
        }

        holder.meta.setText(
                text.toString()
        );

        /*
         * Open recipe details.
         */
        holder.view.setOnClickListener(
                v -> listener.onRecipeClick(recipe)
        );

        /*
         * Button press animation.
         */
        MotionFeedback.apply(holder.view);
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    static class Holder
            extends RecyclerView.ViewHolder {

        TextView name;
        TextView meta;
        Button view;

        Holder(View view) {

            super(view);

            name = view.findViewById(
                    R.id.txtRecipeName
            );

            meta = view.findViewById(
                    R.id.txtRecipeMeta
            );

            this.view = view.findViewById(
                    R.id.btnViewRecipe
            );
        }
    }
}