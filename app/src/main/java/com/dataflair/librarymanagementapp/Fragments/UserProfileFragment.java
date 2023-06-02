package com.dataflair.librarymanagementapp.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dataflair.librarymanagementapp.Activities.BooksActivity;
import com.dataflair.librarymanagementapp.Activities.LoginActivity;
import com.dataflair.librarymanagementapp.MainActivity;
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
import java.util.zip.Inflater;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserProfileFragment extends Fragment {

    CircleImageView circleImageView;
    TextView userNameTxt;
    EditText phoneNumberEditTxt, addressEditTxt, cityNameEditTxt, emailAddressEditTxt;
    Button signOutBtn, updateDetailsBtn, resetPassword;
    DatabaseReference databaseReference;
    String uid;
    StorageReference storageReference;
    int profileChangedCounts = 0;
    int profileChangedCounts2 = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_new_user_profile_fragment, container, false);
        setHasOptionsMenu(true);

        circleImageView = (CircleImageView) view.findViewById(R.id.profile_image);
        userNameTxt = (TextView) view.findViewById(R.id.full_name);
        phoneNumberEditTxt = (EditText) view.findViewById(R.id.numriTekst);
        addressEditTxt = (EditText) view.findViewById(R.id.adresaTekst);
        cityNameEditTxt = (EditText) view.findViewById(R.id.qytetiTekst);
        emailAddressEditTxt = (EditText) view.findViewById(R.id.emailTekst);

        resetPassword = (Button) view.findViewById(R.id.changePassword);
        updateDetailsBtn = (Button) view.findViewById(R.id.ruajNdryshimet);
        signOutBtn = (Button) view.findViewById(R.id.logOutButton);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        storageReference = FirebaseStorage.getInstance().getReference();

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
                            Picasso.get().load(uri).into(circleImageView);
                        }
                    });
                }else{
                    StorageReference profileRef = FirebaseStorage.getInstance().getReference().child("users/" + uid +"/profile.jpg");
                    profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(circleImageView);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference().child("AllUsers");

            databaseReference.child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                    Model model = snapshot.getValue(Model.class);
                    userNameTxt.setText(model.getName());
                    cityNameEditTxt.setText(model.getCity());
                    phoneNumberEditTxt.setText(model.getPhoneNumber());
                    addressEditTxt.setText(model.getAddress());
                    emailAddressEditTxt.setText(model.getPincode());
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });

            resetPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseDatabase.getInstance().getReference().child("AllUsers").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String email = snapshot.child("mail").getValue().toString();
                            FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(view.getContext(),"Linku u dergua me sukses", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(view.getContext(),"Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            });
        updateDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = phoneNumberEditTxt.getText().toString();
                String cityName = cityNameEditTxt.getText().toString();
                String pinCode = emailAddressEditTxt.getText().toString();
                String address = addressEditTxt.getText().toString();

                if (phoneNumber.isEmpty() || cityName.isEmpty() || pinCode.isEmpty() || address.isEmpty()) {
                    Toast.makeText(getContext(), "Plotësoni të gjitha fushat", Toast.LENGTH_SHORT).show();
                } else {
                    updateDetails(phoneNumber, cityName, pinCode, address, uid);
                }
            }
        });

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 500);
            }
        });

        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), LoginActivity.class));
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.toolbar_menu, menu);

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
            case R.id.menu_librat:
                fragment = new BooksActivity();
                break;
            case R.id.menu_user_kerkesat:
                fragment = new DashBoardFragment();
                break;
            case R.id.menu_notifications:
                fragment = new NotificationsFragment();
                break;
            case R.id.menu_user_profili:
                fragment = new UserProfileFragment();
                break;
        }
        getParentFragmentManager().beginTransaction().replace(R.id.UserFragmentContainer, fragment).commit();
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 500){
            if(resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();
                circleImageView.setImageURI(imageUri);
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
                        Picasso.get().load(uri).into(circleImageView);
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

    private void updateDetails(String phoneNumber, String cityName, String pinCode, String address, String userId) {
        HashMap userDetails = new HashMap();

        userDetails.put("phoneNumber", phoneNumber);
        userDetails.put("city", cityName);
        userDetails.put("pincode", pinCode);
        userDetails.put("address", address);
        userDetails.put("id", userId);

        databaseReference.child(userId).updateChildren(userDetails).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull @NotNull Task task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Të dhënat u ruajtën me sukses", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Ju lutem provoni përsëri", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}