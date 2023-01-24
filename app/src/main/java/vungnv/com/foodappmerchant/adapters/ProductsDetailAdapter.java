package vungnv.com.foodappmerchant.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.ArrayList;
import java.util.List;

import vungnv.com.foodappmerchant.R;
import vungnv.com.foodappmerchant.constant.Constant;
import vungnv.com.foodappmerchant.dao.ProductDAO;
import vungnv.com.foodappmerchant.model.ProductModel;


public class ProductsDetailAdapter extends RecyclerView.Adapter<ProductsDetailAdapter.viewHolder> implements Constant, Filterable {
    private Context context;
    private List<ProductModel> list;
    private List<ProductModel> listOld;

    public ProductsDetailAdapter(Context context, List<ProductModel> list) {
        this.context = context;
        this.list = list;
        this.listOld = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_detail, parent, false);
        return new viewHolder(view);
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        ProductModel item = list.get(position);
        String idImage = item.img;
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        storageRef.child("images_product/" + idImage).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL
                // Load the image using Glide
                Glide.with(context)
                        .load(uri)
                        .into(holder.img);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.d(TAG, "get image from firebase: " + exception.getMessage());
            }
        });
        holder.tvTitle.setText(item.name);
        holder.tvDesc.setText(item.description);
        holder.tvRate.setText(String.format("%.2f", item.rate));
        holder.tvQuantitySold.setText("("+ item.quantity_sold + ")");

        float[] results = new float[1];
        double currentLongitude = 105.77553463;
        double currentLatitude = 21.06693654;

        double productLongitude = 106.296250;
        double productLatitude = 20.200560;
        Location.distanceBetween(currentLatitude, currentLongitude,productLatitude, productLongitude, results);
        float distanceInMeters = results[0];
        holder.tvDistance.setText(String.format("%.1f", distanceInMeters/1000) + "km");
    }

    @Override
    public int getItemCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvRate, tvQuantitySold, tvDistance;
        ExpandableTextView tvDesc ;
        ImageView img;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgProduct);
            tvTitle = itemView.findViewById(R.id.tvNameRestaurant);
            tvDesc = itemView.findViewById(R.id.expand_text_view).findViewById(R.id.expand_text_view);
            tvRate = itemView.findViewById(R.id.tvRateProduct);
            tvQuantitySold = itemView.findViewById(R.id.tvQuantitySold);
            tvDistance = itemView.findViewById(R.id.tvDistance);

        }
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
