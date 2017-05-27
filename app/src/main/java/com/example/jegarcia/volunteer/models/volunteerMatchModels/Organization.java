package com.example.jegarcia.volunteer.models.volunteerMatchModels;

import com.example.jegarcia.volunteer.models.BaseObjectModel;
import com.example.jegarcia.volunteer.models.converters.IntegerArrayConverter;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.requery.Convert;
import io.requery.Entity;
import io.requery.Generated;
import io.requery.Key;
import io.requery.ManyToOne;
import io.requery.Persistable;

@Entity
public abstract class Organization extends BaseObjectModel implements Serializable, Persistable {

    @Key
    @Generated
    int id;

    int avgRating;

    @SerializedName("id")
    Integer orgId;

    @Convert(IntegerArrayConverter.class)
    int[] categoryIds;

    String created;
    String description;
    String imageUrl;
    String mission;
    String name;
    String numReviews;
    String plaintextDescription;
    String plaintextMission;
    String updated;
    String type;
    String vmUrl;
    String url;

    @ManyToOne
    Contact contact;

    @ManyToOne
    Location location;

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
}
