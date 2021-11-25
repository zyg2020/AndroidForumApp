package com.yangezhu.forumproject.adapter;

import android.content.Context;
import android.text.Layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.yangezhu.forumproject.R;
import com.yangezhu.forumproject.model.Comment;
import com.yangezhu.forumproject.model.Post;
import com.yangezhu.forumproject.utilities.DateUtilities;

import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.ViewHolder> {

    public List<Comment> comments_list;
    private Context mContext;
    private FirebaseFirestore firestore;
    private Picasso picasso;
    public CommentListAdapter(List<Comment> comments_list, Context context){
        this.comments_list = comments_list;
        this.mContext = context;
        this.firestore = FirebaseFirestore.getInstance();
        this.picasso = Picasso.get();
    }

    @NonNull
    @Override
    public CommentListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentListAdapter.ViewHolder holder, int position) {
        firestore.collection("users").document(comments_list.get(position).getUser_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    String avatar_url = document.getString ("avatar");
                    picasso.load(avatar_url).resize(50,50).placeholder(R.drawable.default_avatar).transform(new CropCircleTransformation() ).into(holder.author_avatar);
                }
            }
        });

        Comment current_comment = comments_list.get(position);
        String username = current_comment.getUser_name();
        if (TextUtils.isEmpty(username)){
            holder.comment_username.setText(current_comment.getName());
        }else{
            holder.comment_username.setText(username);
        }

        holder.comment_content.setText(current_comment.getContent());
        holder.comment_date.setText(DateUtilities.timeFormatterWithFullMonthFirst(current_comment.getReply_date()));
    }

    @Override
    public int getItemCount() {
        return comments_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public View itemView;

        public TextView comment_username;
        public TextView comment_content;
        public TextView comment_date;
        public ImageView author_avatar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.itemView = itemView;

            comment_username = (TextView) itemView.findViewById(R.id.comment_username);
            comment_content = (TextView) itemView.findViewById(R.id.comment_content);
            comment_date = (TextView) itemView.findViewById(R.id.comment_date);
            author_avatar = (ImageView) itemView.findViewById(R.id.author_avatar);
        }
    }
}
