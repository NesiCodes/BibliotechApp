package com.dataflair.librarymanagementapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActionBarContextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.dataflair.librarymanagementapp.Activities.AdminActivity;
import com.dataflair.librarymanagementapp.Activities.BooksActivity;
import com.dataflair.librarymanagementapp.Activities.EditBookDetailsActivity;
import com.dataflair.librarymanagementapp.Activities.LoginActivity;

import com.dataflair.librarymanagementapp.Activities.UserDetailsActivity;
import com.dataflair.librarymanagementapp.Adapters.AdminDashBoardAdapter;
import com.dataflair.librarymanagementapp.Adapters.AllBooksAdapter;
import com.dataflair.librarymanagementapp.Fragments.DashBoardFragment;
import com.dataflair.librarymanagementapp.Fragments.HomeFragment;
import com.dataflair.librarymanagementapp.Fragments.NotificationsFragment;
import com.dataflair.librarymanagementapp.Fragments.UserProfileFragment;
import com.dataflair.librarymanagementapp.Model.Model;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {


    FrameLayout frameLayout;
    BottomNavigationView bottomNavigationView;
    int profileChangedCounts = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println("Got Here1");


        frameLayout = (FrameLayout) findViewById(R.id.UserFragmentContainer);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.UserBottomNavigationView);
        Menu menuNav = bottomNavigationView.getMenu();
        Fragment fragment = new BooksActivity();
        getSupportFragmentManager().beginTransaction().replace(R.id.UserFragmentContainer, fragment).commit();
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavigationMethod);
    }



    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavigationMethod =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull @org.jetbrains.annotations.NotNull MenuItem item) {
                    Fragment fragment = null;
                    switch (item.getItemId()) {

                        case R.id.HomeMenu:
                            fragment = new BooksActivity();
                            break;

                        case R.id.DashBoardMenu:
                            fragment = new DashBoardFragment();
                            break;

                        case R.id.NotificationsMenu:
                            fragment = new NotificationsFragment();
                            break;

                        case R.id.ProfileMenu:
                            fragment = new UserProfileFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.UserFragmentContainer, fragment).commit();
                    return true;
                }
            };

}