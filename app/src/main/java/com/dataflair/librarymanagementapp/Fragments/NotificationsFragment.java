package com.dataflair.librarymanagementapp.Fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dataflair.librarymanagementapp.Activities.BooksActivity;
import com.dataflair.librarymanagementapp.Adapters.HomeAdapter;
import com.dataflair.librarymanagementapp.Adapters.UserNotificationsAdapter;
import com.dataflair.librarymanagementapp.Model.Model;
import com.dataflair.librarymanagementapp.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationsFragment extends Fragment {

    RecyclerView recyclerView;
    UserNotificationsAdapter adapter;
    int profileChangedCounts = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_notifications, container, false);
        setHasOptionsMenu(true);


        recyclerView = (RecyclerView) view.findViewById(R.id.NotificationsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        FirebaseRecyclerOptions<Model> options =
                new FirebaseRecyclerOptions.Builder<Model>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("UserNotifications").child(uid), Model.class)
                        .build();


        adapter = new UserNotificationsAdapter(options);
        recyclerView.setAdapter(adapter);
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
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}