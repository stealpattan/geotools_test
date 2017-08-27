package org.geotools.tutorial.quickstart;

public class ASmapping{
    public static void main(String[] args){
        AsSocket socketinstance = new AsSocket("AS0001",1);
        socketinstance.AsDataGetter(socketinstance.getAsNumber(),socketinstance.getHostNumber());

    }
}