package com.group4.memoryv10;

import android.content.Context;
import android.net.Uri;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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


public class EditImageAdapter extends RecyclerView.Adapter<EditImageAdapter.ImageViewHolder> {
    StorageReference storageRef;
    private OnItemClickListener mListener;
    private Context mContext;
    private List<Memory> mUploads;

    //Constructor
    public EditImageAdapter(Context context, List<Memory> uploads) {
        mContext = context;
        mUploads = uploads;
    }

    //creates view holder
    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.edit_image_item, parent, false);
        return new ImageViewHolder(v);
    }

    //insert each memory into a viewholder
    @Override
    public void onBindViewHolder(final ImageViewHolder holder, int position) {
        //get memories one by one
        Memory uploadCurrent = mUploads.get(position);

        //retrieve memory info from firebase database and insert into textview holders
        holder.textViewPeople.setText(uploadCurrent.getPeople());
        holder.textViewDate.setText(uploadCurrent.getDate());
        holder.textViewPlace.setText(uploadCurrent.getPlace());

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
    public class ImageViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener, View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
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

            //set memory view a click listener to get item's position
            itemView.setOnClickListener(this);

            //create edit menu
            itemView.setOnCreateContextMenuListener(this);
        }

        //when clicked to a memory view
        @Override
        public void onClick(View v) {
            //if view has a click listener
            if (mListener != null) {
                //get position of clicked item
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    //call item click function
                    mListener.onItemClick(position);
                }
            }
        }

        //edit menu
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("İşlemler");
            MenuItem edit = menu.add(Menu.NONE, 1, 1, "Düzenle");
            MenuItem delete = menu.add(Menu.NONE, 2, 2, "Sil");

            //set click lisener to edit option
            edit.setOnMenuItemClickListener(this);

            //set click lisener to delete option
            delete.setOnMenuItemClickListener(this);
        }

        //when an edit menu item is clicked
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            //if somewhere on the menu is clicked
            if (mListener != null) {
                //get click position
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    //get clicked item's id
                    switch (item.getItemId()) {
                        //id is 1 - edit memory
                        case 1:
                            mListener.onEditClick(position);
                            return true;
                        //id is 2 - delete memory
                        case 2:
                            mListener.onDeleteClick(position);
                            return true;
                    }
                }
            }
            return false;
        }

    }

    //interface for item click, edit and delete operations
    public interface OnItemClickListener {
        void onItemClick(int position);

        void onEditClick(int position);

        void onDeleteClick(int position);
    }

    //set item click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

}
