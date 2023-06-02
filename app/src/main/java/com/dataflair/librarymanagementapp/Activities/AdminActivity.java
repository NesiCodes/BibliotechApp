package com.dataflair.librarymanagementapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.dataflair.librarymanagementapp.Adapters.ActiveBooksAdapter;
import com.dataflair.librarymanagementapp.Adapters.AdminDashBoardAdapter;
import com.dataflair.librarymanagementapp.Fragments.ActiveBooksFragment;
import com.dataflair.librarymanagementapp.Fragments.AddBooksFragment;
import com.dataflair.librarymanagementapp.Fragments.AdminDashBoardFragment;
import com.dataflair.librarymanagementapp.Fragments.AdminProfileFragment;
import com.dataflair.librarymanagementapp.MainActivity;
import com.dataflair.librarymanagementapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.sql.SQLOutput;

public class AdminActivity extends AppCompatActivity {

    FrameLayout frameLayout;
    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        frameLayout = (FrameLayout) findViewById(R.id.AdminFragmentContainer);
        Toolbar toolbar3 = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar3);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.AdminBottomNavigationView);
        Menu menuNav = bottomNavigationView.getMenu();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        getSupportFragmentManager().beginTransaction().replace(R.id.AdminFragmentContainer, new AddBooksFragment()).commit();
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavigationMethod);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 80 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            AdminDashBoardAdapter.sendMessage();
        }else if(requestCode == 60 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            ActiveBooksAdapter.sendMessage();
        }
        else{
            Toast.makeText(AdminActivity.this,"Permission Denied",Toast.LENGTH_SHORT).show();
        }
    }



    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavigationMethod =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull @org.jetbrains.annotations.NotNull MenuItem item) {
                    System.out.println("Printed on Admin Actibity3");
                    Fragment fragment = null;
                    switch (item.getItemId()) {

                        case R.id.AddBookMenu:
                            fragment = new AddBooksFragment();
                            break;
                        case R.id.EditDetailsMenu:
                            fragment = new EditBookDetailsActivity();
                            break;
                        case R.id.DashBoardMenu:
                            fragment = new AdminDashBoardFragment();
                            break;
                        case R.id.ViewGivenBooks:
                            fragment = new ActiveBooksFragment();
                            break;
                        case R.id.AdminProfile:
                            fragment = new AdminProfileFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.AdminFragmentContainer, fragment).commit();
                    return true;
                }
            };

}