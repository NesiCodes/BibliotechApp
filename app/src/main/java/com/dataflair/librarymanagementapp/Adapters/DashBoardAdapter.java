package com.dataflair.librarymanagementapp.Adapters;

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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class DashBoardAdapter extends FirebaseRecyclerAdapter<Model, DashBoardAdapter.Viewholder> {

    public DashBoardAdapter(@NonNull FirebaseRecyclerOptions<Model> options) {

        super(options);

    }

    @Override
    protected void onBindViewHolder(@NonNull DashBoardAdapter.Viewholder holder, int position, @NonNull Model model) {
        holder.bookName.setText("Emri: " + model.getBookName());
        holder.booksCount.setText("Totali: " + model.getBooksCount());
        holder.bookLocation.setText("Vendndodhja: " + model.getBookLocation());
        holder.bookAuthor.setText("Autori: " + model.getBookAuthor());

        Picasso.get().load(model.getImageUrl()).into(holder.imageView);

        holder.collectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String uid = user.getUid();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("myOrderedBooks").child(uid);
                reference.orderByChild("bookName").equalTo(model.getBookName()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String key = ds.getKey();
                            reference.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseDatabase.getInstance().getReference().child("OrderedBooks")
                                                .child(key).removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(view.getContext(), "Kërkesa për librin u anullua", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                }
                            });


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
            }
        });

    }


    @NonNull
    @Override
    public DashBoardAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_book_layout, parent, false);
        return new DashBoardAdapter.Viewholder(view);
    }
    class Viewholder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView bookName, booksCount, bookLocation,bookAuthor;
        Button collectBtn;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.BookImage);
            bookName = (TextView) itemView.findViewById(R.id.BookNameTxt);
            booksCount = (TextView) itemView.findViewById(R.id.BooksCountTxt);
            bookLocation = (TextView) itemView.findViewById(R.id.BooksLocationTxt);
            bookAuthor = (TextView) itemView.findViewById(R.id.bookAuthorEditTxt);
            collectBtn = (Button) itemView.findViewById(R.id.CollectBookBtn);
            collectBtn.setText("Anullo Kërkesën");
        }
    }

}

