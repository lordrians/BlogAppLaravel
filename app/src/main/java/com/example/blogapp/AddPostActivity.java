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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.blogapp.fragments.HomeFragment;
import com.example.blogapp.model.Post;
import com.example.blogapp.model.User;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddPostActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnPost;
    private ImageView ivPhoto;
    private EditText etDesc;
    private TextView tvChangePhoto;
    private Bitmap bitmap = null;
    private ProgressDialog dialog;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        init();

    }

    private void init() {
        preferences = getSharedPreferences(GlobalVar.FILE_USER, 0);
        btnPost = findViewById(R.id.btn_addpost_post);
        ivPhoto = findViewById(R.id.iv_addpost_photo);
        etDesc = findViewById(R.id.et_addpost_desc);
        tvChangePhoto = findViewById(R.id.tv_addpost_changephoto);

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);

        retrievePhoto();

        tvChangePhoto.setOnClickListener(this);
        btnPost.setOnClickListener(this);


    }

    private void retrievePhoto() {
        ivPhoto.setImageURI(getIntent().getData());

        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),getIntent().getData());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void pickimage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .start(AddPostActivity.this);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK){
                Uri uriPhoto = result.getUri();
                if (uriPhoto != null){
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uriPhoto);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                ivPhoto.setImageURI(uriPhoto);
            }

        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_addpost_changephoto:
                pickimage();
                break;

            case R.id.btn_addpost_post:
                postImage();
        }

    }

    private void postImage() {
        dialog.setMessage("Posting...");
        dialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, Api.ADD_POST, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")){
                    JSONObject postObject = object.getJSONObject("post");
                    JSONObject userObject = postObject.getJSONObject("user");

                    User user = new User();
                    user.setId(userObject.getInt("id"));
                    user.setUserName(userObject.getString("name") + " " + userObject.getString("lastname"));
                    user.setPhoto(userObject.getString("photo"));

                    Post post = new Post();
                    post.setUser(user);
                    post.setId(postObject.getInt("id"));
                    post.setSelfLike(false);
                    post.setPhoto(postObject.getString("photo"));
                    post.setDesc(postObject.getString("desc"));
                    post.setComments(0);
                    post.setLikes(0);
                    post.setDate(postObject.getString("created_at"));

                    HomeFragment.arrayList.add(0, post);
                    HomeFragment.rvPost.getAdapter().notifyItemInserted(0);
                    HomeFragment.rvPost.getAdapter().notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(), "Posted", Toast.LENGTH_SHORT).show();
                    finish();

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            dialog.dismiss();
        }, error -> {
            error.printStackTrace();
            dialog.dismiss();
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = preferences.getString("token","");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
                return map;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("desc", etDesc.getText().toString().trim());
                map.put("photo", bitmapToString(bitmap));
                return map;
            }
        };

        Volley.newRequestQueue(getApplicationContext()).add(request);

    }
}
