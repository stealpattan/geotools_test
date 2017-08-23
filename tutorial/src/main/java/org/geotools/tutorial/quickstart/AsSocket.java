package org.geotools.tutorial.quickstart;

import java.io.*;
import java.net.Socket;
import java.net.InetSocketAddress;

public class AsSocket{
    public static void main(String[] args){
        String[] host = getHostName();
        int port = 43;
        String result,str;
        result = "finish";
        try{
            Socket sock = new Socket();
            sock.connect(new InetSocketAddress(host[1], port));
            BufferedWriter sockout;
            sockout = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
            BufferedReader sockre = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            callRIR(host[1], sock, sockout, sockre, "AS5000");
        }catch(Exception e){
            System.out.println(e);
        }
        System.out.println("result:" + result);
    }

    /*
    * Call RIR.
    * Request AS number by String type.
    * If AS number does not exist hot server, change number in "host[number]".
    *
    * Relation between number and host is below.
    * number = 0, host = NIC
    * number = 1, host = APNIC
    * number = 2, host = ARIN
    * number = 3, host = RIPE
    * number = 4, host = LACNIC
    * number = 5, host = AFRINIC
    *
    * Request format is "AS****" and variable type is String.
    * */
    public static void callRIR(String host, Socket sock, BufferedWriter sockout, BufferedReader sockre, String ASnumber){
        try{
            sockout.write(ASnumber);
            sockout.newLine();
            sockout.flush();
            saparateHost(host,sockre,ASnumber);
        }catch(Exception e){
            System.out.println("Exception happen:" + e);
        }
    }
    public static void saparateHost(String host, BufferedReader sockre, String ASnumber){
        String[] hostname = getHostName();
        int num = 0;
        for(int i=1;i<6;i++){
            if(hostname[i] == host){
                num = i;
                break;
            }
        }
        takeLocation(num, sockre, ASnumber);
    }
    public static String[] getHostName(){
        String[] host = {"whois.nic.ad.jp", "whois.apnic.net", "whois.arin.net", "whois.ripe.net", "whois.lacnic.net", "whois.afrinic.net"};
        return host;
    }
    public static void takeLocation(int hostnumber, BufferedReader sockre, String ASnumber){
        String str;
        System.out.println("result of call: ASnumber: " + ASnumber);
        try {
            switch (hostnumber) {
                case 1:
                case 3:
                    while ((str = sockre.readLine()) != null) {
                        if (str.indexOf("address") != -1) {
                            System.out.println("result: " + str);
                        }
                    }
                case 2:
                    while ((str = sockre.readLine()) != null) {
                        if (str.indexOf("Street") != -1) {
                            System.out.println("result: " + str);
                        }
                        else if((str.indexOf("City")) != -1){
                            System.out.println("result: " + str);
                        }
                        else if((str.indexOf("State/Province")) != -1){
                            System.out.println("result: " + str);
                        }
                        else if((str.indexOf("Country")) != -1){
                            System.out.println("result: " + str);
                        }
                    }
                case 4:
                case 5:
                    while((str = sockre.readLine()) != null){
                        if((str.indexOf("Address")) != -1){
                            System.out.println("result: " + str);
                        }
                        else if((str.indexOf("City")) != -1){
                            System.out.println("result: " + str);
                        }
                        else if((str.indexOf("StateProv")) != -1){
                            System.out.println("result: " + str);
                        }
                        else if((str.indexOf("Country")) != -1){
                            System.out.println("result: " + str);
                        }
                    }
            }
        }catch(Exception e){
            System.out.println("exception happen:" + e);
        }
    }
}