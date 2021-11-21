package com.yangezhu.forumproject.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.yangezhu.forumproject.R;
import com.yangezhu.forumproject.UpdatePostActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SelectedImagesWhenUpdateAdapter extends RecyclerView.Adapter<SelectedImagesWhenUpdateAdapter.ViewHolder> {

    public List<String> image_uri_list;
    private Context mContext;
    private String post_id;
    private FirebaseFirestore firestore;

    public SelectedImagesWhenUpdateAdapter(List<String> image_uri_list, String post_id, Context context){
        this.image_uri_list = image_uri_list;
        this.mContext = context;
        this.post_id = post_id;
        this.firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public SelectedImagesWhenUpdateAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.selected_image_grid_item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedImagesWhenUpdateAdapter.ViewHolder holder, int position) {
        Picasso.get().load(image_uri_list.get(position)).resize(130, 130).into(holder.image_view_selected_image);
        holder.btn_deselect.setOnClickListener(view -> {
            ProgressDialog progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage("Deleting...");
            progressDialog.show();

            Toast.makeText(mContext, "Deselect", Toast.LENGTH_SHORT).show();

            String url = image_uri_list.get(position);

            DocumentReference post_ref = firestore.collection("posts").document(post_id);
            post_ref.update("images", FieldValue.arrayRemove(url)).addOnCompleteListener(task -> {
                Toast.makeText(mContext, "Delete URL successfully", Toast.LENGTH_SHORT).show();
                image_uri_list.remove(position);
                notifyDataSetChanged();
            });

            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(url);
            storageReference.delete().addOnSuccessListener(unused -> {
                progressDialog.dismiss();
                Toast.makeText(mContext, "Delete Image successfully", Toast.LENGTH_LONG).show();
            }).addOnFailureListener(e -> {
                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("IMAGE_DELTE", e.getMessage());
            });

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
