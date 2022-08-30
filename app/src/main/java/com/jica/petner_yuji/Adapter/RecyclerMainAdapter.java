package com.jica.petner_yuji.Adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jica.petner_yuji.Activity.MyPageActivity;
import com.jica.petner_yuji.GlideApp;
import com.jica.petner_yuji.R;
import com.jica.petner_yuji.model.Post;
import com.jica.petner_yuji.model.User;


import java.util.ArrayList;


public class RecyclerMainAdapter extends RecyclerView.Adapter<RecyclerMainAdapter.ViewHolder> {
    Context context;
    ArrayList<Post> list_itemArrayList;
    LayoutInflater inflater;
    private FirebaseStorage storage;
    private String userID;

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public RecyclerMainAdapter(ArrayList<Post> list_itemArrayList) {
        this.list_itemArrayList = list_itemArrayList;
    }

    @NonNull
    @Override
    public RecyclerMainAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item, parent, false);
        RecyclerMainAdapter.ViewHolder vh = new RecyclerMainAdapter.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerMainAdapter.ViewHolder holder, int position) {
        holder.userName.setText(list_itemArrayList.get(position).getUserName());
        holder.title.setText(list_itemArrayList.get(position).getTitle());
        holder.content.setText(list_itemArrayList.get(position).getContent());

        String uri = list_itemArrayList.get(position).getImg();
        Log.d("TAG", "이미지 로드 시작:" + uri);
        GlideApp.with(holder.itemView)
                .load(uri)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imageEx);
        Log.d("TAG", "이미지 로드 끝:" + uri);

        userID = list_itemArrayList.get(position).getUserID();
        storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        StorageReference path = storageReference.child("photo/"+userID+".png");
        path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                GlideApp.with(holder.itemView).load(uri)
                        .error(R.drawable.ic_logo).into(holder.profileImage);
            }
        });

    }

    @Override
    public int getItemCount() {
        Log.d("TAG", list_itemArrayList.size() + " " + list_itemArrayList.toString());
        return list_itemArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView userName;
        TextView title;
        TextView content;
        //      TextView liketext;
//      TextView reText;
//      TextView commentEx;
        ImageView profileImage;
        ImageView imageEx;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.userName);
            title = itemView.findViewById(R.id.title);
            content = itemView.findViewById(R.id.content);
            imageEx = itemView.findViewById(R.id.imageEx);
            profileImage = itemView.findViewById(R.id.profileImage);
//          liketext = itemView.findViewById(R.id.likeText);
//          reText = itemView.findViewById(R.id.reText);
//          commentEx  =itemView.findViewById(R.id.commentEx);
        }

    }
}
