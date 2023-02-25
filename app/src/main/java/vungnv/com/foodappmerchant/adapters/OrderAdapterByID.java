package vungnv.com.foodappmerchant.adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import vungnv.com.foodappmerchant.R;
import vungnv.com.foodappmerchant.constant.Constant;
import vungnv.com.foodappmerchant.dao.OrderDAO;
import vungnv.com.foodappmerchant.model.Order;


public class OrderAdapterByID extends RecyclerView.Adapter<OrderAdapterByID.ViewHolder> implements Constant, Filterable {
    private static List<Order> list;
    private final List<Order> listOld;
    private static OrderDAO orderDAO;
    private final Context context;


    public OrderAdapterByID(Context context, List<Order> list) {
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
        Order item = list.get(position);

        holder.tvNameProduct.setText(item.items);
        holder.tvQuantity.setText(item.quantity + "x");
        holder.tvPrice.setText(item.price + "đ");

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
        TextView tvQuantity, tvNameProduct, tvPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvNameProduct = itemView.findViewById(R.id.tvNameProduct);
            tvPrice = itemView.findViewById(R.id.tvPrice);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Toast.makeText(v.getContext(), "id: " + list.get(getAdapterPosition()).id, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
