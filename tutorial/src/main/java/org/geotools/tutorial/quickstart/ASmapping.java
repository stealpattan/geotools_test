package org.geotools.tutorial.quickstart;

public class ASmapping{
    public static void main(String[] args){
        AsSocket socketinstance = new AsSocket("AS2300");
        socketinstance.AsDataGetter(socketinstance.getAsNumber());
    }
}