package com.midterm.myposapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DrinkAdapter extends RecyclerView.Adapter<DrinkAdapter.DrinkViewHolder> {

    private List<Drink> drinkList;

    public DrinkAdapter(List<Drink> drinkList) {
        this.drinkList = drinkList;
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

        // Hiển thị tùy chọn size nếu có
        if (drink.hasSizes()) {
            holder.sizeM.setVisibility(View.VISIBLE);
            holder.sizeL.setVisibility(View.VISIBLE);
        } else {
            holder.sizeM.setVisibility(View.GONE);
            holder.sizeL.setVisibility(View.GONE);
        }

        // TODO: Xử lý sự kiện click cho nút thêm (add_button)
        holder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Logic khi nhấn nút thêm thức uống
                // Ví dụ: thêm vào giỏ hàng, hiển thị thông báo, v.v.
            }
        });
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
