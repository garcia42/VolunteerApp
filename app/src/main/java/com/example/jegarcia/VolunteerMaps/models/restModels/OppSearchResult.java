package com.example.jegarcia.VolunteerMaps.models.restModels;

import com.example.jegarcia.VolunteerMaps.models.BaseObjectModel;
import com.example.jegarcia.VolunteerMaps.models.volunteerMatchModels.Opportunities;

import java.util.List;

/**
 * Class to represent the results returned from an API call to "searchOpportunities". This class is
 * used to take the JSON string returned from the call and represent it as Java objects.
 *
 * Limitations: Currently not all possible fields are represented here. Only the ones currently needed
 * by the simple SearchOpportunitiesExample example.
 *
 * Created by jrackwitz on 12/3/15.
 */
public class OppSearchResult extends BaseObjectModel {
    private Integer currentPage;
    private List<Opportunities> opportunities;
    private Integer resultsSize;
    private String sortCriteria;

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public List<Opportunities> getOpportunities() {
        return opportunities;
    }

    public void setOpportunities(List<Opportunities> opportunities) {
        this.opportunities = opportunities;
    }

    public Integer getResultsSize() {
        return resultsSize;
    }

    public void setResultsSize(Integer resultsSize) {
        this.resultsSize = resultsSize;
    }

    public String getSortCriteria() {
        return sortCriteria;
    }

    public void setSortCriteria(String sortCriteria) {
        this.sortCriteria = sortCriteria;
    }


    public OppSearchResult() {
    }
}