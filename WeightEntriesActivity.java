package com.example.weighttrackerapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.List;

public class WeightEntriesActivity extends AppCompatActivity {

    private WeightDatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private WeightAdapter adapter;
    private List<WeightEntry> weightList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_entries);

        dbHelper = new WeightDatabaseHelper(this);

        recyclerView = findViewById(R.id.recyclerViewWeights);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadWeightEntries();

        // Add Weight button
        findViewById(R.id.buttonAddWeight).setOnClickListener(v -> showAddWeightDialog());

        // Sign Out button
        Button signOutButton = findViewById(R.id.buttonSignOut);
        signOutButton.setOnClickListener(v -> {
            Intent intent = new Intent(WeightEntriesActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadWeightEntries() {
        weightList = dbHelper.getAllWeights();
        adapter = new WeightAdapter(weightList, dbHelper);
        recyclerView.setAdapter(adapter);
    }

    private void showAddWeightDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Weight");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.add_weight, null);
        EditText inputWeight = dialogView.findViewById(R.id.editTextWeight);

        builder.setView(dialogView);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String weightStr = inputWeight.getText().toString().trim();

            if (weightStr.isEmpty()) {
                Toast.makeText(this, "Please enter a weight", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                float weight = Float.parseFloat(weightStr);

                String currentDate = LocalDate.now().toString(); // YYYY-MM-DD format
                dbHelper.insertWeight(weight, currentDate);

                loadWeightEntries();
                Toast.makeText(this, "Weight added", Toast.LENGTH_SHORT).show();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid weight entered", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
