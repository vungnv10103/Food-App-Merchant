package vungnv.com.foodappmerchant.ui.account;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vungnv.com.foodappmerchant.MainActivity;
import vungnv.com.foodappmerchant.R;
import vungnv.com.foodappmerchant.activities.LoginActivity;
import vungnv.com.foodappmerchant.constant.Constant;

public class MangerAccountFragment extends Fragment implements Constant, SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout swipeRefreshLayout;
    private ConstraintLayout changePassword;
    private LinearLayout changeName, changePhoneNumber, changeEmail;
    private TextView tvName, tvPhone, tvEmail;
    private TextView logOut;
    private EditText edOldPass, edNewPass, edConfirmPass;
    private EditText edName, edPhone, edEmail;
    private Button btnSave, btnCancel;

    public MangerAccountFragment() {

    }

    public static MangerAccountFragment newInstance() {
        return new MangerAccountFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //Toast.makeText(getContext(), "" + requireActivity().getTitle(), Toast.LENGTH_SHORT).show();
        Objects.requireNonNull(((MainActivity) requireActivity()).getSupportActionBar()).setTitle(Manager_Infor);
        View view = inflater.inflate(R.layout.fragment_manger_account, container, false);

        init(view);

        swipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.red),
                getResources().getColor(R.color.green));

        changePassword.setOnClickListener(v -> {
            // change password
            Dialog dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.dialog_change_password);
            dialog.setCancelable(false);

            edOldPass = dialog.findViewById(R.id.passOld);
            edNewPass = dialog.findViewById(R.id.passNew);
            edConfirmPass = dialog.findViewById(R.id.passConfirm);
            btnSave = dialog.findViewById(R.id.btnSave);
            btnCancel = dialog.findViewById(R.id.btnCancel);
            btnSave.setOnClickListener(v1 -> {
                String passOld = edOldPass.getText().toString().trim();
                String passNew = edNewPass.getText().toString().trim();
                String passConfirm = edConfirmPass.getText().toString().trim();
                if (passOld.isEmpty() || passNew.isEmpty() || passConfirm.isEmpty()) {
                    Toast.makeText(getContext(), REQUEST_FILL, Toast.LENGTH_SHORT).show();
                } else {
                    String tempOldPass = "123";
                    if (passOld.equals(tempOldPass)) {
                        if (!passNew.equals(passConfirm)) {
                            Toast.makeText(getContext(), PASS_NO_MATCH, Toast.LENGTH_SHORT).show();
                        } else {
                            // change pass
                            Toast.makeText(getContext(), CHANGE_PASS_SUCCESS, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    } else {
                        Toast.makeText(getContext(), WRONG_PASS, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            btnCancel.setOnClickListener(v12 -> dialog.dismiss());
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setAttributes(lp);
            dialog.show();
        });
        changeName.setOnClickListener(v -> {
            // change name
            updateInformation();

        });
        changePhoneNumber.setOnClickListener(v -> {
            // change phone number
            updateInformation();
        });
        changeEmail.setOnClickListener(v -> {
            // change email
            updateInformation();
        });
        logOut.setOnClickListener(v -> startActivity(new Intent(getActivity(), LoginActivity.class)));

        return view;
    }

    private void init(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_account);
        swipeRefreshLayout.setOnRefreshListener(this);
        changePassword = view.findViewById(R.id.changePassword);
        changeName = view.findViewById(R.id.changeName);
        changePhoneNumber = view.findViewById(R.id.changePhoneNumber);
        changeEmail = view.findViewById(R.id.changeEmail);
        tvName = view.findViewById(R.id.tvName);
        tvPhone = view.findViewById(R.id.tvPhone);
        tvEmail = view.findViewById(R.id.tvEmail);
        logOut = view.findViewById(R.id.logOut);
    }


    @SuppressLint("ClickableViewAccessibility")
    private void updateInformation() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_update_information);
        dialog.setCancelable(false);

        edName = dialog.findViewById(R.id.edName);
        edPhone = dialog.findViewById(R.id.edPhoneNumber);
        edEmail = dialog.findViewById(R.id.edEmail);
        btnSave = dialog.findViewById(R.id.btnSave);


        edName.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (edName.getRight() - edName.getCompoundDrawables()[2].getBounds().width())) {
                    // Handle the event on the drawableEnd here
                    edName.setText("");
                    edName.setFocusable(false);
                    return true;
                }
            }
            return false;
        });
        edPhone.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (edPhone.getRight() - edPhone.getCompoundDrawables()[2].getBounds().width())) {
                    // Handle the event on the drawableEnd here
                    edPhone.setText("");
                    edPhone.setFocusable(false);
                    return true;
                }
            }
            return false;
        });
        edEmail.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (edEmail.getRight() - edEmail.getCompoundDrawables()[2].getBounds().width())) {
                    // Handle the event on the drawableEnd here
                    edEmail.setText("");
                    edEmail.setFocusable(false);
                    return true;
                }
            }
            return false;
        });

        btnCancel = dialog.findViewById(R.id.btnCancel);
        btnSave.setOnClickListener(v1 -> {
            String fullName = edName.getText().toString().trim();
            String phoneNumber = edPhone.getText().toString().trim();
            String email = edEmail.getText().toString().trim();
            if (fullName.isEmpty() || phoneNumber.isEmpty() || email.isEmpty()) {
                Toast.makeText(getContext(), REQUEST_FILL, Toast.LENGTH_SHORT).show();
            } else {
                if (checkPhoneNumber(phoneNumber)) {
                    if (checkEmail(email)) {
                        Toast.makeText(getContext(), REQUEST_FORM, Toast.LENGTH_SHORT).show();
                        tvName.setText(fullName);
                        tvPhone.setText(phoneNumber);
                        tvEmail.setText(email);
                        dialog.dismiss();
                    }
                }
            }
        });
        btnCancel.setOnClickListener(v12 -> dialog.dismiss());
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
        dialog.show();
    }

    private boolean checkPhoneNumber(String phoneNumber) {
        String regex = "^(03|08)[0-9]{8}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phoneNumber);
        if (!matcher.matches()) {
            Toast.makeText(getContext(), WRONG_PHONE_FORMAT, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean checkEmail(String email) {
        String regex = "^[a-zA-Z0-9._%+-]+@gmail\\.com$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            Toast.makeText(getContext(), WRONG_EMAIL_FORMAT, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void replaceFragment(Fragment fra) {
        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fra);
        fragmentTransaction.commit();
    }


    @Override
    public void onRefresh() {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            swipeRefreshLayout.setRefreshing(false);
            // reload data
            replaceFragment(MangerAccountFragment.newInstance());

        }, 1500);
    }
}