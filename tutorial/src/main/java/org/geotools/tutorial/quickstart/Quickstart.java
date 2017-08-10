package org.geotools.tutorial.quickstart;

import java.io.File;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.data.JFileDataStoreChooser;

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

        JMapFrame.showMap(map);
    }
}
