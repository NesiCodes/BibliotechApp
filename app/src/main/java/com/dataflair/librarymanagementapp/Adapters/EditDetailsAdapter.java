package com.dataflair.librarymanagementapp.Adapters;

import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.dataflair.librarymanagementapp.Activities.BooksActivity;
import com.dataflair.librarymanagementapp.Activities.EditBookDetailsActivity;
import com.dataflair.librarymanagementapp.Model.Model;
import com.dataflair.librarymanagementapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class EditDetailsAdapter extends FirebaseRecyclerAdapter<Model, EditDetailsAdapter.Viewholder> {

    public EditDetailsAdapter(@NonNull FirebaseRecyclerOptions<Model> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull EditDetailsAdapter.Viewholder holder, int position, @NonNull Model model) {
        holder.bookName.setText(model.getBookName());
        holder.booksCount.setText(model.getBooksCount());
        holder.bookLocation.setText(model.getBookLocation());
        holder.bookAuthor.setText(model.getBookAuthor());

        Picasso.get().load(model.getImageUrl()).into(holder.imageView);

        String pushKey=model.getPushKey().toString();

        holder.updateDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bookName=holder.bookName.getText().toString();
                String booksCount=holder.booksCount.getText().toString();
                String bookLocation=holder.bookLocation.getText().toString();
                String bookAuthor=holder.bookAuthor.getText().toString();

                HashMap bookDetails = new HashMap();

                bookDetails.put("bookName", bookName);
                bookDetails.put("booksCount", booksCount);
                bookDetails.put("bookLocation", bookLocation);
                bookDetails.put("bookAuthor", bookAuthor);
                bookDetails.put("pushKey",pushKey);

                FirebaseDatabase.getInstance().getReference().child("AllBooks")
                        .child(pushKey)
                        .updateChildren(bookDetails)
                        .addOnSuccessListener(new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                Toast.makeText(view.getContext(), "Të dhënat u ruajtën me sukses",Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });

    }


    @NonNull
    @Override
    public EditDetailsAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_edit_book_details_layout, parent, false);
        return new EditDetailsAdapter.Viewholder(view);
    }

    class Viewholder extends RecyclerView.ViewHolder {


        ImageView imageView;
        EditText bookName, booksCount, bookLocation, bookAuthor;
        Button updateDetailsBtn;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.BookImage);
            bookName = (EditText) itemView.findViewById(R.id.BookNameTxt);
            booksCount = (EditText) itemView.findViewById(R.id.BooksCountTxt);
            bookLocation = (EditText) itemView.findViewById(R.id.BooksLocationTxt);
            bookAuthor = (EditText) itemView.findViewById(R.id.AuthorNameTxt);
            updateDetailsBtn = (Button) itemView.findViewById(R.id.UpdateDataBtn);
            updateDetailsBtn.setText("Ruaj të dhënat");
        }
    }

}
