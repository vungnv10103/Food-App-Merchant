package vungnv.com.foodappmerchant.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
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
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import vungnv.com.foodappmerchant.MainActivity;
import vungnv.com.foodappmerchant.R;
import vungnv.com.foodappmerchant.constant.Constant;
import vungnv.com.foodappmerchant.dao.OrderDAO;
import vungnv.com.foodappmerchant.model.Order;
import vungnv.com.foodappmerchant.utils.createNotificationChannel;


public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> implements Constant, Filterable {
    private static List<Order> list;
    private final List<Order> listOld;
    private static OrderDAO orderDAO;
    private final Context context;
    createNotificationChannel notification = new createNotificationChannel();


    public OrderAdapter(Context context, List<Order> list) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            notification.mCreateNotificationChannel(context);
        }
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

        holder.tvID.setText(item.id);
        String[] listPrice = item.price.split("-");
        double price = 0.0;
        for (String itemPrice : listPrice) {
            price += Double.parseDouble(itemPrice);
        }
        holder.tvPrice.setText(price + "đ");

        String[] listQuantity = item.quantity.split("-");
        int quantity = 0;
        for (String itemQuantity : listQuantity) {
            quantity += Integer.parseInt(itemQuantity);
        }

        holder.tvQuantity.setText(quantity + " món");


        int sTime = item.waitingTime;
        int minute = sTime / 60;
        int second = sTime % 60;
        holder.tvWaitingTime.setText(minute + ":" + second);

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
        TextView tvQuantity, tvID, tvPrice, tvWaitingTime;
        int temp = 0;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvID = itemView.findViewById(R.id.tvID);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvWaitingTime = itemView.findViewById(R.id.tvWaitingTime);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Toast.makeText(v.getContext(), "id: " + list.get(getAdapterPosition()).id, Toast.LENGTH_SHORT).show();
                    showDialog(v.getContext(), list.get(getAdapterPosition()).idMerchant, list.get(getAdapterPosition()).pos);
                    // temp++;
                }
            });
        }
    }

    public void updateList(List<Order> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new OrderDiffCallback(newList, list));
        int oldSize = list.size();
        list.clear();
        list.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
        int newSize = newList.size();
        if (newSize > oldSize) {
           // Toast.makeText(context, "You have a new order!" + newList.get(newList.size()-1).items, Toast.LENGTH_SHORT).show();
            vungnv.com.foodappmerchant.utils.createNotification.mCreateNotification(context, newList.get(newList.size()-1).items, newList.get(newList.size()-1).dateTime);
        }
    }

    // Create a DiffUtil.Callback class to calculate the difference between old and new order lists
    private static class OrderDiffCallback extends DiffUtil.Callback {
        private final List<Order> oldOrderList;
        private final List<Order> newOrderList;

        public OrderDiffCallback(List<Order> newOrderList, List<Order> oldOrderList) {
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
            return oldOrderList.get(oldItemPosition).id == newOrderList.get(newItemPosition).id;
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            Order oldOrder = oldOrderList.get(oldItemPosition);
            Order newOrder = newOrderList.get(newItemPosition);
            return oldOrder.items.equals(newOrder.items)
                    && Objects.equals(oldOrder.price, newOrder.price)
                    && Objects.equals(oldOrder.quantity, newOrder.quantity)
                    && Objects.equals(oldOrder.waitingTime, newOrder.waitingTime)
                    && Objects.equals(oldOrder.idUser, newOrder.idUser)
                    && Objects.equals(oldOrder.dateTime, newOrder.dateTime)
                    && Objects.equals(oldOrder.id, newOrder.id)
                    && Objects.equals(oldOrder.idMerchant, newOrder.idMerchant)
                    && Objects.equals(oldOrder.notes, newOrder.notes)
                    && Objects.equals(oldOrder.pos, newOrder.pos)
                    && Objects.equals(oldOrder.status, newOrder.status);
        }
    }

    private static void showDialog(Context context, String idMerchant, int pos) {

        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_view_detail_order);
        dialog.setCancelable(false);

        TextView tvID = dialog.findViewById(R.id.tvID);
        TextView tvTime = dialog.findViewById(R.id.tvTime);
        ImageButton imgShowDetailInfoOrder = dialog.findViewById(R.id.imgShowDetailInfoOrder);
        TextView tvNameOrderEr = dialog.findViewById(R.id.tvNameOrderer);
        TextView tvNameProduct = dialog.findViewById(R.id.tvNameProduct);
        TextView tvNotes = dialog.findViewById(R.id.tvNotes);
        TextView tvPrice = dialog.findViewById(R.id.tvPrice);
        TextView tvWaitingTime = dialog.findViewById(R.id.tvWaitingTime);

        // get data
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("list_order").child(idMerchant).child(String.valueOf(pos));
        ref.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Order order = snapshot.getValue(Order.class);
                if (order != null) {
                    String dateTime = order.dateTime;
                    int index = dateTime.indexOf("-");

                    String date = dateTime.substring(0, index);
                    String time = dateTime.substring(index);
                    String fmDateTime = date + time;

                    tvID.setText("# " + order.id);
                    tvNameProduct.setText(order.items);
                    tvWaitingTime.setText(String.valueOf(order.waitingTime));
                    tvTime.setText("Nhận đơn lúc: " + fmDateTime);

                    int waitingTime = Integer.parseInt(tvWaitingTime.getText().toString().trim());
                    if (waitingTime == 1) {
                        dialog.dismiss();
                    }
                    imgShowDetailInfoOrder.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // show detail time (may be rider) + custom
                            Toast.makeText(context, "updating...", Toast.LENGTH_SHORT).show();
                        }
                    });

                }


            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: " + error.getMessage());
            }
        });


        ImageButton imgClose = dialog.findViewById(R.id.imgClose);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();


    }

}
