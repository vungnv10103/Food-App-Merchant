package vungnv.com.foodappmerchant.ui.information;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import vungnv.com.foodappmerchant.R;
import vungnv.com.foodappmerchant.activities.RegisterActivity;
import vungnv.com.foodappmerchant.constant.Constant;
import vungnv.com.foodappmerchant.model.CategoryModel;
import vungnv.com.foodappmerchant.model.UserModel;
import vungnv.com.foodappmerchant.utils.ImagePicker;

public class InformationFragment extends Fragment implements Constant {



    public InformationFragment() {
        // Required empty public constructor
    }

    public static InformationFragment newInstance() {
        return new InformationFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_infomation, container, false);
        init(view);

        return view;
    }
    private void init(View view) {

    }

}