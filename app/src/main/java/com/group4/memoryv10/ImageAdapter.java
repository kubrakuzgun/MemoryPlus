package com.group4.memoryv10;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;


public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    StorageReference storageRef;


    public Uri mUri;

    private Context mContext;
    private List<Memory> mUploads;

    public ImageAdapter(Context context, List<Memory> uploads) {
        mContext = context;
        mUploads = uploads;
    }
    public Uri getmUri() {
        return mUri;
    }

    public void setmUri(Uri mUri) {
        this.mUri = mUri;
    }


    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.image_item, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, int position) {
        Memory uploadCurrent = mUploads.get(position);
        holder.textViewPeople.setText("Fotoğraftakiler: " + uploadCurrent.getPeople());
        holder.textViewDate.setText("Tarih: " + uploadCurrent.getDate());
        holder.textViewPlace.setText("Mekan: " +uploadCurrent.getPlace());
        storageRef= FirebaseStorage.getInstance().getReference();

        String memourl = uploadCurrent.getMemoURL();
        String userid = uploadCurrent.getUserid();

        storageRef.child("Users/" + userid + "/Memories/" + memourl).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(mContext).load(uri.toString()).centerCrop().into(holder.imageView);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // File not found
            }
        });



    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewPeople;
        public TextView textViewDate;
        public TextView textViewPlace;
        public ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);

            textViewPeople = itemView.findViewById(R.id.text_view_people);
            textViewDate = itemView.findViewById(R.id.text_view_date);
            textViewPlace = itemView.findViewById(R.id.text_view_place);

            imageView = itemView.findViewById(R.id.image_view_upload);
        }
    }
}
