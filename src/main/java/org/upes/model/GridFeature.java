package org.upes.model;

import java.util.ArrayList;

public class GridFeature {

    private String currFeatureId;
    private String parentBeat;
    private ArrayList<String> neighbourList;

    public String getCurrFeatureId() {
        return currFeatureId;
    }

    public void setCurrFeatureId(String currFeatureId) {
        this.currFeatureId = currFeatureId;
    }

    public String getParentBeat() {
        return parentBeat;
    }

    public void setParentBeat(String parentBeat) {
        this.parentBeat = parentBeat;
    }

    public ArrayList<String> getNeighbourList() {
        return neighbourList;
    }

    public void setNeighbourList(ArrayList<String> neighbourList) {
        this.neighbourList = neighbourList;
    }
}
