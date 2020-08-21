package com.hotpiecraft.olxapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.hotpiecraft.olxapp.R;
import com.hotpiecraft.olxapp.adapter.AdsAdapter;
import com.hotpiecraft.olxapp.config.FirebaseConfig;
import com.hotpiecraft.olxapp.model.ItemModel;
import com.hotpiecraft.olxapp.util.Constants;
import com.hotpiecraft.olxapp.util.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Button buttonCategory, buttonDistrict;
    private RecyclerView recyclerPublicAds;
    private AdsAdapter adapter;
    private List<ItemModel> publicAdsList = new ArrayList<>();
    private AlertDialog dialog;
    private DatabaseReference adRef;
    private ValueEventListener valueEventListener;
    private String filterDistrict = "";
    private String filterCategory = "";
    private boolean filterByDistrict = false;

    private FirebaseAuth authentication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeComponents();
        configureToolbar();
        retrievePublicAds();
        configureRecyclerView();
        setListeners();

    }

    private void setListeners() {
        buttonDistrict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterByDistrict();
            }
        });
        buttonCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterByCategory();
            }
        });

        recyclerPublicAds.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerPublicAds, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ItemModel item = publicAdsList.get(position);
                Intent i = new Intent(getApplicationContext(), ItemViewActivity.class);
                i.putExtra("adInfo", item);
                startActivity(i);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));
    }


    private void initializeComponents() {
        authentication = FirebaseConfig.getFirebaseAuth();
        toolbar = findViewById(R.id.toolbarCustom);
        buttonCategory = findViewById(R.id.buttonCategory);
        buttonDistrict = findViewById(R.id.buttonDistrict);
        recyclerPublicAds = findViewById(R.id.recyclerPublicAds);
        adRef = FirebaseConfig.getFirebaseDB().child(Constants.ADS_DB);
    }

    private void configureToolbar() {
        toolbar.setTitle("OLX App");
        setSupportActionBar(toolbar);
    }

    private void configureRecyclerView() {

        adapter = new AdsAdapter(getApplicationContext(), publicAdsList);

        recyclerPublicAds.setAdapter(adapter);
        recyclerPublicAds.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerPublicAds.setHasFixedSize(true);
    }

    private void retrievePublicAds() {
        publicAdsList.clear();

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Recovering Ads")
                .setCancelable(false)
                .build();
        dialog.show();

        adRef = FirebaseConfig.getFirebaseDB().child(Constants.ADS_DB);

        valueEventListener = adRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot districts : snapshot.getChildren()) {
                    for (DataSnapshot categories : districts.getChildren()) {
                        for (DataSnapshot ds : categories.getChildren()) {
                            ItemModel itemModel = ds.getValue(ItemModel.class);
                            publicAdsList.add(itemModel);
                        }
                    }
                }

                Collections.reverse(publicAdsList);
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void recoverAdsByDistrict() {

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Recovering Ads")
                .setCancelable(false)
                .build();
        dialog.show();

        adRef = FirebaseConfig.getFirebaseDB().child(Constants.ADS_DB).child(filterDistrict);

        adRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                publicAdsList.clear();
                for (DataSnapshot categories : snapshot.getChildren()) {
                    for (DataSnapshot ads : categories.getChildren()) {
                        ItemModel itemModel = ads.getValue(ItemModel.class);
                        publicAdsList.add(itemModel);
                    }
                }

                Collections.reverse(publicAdsList);
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void recoverAdsByCategory() {
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Recovering Ads")
                .setCancelable(false)
                .build();
        dialog.show();

        adRef = FirebaseConfig.getFirebaseDB()
                .child(Constants.ADS_DB)
                .child(filterDistrict)
                .child(filterCategory);

        adRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                publicAdsList.clear();

                for (DataSnapshot ads : snapshot.getChildren()) {
                        ItemModel itemModel = ads.getValue(ItemModel.class);
                        publicAdsList.add(itemModel);
                    }

                Collections.reverse(publicAdsList);
                adapter.notifyDataSetChanged();
                dialog.dismiss();
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void filterByDistrict() {
        AlertDialog.Builder dialogDistrict = new AlertDialog.Builder(this);
        dialogDistrict.setTitle("Select a district");
        View viewSpinner = View.inflate(this, R.layout.dialog_spinner, null);
        dialogDistrict.setView(viewSpinner);

        final Spinner spinner = viewSpinner.findViewById(R.id.spinnerFilter);
        String[] district = getResources().getStringArray(R.array.estados);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, district);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.getBackground().setColorFilter(getResources().getColor(R.color.colorBlack), PorterDuff.Mode.SRC_ATOP);
        spinner.setAdapter(adapter);

        dialogDistrict.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                filterDistrict = spinner.getSelectedItem().toString();
                buttonDistrict.setText(filterDistrict);
                recoverAdsByDistrict();
                filterByDistrict = true;
            }
        });

        dialogDistrict.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = dialogDistrict.create();
        dialog.show();
    }

    public void filterByCategory() {

        if (filterByDistrict) {
            AlertDialog.Builder dialogCategory = new AlertDialog.Builder(this);
            dialogCategory.setTitle("Select a category");
            View viewSpinner = View.inflate(this, R.layout.dialog_spinner, null);
            dialogCategory.setView(viewSpinner);

            final Spinner spinnerCategory = viewSpinner.findViewById(R.id.spinnerFilter);
            String[] category = getResources().getStringArray(R.array.categoria);
            ArrayAdapter<String> adapterCategory = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, category);
            adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCategory.getBackground().setColorFilter(getResources().getColor(R.color.colorBlack), PorterDuff.Mode.SRC_ATOP);
            spinnerCategory.setAdapter(adapterCategory);

            dialogCategory.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    filterCategory = spinnerCategory.getSelectedItem().toString();
                    buttonCategory.setText(filterCategory);
                    recoverAdsByCategory();
                }
            });

            dialogCategory.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog dialog = dialogCategory.create();
            dialog.show();
        } else {
            Toast.makeText(this, "Please choose a district first!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (authentication.getCurrentUser() == null) {
            menu.setGroupVisible(R.id.group_notLogged, true);
        } else {
            menu.setGroupVisible(R.id.group_logged, true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout:
                authentication.signOut();
                invalidateOptionsMenu();
                break;
            case R.id.menu_login_register:
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                break;
            case R.id.menu_ads:
                startActivity(new Intent(getApplicationContext(), MyItemsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}