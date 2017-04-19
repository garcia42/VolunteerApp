package com.example.jegarcia.volunteer.models;

import com.google.gson.annotations.SerializedName;
import com.orm.dsl.Table;

@Table
public class Opportunities {

    @SerializedName("db_id")
    private transient Long id = null;

    @SerializedName("id")
    private Integer oppId;

    private String title;
    private String updated;
    private String status;
    private Availability availability;
    private String imageUrl;
    private String contact;
    private String volunteersNeeded;

    private String skillsNeeded;
    private String[] greatFor;

    private String[] descriptions;

    private Integer minimumAge;
    private Integer numReferred;
    private Integer spacesAvailable;

    private Organization parentOrg;
    private Location location;

    private boolean virtual;
    private boolean requiresAddress;

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

    public String[] getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(String[] descriptions) {
        this.descriptions = descriptions;
    }

    public String[] getGreatFor() {
        return greatFor;
    }

    public void setGreatFor(String[] greatFor) {
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

    private boolean hasWaitlist;

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
        return oppId;
    }

    public void setoppId(Integer id) {
        this.oppId = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
