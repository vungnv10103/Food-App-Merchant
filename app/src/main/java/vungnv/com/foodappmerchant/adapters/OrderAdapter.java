package vungnv.com.foodappmerchant.adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import vungnv.com.foodappmerchant.R;
import vungnv.com.foodappmerchant.constant.Constant;
import vungnv.com.foodappmerchant.model.Order;
import vungnv.com.foodappmerchant.model.Temp;
import vungnv.com.foodappmerchant.utils.createNotificationChannel;


public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> implements Constant, Filterable {
    private static List<Order> list;
    private final List<Order> listOld;
    private final Context context;
    static boolean isTvTimeClicked = false;
    static boolean isTvAddress = false;


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
        holder.tvPrice.setText(price + "đ ");

        String[] listQuantity = item.quantity.split("-");
        int quantity = 0;
        for (String itemQuantity : listQuantity) {
            quantity += Integer.parseInt(itemQuantity);
        }

        holder.tvQuantity.setText(" " + quantity + " món");


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
            vungnv.com.foodappmerchant.utils.createNotification.mCreateNotification(context, newList.get(newList.size() - 1).items, newList.get(newList.size() - 1).dateTime);
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
            return Objects.equals(oldOrderList.get(oldItemPosition).id, newOrderList.get(newItemPosition).id);
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

    private static void getNameUser(TextView tvNameOrderEr, String idUser) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("list_user_client").child(idUser).child("name");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tvNameOrderEr.setText(Objects.requireNonNull(snapshot.getValue()).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private static void showDialog(Context context, String idMerchant, int pos) {
        final OrderAdapterByID[] orderAdapterByID = {null};
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_view_detail_order);
        dialog.setCancelable(false);


        TextView tvID = dialog.findViewById(R.id.tvID);
        TextView tvTime = dialog.findViewById(R.id.tvTime);
        TextView tvTimeDone = dialog.findViewById(R.id.tvTimeDone);
        ImageButton imgShowDetailInfoOrder = dialog.findViewById(R.id.imgShowDetailInfoOrder);
        ImageButton imgShowDetailInfoOrderEr = dialog.findViewById(R.id.imgShowDetailInfoOrderEr);
        TextView tvResult = dialog.findViewById(R.id.tvResult);
        TextView tvNameOrderEr = dialog.findViewById(R.id.tvNameOrderEr);
        TextView tvAddressOrderEr = dialog.findViewById(R.id.tvAddressOrderEr);
        TextView tvPrice = dialog.findViewById(R.id.tvPrice);
        TextView tvWaitingTime = dialog.findViewById(R.id.tvWaitingTime);

        RecyclerView rcvListOrderByID = dialog.findViewById(R.id.rcvListOrderByID);

        Button btnConfirmOrder = dialog.findViewById(R.id.btnConfirmOrder);
        TextView tvDemo = dialog.findViewById(R.id.tvDemo);


        // get data
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("list_order").child(idMerchant).child(String.valueOf(pos));
        ref.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Order order = snapshot.getValue(Order.class);
                if (order != null) {
                    int posByUserClient = order.posByUserClient;


                    String dateTime = order.dateTime;
                    int index = dateTime.indexOf("-");

                    String date = dateTime.substring(0, index);
                    String time = dateTime.substring(index);
                    String fmDateTime = date + time;

                    tvID.setText("# " + order.id);
                    int sTime = order.waitingTime;
                    int minute = sTime / 60;
                    int second = sTime % 60;
                    tvWaitingTime.setText(minute + ":" + second);
                    tvTime.setText("Nhận đơn lúc: " + fmDateTime);

                    if (minute == 0 && second == 1) {
                        // out of time
                        dialog.dismiss();
                    }


                    imgShowDetailInfoOrder.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // show detail time (may be rider) + custom
                            if (isTvTimeClicked) {
                                //This will shrink textview to 2 lines if it is expanded.
                                tvTimeDone.setMaxLines(1);
                                imgShowDetailInfoOrder.setImageResource(R.drawable.ic_arrow_down_black);
                                isTvTimeClicked = false;
                            } else {
                                //This will expand the textview if it is of 2 lines
                                tvTimeDone.setMaxLines(Integer.MAX_VALUE);
                                imgShowDetailInfoOrder.setImageResource(R.drawable.ic_arrow_up_black);
                                isTvTimeClicked = true;
                            }
                        }
                    });
                    imgShowDetailInfoOrderEr.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (isTvAddress) {
                                tvAddressOrderEr.setMaxLines(0);
                                imgShowDetailInfoOrderEr.setImageResource(R.drawable.ic_arrow_down_black);
                                isTvAddress = false;
                            } else {
                                tvAddressOrderEr.setMaxLines(Integer.MAX_VALUE);
                                imgShowDetailInfoOrderEr.setImageResource(R.drawable.ic_arrow_up_black);
                                isTvAddress = true;
                            }
                        }
                    });

                    String[] listQuantity = order.quantity.split("-");
                    int quantity = 0;
                    for (String itemQuantity : listQuantity) {
                        quantity += Integer.parseInt(itemQuantity);
                    }
                    String[] listPrice = order.price.split("-");
                    double price = 0.0;
                    for (String itemPrice : listPrice) {
                        price += Double.parseDouble(itemPrice);
                    }
                    tvResult.setText(price + "đ (" + quantity + ")");

                    String[] listProductName = order.items.split("-");
                    int temp = 0;
                    String[] listNotes = order.notes.split("-");
                    if (listNotes.length == 0) {
                        temp = 1;
                    }
                    List<Temp> list1 = new ArrayList<>();
                    int numberType = listProductName.length;
                    for (int i = 0; i < numberType; i++) {
                        if (temp == 1) {
                            list1.add(new Temp(Integer.parseInt(listQuantity[i]), listProductName[i], Double.parseDouble(listPrice[i]), ""));
                        } else {
                            list1.add(new Temp(Integer.parseInt(listQuantity[i]), listProductName[i], Double.parseDouble(listPrice[i]), listNotes[i]));

                        }
                    }

                    if (list1.isEmpty()) {
//                    Toast.makeText(getContext(), "Hết đơn", Toast.LENGTH_SHORT).show();
                        if (orderAdapterByID[0] != null) {
                            orderAdapterByID[0].updateList(list1);
                        }
                    } else {
                        if (orderAdapterByID[0] == null) {
                            orderAdapterByID[0] = new OrderAdapterByID(dialog.getContext(), list1);
                            rcvListOrderByID.setAdapter(orderAdapterByID[0]);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(dialog.getContext(), RecyclerView.VERTICAL, false);
                            rcvListOrderByID.setLayoutManager(linearLayoutManager);
                        } else {
                            orderAdapterByID[0].updateList(list1);
                        }
                    }

                    String idUser = order.idUser;

                    tvPrice.setText(price + "đ");
                    getNameUser(tvNameOrderEr, idUser);

                    btnConfirmOrder.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // update status order 1 -> 2

                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().
                                    child("list_order").child(idMerchant).child(String.valueOf(pos)).child("status");

                            ref.setValue(2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("list_order_by_idUserClient")
                                            .child(idUser).child(String.valueOf(posByUserClient)).child("status");
                                    ref.setValue(2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(context, "Xác nhận thành công !", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                    });

                                }
                            });
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
