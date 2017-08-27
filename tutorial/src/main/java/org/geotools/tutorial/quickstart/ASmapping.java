package org.geotools.tutorial.quickstart;

public class ASmapping{
    public static void main(String[] args){
        AsSocket socketinstance = new AsSocket("AS4000",1);
        socketinstance.AsDataGetter(socketinstance.getAsNumber(),socketinstance.getHostNumber());
    }
}