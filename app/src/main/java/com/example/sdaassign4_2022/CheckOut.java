package com.example.sdaassign4_2022;

/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

// This activity manages the process of checking out a book.
public class CheckOut extends AppCompatActivity {

    // Declaration of the needed variables.
    TextView orderSummary;
    Calendar dateAndTime = Calendar.getInstance();
    Button orderButton;
    Button dateButton;
    TextView confirmTextView;
    TextView availabilityTextView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String bookName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

        // Fetch the views from the layout and assign them to our variables.
        orderButton = findViewById(R.id.orderButton);
        dateButton = findViewById(R.id.date);
        confirmTextView = findViewById(R.id.confirm);
        availabilityTextView = findViewById(R.id.availability);
        orderSummary = findViewById(R.id.orderSummary);

        // Disable orderButton and dateButton by default.
        orderButton.setEnabled(false);
        dateButton.setEnabled(false);

        // Set up the toolbar.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Fetch the book id passed in the intent.
        String bookId = getIntent().getStringExtra("BOOK_ID");

        // Fetch the book data from Firestore.
        db.collection("Books").document(bookId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Check if Availability field exists in the document.
                    if (document.contains("Availability")) {
                        bookName = document.getString("Name");
                        Boolean isAvailableObject = document.getBoolean("Availability");
                        boolean isAvailable = isAvailableObject != null && isAvailableObject;
                        // Update UI with the book data.
                        confirmTextView.setText(getString(R.string.check_out_book, bookName));
                        if (isAvailable) {
                            availabilityTextView.setText(R.string.book_is_available);
                            availabilityTextView.setTextColor(ContextCompat.getColor(this, R.color.colorAvailable)); // Use green color for available.
                            dateButton.setEnabled(true);
                        } else {
                            availabilityTextView.setText(R.string.book_is_unavailable);
                            availabilityTextView.setTextColor(ContextCompat.getColor(this, R.color.colorUnavailable)); // Use red color for unavailable.
                        }
                    } else {
                        Toast.makeText(CheckOut.this, "Error: Availability field is missing for this book.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CheckOut.this, "Error: Book not found.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(CheckOut.this, "Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Setup the date button click listener.
        dateButton.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(CheckOut.this, d,
                    dateAndTime.get(Calendar.YEAR),
                    dateAndTime.get(Calendar.MONTH),
                    dateAndTime.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        });

        // Setup the order button click listener.
        orderButton.setOnClickListener(v -> {
            // Get user data from shared preferences.
            SharedPreferences sharedPreferences = getSharedPreferences("UserDetails", MODE_PRIVATE);
            String borrowerName = sharedPreferences.getString("userName", "");
            String borrowerID = sharedPreferences.getString("borrowerID", "");

            // Create a new book order.
            Map<String, Object> bookOrder = new HashMap<>();
            bookOrder.put("bookID", bookId);
            bookOrder.put("borrowerID", borrowerID);
            bookOrder.put("borrowerName", borrowerName);
            bookOrder.put("orderDate", new Date());
            bookOrder.put("requiredDate", dateAndTime.getTime());

            // Add a new document with a generated ID.
            db.collection("BookOrders")
                    .add(bookOrder)
                    .addOnSuccessListener(documentReference -> {
                        orderSummary.setText(R.string.Success);
                        orderSummary.setTextColor(ContextCompat.getColor(this, R.color.colorSuccess));
                        orderButton.setEnabled(false);
                    })
                    .addOnFailureListener(e -> orderSummary.setText(R.string.error_placing_order));
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Listener for date set, which gets invoked when a date is picked in the DatePickerDialog.
    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialData();
            // Enable the orderButton after a date is selected.
            orderButton.setEnabled(true);
        }
    };

    // This method sets the initial data for the book order summary.
    private void setInitialData() {
        // Get user data from shared preferences.
        SharedPreferences sharedPreferences = getSharedPreferences("UserDetails", MODE_PRIVATE);
        String borrowerName = sharedPreferences.getString("userName", "");
        String borrowerID = sharedPreferences.getString("borrowerID", "");

        String date = DateUtils.formatDateTime(this, dateAndTime.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_TIME);
        orderSummary.setText(getString(R.string.order_summary, bookName, borrowerName, borrowerID, date));
    }
}