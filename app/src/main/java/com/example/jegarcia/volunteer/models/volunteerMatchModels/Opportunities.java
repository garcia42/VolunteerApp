package com.example.jegarcia.volunteer.models.volunteerMatchModels;

import com.example.jegarcia.volunteer.models.BaseObjectModel;
import com.example.jegarcia.volunteer.models.converters.StringArrayConverter;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.requery.Convert;
import io.requery.Entity;
import io.requery.Generated;
import io.requery.Key;
import io.requery.ManyToOne;
import io.requery.OneToOne;
import io.requery.Persistable;

@Entity
public abstract class Opportunities extends BaseObjectModel implements Serializable, Persistable {

    @Key
    @Generated
    int id;

    @SerializedName("id")
    Integer oppId;

    String title;
    String updated;
    String status;

    @OneToOne
    AvailabilityEntity availability;
    String imageUrl;
    String contact;
    String volunteersNeeded;

    String skillsNeeded;

    @Convert(StringArrayConverter.class)
    String[] greatFor;

    @Convert(StringArrayConverter.class)
    String[] descriptions;

    Integer minimumAge;
    Integer numReferred;
    Integer spacesAvailable;

    @ManyToOne
    Organization parentOrg;

    @ManyToOne
    Location location;

    boolean virtual;
    boolean requiresAddress;
    boolean hasWaitlist;

    // Fields needed for db
    Integer orgId;
    Integer locationId;

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

    public void setLocation(LocationEntity location) {
        this.location = location;
    }

    public OrganizationEntity getParentOrg() {
        return parentOrg;
    }

    public void setParentOrg(OrganizationEntity parentOrg) {
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

    public void setAvailability(AvailabilityEntity availability) {
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
