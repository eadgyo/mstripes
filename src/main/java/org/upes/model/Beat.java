package org.upes.model;

/**
 * Created by This Pc on 07-07-2017.
 */
public class Beat {

    private String id;
    private double area;
    private double value;

    public Beat(String id)
    {
        this.id=id;
        area=0;
        value =0;
    }

    public String getId()
    {
        return id;
    }
    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
