package com.dataflair.librarymanagementapp.Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dataflair.librarymanagementapp.Activities.BooksActivity;
import com.dataflair.librarymanagementapp.Model.Model;
import com.dataflair.librarymanagementapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class UserNotificationsAdapter extends FirebaseRecyclerAdapter<Model, UserNotificationsAdapter.Viewholder> {

    public UserNotificationsAdapter(@NonNull FirebaseRecyclerOptions<Model> options) {

        super(options);

    }

    @Override
    protected void onBindViewHolder(@NonNull UserNotificationsAdapter.Viewholder holder, int position, @NonNull Model model) {
        holder.notificationTxt.setText(model.getNotification());
    }


    @NonNull
    @Override
    public UserNotificationsAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_notifications_layout, parent, false);
        return new UserNotificationsAdapter.Viewholder(view);
    }

    class Viewholder extends RecyclerView.ViewHolder {


        TextView notificationTxt;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            notificationTxt=(TextView)itemView.findViewById(R.id.NotificationTxt);
        }
    }

}

