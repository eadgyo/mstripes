package org.upes.model;

import org.opengis.filter.identity.FeatureId;

public class Patrol
{
    // location
    private FeatureId gridLocation;

    public FeatureId getGridLocation()
    {
        return gridLocation;
    }

    public void setGridLocation(FeatureId gridLocation)
    {
        this.gridLocation = gridLocation;
    }
}
