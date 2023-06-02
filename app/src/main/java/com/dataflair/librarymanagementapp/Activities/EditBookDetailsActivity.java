package com.dataflair.librarymanagementapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.dataflair.librarymanagementapp.Adapters.AllBooksAdapter;
import com.dataflair.librarymanagementapp.Adapters.EditDetailsAdapter;
import com.dataflair.librarymanagementapp.Fragments.ActiveBooksFragment;
import com.dataflair.librarymanagementapp.Fragments.AddBooksFragment;
import com.dataflair.librarymanagementapp.Fragments.AdminDashBoardFragment;
import com.dataflair.librarymanagementapp.Fragments.AdminProfileFragment;
import com.dataflair.librarymanagementapp.Fragments.UserProfileFragment;
import com.dataflair.librarymanagementapp.Model.Model;
import com.dataflair.librarymanagementapp.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditBookDetailsActivity extends Fragment {

    RecyclerView recyclerView;
    EditDetailsAdapter adapter;
    int profileChangedCounts = 0;

@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
                         Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.activity_edit_book_details, container, false);
    setHasOptionsMenu(true);


    recyclerView = (RecyclerView) view.findViewById(R.id.EditBookDetailsRecyclerView);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    FirebaseRecyclerOptions<Model> options =
            new FirebaseRecyclerOptions.Builder<Model>()
                    .setQuery(FirebaseDatabase.getInstance().getReference().child("AllBooks"), Model.class)
                    .build();


    adapter = new EditDetailsAdapter(options);
    recyclerView.setAdapter(adapter);
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

        SearchView searchView=(SearchView)item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                txtSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                txtSearch(query);
                return false;
            }
        });

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

    private void txtSearch(String str){
        FirebaseRecyclerOptions<Model> options =
                new FirebaseRecyclerOptions.Builder<Model>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("AllBooks").orderByChild("bookName").startAt(str).endAt(str+"~"), Model.class)
                        .build();

        adapter = new EditDetailsAdapter(options);
        adapter.startListening();
        recyclerView.setAdapter(adapter);
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