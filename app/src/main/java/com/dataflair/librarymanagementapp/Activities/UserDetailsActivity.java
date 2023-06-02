package com.dataflair.librarymanagementapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dataflair.librarymanagementapp.MainActivity;
import com.dataflair.librarymanagementapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;

public class UserDetailsActivity extends AppCompatActivity {

    EditText userPhoneNumber, userAddress, userCity, userPinCode;
    Button addDataBtn;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        userAddress = (EditText) findViewById(R.id.AddressEditText);
        userCity = (EditText) findViewById(R.id.CityEditText);
        userPinCode = (EditText) findViewById(R.id.PinCodeExitText);

        addDataBtn = (Button) findViewById(R.id.UpdateProfileBtn);
        storageReference = FirebaseStorage.getInstance().getReference();

        addDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String address = userAddress.getText().toString().trim();
                String city = userCity.getText().toString().trim();
                String pinCode = userPinCode.getText().toString().trim();


                if (address.isEmpty() || city.isEmpty() || pinCode.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Ju lutem, Plotësoni të gjitha fushat", Toast.LENGTH_SHORT).show();
                } else {
                    addUserDetails(address, city, pinCode);

                }

            }
        });
    }

    private void addUserDetails(String address, String city, String pinCode) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        HashMap userDetails = new HashMap();
        userDetails.put("address", address);
        userDetails.put("city", city);
        userDetails.put("pincode", pinCode);
        userDetails.put("profileChangedCount", "0");

        FirebaseDatabase.getInstance().getReference().child("AllUsers").child(uid)
                .updateChildren(userDetails)
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Të dhënat u ruajtën me sukses", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();


                        }
                    }
                });
    }
}