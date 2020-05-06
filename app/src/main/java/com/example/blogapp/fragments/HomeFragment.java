package com.example.blogapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.blogapp.Api;
import com.example.blogapp.GlobalVar;
import com.example.blogapp.HomeActivity;
import com.example.blogapp.R;
import com.example.blogapp.adapter.PostAdapter;
import com.example.blogapp.model.Post;
import com.example.blogapp.model.User;
import com.google.android.material.appbar.MaterialToolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {

    private View view;
    public static RecyclerView rvPost;
    public static ArrayList<Post> arrayList;
    private SwipeRefreshLayout refreshLayout;
    private PostAdapter postAdapter;
    private Context context;
    private MaterialToolbar toolbar;
    private SharedPreferences sharedPreferences;

    public HomeFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        init();
        return view;
    }

    private void init() {
        sharedPreferences = getActivity().getSharedPreferences(GlobalVar.FILE_USER, 0);
        rvPost = view.findViewById(R.id.rv_home_fragment);
        rvPost.setHasFixedSize(true);
        rvPost.setLayoutManager(new LinearLayoutManager(getContext()));
        toolbar = view.findViewById(R.id.tb_home_fragment);

        refreshLayout = view.findViewById(R.id.swipe_home_fragment);
        ((HomeActivity)getContext()).setSupportActionBar(toolbar);

        getPost();

        refreshLayout.setOnRefreshListener(() -> {
            getPost();
        });
    }

    private void getPost() {
        arrayList = new ArrayList<>();
        refreshLayout.setRefreshing(true);

        StringRequest request = new StringRequest(Request.Method.GET, Api.POST, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")){
                    JSONArray arrayPost = new JSONArray(object.getString("posts"));
                    for (int i = 0; i < arrayPost.length(); i++){
                        JSONObject postObject = arrayPost.getJSONObject(i);
                        JSONObject userObject = postObject.getJSONObject("user");

                        User user = new User();
                        user.setId(userObject.getInt("id"));
                        user.setUserName(userObject.getString("name") + " " + userObject.getString("lastname"));
                        user.setPhoto(userObject.getString("photo"));

                        Post post = new Post();
                        post.setId(postObject.getInt("id"));
                        post.setUser(user);
                        post.setLikes(postObject.getInt("likesCount"));
                        post.setComments(postObject.getInt("commentsCount"));
                        post.setDate(postObject.getString("created_at"));
                        post.setDesc(postObject.getString("desc"));
                        post.setPhoto(postObject.getString("photo"));
                        post.setSelfLike(postObject.getBoolean("selfLike"));

                        arrayList.add(post);
                    }
                    postAdapter = new PostAdapter(getContext(), arrayList);
                    rvPost.setAdapter(postAdapter);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            refreshLayout.setRefreshing(false);

        }, error -> {
            error.printStackTrace();
            refreshLayout.setRefreshing(false);
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
                return map;

            }
        };

        Volley.newRequestQueue(getContext()).add(request);

    }

}
