package com.example.jegarcia.volunteer.models.volunteerMatchModels;

import com.example.jegarcia.volunteer.StringRealmListConverter;
import com.google.gson.annotations.JsonAdapter;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Opportunities extends RealmObject {

    @PrimaryKey
    Integer id;

    String title;
    String updated;
    String status;

    Availability availability;
    String imageUrl;
    String contact;
    String volunteersNeeded;

    String skillsNeeded;

    @JsonAdapter(StringRealmListConverter.class)
    RealmList<RealmString> greatFor;

    @JsonAdapter(StringRealmListConverter.class)
    RealmList<RealmString> descriptions;

    Integer minimumAge;
    Integer numReferred;
    Integer spacesAvailable;

    Organization parentOrg;

    Location location;

    boolean virtual;
    boolean requiresAddress;
    boolean hasWaitlist;

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

    public RealmList<RealmString> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(RealmList<RealmString> descriptions) {
        this.descriptions = descriptions;
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
}
