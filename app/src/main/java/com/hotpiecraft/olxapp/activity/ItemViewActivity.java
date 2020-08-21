package com.hotpiecraft.olxapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hotpiecraft.olxapp.R;
import com.hotpiecraft.olxapp.model.ItemModel;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ItemViewActivity extends AppCompatActivity {

    private ItemModel item;
    private Toolbar toolbar;
    private CarouselView carouselView;
    private List<String> photoUrlList = new ArrayList<>();

    private TextView textValue, textTitle, textDescription, textDistrict;
    private Button buttonCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);

        initializeComponents();

        toolbar.setTitle("Product Details");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recoverAd();
        configureCarouselView();

        setListeners();

    }

    private void setListeners() {
        buttonCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", item.getPhone(), null));
                startActivity(intent);
            }
        });
    }

    private void initializeComponents() {
        toolbar = findViewById(R.id.toolbarCustom);
        carouselView = findViewById(R.id.carouselView);
        textDescription = findViewById(R.id.textDetailDescription);
        textTitle = findViewById(R.id.textDetailTitle);
        textValue = findViewById(R.id.textDetailValue);
        textDistrict = findViewById(R.id.textDetailDistrict);
        buttonCall = findViewById(R.id.buttonCall);
    }

    private void configureCarouselView() {
        carouselView.setPageCount(photoUrlList.size());

        ImageListener imageListener = new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                Picasso.get().load(photoUrlList.get(position)).into(imageView);
            }
        };

        carouselView.setImageListener(imageListener);
    }

    private void recoverAd() {
        Bundle bundle = getIntent().getExtras();

        if(bundle != null) {
            item = (ItemModel) bundle.getSerializable("adInfo");
            assert item != null;
            photoUrlList = item.getPhotoPath();
            textTitle.setText(item.getTitle());
            textDistrict.setText(item.getDistrict());
            textValue.setText(item.getValue());
            textDescription.setText(item.getDescription());
        }

    }
}