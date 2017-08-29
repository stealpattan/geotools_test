package org.geotools.tutorial.quickstart;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class getLatitude{
    //if you need get ArcGIS Application temporary Token, you can use below class.
    public static String getAccessToken(String cliant_id, String cliant_secret_id){
        String str;
        String token = "";
        try{
            HttpURLConnection connector = null;
            URL urlsetter = new URL("https://www.arcgis.com/sharing/oauth2/token?client_id=" + cliant_id +  "&grant_type=client_credentials&client_secret=" + cliant_secret_id + "&f=pjson");
            connector = (HttpURLConnection)urlsetter.openConnection();
            connector.setRequestMethod("GET");
            connector.connect();
            System.out.println("connector open");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connector.getInputStream()));
            final int status = connector.getResponseCode();
            if(status == HttpURLConnection.HTTP_OK){
                System.out.println("connect achive");
                while((str = reader.readLine()) != null){
                    if((str.indexOf("access_token")) != -1){
                        str = str.replaceAll("\"" , "");
                        str = str.replaceAll("," , "");
                        str = str.replaceAll("access_token:" , "");
                        str = str.replaceAll(" " , "");
                        System.out.println("parse token: " + str);
                        token = str;
                    }
                }
            }
            System.out.println("connection result: " + status);
        }catch(Exception e){
            System.out.println("error log: " + e);
        }
        return token;
    }
    private static void getDatas(String address){
        String url;

        //test url below.
        url = "http://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer/findAddressCandidates?SingleLine=380+New+York+Street,+Redlands,+CA+92373&category=&outFields=*&forStorage=false&f=pjson";
        //url below.
        url = "http://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer/findAddressCandidates?SingleLine=" + address + "&category=&outFields=*&forStorage=false&f=pjson";
        //System.out.println("make up url: " + url);
        try{
            //Access global variables
            GlobalVariables2 globalvariable = new GlobalVariables2();

            HttpURLConnection connector;
            URL seturl = new URL(url);
            connector = (HttpURLConnection)seturl.openConnection();
            connector.setRequestMethod("GET");
            connector.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connector.getInputStream()));

            String str;

            final int connectionstatus = connector.getResponseCode();
            if(connectionstatus == HttpURLConnection.HTTP_OK){
                //System.out.println("connector open");
                while((str = reader.readLine()) != null){
                    //System.out.println(str);
                    if(str.indexOf("\"x\":") != -1){
                        str = str.replaceAll("\"x\":", "");
                        str = str.replaceAll(" " , "");
                        str = str.replaceAll("," , "");
                        globalvariable.latdata = str;
                    }
                    else if(str.indexOf("\"y\":") != -1){
                        str = str.replaceAll("\"y\":", "");
                        str = str.replaceAll(" " , "");
                        str = str.replaceAll("," , "");
                        globalvariable.longdata = str;
                    }
                }
            }
            //System.out.println("connection status: " + connectionstatus);
        }catch(Exception e){
            System.out.println("error log: " + e);
        }
    }
    public static void getLatitude(String address){
        address = address.replaceAll(" " , "+");
        getDatas(address);
    }
    public static String returnDatas(){
        GlobalVariables2 gb = new GlobalVariables2();
        return gb.latdata + "," + gb.longdata;
    }
}
//Global variables are defined below.
class GlobalVariables2{
    public static String latdata;
    public static String longdata;
}