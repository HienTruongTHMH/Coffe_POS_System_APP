package com.midterm.myposapplication;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DrinkAdapter extends RecyclerView.Adapter<DrinkAdapter.DrinkViewHolder> {

    private List<Drink> drinkList;
    private OnDrinkClickListener listener;

    public DrinkAdapter(List<Drink> drinkList, OnDrinkClickListener listener) {
        this.drinkList = drinkList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DrinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_drink, parent, false);
        return new DrinkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DrinkViewHolder holder, int position) {
        Drink drink = drinkList.get(position);
        holder.drinkName.setText(drink.getName());
        holder.drinkPrice.setText(String.format("%.2f$", drink.getPrice()));
        holder.drinkImage.setImageResource(drink.getImageResId());

        // Reset size selection
        holder.selectedSize = "S"; // Default size

        // Show/hide size options
        if (drink.hasSizes()) {
            holder.sizeM.setVisibility(View.VISIBLE);
            holder.sizeL.setVisibility(View.VISIBLE);

            // Set up size selection
            setupSizeSelection(holder, drink);
        } else {
            holder.sizeM.setVisibility(View.GONE);
            holder.sizeL.setVisibility(View.GONE);
        }

        // Handle add button click
        holder.addButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAddDrinkClicked(drink, holder.selectedSize);
            }
        });
    }

    private void setupSizeSelection(DrinkViewHolder holder, Drink drink) {
        // Default selection is S (already set in selectedSize)
        updateSizeButtonAppearance(holder);

        holder.sizeM.setOnClickListener(v -> {
            holder.selectedSize = "M";
            updateSizeButtonAppearance(holder);
        });

        holder.sizeL.setOnClickListener(v -> {
            holder.selectedSize = "L";
            updateSizeButtonAppearance(holder);
        });
    }

    private void updateSizeButtonAppearance(DrinkViewHolder holder) {
        // Reset all buttons
        resetSizeButton(holder.sizeM);
        resetSizeButton(holder.sizeL);

        // Highlight selected button
        switch (holder.selectedSize) {
            case "M":
                highlightSizeButton(holder.sizeM);
                break;
            case "L":
                highlightSizeButton(holder.sizeL);
                break;
        }
    }

    private void resetSizeButton(TextView button) {
        button.setBackgroundResource(R.drawable.rounded_background);
        button.setTextColor(Color.BLACK);
    }

    private void highlightSizeButton(TextView button) {
        button.setBackgroundColor(ContextCompat.getColor(button.getContext(), android.R.color.holo_orange_light));
        button.setTextColor(Color.WHITE);
    }

    @Override
    public int getItemCount() {
        return drinkList.size();
    }

    public static class DrinkViewHolder extends RecyclerView.ViewHolder {
        ImageView drinkImage;
        TextView drinkName;
        TextView drinkPrice;
        TextView sizeM;
        TextView sizeL;
        ImageView addButton;
        String selectedSize = "S"; // Default size

        public DrinkViewHolder(@NonNull View itemView) {
            super(itemView);
            drinkImage = itemView.findViewById(R.id.drink_image);
            drinkName = itemView.findViewById(R.id.drink_name);
            drinkPrice = itemView.findViewById(R.id.drink_price);
            sizeM = itemView.findViewById(R.id.size_m);
            sizeL = itemView.findViewById(R.id.size_l);
            addButton = itemView.findViewById(R.id.add_button);
        }
    }
}