package com.yangezhu.forumproject.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
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
        ResizeTransformation resizeTransformation = new ResizeTransformation();
        Picasso.get().load(images_list.get(position)).transform(resizeTransformation).into(holder.image);

        holder.item_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int width = holder.image.getWidth();
                int height = holder.image.getHeight();
                Log.d("Item_DIMENSION", "Image - Width:" + width + ", height: " + height);

                width = holder.item_view.getWidth();
                height = holder.item_view.getHeight();

                Log.d("Item_DIMENSION", "item_view - Width:" + width + ", height: " + height);
            }
        });


    }

    @Override
    public int getItemCount() {
        return images_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public View item_view;
        public ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_view = itemView;
            image = (ImageView) itemView.findViewById(R.id.post_image);
        }
    }
}

class ResizeTransformation implements Transformation{

    @Override
    public Bitmap transform(Bitmap source) {
        int target_width = 1080;

        double aspect_ration = (double) source.getHeight()/source.getWidth();
        int target_height = (int) (target_width * aspect_ration);
        Bitmap result = Bitmap.createScaledBitmap(source, target_width, target_height,false);
        if (result != source) {
            // Same bitmap is returned if sizes are the same
            source.recycle();
        }
        return result;
    }

    @Override
    public String key() {
        return "ResizeTransformation";
    }
}
