package com.dataflair.librarymanagementapp;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dataflair.librarymanagementapp.Activities.AdminActivity;
import com.dataflair.librarymanagementapp.Activities.LoginActivity;
import com.dataflair.librarymanagementapp.Activities.UserDetailsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Start extends AppCompatActivity {

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("Got Here5");
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            System.out.println("Got Here6");
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String uid = user.getUid();
            FirebaseDatabase.getInstance().getReference().child("AllUsers").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                boolean gaveDetailsBefore = false;
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snapShot2 : snapshot.getChildren()) {
                        if (snapShot2.getKey().equals("pincode") || snapShot2.getKey().equals("address") || snapShot2.getKey().equals("city")) {
                            gaveDetailsBefore = true;
                        }
                        if (gaveDetailsBefore) {
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("AllUsers").child(uid).child("role");
                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    String data = snapshot.getValue().toString();

                                    if (data.equals("Admin")) {
                                        Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
                                        startActivity(intent);
                                        finish();

                                    } else {
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }else{
                            startActivity(new Intent(getApplicationContext(), UserDetailsActivity.class));
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });
        }
    }
}
