package org.upes.model;

import org.opengis.filter.identity.FeatureId;

public class Patrol
{
    // location
    private Beat beatLocation;
    private double latitude,longitude;

    public Beat getGridLocation()
    {
        return beatLocation;
    }

    public void setGridLocation(Beat beatLocation)
    {
        this.beatLocation = beatLocation;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
