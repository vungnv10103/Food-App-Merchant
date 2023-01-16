package vungnv.com.foodappmerchant;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import vungnv.com.foodappmerchant.activities.AddCategoryActivity;
import vungnv.com.foodappmerchant.constant.Constant;
import vungnv.com.foodappmerchant.ui.account.MangerAccountFragment;
import vungnv.com.foodappmerchant.ui.home.OrderFragment;
import vungnv.com.foodappmerchant.ui.information.InformationFragment;
import vungnv.com.foodappmerchant.ui.manager_menu.ManageMenuActivity;
import vungnv.com.foodappmerchant.utils.NetworkChangeListener;
import vungnv.com.foodappmerchant.utils.createNotificationChannel;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, Constant {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ImageView imgBack;
    private TextView tvHome, tvManagerAccount;
    private final NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    createNotificationChannel notification = new createNotificationChannel();


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        notification.createNotificationChannel(MainActivity.this);
        //vungnv.com.foodappmerchant.utils.createNotification.mCreateNotification(MainActivity.this, "Tiêu đề", "Nội dung");

        toolbar.setTitle(ORDER);
        toolbar.setTitleTextColor(Color.WHITE);


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        tvHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finishAffinity();

            }
        });

        tvManagerAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(MangerAccountFragment.newInstance());
                drawerLayout.closeDrawer(navigationView);
            }
        });

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, toolbar, 0, 0);
        toggle.syncState();

        navigationView.setItemIconTintList(ColorStateList.valueOf(Color.WHITE));
        navigationView.setNavigationItemSelectedListener(this);
        //replaceFragment(OrderFragment.newInstance());
        Fragment initialFragment = new OrderFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frameLayout, initialFragment)
                .addToBackStack(null)
                .commit();

    }

    private void init() {
        drawerLayout = findViewById(R.id.drawer);
        toolbar = findViewById(R.id.toolBar);
        navigationView = findViewById(R.id.naviView);
        View mHeaderView = navigationView.getHeaderView(0);
        imgBack = mHeaderView.findViewById(R.id.imgBack);
        tvManagerAccount = mHeaderView.findViewById(R.id.tvManagerAccount);
        tvHome = mHeaderView.findViewById(R.id.tvHome);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int index = item.getItemId();
        switch (index) {
            case R.id.mail_box:
                startActivity(new Intent(MainActivity.this, AddCategoryActivity.class));
                break;
            case R.id.order_history:
                Toast.makeText(this, "Lịch sử đơn hàng", Toast.LENGTH_SHORT).show();
                break;
            case R.id.order_complaint:
                Toast.makeText(this, "Đơn hàng khiếu nại", Toast.LENGTH_SHORT).show();
                break;
            case R.id.restaurant_manager:
                Toast.makeText(this, "Quản lý nhà hàng", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_manager:
                startActivity(new Intent(MainActivity.this, ManageMenuActivity.class));
                break;
            case R.id.statistical:
                Toast.makeText(this, "Thống kê", Toast.LENGTH_SHORT).show();
                break;
            case R.id.info:
                replaceFragment(InformationFragment.newInstance());
                break;

        }
        drawerLayout.closeDrawer(navigationView);
        return true;
    }

    public void replaceFragment(Fragment fra) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fra);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawer(navigationView);
        } else {
            super.onBackPressed();
        }
    }

}