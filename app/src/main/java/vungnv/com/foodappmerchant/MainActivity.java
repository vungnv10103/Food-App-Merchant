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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.List;

import vungnv.com.foodappmerchant.constant.Constant;
import vungnv.com.foodappmerchant.model.CategoryModel;
import vungnv.com.foodappmerchant.model.ProductModel;
import vungnv.com.foodappmerchant.model.ProductSlideShowModel;
import vungnv.com.foodappmerchant.model.UserModel;
import vungnv.com.foodappmerchant.ui.account.MangerAccountFragment;
import vungnv.com.foodappmerchant.ui.home.OrderFragment;
import vungnv.com.foodappmerchant.ui.infomation.InformationFragment;
import vungnv.com.foodappmerchant.ui.manager_menu.ManageMenuActivity;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, Constant {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ImageView imgBack;
    private View mHeaderView;
    private TextView tvHome, tvManagerAccount;
    public static final String CHANNEL_ID = "my_channel_id";


    private List<UserModel> listUser;
    private List<ProductModel> listProduct;
    private List<ProductSlideShowModel> listSlideShow;
    private List<CategoryModel> listCate;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        // getData();


        toolbar.setTitle(ORDER);
        toolbar.setTitleTextColor(Color.WHITE);


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        //createNotification(MainActivity.this, NOTIFICATION, MESSAGE);
        createNotificationChannel(MainActivity.this);


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
        mHeaderView = navigationView.getHeaderView(0);
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
                Toast.makeText(this, "Hộp thư", Toast.LENGTH_SHORT).show();
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(Context context) {
        // NotificationChannel for Android 8.0 and higher
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        CharSequence channelName = "My Channel";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel notificationChannel = null;
        notificationChannel = new NotificationChannel(CHANNEL_ID, channelName, importance);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.enableVibration(true);
        notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        notificationManager.createNotificationChannel(notificationChannel);

    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private void createNotification(Context context, String title, String message) {
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.food_app_merchant_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(0, builder.build());
    }

    private void getData() {
        int temp = 0;
//        Toast.makeText(this, ""+ listProduct.size(), Toast.LENGTH_SHORT).show();

//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference reference = database.getReference("my_path");
//        reference.setValue(listCate, new DatabaseReference.CompletionListener() {
//            @Override
//            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
//                Toast.makeText(getApplicationContext(), "Thàng công", Toast.LENGTH_SHORT).show();
//            }
//        });


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