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
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.styling.*;
import org.geotools.styling.Stroke;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.FilterFactory;


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

        StyleFactory sf = CommonFactoryFinder.getStyleFactory(null);
        FilterFactory ff = CommonFactoryFinder.getFilterFactory2(null);
        Stroke stroke = sf.createStroke(
                ff.literal(new Color(0xC8, 0x46, 0x63)),
                ff.literal(3)
        );

        GeometryDescriptor geomdesc = featuresource.getSchema().getGeometryDescriptor();
        String hoe = geomdesc.getLocalName();
        LineSymbolizer LS = sf.createLineSymbolizer(stroke,hoe);

        Rule rule = sf.createRule();
        rule.symbolizers().add(LS);
        Rule rules[] = {rule};
        FeatureTypeStyle fts = sf.createFeatureTypeStyle(rules);
        Style style2 = sf.createStyle();
        style2.featureTypeStyles().add(fts);

        Layer lineLayer = new FeatureLayer(lineCollection,style2);

        map.addLayer(lineLayer);

        //show Map.
        JMapFrame.showMap(map);
    }
}
