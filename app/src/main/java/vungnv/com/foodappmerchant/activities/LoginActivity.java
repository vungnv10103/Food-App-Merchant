package vungnv.com.foodappmerchant.activities;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dmax.dialog.SpotsDialog;
import vungnv.com.foodappmerchant.MainActivity;
import vungnv.com.foodappmerchant.R;
import vungnv.com.foodappmerchant.constant.Constant;
import vungnv.com.foodappmerchant.dao.UsersDAO;
import vungnv.com.foodappmerchant.model.UserModel;
import vungnv.com.foodappmerchant.utils.LocationProvider;
import vungnv.com.foodappmerchant.utils.NetworkChangeListener;

public class LoginActivity extends AppCompatActivity implements Constant {
    private Button btnLogin;
    private EditText edPass;
    private ToggleButton chkLanguage;
    private AutoCompleteTextView edEmail;
    private CheckBox cbRemember;
    private TextView tvRegister;
    private final NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    private String mLocation = "";
    private String coordinate = "";
    private FusedLocationProviderClient fusedLocationProviderClient;
    private SpotsDialog progressDialog;
//    double coNhueLongitude = 105.77553463;
//    double coNhueLatitude = 21.06693654;

    private UsersDAO usersDAO;
    private ArrayList<UserModel> listDataUser;
    ArrayAdapter<String> adapterItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
        String currentLanguage = getResources().getConfiguration().locale.getLanguage();
        chkLanguage.setChecked(currentLanguage.equals("en"));
        getLastLocation1();

        SharedPreferences pref = getSharedPreferences("USER_FILE", MODE_PRIVATE);
        if (pref != null) {
            edEmail.setText(pref.getString("EMAIL", ""));
            edPass.setText(pref.getString("PASSWORD", ""));
            cbRemember.setChecked(pref.getBoolean("REMEMBER", false));
        }
        listDataUser = (ArrayList<UserModel>) usersDAO.getALL();
        String[] listUser = new String[listDataUser.size()];

        for (int i = 0; i < listDataUser.size(); i++) {
            String temp = listDataUser.get(i).email;
            listUser[i] = temp;
        }
        adapterItems = new ArrayAdapter<String>(this, R.layout.list_item_acc, listUser);
        edEmail.setAdapter(adapterItems);
        edEmail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String pass = usersDAO.autoFillPassWord(listUser[position]);
                edPass.setText(pass);
            }
        });

        chkLanguage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // false = vi , true = en
                String currentLanguage = getResources().getConfiguration().locale.getLanguage();
                String selectLanguage;
                if (isChecked) {
                    // change to en
                    selectLanguage = "en";
                } else {
                    // change to vi
                    selectLanguage = "vi";
                }
                if (!currentLanguage.equals(selectLanguage)) {
                    changeLanguage(selectLanguage);
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                progressDialog.setCancelable(false);
                String email = edEmail.getText().toString().trim();
                String pass = edPass.getText().toString().trim();
                FirebaseAuth auth = FirebaseAuth.getInstance();
                if (validate()) {
                    if (checkEmail(email)) {
                        auth.signInWithEmailAndPassword(email, pass)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {

                                            rememberUser(email, pass, cbRemember.isChecked());
                                            if (usersDAO.checkAccountExist(auth.getUid())) {
                                                saveDbUserInLocal(auth.getUid());
                                            }
                                            progressDialog.dismiss();
                                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                            finishAffinity();
                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(LoginActivity.this, ERROR_LOGIN + "\n" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }
            }
        });
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finishAffinity();
            }
        });

    }

    private void init() {
        btnLogin = findViewById(R.id.btnSignIn);
        edEmail = findViewById(R.id.edEmail);
        edPass = findViewById(R.id.edPass);
        cbRemember = findViewById(R.id.checkBoxRemember);
        tvRegister = findViewById(R.id.tvRegister);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        chkLanguage = findViewById(R.id.chkLanguage);
        progressDialog = new SpotsDialog(LoginActivity.this, R.style.Custom);
        usersDAO = new UsersDAO(getApplicationContext());
    }

    private boolean validate() {
        String email = edEmail.getText().toString().trim();
        String pass = edPass.getText().toString().trim();
        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, REQUEST_FILL, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;

    }

    private boolean checkEmail(String email) {
        String regex = "^[a-zA-Z0-9._%+-]+@merchant\\.com$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            Toast.makeText(getApplicationContext(), WRONG_EMAIL_FORMAT, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    private void saveDbUserInLocal(String idUser) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("list_user_merchant").child(idUser);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel item = snapshot.getValue(UserModel.class);
                UserModel itemUser = new UserModel();
                assert item != null;
                itemUser.id = item.id;
                itemUser.status = item.status;
                itemUser.img = item.img;
                itemUser.name = item.name;
                itemUser.email = item.email;
                itemUser.pass = item.pass;
                itemUser.phoneNumber = item.phoneNumber;
                itemUser.restaurantName = item.restaurantName;
                itemUser.address = item.address;
                itemUser.coordinates = item.coordinates;
                itemUser.feedback = item.feedback;

                if (usersDAO.insert(itemUser) > 0) {
                    Log.d(TAG, "save db user success: ");
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void askPermission() {
        ActivityCompat.requestPermissions(LoginActivity.this, new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    private void rememberUser(String email, String pass, boolean isRemember) {
        SharedPreferences pref = getSharedPreferences("USER_FILE", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        if (!isRemember) {
            editor.clear();
        } else {
            // save data
            editor.putString("EMAIL", email);
            editor.putString("PASSWORD", pass);
            editor.putBoolean("REMEMBER", true);
        }
        // save
        editor.apply();
    }

    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationProvider locationManager = new LocationProvider(LoginActivity.this);
            locationManager.getLastLocation(new LocationProvider.OnLocationChangedListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if (location != null) {
                        Geocoder geocoder = new Geocoder(LoginActivity.this, Locale.getDefault());
                        List<Address> addresses = null;
                        try {
                            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            mLocation = addresses.get(0).getAddressLine(0);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
                }
            });


        } else {

            askPermission();

        }
    }

    private void getLastLocation1() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                Geocoder geocoder = new Geocoder(LoginActivity.this, Locale.getDefault());
                                List<Address> addresses = null;
                                try {
                                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                    mLocation = addresses.get(0).getAddressLine(0);
                                    Log.d(TAG, "current Location: " + mLocation);
                                    double currentLongitude = addresses.get(0).getLongitude();
                                    double currentLatitude = addresses.get(0).getLatitude();

                                    coordinate = currentLongitude + "-" + currentLatitude;


                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


                            }

                        }
                    });


        } else {

            askPermission();

        }
    }

    private void changeLanguage(String language) {
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.setLocale(new Locale(language));
        res.updateConfiguration(conf, dm);

        recreate();
    }

    @Override
    protected void onStart() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, intentFilter);
//        startActivity(new Intent(LoginActivity.this, MainActivity.class));
//        finishAffinity();
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }

}