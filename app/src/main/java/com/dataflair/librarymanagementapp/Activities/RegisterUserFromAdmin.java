package com.dataflair.librarymanagementapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dataflair.librarymanagementapp.Fragments.AdminProfileFragment;
import com.dataflair.librarymanagementapp.Fragments.UserProfileFragment;
import com.dataflair.librarymanagementapp.R;
import com.dataflair.librarymanagementapp.Start;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterUserFromAdmin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user_from_admin);

        EditText mFullName,mEmail,mPassword,mPhone;
        Button mRegisterBtn;
        FirebaseAuth fAuth;
        mFullName = findViewById(R.id.fullName);
        mEmail = findViewById(R.id.emailAddress);
        mPassword = findViewById(R.id.passwordForRegister);
        mPhone = findViewById(R.id.phoneNumberForRegister);
        mRegisterBtn = findViewById(R.id.registerNow);
        String userRole = "User";

        fAuth = FirebaseAuth.getInstance();



        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email is Required");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password is Required");
                    return;
                }

                if(password.length() < 6) {
                    mPassword.setError("Password must be more or equals to 6 characters");
                    return;
                }


                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {

                            HashMap<String, Object> user_details = new HashMap<>();

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = user.getUid();

                            String id = uid;
                            String name = mFullName.getText().toString().trim();
                            String mail = mEmail.getText().toString().trim();
                            String phone = mPhone.getText().toString().trim();
                            String pic = "";

                            user_details.put("id", id);
                            user_details.put("name", name);
                            user_details.put("mail", mail);
                            user_details.put("profilepic", pic);
                            user_details.put("phoneNumber", phone);
                            user_details.put("role", userRole);

                            FirebaseDatabase.getInstance().getReference().child("AllUsers").child(id)
                                    .updateChildren(user_details).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(RegisterUserFromAdmin.this,"Email per verifikim u dergua me sukses", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                System.out.println("Email verification was not sent");

                                            }
                                        });
                                        Toast.makeText(RegisterUserFromAdmin.this,"Perdoruesi u regjistrua me sukses", Toast.LENGTH_SHORT).show();
                                        fAuth.signInWithEmailAndPassword("admin@bibliotech.al","bibliotechadmlogin").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if(task.isSuccessful()){
                                                    Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                System.out.println(e.getMessage());
                                            }
                                        });
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(RegisterUserFromAdmin.this, "Error !" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
}