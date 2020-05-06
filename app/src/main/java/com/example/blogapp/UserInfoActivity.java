package com.example.blogapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private TextInputLayout etLName, etLLastname;
    private TextInputEditText etName, etLastname;
    private TextView tvSelectPhoto;
    private Button btnContinue;
    private CircleImageView ivUserInfo;
    private static final int GALLERY_ADD_PROFILE = 1;
    private Bitmap bitmapUserInfo = null;
    private SharedPreferences userPref;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        init();
    }

    private void init() {
        etLName = findViewById(R.id.etl_user_info_name);
        etLLastname = findViewById(R.id.etl_user_info_lastname);
        etName = findViewById(R.id.et_user_info_name);
        etLastname = findViewById(R.id.et_user_info_lastname);
        tvSelectPhoto = findViewById(R.id.tv_user_info_select_photo);
        ivUserInfo = findViewById(R.id.iv_user_info);
        btnContinue = findViewById(R.id.btn_user_info_continue);

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);

        userPref = getSharedPreferences(GlobalVar.FILE_USER, 0);

        tvSelectPhoto.setOnClickListener(this);
        btnContinue.setOnClickListener(this);
    }


    private boolean validate() {
        if (etName.getText().toString().isEmpty()){
            etLName.setErrorEnabled(true);
            etLName.setError("Name is Required");
            return false;
        }
        if (etLastname.getText().toString().isEmpty()){
            etLLastname.setErrorEnabled(true);
            etLLastname.setError("Lastname is Required");
            return false;
        }
        return true;


    }


    private void saveUserInfo() {

        String name = etName.getText().toString().trim();
        String lastname = etLastname.getText().toString().trim();

        dialog.setMessage("Loading...");
        dialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, Api.SAVE_USER_INFO, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")){
                    SharedPreferences.Editor editor = userPref.edit();
                    editor.putString("photo", object.getString("photo"));
                    editor.apply();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
            dialog.dismiss();

        }, error -> {
            error.printStackTrace();
            dialog.dismiss();
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = userPref.getString("token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
                return map;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("name" , name);
                map.put("lastname", lastname);
                map.put("photo", bitmapToString(bitmapUserInfo));
                return map;
            }
        };

        Volley.newRequestQueue(getApplicationContext()).add(request);


    }

    private String bitmapToString(Bitmap bitmapUserInfo) {
        if (bitmapUserInfo != null){
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmapUserInfo.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] imageByte = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(imageByte, Base64.DEFAULT);
        } else {
            return "";
        }

    }


    private void pickimage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(CropImageView.CropShape.OVAL)
                .start(UserInfoActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK){
                Uri uriPhoto = result.getUri();
                if (uriPhoto != null){
                    try {
                        bitmapUserInfo = MediaStore.Images.Media.getBitmap(getContentResolver(),uriPhoto);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                ivUserInfo.setImageURI(uriPhoto);
            }

//            Uri imgUri = data.getData();
//            ivUserInfo.setImageURI(imgUri);
//
//            try {
//                bitmapUserInfo = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.tv_user_info_select_photo:
                pickimage();
//                Intent i = new Intent(Intent.ACTION_PICK);
//                i.setType("image/*");
//                startActivityForResult(i, GALLERY_ADD_PROFILE);
                break;

            case R.id.btn_user_info_continue:
                if (validate()){
                    saveUserInfo();
                }
                break;

        }

    }
}
