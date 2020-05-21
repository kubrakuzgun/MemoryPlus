package com.group4.memoryv10;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
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


public class EditImageAdapter extends RecyclerView.Adapter<EditImageAdapter.ImageViewHolder> {
    StorageReference storageRef;

    private OnItemClickListener mListener;

    public Uri mUri;

    private Context mContext;
    private List<Memory> mUploads;

    public EditImageAdapter(Context context, List<Memory> uploads) {
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
        View v = LayoutInflater.from(mContext).inflate(R.layout.edit_image_item, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, int position) {
        Memory uploadCurrent = mUploads.get(position);
        holder.textViewPeople.setText(uploadCurrent.getPeople());
        holder.textViewDate.setText(uploadCurrent.getDate());
        holder.textViewPlace.setText(uploadCurrent.getPlace());
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



        Log.d(memourl,"deneme");


    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }



    public class ImageViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
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

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);

        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItemClick(position);
                }
            }

        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("İşlemler");
            MenuItem edit = menu.add(Menu.NONE, 1, 1, "Düzenle");
            MenuItem delete = menu.add(Menu.NONE, 2, 2, "Sil");

            edit.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);

        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {

                    switch (item.getItemId()) {
                        case 1:
                            mListener.onEditClick(position);
                            return true;
                        case 2:
                            mListener.onDeleteClick(position);
                            return true;
                    }
                }
            }
            return false;
        }

    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onEditClick(int position);

        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

}
