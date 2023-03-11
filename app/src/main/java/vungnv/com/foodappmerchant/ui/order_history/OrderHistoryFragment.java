package vungnv.com.foodappmerchant.ui.order_history;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

import vungnv.com.foodappmerchant.MainActivity;
import vungnv.com.foodappmerchant.R;
import vungnv.com.foodappmerchant.adapters.OrderAdapter;
import vungnv.com.foodappmerchant.adapters.OrderHistoryAdapter;
import vungnv.com.foodappmerchant.constant.Constant;
import vungnv.com.foodappmerchant.dao.OrderHistoryDAO;
import vungnv.com.foodappmerchant.model.Order;

public class OrderHistoryFragment extends Fragment implements Constant {

    private OrderHistoryDAO orderHistoryDAO;
    private RecyclerView rcvListOrderHistory;
    private OrderHistoryAdapter orderAdapter;
    private EditText edDate;
    int mYear, mMonth, mDay;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    public OrderHistoryFragment() {

    }

    public static OrderHistoryFragment newInstance() {
        return new OrderHistoryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Objects.requireNonNull(((MainActivity) requireActivity()).getSupportActionBar()).setTitle(ORDER_HISTORY);
        View view = inflater.inflate(R.layout.fragment_order_history, container, false);

        init(view);

        orderHistoryDAO.deleteTempDBOrderHistory(); // delete table old
        getOrderHistory();

        edDate.addTextChangedListener(new TextWatcher() {
            boolean isNewInput = true;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isNewInput = i2 != 0;
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable editable) {
                if (!isNewInput) {
                    return;
                }
                String text = editable.toString();
                if (text.length() == 2 && !text.contains("-")) {
                    editable.insert(2, "-");
                } else if (text.length() == 5 && !text.endsWith("-")) {
                    editable.insert(5, "-");
                }
                isNewInput = false;
            }
        });

        edDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int DRAWABLE_RIGHT = 2;
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (motionEvent.getRawX() >= (edDate.getRight() - edDate.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        Calendar calendar = Calendar.getInstance();
                        mYear = calendar.get(Calendar.YEAR);
                        mMonth = calendar.get(Calendar.MONTH);
                        mDay = calendar.get(Calendar.DAY_OF_MONTH);
                        DatePickerDialog dpd = new DatePickerDialog(getActivity(), 0, mDate, mYear, mMonth, mDay);
                        dpd.show();
                        return true;
                    }
                }
                return false;
            }
        });

        return view;
    }

    private void init(View view) {
        orderHistoryDAO = new OrderHistoryDAO(getContext());
        edDate = view.findViewById(R.id.edDate);
        rcvListOrderHistory = view.findViewById(R.id.rcvListOrderHistory);
    }

    DatePickerDialog.OnDateSetListener mDate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            mYear = i;
            mMonth = i1;
            mDay = i2;
            GregorianCalendar gc = new GregorianCalendar(mYear, mMonth, mDay);
            edDate.setText(sdf.format(gc.getTime()));
        }
    };

    private void getOrderHistory(){
        String idMerchant = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("list_order").child(idMerchant);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                List<Order> newOrderList = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Order order = ds.getValue(Order.class);
                    assert order != null;
                    if (order.status == 1) {
                        newOrderList.add(order);
                    }
                }
                if (newOrderList.isEmpty()) {
//                    Toast.makeText(getContext(), "Hết đơn", Toast.LENGTH_SHORT).show();
                    if (orderAdapter != null) {
                        orderAdapter.updateList(newOrderList);
                    }
                } else {
                    if (orderAdapter == null) {
                        orderAdapter = new OrderHistoryAdapter(getContext(), newOrderList);
                        rcvListOrderHistory.setAdapter(orderAdapter);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                        rcvListOrderHistory.setLayoutManager(linearLayoutManager);
                    } else {
                        orderAdapter.updateList(newOrderList);
                    }
                }
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: " + error.getMessage());
            }
        });
    }

    private void syncOrderHistory() {

    }
}