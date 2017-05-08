package com.example.jegarcia.volunteer.models;

import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

public class Organization extends SugarRecord {

    private int avgRating;

    @SerializedName("id")
    private Integer orgId;

    private int[] categoryIds;

    private String created;
    private String description;
    private String imageUrl;
    private String mission;
    private String name;
    private String numReviews;
    private String plaintextDescription;
    private String plaintextMission;
    private String updated;
    private String type;
    private String vmUrl;
    private String url;

    private Contact contact;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVmUrl() {
        return vmUrl;
    }

    public void setVmUrl(String vmUrl) {
        this.vmUrl = vmUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getPlaintextMission() {
        return plaintextMission;
    }

    public void setPlaintextMission(String plaintextMission) {
        this.plaintextMission = plaintextMission;
    }

    public String getPlaintextDescription() {
        return plaintextDescription;
    }

    public void setPlaintextDescription(String plaintextDescription) {
        this.plaintextDescription = plaintextDescription;
    }

    public String getNumReviews() {
        return numReviews;
    }

    public void setNumReviews(String numReviews) {
        this.numReviews = numReviews;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMission() {
        return mission;
    }

    public void setMission(String mission) {
        this.mission = mission;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public int[] getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(int[] categoryIds) {
        this.categoryIds = categoryIds;
    }

    public Integer getOrgId() {
        return orgId;
    }

    public void setOrgId(Integer id) {
        this.orgId = id;
    }

    public int getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(int avgRating) {
        this.avgRating = avgRating;
    }

    private Location location;

}
