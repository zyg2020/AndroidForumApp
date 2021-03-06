package com.yangezhu.forumproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.yangezhu.forumproject.PostDetailsWithCommentsActivity;
import com.yangezhu.forumproject.R;
import com.yangezhu.forumproject.UpdatePostActivity;
import com.yangezhu.forumproject.model.Post;
import com.yangezhu.forumproject.utilities.DateUtilities;

import java.util.List;

public class MyPostsListAdapter extends RecyclerView.Adapter<MyPostsListAdapter.ViewHolder> {

    public List<Post> posts_list;
    private Context mContext;
    private FirebaseFirestore firestore;

    public static String SELECTED_POST = "SELECTED_POST";

    public MyPostsListAdapter(List<Post> posts_list, Context context){
        this.posts_list = posts_list;
        this.mContext = context;
        firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public MyPostsListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_posts_entry_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyPostsListAdapter.ViewHolder holder, int position) {

        // set text color
        String text_color = load_text_color();
        holder.post_title.setTextColor(Color.parseColor(text_color));
        holder.post_date.setTextColor(Color.parseColor(text_color));
        holder.post_description.setTextColor(Color.parseColor(text_color));

        String btn_text_color = load_btn_text_color();
        holder.btn_delete_post.setTextColor(Color.parseColor(btn_text_color));
        holder.btn_update_post.setTextColor(Color.parseColor(btn_text_color));

        // set text fize
        float font_size = load_font_size_settings();
        // holder.post_title.setTextSize(font_size);
        holder.post_date.setTextSize(font_size);
        holder.post_description.setTextSize(font_size);

        holder.post_title.setText(posts_list.get(position).getTitle());
        holder.post_date.setText(DateUtilities.timeFormatter(posts_list.get(position).getPublish_date()));
        Post selected_post = posts_list.get(position);
        String description = posts_list.get(position).getDescription();

        if (description.length() > 100){
            description = description.substring(0, 100) + "...";
        }

        holder.post_description.setText(description);

        holder.btn_delete_post.setOnClickListener(view -> {
            posts_list.remove(position);
            notifyDataSetChanged();
            firestore.collection("posts").document(selected_post.getPost_id())
                    .delete()
                    .addOnSuccessListener(unused -> {
                          int sd = position;
//                        posts_list.remove(position);
//                        notifyItemRemoved(position);
                        // Toast.makeText(mContext, "Delete successfully ", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                Toast.makeText(mContext, "Error deleting document", Toast.LENGTH_SHORT).show();
            });
        });

        holder.btn_update_post.setOnClickListener(view -> {
            Toast.makeText(mContext, "Update clicked", Toast.LENGTH_SHORT).show();
            Gson gson = new Gson();
            String stringifiedPost = gson.toJson(posts_list.get(position));

            Intent intent = new Intent(mContext, UpdatePostActivity.class);
            intent.putExtra(SELECTED_POST, stringifiedPost);
            intent.putExtra("Activity", "FROM_MY_POSTS_LIST");
            mContext.startActivity(intent);
        });

        holder.itemView.setOnClickListener(view -> {
            Gson gson = new Gson();
            String stringifiedPost = gson.toJson(posts_list.get(position));
            Log.d("YZHU_CLICK_ITEM", "Item clicked: " + stringifiedPost);

            Intent intent = new Intent(mContext, PostDetailsWithCommentsActivity.class);
            intent.putExtra(SELECTED_POST, stringifiedPost);
            intent.putExtra("Activity", "FROM_MY_POSTS");
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return posts_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public View itemView;

        public TextView post_title;
        public TextView post_description;
        public TextView post_date;
        public Button btn_update_post;
        public Button btn_delete_post;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;

            post_title = (TextView) itemView.findViewById(R.id.post_title);
            post_description = (TextView) itemView.findViewById(R.id.post_description);
            post_date = (TextView) itemView.findViewById(R.id.post_date);

            btn_update_post = (Button) itemView.findViewById(R.id.btn_update_post);
            btn_delete_post = (Button) itemView.findViewById(R.id.btn_delete_post);
        }
    }

    private String load_text_color(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);

        boolean chk_night = sp.getBoolean("NIGHT", false);
        String text_color = "";
        if (chk_night){
            text_color = "#b5b5b5";
        }else{
            text_color = "#333333";
        }
        return text_color;
    }

    private String load_btn_text_color(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);

        boolean chk_night = sp.getBoolean("NIGHT", false);
        String text_color = "";
        if (chk_night){
            text_color = "#b5b5b5";
        }else{
            text_color = "#ffffff";
        }
        return text_color;
    }

    private float load_font_size_settings(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        String font_size = sp.getString("FONT_SIZE", "false");
        float settings_font_size = 10;
        if ("Small".equals(font_size)){
            settings_font_size=12;
        }else if ("Medium".equals(font_size)){
            settings_font_size=16;
        }else if ("Large".equals(font_size)){
            settings_font_size=20;
        }

        return settings_font_size;
    }
}
