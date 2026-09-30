package com.example.smartpantry;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smartpantry.db.DatabaseHelper;
import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.util.MotionFeedback;

public class AddEditIngredientActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private EditText name, quantity, unit, expiry;
    private long editId = -1;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        db = new DatabaseHelper(this);
        name=findViewById(R.id.inputName); quantity=findViewById(R.id.inputQuantity); unit=findViewById(R.id.inputUnit); expiry=findViewById(R.id.inputExpiry);
        TextView title=findViewById(R.id.txtTitle);
        editId=getIntent().getLongExtra("id",-1);
        if (editId != -1) {
            title.setText("Edit ingredient");
            PantryItem item=db.getPantryItem(editId);
            if (item != null) { name.setText(item.getName()); quantity.setText(String.valueOf(item.getQuantity())); unit.setText(item.getUnit()); expiry.setText(item.getExpiryDate()); }
        }
        Button save=findViewById(R.id.btnSave), cancel=findViewById(R.id.btnCancel);
        save.setOnClickListener(v -> save());
        cancel.setOnClickListener(v -> finish());
        MotionFeedback.apply(save); MotionFeedback.apply(cancel);
    }

    private void save() {
        String n=name.getText().toString().trim(), q=quantity.getText().toString().trim(), u=unit.getText().toString().trim(), e=expiry.getText().toString().trim();
        if (TextUtils.isEmpty(n) || TextUtils.isEmpty(q) || TextUtils.isEmpty(u)) {
            Toast.makeText(this, "Name, quantity and unit are required.", Toast.LENGTH_LONG).show(); return;
        }
        double amount;
        try { amount=Double.parseDouble(q); if (amount<=0) throw new NumberFormatException(); }
        catch (NumberFormatException ex) { Toast.makeText(this, "Quantity must be a number greater than zero.", Toast.LENGTH_LONG).show(); return; }
        if (!e.isEmpty() && !e.matches("\\d{4}-\\d{2}-\\d{2}")) {
            Toast.makeText(this, "Expiry must use YYYY-MM-DD.", Toast.LENGTH_LONG).show(); return;
        }
        if (editId == -1) db.insertPantryItem(n,amount,u,e); else db.updatePantryItem(editId,n,amount,u,e);
        Toast.makeText(this, editId == -1 ? "Ingredient added" : "Ingredient updated", Toast.LENGTH_SHORT).show();
        finish();
    }
}
