package vungnv.com.foodappmerchant.ui.restaurant_manager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dmax.dialog.SpotsDialog;
import vungnv.com.foodappmerchant.MainActivity;
import vungnv.com.foodappmerchant.R;
import vungnv.com.foodappmerchant.constant.Constant;
import vungnv.com.foodappmerchant.model.Product;
import vungnv.com.foodappmerchant.model.ProductModel;


public class RestaurantManagerFragment extends Fragment implements Constant {

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch aSwitch;
    private TextView tvOperatingTime, tvDayClosed;
    private SpotsDialog progressDialog;
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    private boolean isActive = false;


    public RestaurantManagerFragment() {
        // Required empty public constructor
    }

    public static RestaurantManagerFragment newInstance() {
        return new RestaurantManagerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        Objects.requireNonNull(((MainActivity) requireActivity()).getSupportActionBar()).setTitle(RESTAURANT_MANAGER);
        View view = inflater.inflate(R.layout.fragment_restaurant_manager, container, false);

        init(view);

        String idMerChant = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("list_user_merchant").child(idMerChant).child("status");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int status = Integer.parseInt(Objects.requireNonNull(snapshot.getValue()).toString());

                aSwitch.setChecked(status == 2);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (isActive) {
            aSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean check = aSwitch.isChecked();
                    Dialog dialog = new Dialog(getContext());
                    if (check) {
                        dialog.setContentView(R.layout.dialog_confirm_on);
//                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setCancelable(false);


                        TextView tvCancel, tvConfirm;
                        tvCancel = dialog.findViewById(R.id.tvCancel);
                        tvConfirm = dialog.findViewById(R.id.tvConfirm);
                        tvCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                aSwitch.setChecked(false);
                                dialog.dismiss();
                            }
                        });
                        tvConfirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // active stop order
                                getAllProduct(0, 2);
                                dialog.dismiss();
                            }
                        });


                    } else {

                        dialog.setContentView(R.layout.dialog_confirm_off);
//                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setCancelable(false);


                        TextView tvCancel, tvConfirm;
                        tvCancel = dialog.findViewById(R.id.tvCancel);
                        tvConfirm = dialog.findViewById(R.id.tvConfirm);
                        tvCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                aSwitch.setChecked(true);
                                dialog.dismiss();
                            }
                        });
                        tvConfirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // active stop order
                                getAllProduct(2, 1);
                                dialog.dismiss();
                            }
                        });


                    }
                    dialog.show();
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                    dialog.getWindow().setGravity(Gravity.CENTER);
                }
            });
        }
        else {
            Toast.makeText(getContext(), "updating", Toast.LENGTH_SHORT).show();
        }


        tvOperatingTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "open", Toast.LENGTH_SHORT).show();
            }
        });
        tvDayClosed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "closed", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    private void init(View view) {
        aSwitch = view.findViewById(R.id.switchStopOrder);
        tvOperatingTime = view.findViewById(R.id.tvOperatingTime);
        tvDayClosed = view.findViewById(R.id.tvDayClosed);
        progressDialog = new SpotsDialog(getContext(), R.style.Custom);
    }

    private void getAllProduct(int status, int statusMerchant) {
        progressDialog.show();
        String idMerchant = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("list_product/" + idMerchant);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<ProductModel> listProduct = new ArrayList<>();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    ProductModel model = snapshot1.getValue(ProductModel.class);
                    if (model == null) {
                        return;
                    }
                    if (model.status != -1) {
                        listProduct.add(model);
                    }

                }

                if (listProduct.size() == 0) {
                    Toast.makeText(getContext(), NO_PRODUCT, Toast.LENGTH_SHORT).show();
                    return;
                }
                for (int i = 0; i < listProduct.size(); i++) {
                    int pos = listProduct.get(i).pos;
                    String idProduct = listProduct.get(i).id;
                    // update status product -> 0;
                    getPositionInALl(pos, idProduct, status, statusMerchant);
                }
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
            }
        });

    }

    private void updateStatus(int pos, int position, int status, int statusMerchant) {
        String idMerchant = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference().child("list_product")
                .child(idMerchant).child(String.valueOf(pos)).child("status");
        ref.setValue(status).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                DatabaseReference ref = FirebaseDatabase.getInstance()
                        .getReference().child("list_product_all")
                        .child(String.valueOf(position)).child("status");
                ref.setValue(status).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        String idMerchant = auth.getCurrentUser().getUid();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("list_user_merchant").child(idMerchant).child("status");
                        ref.setValue(statusMerchant).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
//                               Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "update status in list_product_all success");
                            }
                        });
                    }
                });

            }
        });
    }

    private void getPositionInALl(int pos, String id, int status, int statusMerchant) {
        String path = "list_product_all";
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot1 : snapshot.getChildren()) {
                    Product value = childSnapshot1.getValue(Product.class);
                    assert value != null;
                    if (value.id.equals(id)) {
                        int position = value.pos;
                        updateStatus(pos, position, status, statusMerchant);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}