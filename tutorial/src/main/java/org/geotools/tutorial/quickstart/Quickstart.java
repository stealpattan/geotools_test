package org.geotools.tutorial.quickstart;

import java.awt.*;
import java.io.File;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import org.geotools.data.DataUtilities;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;


/**
 * Created by shoma-ito on 8/9/2017 AD.
 */
public class Quickstart {
    public static void main(String[] args)throws Exception{
        File file = JFileDataStoreChooser.showOpenFile("shp", null);
        if(file == null){
            return;
        }
        FileDataStore store = FileDataStoreFinder.getDataStore(file);
        SimpleFeatureSource featuresource = store.getFeatureSource();

        MapContent map = new MapContent();
        map.setTitle("quick start");

        Style style = SLD.createSimpleStyle(featuresource.getSchema());
        Layer layer = new FeatureLayer(featuresource, style);
        map.addLayer(layer);

        //code below is used when I draw the line on the map
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory( null );

        SimpleFeatureType pointtype = DataUtilities.createType(
                "Location",
                "the_geom:LineString," +
                "name:String"
        );
        SimpleFeatureBuilder sfb = new SimpleFeatureBuilder(pointtype);

        Coordinate[] coords  = new Coordinate[] {new Coordinate(139.6917064, 35.6894875), new Coordinate(0,0) };

        LineString line = geometryFactory.createLineString(coords);
        sfb.add(line);
        sfb.add("just want to draw lines");

        DefaultFeatureCollection lineCollection = new DefaultFeatureCollection();
        SimpleFeature simpleFeature = sfb.buildFeature(null);
        lineCollection.add(simpleFeature);

        Layer lineLayer = new FeatureLayer(lineCollection,style);

        map.addLayer(lineLayer);

        //show Map.
        JMapFrame.showMap(map);
    }
}
