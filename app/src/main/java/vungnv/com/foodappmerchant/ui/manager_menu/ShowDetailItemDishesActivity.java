package vungnv.com.foodappmerchant.ui.manager_menu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import vungnv.com.foodappmerchant.R;

public class ShowDetailItemDishesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_detail_item_dishes);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("data-type");
        if (bundle != null) {
            String name = bundle.getString("name");
            Toast.makeText(this, "" + name, Toast.LENGTH_SHORT).show();
        }
    }
}