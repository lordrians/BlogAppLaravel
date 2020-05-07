package com.example.blogapp.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.blogapp.Api;
import com.example.blogapp.EditPostActivity;
import com.example.blogapp.GlobalVar;
import com.example.blogapp.R;
import com.example.blogapp.model.Post;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostsHolder>{

    private Context context;
    private ArrayList<Post> listPost;
    private SharedPreferences preferences;
    private ProgressDialog progressDialog;

    public PostAdapter(Context context, ArrayList<Post> listPost) {
        this.context = context;
        this.listPost = listPost;
        preferences = context.getSharedPreferences(GlobalVar.FILE_USER, 0);
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
    }

    @NonNull
    @Override
    public PostsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostsHolder holder, int position) {
        Post post = listPost.get(position);
        Picasso.get().load(Api.URL + "storage/profiles/" + post.getUser().getPhoto()).into(holder.ivPhotoProfile);
        Picasso.get().load(Api.URL + "storage/posts/" + post.getPhoto()).into(holder.ivPhotoPost);
        holder.tvName.setText(post.getUser().getUserName());
        holder.tvComment.setText("View all " + post.getComments());
        holder.tvLikes.setText(post.getLikes() + " Likes");
        holder.tvDate.setText(post.getDate());
        holder.tvDesc.setText(post.getDesc());

        if (post.getUser().getId() == preferences.getInt("id", 0)){
            holder.btnPostOption.setVisibility(View.VISIBLE);
        } else {
            holder.btnPostOption.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return listPost.size();
    }

    class PostsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvName, tvDate, tvDesc, tvLikes, tvComment;
        CircleImageView ivPhotoProfile;
        ImageView ivPhotoPost;
        ImageButton btnPostOption, btnLike, btnComment;


        public PostsHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_nama_item_post);
            tvDate = itemView.findViewById(R.id.tv_date_item_post);
            tvDesc = itemView.findViewById(R.id.tv_desc_item_post);
            tvLikes = itemView.findViewById(R.id.tv_likes_item_post);
            tvComment = itemView.findViewById(R.id.tv_view_comment_item_post);
            ivPhotoProfile = itemView.findViewById(R.id.iv_user_photo_item_post);
            ivPhotoPost = itemView.findViewById(R.id.iv_photo_item_post);
            btnComment = itemView.findViewById(R.id.btn_comment_item_post);
            btnLike = itemView.findViewById(R.id.btn_like_item_post);
            btnPostOption = itemView.findViewById(R.id.btn_option_item_post);

            btnPostOption.setVisibility(View.GONE);

            btnPostOption.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_option_item_post:
                    int itemPosition = getAdapterPosition();
                    PopupMenu popMenu = new PopupMenu(context, btnPostOption);
                    popMenu.inflate(R.menu.menu_post_option);
                    popMenu.setOnMenuItemClickListener(item -> {

                        switch (item.getItemId()){

                            case R.id.item_menu_edit:
                                Intent i = new Intent(context, EditPostActivity.class);
                                i.putExtra("postId", listPost.get(itemPosition).getId());
                                i.putExtra("position", itemPosition);
                                i.putExtra("desc", listPost.get(itemPosition).getDesc());
                                context.startActivity(i);
                                return true;

                            case R.id.item_menu_delete:
                                deletePost(listPost.get(itemPosition).getId(), itemPosition);
                                return true;

                        }
                        return false;

                    });

                    popMenu.show();
            }
        }
    }

    private void deletePost(int postId, int itemPosition) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confrimation");
        builder.setMessage("Are you sure to delete this post?");
        builder.setPositiveButton("Delete", (dialog, which) -> {
            progressDialog.setMessage("Deleting...");
            progressDialog.show();
            StringRequest request = new StringRequest(Request.Method.POST, Api.DELETE_POST, response -> {
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getBoolean("success")){
                        listPost.remove(itemPosition);
                        notifyItemRemoved(itemPosition);
                        notifyDataSetChanged();
                        Toast.makeText(context, "A",Toast.LENGTH_LONG).show();
                    }
                    if (object.getString("message").isEmpty()){
                        Toast.makeText(context, object.getString("message"),Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(context, "B",Toast.LENGTH_LONG).show();
                progressDialog.dismiss();

            }, error -> {
                error.printStackTrace();
                Toast.makeText(context, "C",Toast.LENGTH_LONG).show();
                progressDialog.dismiss();

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
                    HashMap<String,String> map = new HashMap<>();
                    map.put("id", postId + "");
                    return map;
                }

            };

            Volley.newRequestQueue(context).add(request);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {

        });
        builder.show();

    }

}
