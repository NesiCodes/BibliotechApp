package com.dataflair.librarymanagementapp.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.dataflair.librarymanagementapp.Activities.AdminActivity;
import com.dataflair.librarymanagementapp.Activities.EditBookDetailsActivity;
import com.dataflair.librarymanagementapp.Activities.LoginActivity;
import com.dataflair.librarymanagementapp.Activities.RegisterUserFromAdmin;
import com.dataflair.librarymanagementapp.Model.Model;
import com.dataflair.librarymanagementapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
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

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class AdminProfileFragment extends Fragment {

    CircleImageView imageView;
    TextView userName;
    TextView perdoruesCount;
    TextView libraCount;
    TextView orderedBooksCount;
    TextView activeBooksCount;
    Button registerUser;
    Button signOutBtn;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    int profileChangedCounts = 0;
    int profileChangedCounts2 = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.activity_new_admin_profile_fragment, container, false);
        setHasOptionsMenu(true);

        imageView = (CircleImageView) view.findViewById(R.id.ProfilePic);
        perdoruesCount = (TextView) view.findViewById(R.id.booking_label);
        libraCount = (TextView) view.findViewById(R.id.payment_label);
        orderedBooksCount = (TextView)  view.findViewById(R.id.ordered_books);
        activeBooksCount = (TextView) view.findViewById(R.id.active_books);
        userName = (TextView) view.findViewById(R.id.UserNameTxt);
        registerUser = (Button) view.findViewById(R.id.registerUserByAdmin);
        signOutBtn = (Button) view.findViewById(R.id.SignOutBtn);
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("AllUsers");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        FirebaseDatabase.getInstance().getReference().child("AllUsers").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String profileChangedCountFromFireBase = snapshot.child("profileChangedCount").getValue().toString();
                int profileChangedCounts2 = Integer.parseInt(profileChangedCountFromFireBase);
                if(profileChangedCounts2 == 0){
                    StorageReference defaultProfileRef = FirebaseStorage.getInstance().getReference().child("users/" + "/profile.jpg");
                    defaultProfileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(imageView);
                        }
                    });
                }else{
                    StorageReference profileRef = FirebaseStorage.getInstance().getReference().child("users/" + uid +"/profile.jpg");
                    profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(imageView);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                Model model = snapshot.getValue(Model.class);
                userName.setText(model.getName());
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
                    int perdoruesNeSistem = 0;
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            perdoruesNeSistem++;
                        }
                        perdoruesCount.setText(String.valueOf(perdoruesNeSistem));
                        perdoruesNeSistem = 0;
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

        FirebaseDatabase.getInstance().getReference().child("AllBooks")
                .addValueEventListener(new ValueEventListener() {
                    int libraNeSistem = 0;
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            libraNeSistem++;
                        }
                        libraCount.setText(String.valueOf(libraNeSistem));
                        libraNeSistem = 0;
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

        FirebaseDatabase.getInstance().getReference().child("OrderedBooks")
                .addValueEventListener(new ValueEventListener() {
                    int orderedBooksNeSistem = 0;
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            orderedBooksNeSistem++;
                        }
                        orderedBooksCount.setText(String.valueOf(orderedBooksNeSistem));
                        orderedBooksNeSistem = 0;
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

        FirebaseDatabase.getInstance().getReference().child("ActiveBooks")
                .addValueEventListener(new ValueEventListener() {
                    int activeBooksNeSistem = 0;
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            activeBooksNeSistem++;
                        }
                        activeBooksCount.setText(String.valueOf(activeBooksNeSistem));
                        activeBooksNeSistem = 0;
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });



        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), LoginActivity.class));
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });

        registerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), RegisterUserFromAdmin.class);
                startActivity(intent);
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
                profileChangedCounts2 = Integer.parseInt(profileChangedCountFromFireBase);
                if(profileChangedCounts2 == 0){
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();
                imageView.setImageURI(imageUri);
                uploadImageToFirebase(imageUri);
            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        StorageReference fileRef = storageReference.child("users/" + uid +"/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(imageView);
                    }
                });
                Toast.makeText(getContext(),"Fotoja u shtua me sukses", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"Error", Toast.LENGTH_SHORT).show();
            }
        });

        FirebaseDatabase.getInstance().getReference().child("AllUsers")
                .child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot SnapShotData) {
                String profileChangedCountFromFireBase = SnapShotData.child("profileChangedCount").getValue().toString();
                profileChangedCounts = Integer.parseInt(profileChangedCountFromFireBase);
                profileChangedCounts += 1;
                HashMap userDetails = new HashMap();
                userDetails.put("profileChangedCount", String.valueOf(profileChangedCounts));
                databaseReference.child(uid).updateChildren(userDetails).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            System.out.println("Sukses");
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}