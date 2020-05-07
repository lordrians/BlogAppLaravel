package com.example.blogapp.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.blogapp.Api;
import com.example.blogapp.GlobalVar;
import com.example.blogapp.R;
import com.example.blogapp.UserInfoActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUpFragment extends Fragment implements View.OnClickListener {
    private View view;
    private TextInputLayout etLEmail , etLPassword, etLConfPass;
    private TextInputEditText etEmail, etPassword, etConfPass;
    private TextView tvSignin;
    private Button btnSignup;
    private ProgressDialog dialog;


    public SignUpFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        init();
        return  view;
    }

    private void init() {
        etLEmail = view.findViewById(R.id.etl_signup_email);
        etEmail = view.findViewById(R.id.et_signup_email);
        etLPassword = view.findViewById(R.id.etl_signup_password);
        etPassword = view.findViewById(R.id.et_signup_password);
        etLConfPass = view.findViewById(R.id.etl_signup_conf_password);
        etConfPass = view.findViewById(R.id.et_signup_conf_password);
        tvSignin = view.findViewById(R.id.tv_signup_signin);
        btnSignup = view.findViewById(R.id.btn_signup);

        dialog = new ProgressDialog(getContext());
        dialog.setCancelable(false);

        tvSignin.setOnClickListener(this);
        btnSignup.setOnClickListener(this);

        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!etEmail.getText().toString().isEmpty()){
                    etLEmail.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etPassword.getText().toString().length() > 7){
                    etLPassword.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etConfPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etConfPass.getText().toString().equals(etPassword.getText().toString())){
                    etLConfPass.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private boolean validate() {
        if (etEmail.getText().toString().isEmpty()){
            etLEmail.setErrorEnabled(true);
            etLEmail.setError("Email is Required");
            return false;
        }
        if (etPassword.getText().toString().length()<8){
            etLPassword.setErrorEnabled(true);
            etLPassword.setError("Required at least 8 characters");
            return false;
        }
        if (!etConfPass.getText().toString().equals(etPassword.getText().toString())){
            etLConfPass.setErrorEnabled(true);
            etLConfPass.setError("Password doesn't match");
            return false;
        }
        return true;


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.tv_signup_signin:
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fl_auth_activity, new SignInFragment()).commit();
                break;

            case R.id.btn_signup:
                if (validate()){
                    register();
                }

        }
    }

    private void register() {
        dialog.setMessage("Registering");
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Api.REGISTER, response->{
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")){
                    JSONObject user =  object.getJSONObject("user");
                    //save to shared preference
                    SharedPreferences userPref = getActivity().getApplicationContext().getSharedPreferences(GlobalVar.FILE_USER,0);
                    SharedPreferences.Editor editor = userPref.edit();
                    editor.putString("token", object.getString("token"));
                    editor.putInt("id", user.getInt("id"));
                    editor.putString("name", user.getString("name"));
                    editor.putString("lastname", user.getString("lastname"));
                    editor.putString("photo", user.getString("photo"));
                    editor.putBoolean("isLoggedIn", true);
                    editor.apply();
                    //if succes
                    startActivity(new Intent(getContext(), UserInfoActivity.class));
                    getActivity().finish();
                    Toast.makeText(getContext(), "Sign Up Succes", Toast.LENGTH_LONG).show();

                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), e.getMessage().toString(),Toast.LENGTH_LONG).show();
            }
            dialog.dismiss();
        }, error -> {
            error.printStackTrace();
            dialog.dismiss();
        }){
            protected Map<String , String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("email", etEmail.getText().toString().trim());
                map.put("password", etPassword.getText().toString());
                return map;
            }
        };
        Volley.newRequestQueue(getContext()).add(request);
    }

}
