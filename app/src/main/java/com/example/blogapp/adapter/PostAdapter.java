package com.example.blogapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blogapp.Api;
import com.example.blogapp.R;
import com.example.blogapp.model.Post;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostsHolder>{

    private Context context;
    private ArrayList<Post> listPost;

    public PostAdapter(Context context, ArrayList<Post> listPost) {
        this.context = context;
        this.listPost = listPost;
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


    }

    @Override
    public int getItemCount() {
        return listPost.size();
    }

    class PostsHolder extends RecyclerView.ViewHolder{

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

        }

    }

}
