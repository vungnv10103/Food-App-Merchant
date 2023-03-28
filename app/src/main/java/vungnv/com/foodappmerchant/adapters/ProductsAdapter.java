package vungnv.com.foodappmerchant.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.ArrayList;
import java.util.List;

import vungnv.com.foodappmerchant.R;
import vungnv.com.foodappmerchant.constant.Constant;
import vungnv.com.foodappmerchant.model.ProductModel;
import vungnv.com.foodappmerchant.ui.manager_menu.ShowDetailItemProductActivity;


public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.viewHolder> implements Constant, Filterable {
    private final Context context;
    private static List<ProductModel> list;
    private final List<ProductModel> listOld;

    public ProductsAdapter(Context context, List<ProductModel> list) {
        this.context = context;
        ProductsAdapter.list = list;
        this.listOld = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, @SuppressLint("RecyclerView") int position) {
        ProductModel item = list.get(position);
        holder.tvName.setText(item.name);
        holder.imgShowDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData(context, item.pos, item.id, item.status, item.name);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        ExpandableTextView tvName;
        ImageView imgShowDetail;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.expand_text_view).findViewById(R.id.expand_text_view);
            imgShowDetail = itemView.findViewById(R.id.imgShowDetail);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProductModel item = list.get(getAdapterPosition());
                    sendData(itemView.getContext(), item.pos, item.id, item.status, item.name);
                }
            });
        }
    }

    private static void sendData(Context context, int pos, String id, int status, String name) {
        Intent intent = new Intent(context, ShowDetailItemProductActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        bundle.putInt("status", status);
        bundle.putString("name", name);
        bundle.putString("temp", String.valueOf(pos));
        Toast.makeText(context, "send pos: " + pos, Toast.LENGTH_SHORT).show();

        intent.putExtra("data-type", bundle);
        context.startActivity(intent);
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
                    List<ProductModel> mList = new ArrayList<>();
                    for (ProductModel item : listOld) {
                        if (item.name.toLowerCase().contains(str.toLowerCase())) {
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
}
