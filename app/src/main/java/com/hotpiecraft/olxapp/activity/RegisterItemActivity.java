package com.hotpiecraft.olxapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hotpiecraft.olxapp.R;
import com.hotpiecraft.olxapp.config.FirebaseConfig;
import com.hotpiecraft.olxapp.model.ItemModel;
import com.hotpiecraft.olxapp.util.Constants;
import com.hotpiecraft.olxapp.util.Permissions;
import com.santalu.maskara.widget.MaskEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dmax.dialog.SpotsDialog;

public class RegisterItemActivity extends AppCompatActivity
        implements View.OnClickListener {

    private String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
    private List<String> recoveredPhotoList = new ArrayList<>();
    private List<String> photoUrlList = new ArrayList<>();

    private EditText editProductName, editProductDescription;
    private CurrencyEditText editProductValue;
    private Button buttonRegisterItem;
    private MaskEditText editPhone;
    private ImageView imagePhoto1, imagePhoto2, imagePhoto3;
    private Spinner spinnerDistrict, spinnerCategory;
    private Toolbar toolbar;
    private android.app.AlertDialog dialog;
    private ItemModel item = new ItemModel();

    private StorageReference storage;
    private DatabaseReference database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_item);

        Permissions.validarPermissoes(permissions, this, 1);

        initializeComponents();
        configureToolbar();
        setListeners();
        loadSpinners();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void configureToolbar() {
        toolbar.setTitle("Register Product");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setListeners() {
        buttonRegisterItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateDataItem();
            }
        });

        imagePhoto1.setOnClickListener(this);
        imagePhoto2.setOnClickListener(this);
        imagePhoto3.setOnClickListener(this);
    }

    private void initializeComponents() {
        editProductDescription = findViewById(R.id.editProductDescription);
        editProductName = findViewById(R.id.editProductName);
        editProductValue = findViewById(R.id.editProductValue);
        buttonRegisterItem = findViewById(R.id.buttonRegisterItem);
        editPhone = findViewById(R.id.editPhone);
        imagePhoto1 = findViewById(R.id.imagePhoto1);
        imagePhoto2 = findViewById(R.id.imagePhoto2);
        imagePhoto3 = findViewById(R.id.imagePhoto3);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerDistrict = findViewById(R.id.spinnerDistrict);
        toolbar = findViewById(R.id.toolbarCustom);

        database = FirebaseConfig.getFirebaseDB();
        storage = FirebaseConfig.getFirebaseStorage();


        Locale locale = new Locale("en_GB", "GB");
        editProductValue.setLocale(locale);
    }

    private void saveProduct() {

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Saving Ad")
                .setCancelable(false)
                .build();
        dialog.show();

        for (int i = 0; i < recoveredPhotoList.size(); i++) {
            String urlImage = recoveredPhotoList.get(i);
            int listSize = recoveredPhotoList.size();
            savePhotoStorage(urlImage, listSize, i);
        }

    }

    private void savePhotoStorage(String urlImage, final int totalPhotos, int counter) {

        /*Save images in storage */
        final StorageReference imageRef = storage.child(Constants.IMAGES)
                .child(Constants.ADS)
                .child(item.getId())
                .child("image" + counter);

        //upload photo
        final UploadTask uploadTask = imageRef.putFile(Uri.parse(urlImage));
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri firebaseUrl = task.getResult();
                            String url = firebaseUrl.toString();
                            photoUrlList.add(url);

                            if (totalPhotos == photoUrlList.size()) {
                                item.setPhotoPath(photoUrlList);
                                item.saveItem();
                                dialog.dismiss();
                                finish();
                            }
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showErrorMessage("Failed to upload image!");
            }
        });


    }


    private ItemModel configureItem() {
        String phone = "";
        String district = spinnerDistrict.getSelectedItem().toString();
        String category = spinnerCategory.getSelectedItem().toString();
        String product = editProductName.getText().toString();
        String description = editProductDescription.getText().toString();
        String value = editProductValue.getText().toString();
        if (!editPhone.getUnMasked().equals("")) {
            phone = editPhone.getUnMasked();
        }

        ItemModel itemModel = new ItemModel();
        itemModel.setDistrict(district);
        itemModel.setCategory(category);
        itemModel.setDescription(description);
        itemModel.setTitle(product);
        itemModel.setPhone(phone);
        itemModel.setValue(value);

        return itemModel;
    }

    public void validateDataItem() {

        item = configureItem();
        String value = String.valueOf(editProductValue.getRawValue());

        if (recoveredPhotoList.size() != 0) {
            if (!item.getDistrict().isEmpty()) {
                if (!item.getCategory().isEmpty()) {
                    if (!item.getTitle().equals("") && item.getTitle().length() >= 2) {
                        if (!value.isEmpty() && !item.getValue().equals("0")) {
                            if (!item.getPhone().isEmpty() && item.getPhone().length() >= 10) {
                                if (!item.getDescription().isEmpty()) {

                                    saveProduct();
                                } else {
                                    showErrorMessage("Please add a description!");
                                }
                            } else {
                                showErrorMessage("Please enter your mobile number!");
                            }
                        } else {
                            showErrorMessage("Please specify sale value");
                        }
                    } else {
                        showErrorMessage("Please specify product name!");
                    }
                } else {
                    showErrorMessage("Please select a category!");
                }
            } else {
                showErrorMessage("Please select a district!");
            }
        } else {
            showErrorMessage("Please add 1 photo!");
        }
    }

    private void showErrorMessage(String string) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissionResult : grantResults) {
            if (permissionResult == PackageManager.PERMISSION_DENIED) {
                showAlertDialog();
            }
        }
    }

    private void showAlertDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permission Denied");
        builder.setMessage("Permission needed to register a product!");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imagePhoto1:
                openGallery(1);
                break;
            case R.id.imagePhoto2:
                openGallery(2);
                break;
            case R.id.imagePhoto3:
                openGallery(3);
                break;
        }
    }

    private void openGallery(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            Uri imageSelected = data.getData();
            String imagePath = imageSelected.toString();

            //configure image
            if (requestCode == 1) {
                imagePhoto1.setImageURI(imageSelected);
            } else if (requestCode == 2) {
                imagePhoto2.setImageURI(imageSelected);
            } else if (requestCode == 3) {
                imagePhoto3.setImageURI(imageSelected);
            }

            recoveredPhotoList.add(imagePath);
        }
    }

    private void loadSpinners() {
        String[] district = getResources().getStringArray(R.array.estados);
        String[] category = getResources().getStringArray(R.array.categoria);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, district);
        ArrayAdapter<String> adapterCategory = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, category);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDistrict.setAdapter(adapter);
        spinnerCategory.setAdapter(adapterCategory);
    }
}