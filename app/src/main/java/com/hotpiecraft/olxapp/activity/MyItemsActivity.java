package com.hotpiecraft.olxapp.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.AdapterView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.hotpiecraft.olxapp.R;
import com.hotpiecraft.olxapp.adapter.AdsAdapter;
import com.hotpiecraft.olxapp.config.FirebaseConfig;
import com.hotpiecraft.olxapp.model.ItemModel;
import com.hotpiecraft.olxapp.util.Base64Custom;
import com.hotpiecraft.olxapp.util.Constants;
import com.hotpiecraft.olxapp.util.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class MyItemsActivity extends AppCompatActivity {

    private RecyclerView recyclerAds;
    private List<ItemModel> ads = new ArrayList<>();
    private AdsAdapter adapter;
    private ValueEventListener valueEventListener;
    private DatabaseReference myAdsRef;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_items_acitivy);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeComponents();
        configureFab();
        configureRecyclerView();
        setListeners();
    }

    private void setListeners() {
        recyclerAds.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerAds, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onLongItemClick(View view, int position) {
                ItemModel itemSelected = ads.get(position);
                itemSelected.removeItem();

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));
    }

    private void initializeComponents() {
        recyclerAds = findViewById(R.id.recyclerMyItems);

    }

    private void configureFab() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), RegisterItemActivity.class);
                startActivity(i);
            }
        });
    }

    private void configureRecyclerView() {

        adapter = new AdsAdapter(getApplicationContext(), ads);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerAds.setLayoutManager(layoutManager);
        recyclerAds.setHasFixedSize(true);
        recyclerAds.setAdapter(adapter);

        recoverAds();
    }

    private void recoverAds() {
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Recovering Ads")
                .setCancelable(false)
                .build();
        dialog.show();

        FirebaseAuth authentication = FirebaseConfig.getFirebaseAuth();
        String email = authentication.getCurrentUser().getEmail();
        String userId = Base64Custom.encodeString(email);
        myAdsRef = FirebaseConfig.getFirebaseDB().child(Constants.MY_ADS).child(userId);

        valueEventListener = myAdsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ads.clear();

                for(DataSnapshot ds: snapshot.getChildren()) {
                    ItemModel itemModel = ds.getValue(ItemModel.class);
                    ads.add(itemModel);
                }

                Collections.reverse(ads);
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        myAdsRef.removeEventListener(valueEventListener);
    }
}