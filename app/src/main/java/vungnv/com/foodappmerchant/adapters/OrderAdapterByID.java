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
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import vungnv.com.foodappmerchant.R;
import vungnv.com.foodappmerchant.constant.Constant;
import vungnv.com.foodappmerchant.model.Order;
import vungnv.com.foodappmerchant.model.Temp;


public class OrderAdapterByID extends RecyclerView.Adapter<OrderAdapterByID.ViewHolder> implements Constant {
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
        if (item.notes.length() == 0) {
            holder.tvNotes.setVisibility(View.INVISIBLE);
        } else {
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
                    Toast.makeText(v.getContext(), list.get(getAdapterPosition()).name, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void updateList(List<Temp> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new OrderDiffCallback(newList, list));
        int oldSize = list.size();
        list.clear();
        list.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
        int newSize = newList.size();
        if (newSize > oldSize) {
            // Toast.makeText(context, "You have a new order!" + newList.get(newList.size()-1).items, Toast.LENGTH_SHORT).show();
            //vungnv.com.foodappmerchant.utils.createNotification.mCreateNotification(context, newList.get(newList.size() - 1).items, newList.get(newList.size() - 1).dateTime);
        }
    }

    private static class OrderDiffCallback extends DiffUtil.Callback {
        private final List<Temp> oldOrderList;
        private final List<Temp> newOrderList;

        public OrderDiffCallback(List<Temp> newOrderList, List<Temp> oldOrderList) {
            this.newOrderList = newOrderList;
            this.oldOrderList = oldOrderList;
        }

        @Override
        public int getOldListSize() {
            return oldOrderList.size();
        }

        @Override
        public int getNewListSize() {
            return newOrderList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return Objects.equals(oldOrderList.get(oldItemPosition).name, newOrderList.get(newItemPosition).name);
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            Temp oldOrder = oldOrderList.get(oldItemPosition);
            Temp newOrder = newOrderList.get(newItemPosition);
            return oldOrder.name.equals(newOrder.name)
                    && Objects.equals(oldOrder.price, newOrder.price)
                    && Objects.equals(oldOrder.quantity, newOrder.quantity)
                    && Objects.equals(oldOrder.notes, newOrder.notes);

        }
    }

}
