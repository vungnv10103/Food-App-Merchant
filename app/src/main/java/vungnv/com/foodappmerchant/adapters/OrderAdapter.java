package vungnv.com.foodappmerchant.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;

import vungnv.com.foodappmerchant.R;
import vungnv.com.foodappmerchant.constant.Constant;
import vungnv.com.foodappmerchant.dao.OrderDAO;
import vungnv.com.foodappmerchant.model.Order;


public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> implements Constant, Filterable {
    private static List<Order> list;
    private final List<Order> listOld;
    private static OrderDAO orderDAO;
    private final Context context;
    private static Dialog dialog;


    public OrderAdapter(Context context, List<Order> list) {
        this.context = context;
        OrderAdapter.list = list;
        this.listOld = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order item = list.get(position);

        holder.tvNameProduct.setText(item.items);
        holder.tvQuantity.setText(item.quantity + "x");
        holder.tvWaitingTime.setText(item.waitingTime + "s");
//        if (item.waitingTime == 15) {
//            showDialog(context, true);
//        }
        if (item.waitingTime == 2) {
            showDialog(context, false);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String str = constraint.toString();
                if (str.isEmpty()) {
                    list = listOld;
                } else {
                    List<Order> mList = new ArrayList<>();
                    for (Order item : listOld) {
                        if (item.id.toLowerCase().contains(str.toLowerCase())) {
                            mList.add(item);
                        }
                    }
                    list = mList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = list;
                return filterResults;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
//                list = (List<ProductModel>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuantity, tvNameProduct, tvWaitingTime;
        int temp = 0;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvNameProduct = itemView.findViewById(R.id.tvNameProduct);
            tvWaitingTime = itemView.findViewById(R.id.tvWaitingTime);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (temp == 0) {
                        // Toast.makeText(v.getContext(), "" + list.get(getAdapterPosition()).id, Toast.LENGTH_SHORT).show();
                        showDialog(v.getContext(), true);
                        temp++;

                    }
                }
            });
        }
    }

    private static void showDialog(Context context, boolean isCheck) {
        if (dialog == null) {
            dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.bottom_sheet);
            dialog.setCancelable(false);

            ImageButton imgClose = dialog.findViewById(R.id.imgClose);
            imgClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            dialog.getWindow().setGravity(Gravity.BOTTOM);
        }

        if (isCheck) {
            dialog.show();
        } else {
            dialog.dismiss();
        }
    }

}
