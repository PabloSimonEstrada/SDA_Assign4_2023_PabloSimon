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
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;

// The LibraryViewAdapter is responsible for displaying the book data in a RecyclerView.
public class LibraryViewAdapter extends RecyclerView.Adapter<LibraryViewAdapter.ViewHolder> {

    // We need a context to inflate the view and to start a new activity.
    private Context mContext;
    // The book data that we will display.
    private ArrayList<HashMap<String, Object>> mBooksData;

    LibraryViewAdapter(Context mContext, ArrayList<HashMap<String, Object>> booksData) {
        this.mContext = mContext;
        this.mBooksData = booksData;
    }

    // This method is called when the RecyclerView needs a new ViewHolder to represent an item.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // We create a new view by inflating the layout.
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_list_item, viewGroup, false);
        return new ViewHolder(view);
    }

    // This method is called to display the data for one list item.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        HashMap<String, Object> bookData = mBooksData.get(position);

        viewHolder.authorText.setText((String) bookData.get("author"));
        viewHolder.titleText.setText((String) bookData.get("name"));

        // We use Glide to load the image from the url and display it in the ImageView.
        Glide.with(mContext)
                .load((String) bookData.get("imageUrl"))
                .into(viewHolder.imageItem);

        // We set a click listener for the checkout button.
        viewHolder.checkOut.setOnClickListener(v -> {
            Settings settings = new Settings();
            if (!settings.areDetailsSaved(mContext)) {
                Toast.makeText(mContext, "Please fill out settings first", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, (String) bookData.get("name"), Toast.LENGTH_SHORT).show();
                Intent myOrder = new Intent(mContext, CheckOut.class);
                // We pass the book id to the Checkout activity.
                myOrder.putExtra("BOOK_ID", (String) bookData.get("id"));
                mContext.startActivity(myOrder);
            }
        });
    }

    // This method returns the total number of items in the data set.
    @Override
    public int getItemCount() {
        return mBooksData.size();
    }

    // Each ViewHolder is responsible for displaying a single item with a view.
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageItem;
        TextView authorText;
        TextView titleText;
        Button checkOut;
        RelativeLayout itemParentLayout;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            // We find the views inside the inflated view.
            imageItem = itemView.findViewById(R.id.bookImage);
            authorText = itemView.findViewById(R.id.authorText);
            titleText = itemView.findViewById(R.id.bookTitle);
            checkOut = itemView.findViewById(R.id.out_button);
            itemParentLayout = itemView.findViewById(R.id.listItemLayout);
        }
    }
}