package com.example.smartpantry;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smartpantry.db.DatabaseHelper;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;
import com.example.smartpantry.util.MotionFeedback;

public class RecipeDetailActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        long id=getIntent().getLongExtra("recipeId",-1);
        Recipe recipe=new DatabaseHelper(this).getRecipe(id);
        if (recipe == null) { finish(); return; }

        ((TextView)findViewById(R.id.txtRecipeTitle)).setText(recipe.name);
        ((TextView)findViewById(R.id.txtRecipeDescription)).setText(recipe.description);
        StringBuilder ingredients=new StringBuilder();
        for (RecipeIngredient i: recipe.ingredients) ingredients.append("• ").append(i.quantity).append(" ").append(i.unit).append(" ").append(i.name).append("\n");
        ((TextView)findViewById(R.id.txtIngredients)).setText(ingredients.toString().trim());
        ((TextView)findViewById(R.id.txtMethod)).setText(recipe.method);
        Button back=findViewById(R.id.btnBack); back.setOnClickListener(v->finish()); MotionFeedback.apply(back);
    }
}
