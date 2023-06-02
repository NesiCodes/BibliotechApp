package com.dataflair.librarymanagementapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dataflair.librarymanagementapp.Activities.BooksActivity;
import com.dataflair.librarymanagementapp.Model.Model;
import com.dataflair.librarymanagementapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class HomeAdapter extends FirebaseRecyclerAdapter<Model, HomeAdapter.Viewholder> {

    public HomeAdapter(@NonNull FirebaseRecyclerOptions<Model> options) {

        super(options);

    }

    @Override
    protected void onBindViewHolder(@NonNull HomeAdapter.Viewholder holder, int position, @NonNull Model model) {

        String imageUrl=model.getCategoryImage();
        String category=model.getCategory().toString();
        Picasso.get().load(imageUrl).into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(), BooksActivity.class);
                intent.putExtra("category",category);
                view.getContext().startActivity(intent);
            }
        });

    }


    @NonNull
    @Override
    public HomeAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_category_file, parent, false);
        return new HomeAdapter.Viewholder(view);
    }

    class Viewholder extends RecyclerView.ViewHolder {


        ImageView imageView;


        public Viewholder(@NonNull View itemView) {
            super(itemView);

            imageView=(ImageView)itemView.findViewById(R.id.CategoryImage);

        }
    }

}
