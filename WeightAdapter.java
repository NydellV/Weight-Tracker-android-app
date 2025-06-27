package com.example.weighttrackerapp;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WeightAdapter extends RecyclerView.Adapter<WeightAdapter.WeightViewHolder> {
    private List<WeightEntry> weightList;
    private WeightDatabaseHelper dbHelper;

    public WeightAdapter(List<WeightEntry> weightList, WeightDatabaseHelper dbHelper) {
        this.weightList = weightList;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public WeightViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_weight_entry, parent, false);
        return new WeightViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeightViewHolder holder, int position) {
        WeightEntry entry = weightList.get(position);
        holder.dateText.setText(entry.getDate());
        holder.weightText.setText(String.format("%.1f lbs", entry.getWeight()));

        holder.deleteButton.setOnClickListener(v -> {
            dbHelper.deleteWeight(entry.getId());
            weightList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, weightList.size());
        });

        holder.editButton.setOnClickListener(v -> {
            showEditWeightDialog(holder.itemView, entry, position);
        });
    }

    @Override
    public int getItemCount() {
        return weightList.size();
    }

    public static class WeightViewHolder extends RecyclerView.ViewHolder {
        TextView dateText, weightText;
        Button deleteButton, editButton;

        public WeightViewHolder(@NonNull View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.textViewDate);
            weightText = itemView.findViewById(R.id.textViewWeight);
            deleteButton = itemView.findViewById(R.id.buttonDelete);
            editButton = itemView.findViewById(R.id.buttonEdit);
        }
    }
    private void showEditWeightDialog(View view, WeightEntry entry, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Edit Weight Entry");

        LinearLayout layout = new LinearLayout(view.getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        // Enter weight
        final EditText inputWeight = new EditText(view.getContext());
        inputWeight.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        inputWeight.setHint("Weight (lbs)");
        inputWeight.setText(String.valueOf(entry.getWeight()));
        layout.addView(inputWeight);

        // Enter the dates
        final EditText inputDate = new EditText(view.getContext());
        inputDate.setInputType(android.text.InputType.TYPE_CLASS_DATETIME | android.text.InputType.TYPE_DATETIME_VARIATION_DATE);
        inputDate.setHint("Date (YYYY-MM-DD)");
        inputDate.setText(entry.getDate());
        layout.addView(inputDate);
        builder.setView(layout);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String newWeightStr = inputWeight.getText().toString().trim();
            String newDateStr = inputDate.getText().toString().trim();

            if (newWeightStr.isEmpty() || newDateStr.isEmpty()) {
                Toast.makeText(view.getContext(), "Please enter both weight and date", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                float newWeight = Float.parseFloat(newWeightStr);
                // Update in DB
                dbHelper.updateWeightAndDate(entry.getId(), newWeight, newDateStr);
                // Update list
                weightList.set(position, new WeightEntry(entry.getId(), newWeight, newDateStr));
                notifyItemChanged(position);
                Toast.makeText(view.getContext(), "Entry updated", Toast.LENGTH_SHORT).show();
            } catch (NumberFormatException e) {
                Toast.makeText(view.getContext(), "Invalid weight entered", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}
