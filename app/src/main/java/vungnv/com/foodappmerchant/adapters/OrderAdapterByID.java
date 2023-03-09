package vungnv.com.foodappmerchant.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;

import vungnv.com.foodappmerchant.R;
import vungnv.com.foodappmerchant.constant.Constant;
import vungnv.com.foodappmerchant.model.Order;
import vungnv.com.foodappmerchant.model.Temp;


public class OrderAdapterByID extends RecyclerView.Adapter<OrderAdapterByID.ViewHolder> implements Constant{
    private static List<Temp> list;
    private final List<Temp> listOld;
    private final Context context;


    public OrderAdapterByID(Context context, List<Temp> list) {
        this.context = context;
        OrderAdapterByID.list = list;
        this.listOld = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_by_id, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Temp item = list.get(position);

        holder.tvNameProduct.setText(item.name);
        holder.tvQuantity.setText(item.quantity + "x");
        holder.tvPrice.setText(item.price + "đ");
        if (item.notes.length() ==0){
            holder.tvNotes.setVisibility(View.INVISIBLE);
        }else {
            holder.tvNotes.setText("( Ghi chú: " + item.notes + " )");
        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuantity, tvNameProduct, tvPrice, tvNotes;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvNameProduct = itemView.findViewById(R.id.tvNameProduct);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvNotes = itemView.findViewById(R.id.tvNotes);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                     Toast.makeText(v.getContext(), "id: " + list.get(getAdapterPosition()).name, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
