package org.geotools.tutorial.quickstart;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

/**
 * SHPファイル表示プログラム
 * 2015年度の日本の主要輸出先への輸出額をプロット
 * 「https://github.com/rafalrusin/geotools-fx-test/blob/master/src/geotools/fx/test/GeotoolsFxTest.java」をベースにしている
 * @author karura
 *
 */

/*
*　・下記のURLより作成者様のコードをお借りしております。
*　・改変を加えさせていただきます
*　・http://krr.blog.shinobi.jp/javafx/javafx%20シェープファイル形式の地図を描く-ge#サンプル・プログラム２（地図上で国を指定）
* */
public class TestGeoMap2 extends Application
{
    // 定数宣言(輸出国情報)
    private final Color[]   COLORS          = new Color[] { Color.RED, Color.ORANGE, Color.ORANGE, Color.ORANGE, Color.ORANGE, Color.ORANGE, Color.ORANGE, Color.ORANGE, Color.ORANGE, Color.ORANGE, Color.ORANGE };
    private final String[]  COUNTRIES       = { "Japan" , "United States of America" , "China" , "South Korea" , "Taiwan" , "Hong Kong S.A.R." , "Thailand" , "Singapore" , "Germany" , "Australia" , "Vietnam" };
    private final long[]    EXPORT_VALUES   = { 0 , 152246 , 132234 , 53266 , 44725 , 42360 , 33863 , 24026 , 19648 , 15549 , 15164 };

    // 変数宣言(マウスドラッグ用の変数)
    private double dragBaseX, dragBaseY;
    private double dragBase2X, dragBase2Y;

