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
import com.example.smartpantry.util.MotionFeedback;
import java.util.ArrayList;
import java.util.List;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.Holder> {
    public interface Listener { void onEdit(PantryItem item); void onDelete(PantryItem item); }
    private final Listener listener;
    private final List<PantryItem> items = new ArrayList<>();

    public PantryAdapter(Listener listener) { this.listener = listener; }
    public void submitList(List<PantryItem> data) { items.clear(); items.addAll(data); notifyDataSetChanged(); }

    @NonNull @Override public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pantry, parent, false));
    }
    @Override public void onBindViewHolder(@NonNull Holder h, int position) {
        PantryItem item = items.get(position);
        h.name.setText(item.getName());
        h.quantity.setText(String.format(java.util.Locale.US, "%.2f %s", item.getQuantity(), item.getUnit()));
        h.expiry.setText(item.getExpiryDate() == null || item.getExpiryDate().isEmpty() ? "No expiry date" : "Expiry: " + item.getExpiryDate());
        h.edit.setOnClickListener(v -> listener.onEdit(item));
        h.delete.setOnClickListener(v -> listener.onDelete(item));
        MotionFeedback.apply(h.edit); MotionFeedback.apply(h.delete);
    }
    @Override public int getItemCount() { return items.size(); }

    static class Holder extends RecyclerView.ViewHolder {
        TextView name, quantity, expiry; Button edit, delete;
        Holder(View v) {
            super(v);
            name=v.findViewById(R.id.txtName); quantity=v.findViewById(R.id.txtQuantity); expiry=v.findViewById(R.id.txtExpiry);
            edit=v.findViewById(R.id.btnEdit); delete=v.findViewById(R.id.btnDelete);
        }
    }
}
