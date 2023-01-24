package vungnv.com.foodappmerchant.adapters;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vungnv.com.foodappmerchant.R;
import vungnv.com.foodappmerchant.model.StatusProductModel;
import vungnv.com.foodappmerchant.ui.manager_menu.ShowDetailItemProductActivity;
import vungnv.com.foodappmerchant.utils.IOnBackPressed;

public class StatusProductAdapter extends RecyclerView.Adapter<StatusProductAdapter.userViewHolder>{

    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private static List<StatusProductModel> listStatus;


    public StatusProductAdapter(Context context, List<StatusProductModel> listStatus) {
        StatusProductAdapter.context = context;
        StatusProductAdapter.listStatus = listStatus;
    }

    @NonNull
    @Override
    public userViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_change_status, parent, false);
        return new userViewHolder(view);
    }

    @Override
    public int getItemCount() {
        if (listStatus != null) {
            return listStatus.size();
        }
        return 0;
    }

    @Override
    public void onBindViewHolder(@NonNull userViewHolder holder, int position) {
        StatusProductModel itemsObject = listStatus.get(position);
        if (itemsObject == null) {
            return;
        }
        holder.type.setText(itemsObject.type);
//        SharedPreferences pref = context.getSharedPreferences("STATUS_PRODUCT", MODE_PRIVATE);
//        if (pref != null) {
//            String type = pref.getString("type", "");
//            for (int i = 0; i < listStatus.size(); i++) {
//                if (itemsObject.type.equals(type)) {
//                    holder.imgCheck.setVisibility(View.VISIBLE);
//                }
//            }
//        }

    }


    public static class userViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgCheck;
        private final TextView type;
        int check = 0;

        public userViewHolder(@NonNull View itemView) {
            super(itemView);

            type = itemView.findViewById(R.id.tvStatus);
            imgCheck = itemView.findViewById(R.id.imgCheck);
            itemView.setOnClickListener(v -> {
                if (check % 2 == 0) {
                    imgCheck.setVisibility(View.VISIBLE);
                    rememberStatus(listStatus.get(getAdapterPosition()).type, true);
                } else {
                    imgCheck.setVisibility(View.INVISIBLE);
                    rememberStatus(listStatus.get(getAdapterPosition()).type, false);
                }
                check++;


            });


        }
    }

    private static void rememberStatus(String type, boolean check) {
        SharedPreferences pref = context.getSharedPreferences("STATUS_PRODUCT", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        // save data
        if (!check) {
            editor.clear();
        }
        editor.putString("type", type);

        // save
        editor.apply();
    }
}
