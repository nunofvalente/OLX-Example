package com.hotpiecraft.olxapp.model;

import com.google.firebase.database.DatabaseReference;
import com.hotpiecraft.olxapp.config.FirebaseConfig;
import com.hotpiecraft.olxapp.util.Constants;

import java.io.Serializable;
import java.util.List;

public class ItemModel implements Serializable {

    private String id;
    private String title;
    private String category;
    private String district;
    private String value;
    private String description;
    private String phone;
    private List<String> photoPath;

    public ItemModel() {
        DatabaseReference adRef = FirebaseConfig.getFirebaseDB().child(Constants.MY_ADS);
        setId(adRef.push().getKey());

    }

    public void saveItem() {
        String userId = FirebaseConfig.getLoggedUserId();
        DatabaseReference itemRef = FirebaseConfig.getFirebaseDB()
                .child(Constants.MY_ADS);

        itemRef.child(userId).child(getId())
                .setValue(this);

        savePublicItem();

    }

    public void savePublicItem() {
        DatabaseReference itemRef = FirebaseConfig.getFirebaseDB()
                .child(Constants.ADS_DB);

        itemRef.child(getDistrict())
                .child(getCategory())
                .child(getId())
                .setValue(this);
    }

    public void removeItem() {
        String userId = FirebaseConfig.getLoggedUserId();
        DatabaseReference database = FirebaseConfig.getFirebaseDB();

        DatabaseReference myAdsRef = database.child(Constants.MY_ADS)
                .child(userId)
                .child(getId());
        myAdsRef.removeValue();
        removePublicItem();
    }

    public  void removePublicItem() {
        DatabaseReference itemRef = FirebaseConfig.getFirebaseDB()
                .child(Constants.ADS_DB);

        DatabaseReference item = itemRef.child(getDistrict())
                .child(getCategory())
                .child(getId());

        item.removeValue();

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(List<String> photoPath) {
        this.photoPath = photoPath;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
