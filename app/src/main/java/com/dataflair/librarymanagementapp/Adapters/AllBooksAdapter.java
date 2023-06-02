package com.dataflair.librarymanagementapp.Adapters;

import android.annotation.SuppressLint;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class AllBooksAdapter extends FirebaseRecyclerAdapter<Model, AllBooksAdapter.Viewholder> {


    public AllBooksAdapter(@NonNull FirebaseRecyclerOptions<Model> options) {

        super(options);

    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull AllBooksAdapter.Viewholder holder, int position, @NonNull Model model) {

        holder.bookName.setText("Emri: " + model.getBookName());
        holder.booksCount.setText("Sasia: " + model.getBooksCount());
        holder.bookLocation.setText("Vendndodhja: " + model.getBookLocation());
        holder.bookAuthor.setText("Autori: " + model.getBookAuthor());

        Picasso.get().load(model.getImageUrl()).into(holder.imageView);

        holder.collectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bookLocation = model.getBookLocation();
                String bookName = model.getBookName();
                String booksCount = model.getBooksCount();
                String bookAuthor = model.getBookAuthor();
                String imageUrl = model.getImageUrl();

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String uid = user.getUid();
                FirebaseDatabase.getInstance().getReference().child("AllUsers").child(uid)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                Model model1 = snapshot.getValue(Model.class);
                                String name = model1.getName();
                                String city = model1.getCity();
                                String phoneNumber = model1.getPhoneNumber();
                                String address = model1.getAddress();
                                String pincode = model1.getPincode();
                                String email = model1.getMail();

                                HashMap userDetails=new HashMap();
                                userDetails.put("name",name);
                                userDetails.put("city",city);
                                userDetails.put("phoneNumber",phoneNumber);
                                userDetails.put("address",address);
                                userDetails.put("pincode",pincode);
                                userDetails.put("mail",email);
                                userDetails.put("bookLocation",bookLocation);
                                userDetails.put("bookName",bookName);
                                userDetails.put("booksCount",booksCount);
                                userDetails.put("bookAuthor", bookAuthor);
                                userDetails.put("imageUrl",imageUrl);
                                userDetails.put("userId",uid);

                                String push=FirebaseDatabase.getInstance().getReference().child("OrderedBooks").push().getKey();
                                FirebaseDatabase.getInstance().getReference().child("myOrderedBooks")
                                        .child(uid)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            int booksCountInt = 0;
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                    booksCountInt++;
                                                }
                                                if(booksCountInt==0){
                                                    FirebaseDatabase.getInstance().getReference().child("OrderedBooks").child(push)
                                                            .updateChildren(userDetails)
                                                            .addOnSuccessListener(new OnSuccessListener() {
                                                                @Override
                                                                public void onSuccess(Object o) {

                                                                    FirebaseDatabase.getInstance().getReference().child("myOrderedBooks")
                                                                            .child(uid).child(push)
                                                                            .updateChildren(userDetails)
                                                                            .addOnSuccessListener(new OnSuccessListener() {
                                                                                @Override
                                                                                public void onSuccess(Object o) {
                                                                                    Toast.makeText(view.getContext(), "Libri u kërkua me sukses",Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            });

                                                                }
                                                            });
                                                    Toast.makeText(view.getContext(), "Libri u kërkua me sukses", Toast.LENGTH_SHORT).show();
                                                }else{
                                                    Toast.makeText(view.getContext(), "Ju nuk mund të kërkoni më shumë se 1 libër", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                            }
                                        });

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
    public AllBooksAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_book_layout, parent, false);
        return new AllBooksAdapter.Viewholder(view);
    }

    class Viewholder extends RecyclerView.ViewHolder {


        ImageView imageView;
        TextView bookName, booksCount, bookLocation, bookAuthor;
        Button collectBtn;


        public Viewholder(@NonNull View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.BookImage);
            bookName = (TextView) itemView.findViewById(R.id.BookNameTxt);
            booksCount = (TextView) itemView.findViewById(R.id.BooksCountTxt);
            bookLocation = (TextView) itemView.findViewById(R.id.BooksLocationTxt);
            bookAuthor = (TextView) itemView.findViewById(R.id.bookAuthorEditTxt);

            collectBtn = (Button) itemView.findViewById(R.id.CollectBookBtn);

        }
    }

}

