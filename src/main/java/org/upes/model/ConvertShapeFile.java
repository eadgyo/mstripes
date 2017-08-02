package org.upes.model;

import com.vividsolutions.jts.geom.Geometry;
import org.geotools.data.*;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import sun.plugin.javascript.navig4.Layer;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ConvertShapeFile{

    File beatFile=new File("F:\\intern\\Basemaps\\BEAT.shp");

    public void convert(File file) throws IOException {


        SimpleFeatureIterator iterator =null;
        Transaction transaction=null;
        FeatureWriter<SimpleFeatureType, SimpleFeature> writer=null;

        try {

            FileDataStore store = FileDataStoreFinder.getDataStore(beatFile);
            FeatureSource featureSource = store.getFeatureSource();

            FileDataStore store2 = FileDataStoreFinder.getDataStore(file);
            FeatureSource featureSource1 = store2.getFeatureSource();

            SimpleFeatureType map = (SimpleFeatureType) featureSource.getSchema();
            SimpleFeatureType schema = (SimpleFeatureType) featureSource1.getSchema();

            CoordinateReferenceSystem dataCRS = schema.getCoordinateReferenceSystem();
            CoordinateReferenceSystem worldCRS = map.getCoordinateReferenceSystem();
            boolean lenient = true; // allow for some error due to different datums
            MathTransform transform = CRS.findMathTransform(dataCRS, worldCRS, lenient);

            SimpleFeatureCollection featureCollection = (SimpleFeatureCollection) featureSource1.getFeatures();

            //Creating a temp folder

            File dir = new File("F:\\intern\\Basemaps\\trial");
            if (!dir.exists())
            {
                dir.mkdir();
            }

        File destination=new File("F:\\intern\\Basemaps\\trial\\"+file.getName());
            DataStoreFactorySpi factory = new ShapefileDataStoreFactory();
            Map<String, Serializable> create = new HashMap<String, Serializable>();
            create.put("url", destination.toURI().toURL());
            create.put("create spatial index", Boolean.TRUE);
            DataStore dataStore = factory.createNewDataStore(create);
            SimpleFeatureType featureType = SimpleFeatureTypeBuilder.retype(schema, worldCRS);
            dataStore.createSchema(featureType);
            System.out.println(featureType.getCoordinateReferenceSystem().toWKT());
            transaction = new DefaultTransaction("Reproject");
            writer = dataStore.getFeatureWriterAppend(featureType.getTypeName(), transaction);
            iterator = featureCollection.features();

            while (iterator.hasNext()) {
                // copy the contents of each feature and transform the geometry

                SimpleFeature feature = iterator.next();
                SimpleFeature copy = writer.next();
                copy.setAttributes(feature.getAttributes());

                System.out.println(feature.getID());

                Geometry geometry =(Geometry) feature.getDefaultGeometry();
                Geometry geometry2 = JTS.transform(geometry, transform);

                copy.setDefaultGeometry(geometry2);
                writer.write();
            }

            transaction.commit();

        } catch (FactoryException e) {
            e.printStackTrace();
        }
         catch (Exception problem) {
            problem.printStackTrace();
            transaction.rollback();
        } finally {
            writer.close();
            iterator.close();
            transaction.close();
        }
    }

    public static void main(String args[])
    {
        File roadFile=new File("F:\\intern\\Basemaps\\Wireless_station.shp");
        ConvertShapeFile convertShapeFile=new ConvertShapeFile();
        try {
            convertShapeFile.convert(roadFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

 }

