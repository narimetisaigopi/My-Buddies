package com.programmingintelugu.mybuddies.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.programmingintelugu.mybuddies.Models.Post;
import com.programmingintelugu.mybuddies.PostActivity;
import com.programmingintelugu.mybuddies.R;
import com.programmingintelugu.mybuddies.Utilities.FirebaseUtilities;
import com.programmingintelugu.mybuddies.myinterfaces.RefreshList;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecyclerviewPostsAdapter extends RecyclerView.Adapter<RecyclerviewPostsAdapter.PostsViewHolder> {


    Context context;
    List<Post> postList;
    FirebaseUtilities firebaseUtilities;
    RefreshList refreshList;

    public RecyclerviewPostsAdapter(Context context, List<Post> postList, RefreshList refreshList){
        this.context = context;
        this.postList = postList;
        firebaseUtilities = new FirebaseUtilities(context);
        this.refreshList = refreshList;

    }


    @NonNull
    @Override
    public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostsViewHolder(LayoutInflater.from(context).inflate(R.layout.post_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull PostsViewHolder holder, int position) {
        holder.title.setText(postList.get(position).getTitle());
        holder.description.setText(postList.get(position).getDescription());
        Glide.with(context).load(postList.get(position).getImageURL()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    class PostsViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.image)
        ImageView image;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.description)
        TextView description;

        View view;
        public PostsViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            ButterKnife.bind(this,itemView);

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    new AlertDialog.Builder(context).setMessage("Action")
                            .setMessage("Choose Action to perform")
                            .setNegativeButton("Close",null)
                            .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(context, PostActivity.class);
                                    intent.putExtra("data",postList.get(getAdapterPosition()));
                                    context.startActivity(intent);
                                }
                            })
                            .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    firebaseUtilities.deleteImageFirst(null,postList.get(getAdapterPosition()),false);
                                    refreshList.refreshList();
                                }
                            }).create().show();
                    return false;
                }
            });
        }


    }
}
