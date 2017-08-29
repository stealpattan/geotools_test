package org.geotools.tutorial.quickstart;

public class ASmapping{
    public static void main(String[] args){
        AsSocket socketinstance = new AsSocket("AS12345",1);
        socketinstance.AsDataGetter(socketinstance.getAsNumber(),socketinstance.getHostNumber());
        System.out.println("get address: result: " + socketinstance.getResult());
        getLatitude getlat = new getLatitude();
        getlat.getLatitude(socketinstance.getResult());
        System.out.println(getlat.returnDatas());
    }
}