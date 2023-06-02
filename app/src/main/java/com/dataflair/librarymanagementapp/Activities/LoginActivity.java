package com.dataflair.librarymanagementapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dataflair.librarymanagementapp.Fragments.UserProfileFragment;
import com.dataflair.librarymanagementapp.MainActivity;
import com.dataflair.librarymanagementapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    EditText mPhoneNumber;
    EditText mPassword;
    CheckBox checkBox;
    Button mLoginButton;
    TextView forgotPasswordTextLink;
    FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_login_for_user);

        firebaseAuth = FirebaseAuth.getInstance();
        mPhoneNumber = (EditText) findViewById(R.id.phoneNumberForLogin);
        mPassword = (EditText) findViewById(R.id.passwordForLogin);
        checkBox = (CheckBox) findViewById(R.id.rememberMeCheckbox);
        mLoginButton = (Button) findViewById(R.id.logInButton);
        forgotPasswordTextLink = (TextView) findViewById(R.id.forgotPasswordReset);
        fAuth = FirebaseAuth.getInstance();

        forgotPasswordTextLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText resetMail = new EditText(v.getContext());
                AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Reset Password ?");
                passwordResetDialog.setMessage("Vendosni email tuaj per te mare linkun e ndryshimit te password");
                passwordResetDialog.setView(resetMail);

                passwordResetDialog.setPositiveButton("Dergo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mail = resetMail.getText().toString();
                        fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(LoginActivity.this,"Linku u dergua me sukses", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginActivity.this,"Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                passwordResetDialog.setNegativeButton("Anullo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                passwordResetDialog.create().show();
            }
        });
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mPhoneNumber.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    mPhoneNumber.setError("Jepni Email");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Jepni Password");
                    return;
                }

                if(password.length() < 6){
                    mPassword.setError("Passwordi duhet te jete me shume se 5 karaktere");
                    return;
                }
                //authenticate the user
                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = user.getUid();
                            if(user.isEmailVerified()){
                                FirebaseDatabase.getInstance().getReference().child("AllUsers").child(uid).addValueEventListener(new ValueEventListener() {
                                    boolean gaveDetailsBefore = false;
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot snapShot2 : snapshot.getChildren()) {
                                            if(snapShot2.getKey().equals("pincode") || snapShot2.getKey().equals("address") || snapShot2.getKey().equals("city")){
                                                gaveDetailsBefore = true;
                                            }
                                            if(gaveDetailsBefore){
                                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("AllUsers").child(uid).child("role");
                                                reference.addValueEventListener(new ValueEventListener() {
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
                            }else{
                                Toast.makeText(LoginActivity.this,"Ju lutem verifikoni emailin tuaj",Toast.LENGTH_SHORT).show();
                            }
                            }else{
                            Toast.makeText(LoginActivity.this,"Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });


    }


}