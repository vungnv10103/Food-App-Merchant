package vungnv.com.foodappmerchant.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import vungnv.com.foodappmerchant.R;
import vungnv.com.foodappmerchant.constant.Constant;
import vungnv.com.foodappmerchant.model.DishesModel;
import vungnv.com.foodappmerchant.ui.manager_menu.ShowDetailItemDishesActivity;

public class ListOfDishesAdapter extends RecyclerView.Adapter<ListOfDishesAdapter.viewHolder> implements Constant, Filterable {

     Context context;
    private static List<DishesModel> list;
    private static List<DishesModel> listOld;

    public ListOfDishesAdapter(Context context, List<DishesModel> list) {
        this.context = context;
        ListOfDishesAdapter.list = list;
        ListOfDishesAdapter.listOld = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dishes, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        DishesModel item = list.get(position);
        holder.tvTitle.setText(item.name);

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
                    List<DishesModel> mList = new ArrayList<>();
                    for (DishesModel food : listOld) {
                        if (food.name.toLowerCase().contains(str.toLowerCase())) {
                            mList.add(food);
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
                list = (List<DishesModel>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitleDishes);
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), ShowDetailItemDishesActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("name", list.get(getLayoutPosition()).name);
                intent.putExtra("data-type", bundle);
                v.getContext().startActivity(intent);
            });

        }
    }
}
