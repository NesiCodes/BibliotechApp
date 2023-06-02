package com.dataflair.librarymanagementapp.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dataflair.librarymanagementapp.Adapters.HomeAdapter;
import com.dataflair.librarymanagementapp.Model.Model;
import com.dataflair.librarymanagementapp.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;


public class HomeFragment extends Fragment {


    RecyclerView recyclerView;
    HomeAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.HomeRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        FirebaseRecyclerOptions<Model> options =
                new FirebaseRecyclerOptions.Builder<Model>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("TotalBooksCategories"), Model.class)
                        .build();


        adapter = new HomeAdapter(options);
        recyclerView.setAdapter(adapter);
        return view;
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