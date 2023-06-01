package com.example.sdaassign4_2022;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class Settings extends Fragment {

    // Define user input fields and buttons
    private EditText userName, borrowerID, email;
    private Button saveButton, resetButton;

    public Settings() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Bind the user input fields and buttons to their XML counterparts
        userName = view.findViewById(R.id.userName);
        borrowerID = view.findViewById(R.id.borrowerID);
        email = view.findViewById(R.id.email);
        saveButton = view.findViewById(R.id.button);
        resetButton = view.findViewById(R.id.reset_button);

        // Load any previously saved user details
        loadPreferences();

        // Define what happens when the Save button is clicked
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDetails();
            }
        });

        // Define what happens when the Reset button is clicked
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetDetails();
            }
        });

        return view;
    }

    // Method to load saved user details
    private void loadPreferences() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        String savedUserName = sharedPreferences.getString("userName", "");
        String savedBorrowerID = sharedPreferences.getString("borrowerID", "");
        String savedEmail = sharedPreferences.getString("email", "");

        // Set the input fields to the saved values
        userName.setText(savedUserName);
        borrowerID.setText(savedBorrowerID);
        email.setText(savedEmail);
    }

    // Method to save user details
    private void saveDetails() {
        String userNameText = userName.getText().toString();
        String borrowerIDText = borrowerID.getText().toString();
        String emailText = email.getText().toString();

        // Validate email input before saving
        if (!isValidEmail(emailText)) {
            Toast.makeText(getActivity(), "Please enter a valid email address", Toast.LENGTH_SHORT).show();
        } else {
            // Save user details using SharedPreferences
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString("userName", userNameText);
            editor.putString("borrowerID", borrowerIDText);
            editor.putString("email", emailText);
            editor.apply();

            Toast.makeText(getActivity(), "Details Saved", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to reset user details
    private void resetDetails() {
        // Clear SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Clear the input fields
        userName.setText("");
        borrowerID.setText("");
        email.setText("");

        Toast.makeText(getActivity(), "Details Reset", Toast.LENGTH_SHORT).show();
    }

    // Method to validate email input
    private boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    // Method to checks if the user details have been saved
    public boolean areDetailsSaved(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        String savedUserName = sharedPreferences.getString("userName", "");
        String savedBorrowerID = sharedPreferences.getString("borrowerID", "");
        String savedEmail = sharedPreferences.getString("email", "");

        return !savedUserName.isEmpty() && !savedBorrowerID.isEmpty() && !savedEmail.isEmpty();
    }
}