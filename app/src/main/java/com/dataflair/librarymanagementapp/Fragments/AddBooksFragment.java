package com.dataflair.librarymanagementapp.Fragments;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.dataflair.librarymanagementapp.Activities.AdminActivity;
import com.dataflair.librarymanagementapp.Activities.EditBookDetailsActivity;
import com.dataflair.librarymanagementapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

import de.hdodenhof.circleimageview.CircleImageView;


public class AddBooksFragment extends Fragment {

    Button submitBtn;
    ImageView imageView;
    EditText bookNameEditTxt,booksCountEditTxt,booksLocationEditText,bookAuthorEditTxt;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    Uri imageUri;

    int profileChangedCounts = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_add_books, container, false);
        setHasOptionsMenu(true);

        imageView = (ImageView) view.findViewById(R.id.BookImage);
        bookNameEditTxt= (EditText) view.findViewById(R.id.BookNameEditTxt);
        booksCountEditTxt= (EditText) view.findViewById(R.id.TotalBooksEditTxt);
        booksLocationEditText = (EditText) view.findViewById(R.id.BookLocationEditTxt);
        bookAuthorEditTxt = (EditText) view.findViewById(R.id.bookAuthorEditTxt);


        submitBtn = (Button) view.findViewById(R.id.AddBookBtn);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("AllBooks");
        storageReference = FirebaseStorage.getInstance().getReference();


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 2);

            }
        });


            submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String bookName= bookNameEditTxt.getText().toString();
               String booksCount=booksCountEditTxt.getText().toString();
               String bookLocation=booksLocationEditText.getText().toString();
               String bookAuthor=bookAuthorEditTxt.getText().toString();
                if (bookName.isEmpty() || booksCount.isEmpty() || bookLocation.isEmpty() || bookAuthor.isEmpty()) {
                    Toast.makeText(getContext(), "Ju lutem plotësoni të dhënat", Toast.LENGTH_SHORT).show();
                } else if (imageUri == null) {
                    Toast.makeText(getContext(), "Ju lutem zgjidhni një foto", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseDatabase.getInstance().getReference().child("AllBooks")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
               boolean bookAlreadyExists = false;
               @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                String bookNameToCheck = snapshot.child("bookName").getValue().toString();
                if(bookName.toLowerCase(Locale.ROOT).equals(bookNameToCheck.toLowerCase(Locale.ROOT))){
                bookAlreadyExists = true;
                }
                }
                if(!bookAlreadyExists){
                uploadData(imageUri,bookName,booksCount,bookLocation,bookAuthor);
                Toast.makeText(getContext(),"Duke ruajtur të dhënat...",
                        Toast.LENGTH_SHORT).show();
                }else{
                Toast.makeText(getContext(), "Ju nuk mund të vendosni" +
                        " të njëjtin libër 2 herë",Toast.LENGTH_SHORT).show();
                }
                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });

                }
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.toolbar_menu_admin, menu);

        MenuItem item = menu.findItem(R.id.menu_one);
        MenuItem menuItem = menu.findItem(R.id.menu_two);
        View view = MenuItemCompat.getActionView(menuItem);
        CircleImageView profileImage = view.findViewById(R.id.toolbar_profile_image);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        FirebaseDatabase.getInstance().getReference().child("AllUsers").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String profileChangedCountFromFireBase = snapshot.child("profileChangedCount").getValue().toString();
                profileChangedCounts = Integer.parseInt(profileChangedCountFromFireBase);
                if(profileChangedCounts == 0){
                    StorageReference defaultProfileRef = FirebaseStorage.getInstance().getReference().child("users/" + "/profile.jpg");
                    defaultProfileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(profileImage);
                        }
                    });
                }else{
                    StorageReference profileRef = FirebaseStorage.getInstance().getReference().child("users/" + uid +"/profile.jpg");
                    profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(profileImage);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()){
            case R.id.menu_one:
                break;
            case R.id.menu_two:
                break;
            case R.id.menu_shto_librat:
                fragment = new AddBooksFragment();
                break;
            case R.id.menu_ndrysho_librat:
                fragment = new EditBookDetailsActivity();
                break;
            case R.id.menu_kerkesat:
                fragment = new AdminDashBoardFragment();
                break;
            case R.id.menu_librat_aktiv:
                fragment = new ActiveBooksFragment();
                break;
            case R.id.menu_profili:
                fragment = new AdminProfileFragment();
                break;
        }
        getParentFragmentManager().beginTransaction().replace(R.id.AdminFragmentContainer, fragment).commit();
        return super.onOptionsItemSelected(item);
    }

    private void uploadData(Uri imageUri, String bookName, String booksCount, String bookLocation, String bookAuthor) {

        StorageReference fileRef = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
        fileRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String push = databaseReference.push().getKey().toString();

                            HashMap bookDetails = new HashMap();

                            bookDetails.put("bookName", bookName);
                            bookDetails.put("booksCount", booksCount);
                            bookDetails.put("bookLocation", bookLocation);
                            bookDetails.put("bookAuthor", bookAuthor);
                            bookDetails.put("imageUrl", uri.toString());
                            bookDetails.put("pushKey",push);

                            databaseReference.child(push).setValue(bookDetails)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Intent intent = new Intent(getContext(), AdminActivity.class);
                                    getActivity().startActivity(intent);
                                    getActivity().finish();
                                    Toast.makeText(getContext(),"Te dhenat u ruajten me sukses", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {

                            Toast.makeText(getContext(), "Failed To Upload Please,Try Again", Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            }
        });

    }

    private String getFileExtension(Uri imageUri) {

        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String extension = mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));
        return extension;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {

            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

}