    public static void main(String[] args)
    {
        launch( args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Group root  = new Group();
        Scene scene;

        // シーンを作成
        primaryStage.setTitle( "日本貿易統計：2015年度輸出額上位10ヵ国" );
        scene = new Scene(root, 1100, 500, Color.LIGHTBLUE);

        // 地図を作成
        Group map   = createMap( "/Users/shoma-ito/Downloads/ne_50m_admin_0_countries/ne_50m_admin_0_countries.shp" );
        root.getChildren().add(map);


        // ウィンドウ表示
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * 地図を表示するノードを作成する
     * @param shpFilePath
     * @return
     * @throws IOException
     */
    protected Group createMap( String shpFilePath ) throws IOException
    {
        // 変数定義
        Group   map             = new Group();      // 戻り値
        Group   mapShape        = new Group();      // 地図の図形データ
        Group   textNotExport   = new Group();      // 輸出国以外の国名
        Group   textExport      = new Group();      // 輸出国名
        Group   lineExport      = new Group();      // 輸出国間に引く線

        // SHPファイルを読込
        File                    file             = new File( shpFilePath );
        FileDataStore           store            = FileDataStoreFinder.getDataStore(file);

        // ファイルからfeature(地図オブジェクト)を取得
        SimpleFeatureSource     featureSource    = store.getFeatureSource();
        SimpleFeatureCollection c                = featureSource.getFeatures();
        SimpleFeatureIterator   featuresIterator = c.features();

        // 各featureを画面出力可能なクラスに変更
        Map<String,Point>   countryMap      = new HashMap<String,Point>();
        while ( featuresIterator.hasNext() )
        {
            // 1つのfeatureの取得
            SimpleFeature   o               = featuresIterator.next();
            int             countryIndex    = 0;                                        // 処理対象が表す国番号
            String          name            = (String) o.getAttribute("sovereignt");    // featureが表す国名を取得
            String          type            = (String) o.getAttribute("type");          // featureが示すタイプ（国(country)や属国(dependency)など）
            String          subUnit         = (String) o.getAttribute("subunit");       // featureが示すサブユニット(地域名)
            Object          geometry        = o.getDefaultGeometry();                   // featureが表すポリゴンを取得

            // 地理情報が複合ポリゴンの場合のみ処理実行。処理対象のポリゴンを取得
            if ( !( geometry instanceof MultiPolygon ) ){ continue; }
            MultiPolygon    multiPolygon    = (MultiPolygon) geometry;

            // ポリゴンを画面出力可能なクラスに変換
            for ( int geometryI=0 ; geometryI<multiPolygon.getNumGeometries() ; geometryI++ )
            {
                // 変数の初期化
                Geometry        polygon         = multiPolygon.getGeometryN(geometryI); // ポリゴンの1つを取得
                Coordinate[]    coords          = polygon.getCoordinates();             // ポリゴンの頂点配列を取得

                // 地形の塗りつぶし色を定義
                java.util.List<String>  countries       = Arrays.asList( COUNTRIES );
                Color                   currentColor    = null;
                if( countries.contains( name ) )
                {
                    // 輸出国リストにある場合、国番号と色を取得
                    countryIndex    = countries.indexOf( name );
                    currentColor    = COLORS[ countryIndex ];
                }else{
                    // 輸出国リストにない場合、デフォルトの国番号と色を取得
                    countryIndex    = 0;
                    currentColor    = Color.ALICEBLUE;
                }

                // 頂点をつないでポリゴンを描く
                Path path = new Path();
                path.setStrokeWidth(0.05);
                path.setFill(currentColor);
                path.getElements().add( new MoveTo(coords[0].x, coords[0].y) );
                for (Coordinate p : coords){ path.getElements().add( new LineTo( p.x, p.y ) ); }
                path.getElements().add( new LineTo(coords[0].x, coords[0].y) );

                // マップに追加
                mapShape.getChildren().add(path);
            }

            // 本国以外はテキストを表示しない
            //   国を表すfeatureでない場合は、テキスト情報を出力しない。
            //   本国名とサブユニット名が異なる場合はテキスト情報を出力しない。(香港だけ特別扱い)
            if( type.equalsIgnoreCase( "dependency" ) ){ continue; }
            if( ! ( subUnit.equals( name ) || subUnit.equals( "Hong Kong S.A.R." ) ) ){ continue; }

            // 国名と輸出額表示ノードを作成
            Node node   = ( countryIndex == 0 )? new Text( subUnit )
                    : createExportValue( subUnit , EXPORT_VALUES[ countryIndex ] );

            // 国名と輸出額表示ノードの位置を設定
            Point           centroid        = multiPolygon.getCentroid();   // ポリゴンの重心座標を取得
            Bounds          bounds          = node.getBoundsInLocal();
            node.getTransforms().add(new Translate(centroid.getX(), centroid.getY()));
            node.getTransforms().add(new Scale(0.1,-0.1));
            node.getTransforms().add(new Translate(-bounds.getWidth()/2., bounds.getHeight()/2.));

            // シーングラフに追加
            Group   group   = ( countryIndex == 0 )? textNotExport : textExport;
            group.getChildren().add( node );

            // 後で矢印を描くために各国の重心位置を保持
            countryMap.put( subUnit , centroid );
        }

        // 矢印を描く
        for( int i=1 ; i<COUNTRIES.length ; i++ )
        {
            // 位置を取得
            Point   pFrom   = countryMap.get( COUNTRIES[0] );
            Point   pTo     = countryMap.get( COUNTRIES[i] );
            if( ( pFrom == null ) || ( pTo == null ) ){ continue; }

            // 矢印を記入
            Path path   = new Path();
            path.setStrokeWidth( 0.25 );
            path.setStroke( Color.BLACK );
            path.setOpacity( 0.5 );
            path.getElements().add( new MoveTo( pFrom.getX() , pFrom.getY() ) );
            path.getElements().add( new LineTo( pTo.getX()   , pTo.getY() ) );
            lineExport.getChildren().add( path );
        }

        // 画面に地図と文字を追加
        map.getChildren().add( mapShape );          // 地形
        map.getChildren().add( textNotExport );     // 輸出国以外の国名
        map.getChildren().add( lineExport );        // 輸出国間の線
        map.getChildren().add( textExport );        // 輸出国名

        // 初期表示位置とズーム
        map.translateXProperty().set(520);
        map.translateYProperty().set(300);
        map.scaleXProperty().set(3);
        map.scaleYProperty().set(-3);

        // 地図を返す
        return map;
    }

    /**
     * 輸出額を示すテキスト情報を表示するノードを作成
     * @param name 国名
     * @param exportValue 輸出額
     * @return
     */
    protected Node createExportValue( String name , long exportValue )
    {
        // ルートノード
        VBox    root    = new VBox();

        // グラフを追加
        ProgressIndicator   pi  = new ProgressIndicator();
        long                sum = 0;
        for( long value : EXPORT_VALUES ){ sum += value; }
        pi.setProgress( (double) exportValue / sum );
        pi.setPrefSize( 50 , 50 );
        root.getChildren().add( pi );

        // テキストを追加
        Text    country  = new Text( name );
        Text    value    = new Text( String.format( "( %,3d億円 )" ,exportValue ) );
        country.setFill( Color.RED );
        value.setFill( Color.RED );
        root.getChildren().addAll( country , value );

        return root;
    }
}