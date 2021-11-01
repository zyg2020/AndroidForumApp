package com.yangezhu.forumproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.util.Printer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.auth.User;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.yangezhu.forumproject.PostDetailsWithCommentsActivity;
import com.yangezhu.forumproject.R;
import com.yangezhu.forumproject.model.Post;

import java.util.List;

public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.ViewHolder> {

    public List<Post> posts_list;
    private Context mContext;

    public static String SELECTED_POST = "SELECTED_POST";

    public PostListAdapter(List<Post> posts_list, Context context){
        this.posts_list = posts_list;
        this.mContext = context;
    }

    @NonNull
    @Override
    public PostListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostListAdapter.ViewHolder holder, int position) {
        holder.post_username.setText(posts_list.get(position).getUser_name());
        holder.post_title.setText(posts_list.get(position).getTitle());
        holder.post_date.setText(posts_list.get(position).getPublish_date().toString());
        String description = posts_list.get(position).getDescription();

        if (description.length() > 100){
            description = description.substring(0, 100) + "...";
        }

        holder.post_description.setText(description);

        List<String> images_list = posts_list.get(position).getImages();
        holder.image_left.setVisibility(View.GONE);
        holder.image_right.setVisibility(View.GONE);

        if (images_list.size() > 0){
            holder.image_left.setVisibility(View.VISIBLE);
            Picasso.get().load(images_list.get(0)).into(holder.image_left);
            if (images_list.size()>1){
                holder.image_right.setVisibility(View.VISIBLE);
                Picasso.get().load(images_list.get(1)).into(holder.image_right);
            }
        }

        holder.itemView.setOnClickListener(view -> {
            Gson gson = new Gson();
            String stringifiedPost = gson.toJson(posts_list.get(position));
            Log.d("YZHU_CLICK_ITEM", "Item clicked: " + stringifiedPost);

            Intent intent = new Intent(mContext, PostDetailsWithCommentsActivity.class);
            intent.putExtra(SELECTED_POST, stringifiedPost);
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return posts_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public View itemView;

        public TextView post_username;
        public TextView post_title;
        public TextView post_description;
        public TextView post_date;
        public ImageView image_left;
        public ImageView image_right;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;

            post_username = (TextView) itemView.findViewById(R.id.post_username);
            post_title = (TextView) itemView.findViewById(R.id.post_title);
            post_description = (TextView) itemView.findViewById(R.id.post_description);
            post_date = (TextView) itemView.findViewById(R.id.post_date);

            image_left = (ImageView) itemView.findViewById(R.id.post_image);
            image_right = (ImageView) itemView.findViewById(R.id.post_image2);
        }
    }
}
