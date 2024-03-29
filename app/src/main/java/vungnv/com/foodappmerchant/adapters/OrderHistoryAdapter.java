package vungnv.com.foodappmerchant.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import vungnv.com.foodappmerchant.R;
import vungnv.com.foodappmerchant.constant.Constant;
import vungnv.com.foodappmerchant.model.Order;


public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> implements Constant, Filterable {
    private static List<Order> list;
    private final List<Order> listOld;
    private final Context context;


    public OrderHistoryAdapter(Context context, List<Order> list) {
        this.context = context;
        OrderHistoryAdapter.list = list;
        this.listOld = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_history, parent, false);
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

        holder.tvQuantity.setText(" " +quantity + " món");

        holder.tvTime.setText("format this");

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
        TextView tvQuantity, tvID, tvPrice, tvStatus, tvTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvID = itemView.findViewById(R.id.tvID);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvStatus = itemView.findViewById(R.id.tvStatusOrder);
            tvTime = itemView.findViewById(R.id.tvTime);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Toast.makeText(v.getContext(), "id: " + list.get(getAdapterPosition()).id, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    public void updateList(List<Order> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new OrderHistoryAdapter.OrderDiffCallback(newList, list));
        int oldSize = list.size();
        list.clear();
        list.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
        int newSize = newList.size();
        if (newSize > oldSize) {
            // Toast.makeText(context, "You have a new order!" + newList.get(newList.size()-1).items, Toast.LENGTH_SHORT).show();
          //  vungnv.com.foodappmerchant.utils.createNotification.mCreateNotification(context, newList.get(newList.size() - 1).items, newList.get(newList.size() - 1).dateTime);
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
}
