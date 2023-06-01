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

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// This fragment is responsible for showing a list of books.
public class BookList extends Fragment {

    // Mandatory empty constructor for the fragment manager to instantiate the fragment
    public BookList() {
        // Required empty public constructor
    }

    // onCreateView is called to "inflate" the layout for this fragment.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_book_list, container, false);

        // Get a handle to the RecyclerView.
        RecyclerView recyclerView = root.findViewById(R.id.bookView_view);

        // Get an instance of Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Fetch the collection "Books" from Firestore
        db.collection("Books")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // This is used to keep track of image download tasks
                        List<Task<Uri>> downloadUrlTasks = new ArrayList<>();

                        // This will store data of all the books
                        ArrayList<HashMap<String, Object>> booksData = new ArrayList<>();

                        // Iterate through all the documents in the "Books" collection
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Get an instance of FirebaseStorage
                            FirebaseStorage storage = FirebaseStorage.getInstance();

                            // Fetch the image URL from the document
                            String imageUrl = document.getString("ImageUrl");
                            assert imageUrl != null;
                            StorageReference imageRef = storage.getReferenceFromUrl(imageUrl);

                            // Start a task to download the image URL and add it to the list
                            Task<Uri> downloadUrlTask = imageRef.getDownloadUrl();
                            downloadUrlTasks.add(downloadUrlTask);

                            // Create a map to store book data and add it to the list
                            HashMap<String, Object> bookData = new HashMap<>();
                            bookData.put("id", document.getId());
                            bookData.put("author", document.getString("Author"));
                            bookData.put("name", document.getString("Name"));

                            booksData.add(bookData);
                        }

                        // Once all the download tasks are complete, update the book data with the image URLs
                        Tasks.whenAllSuccess(downloadUrlTasks).addOnSuccessListener(results -> {
                            for (int i = 0; i < results.size(); i++) {
                                Uri downloadUrl = (Uri) results.get(i);
                                booksData.get(i).put("imageUrl", downloadUrl.toString());
                            }

                            // Set the adapter for the RecyclerView
                            LibraryViewAdapter recyclerViewAdapter = new LibraryViewAdapter(getContext(), booksData);
                            recyclerView.setAdapter(recyclerViewAdapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        });
                    } else {
                        // Log the error if the task was not successful
                        Log.e("Firebase", "Error getting documents.", task.getException());
                    }
                });

        return root;
    }
}
