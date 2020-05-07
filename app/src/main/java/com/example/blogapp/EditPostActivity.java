package com.example.blogapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.blogapp.fragments.HomeFragment;
import com.example.blogapp.model.Post;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditPostActivity extends AppCompatActivity implements View.OnClickListener {

    private int position, id;
    private EditText etDesc;
    private Button btnSave;
    private ProgressDialog dialog;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);
        init();
    }

    private void init() {
        preferences = getSharedPreferences(GlobalVar.FILE_USER, 0);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        etDesc = findViewById(R.id.et_editpost_desc);
        btnSave = findViewById(R.id.btn_editpost_save);

        position = getIntent().getIntExtra("position", 0);
        id = getIntent().getIntExtra("postId",0);
        etDesc.setText(getIntent().getStringExtra("desc"));

        btnSave.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_editpost_save:
                savePost();
        }
    }

    private void savePost() {
        dialog.setMessage("Saving...");
        dialog.show();

        StringRequest request = new StringRequest(Request.Method.PUT, Api.UPDATE_POST, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")){
                    Post post = HomeFragment.arrayList.get(position);
                    post.setDesc(etDesc.getText().toString());
                    HomeFragment.arrayList.set(position, post);
                    HomeFragment.rvPost.getAdapter().notifyItemChanged(position);
                    HomeFragment.rvPost.getAdapter().notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            dialog.dismiss();
            finish();
        }, error -> {
            error.printStackTrace();
            dialog.dismiss();
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = preferences.getString("token","");
                HashMap<String,String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
                return map;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("id", id +"");
                map.put("desc", etDesc.getText().toString());
                return map;
            }
        };

        Volley.newRequestQueue(getApplicationContext()).add(request);
    }
}
