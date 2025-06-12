package com.midterm.myposapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DrinkAdapter extends RecyclerView.Adapter<DrinkAdapter.DrinkViewHolder> {

    private List<Drink> drinkList;
    private OnDrinkClickListener listener;

    // ✅ Interface definition
    public interface OnDrinkClickListener {
        void onDrinkClick(Drink drink);
    }

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

        // Set drink data
        holder.drinkName.setText(drink.getName());
        holder.drinkPrice.setText(String.format("$%.2f", drink.getPrice()));
        
        // ✅ Use correct method name
        if (drink.getImageResId() != 0) {
            holder.drinkImage.setImageResource(drink.getImageResId());
        } else {
            holder.drinkImage.setImageResource(R.drawable.placeholder_drink);
        }

        // Handle click event
        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDrinkClick(drink);
            }
        });

        // Add click animation
        holder.cardView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).start();
                    break;
                case android.view.MotionEvent.ACTION_UP:
                case android.view.MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
                    break;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return drinkList != null ? drinkList.size() : 0;
    }

    public void updateDrinkList(List<Drink> newDrinkList) {
        this.drinkList = newDrinkList;
        notifyDataSetChanged();
    }

    public static class DrinkViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView drinkImage;
        TextView drinkName;
        TextView drinkPrice;

        public DrinkViewHolder(@NonNull View itemView) {
            super(itemView);
            
            cardView = (CardView) itemView;
            drinkImage = itemView.findViewById(R.id.drink_image);
            drinkName = itemView.findViewById(R.id.drink_name);
            drinkPrice = itemView.findViewById(R.id.drink_price);
        }
    }
}