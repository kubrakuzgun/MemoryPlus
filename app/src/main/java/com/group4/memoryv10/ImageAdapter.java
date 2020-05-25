package com.group4.memoryv10;

import android.content.Context;
import android.net.Uri;
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
    private Context mContext;
    private List<Memory> mUploads;


    //Constructor
    public ImageAdapter(Context context, List<Memory> uploads) {
        mContext = context;
        mUploads = uploads;
    }

    //creates view holder
    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.image_item, parent, false);
        return new ImageViewHolder(v);
    }

    //insert each memory into a viewholder
    @Override
    public void onBindViewHolder(final ImageViewHolder holder, int position) {
        //get memories one by one
        Memory uploadCurrent = mUploads.get(position);

        //retrieve memory info from firebase database and insert into textview holders
        holder.textViewPeople.setText("Fotoğraftakiler: " + uploadCurrent.getPeople());
        holder.textViewDate.setText("Tarih: " + uploadCurrent.getDate());
        holder.textViewPlace.setText("Mekan: " +uploadCurrent.getPlace());

        //get firebase storage reference
        storageRef= FirebaseStorage.getInstance().getReference();
        String memourl = uploadCurrent.getMemoURL();
        String userid = uploadCurrent.getUserid();

        //retrieve memory photo from firebase storage and insert into image holder
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

    //get number of memories
    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    //layout template for a memory (like a CardView)
    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewPeople;
        public TextView textViewDate;
        public TextView textViewPlace;
        public ImageView imageView;

        //create an ImageView to insert memory photo and TextViews to insert memory info(people, date, place)
        public ImageViewHolder(View itemView) {
            super(itemView);
            textViewPeople = itemView.findViewById(R.id.text_view_people);
            textViewDate = itemView.findViewById(R.id.text_view_date);
            textViewPlace = itemView.findViewById(R.id.text_view_place);
            imageView = itemView.findViewById(R.id.image_view_upload);
        }
    }
}
