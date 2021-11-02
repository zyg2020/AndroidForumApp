package com.yangezhu.forumproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.yangezhu.forumproject.R;

import java.util.List;

public class PostImageAdapter extends RecyclerView.Adapter<PostImageAdapter.ViewHolder> {

    public List<String> images_list;
    private Context mContext;

    public PostImageAdapter(List<String> images_list, Context context){
        this.images_list = images_list;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_image_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Picasso.get().load(images_list.get(position)).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return images_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.post_image);
        }
    }
}
