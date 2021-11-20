package com.yangezhu.forumproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.yangezhu.forumproject.PostDetailsWithCommentsActivity;
import com.yangezhu.forumproject.R;
import com.yangezhu.forumproject.model.Post;
import com.yangezhu.forumproject.utilities.DateUtilities;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class SelectedImagesAdapter extends RecyclerView.Adapter<SelectedImagesAdapter.ViewHolder> {

    public ArrayList<Uri> image_uri_list;
    private Context mContext;

    public SelectedImagesAdapter(ArrayList<Uri> image_uri_list, Context context){
        this.image_uri_list = image_uri_list;
        this.mContext = context;
    }

    @NonNull
    @Override
    public SelectedImagesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.selected_image_grid_item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedImagesAdapter.ViewHolder holder, int position) {
        Picasso.get().load(image_uri_list.get(position)).resize(130, 130).into(holder.image_view_selected_image);
        holder.btn_deselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "Deselect", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return image_uri_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public View itemView;

        public ImageButton btn_deselect;
        public ImageView image_view_selected_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.btn_deselect = itemView.findViewById(R.id.btn_deselect);
            this.image_view_selected_image = itemView.findViewById(R.id.image_view_selected_image);
        }
    }
}
