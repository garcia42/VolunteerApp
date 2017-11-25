package com.example.jegarcia.VolunteerMaps.models.volunteerMatchModels;

import com.example.jegarcia.VolunteerMaps.models.StringRealmListConverter;
import com.google.gson.annotations.JsonAdapter;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Opportunities extends RealmObject {

    @PrimaryKey
    Integer id;

    String title;

    //ex "2010-07-02T10:23:46-0700"
    String updated;
    String status;

    Availability availability;
    String imageUrl;
    String contact;
    String volunteersNeeded;

    String skillsNeeded;

    @JsonAdapter(StringRealmListConverter.class)
    RealmList<RealmString> greatFor;

    String description;

    Integer minimumAge;
    Integer numReferred;
    Integer spacesAvailable;

    Organization parentOrg;

    Location location;

    boolean virtual;
    boolean requiresAddress;
    boolean hasWaitlist;

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    boolean isLiked;

    @JsonAdapter(StringRealmListConverter.class)
    RealmList<String> keywords;

    public RealmList<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(RealmList<String> keywords) {
        this.keywords = keywords;
    }

    private Date downloadTime = new Date();

    public boolean isHasWaitlist() {
        return hasWaitlist;
    }

    public void setHasWaitlist(boolean hasWaitlist) {
        this.hasWaitlist = hasWaitlist;
    }

    public boolean isRequiresAddress() {
        return requiresAddress;
    }

    public void setRequiresAddress(boolean requiresAddress) {
        this.requiresAddress = requiresAddress;
    }

    public boolean isVirtual() {
        return virtual;
    }

    public void setVirtual(boolean virtual) {
        this.virtual = virtual;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Organization getParentOrg() {
        return parentOrg;
    }

    public void setParentOrg(Organization parentOrg) {
        this.parentOrg = parentOrg;
    }

    public Integer getSpacesAvailable() {
        return spacesAvailable;
    }

    public void setSpacesAvailable(Integer spacesAvailable) {
        this.spacesAvailable = spacesAvailable;
    }

    public Integer getNumReferred() {
        return numReferred;
    }

    public void setNumReferred(Integer numReferred) {
        this.numReferred = numReferred;
    }

    public Integer getMinimumAge() {
        return minimumAge;
    }

    public void setMinimumAge(Integer minimumAge) {
        this.minimumAge = minimumAge;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RealmList<RealmString> getGreatFor() {
        return greatFor;
    }

    public void setGreatFor(RealmList<RealmString> greatFor) {
        this.greatFor = greatFor;
    }

    public String getSkillsNeeded() {
        return skillsNeeded;
    }

    public void setSkillsNeeded(String skillsNeeded) {
        this.skillsNeeded = skillsNeeded;
    }

    public String getVolunteersNeeded() {
        return volunteersNeeded;
    }

    public void setVolunteersNeeded(String volunteersNeeded) {
        this.volunteersNeeded = volunteersNeeded;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Availability getAvailability() {
        return availability;
    }

    public void setAvailability(Availability availability) {
        this.availability = availability;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public Integer getOppId() {
        return id;
    }

    public void setoppId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDownloadTime() {
        return downloadTime;
    }

    public void setDownloadTime(Date downloadTime) {
        this.downloadTime = downloadTime;
    }
}
