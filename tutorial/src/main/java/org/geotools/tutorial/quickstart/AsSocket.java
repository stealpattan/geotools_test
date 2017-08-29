package org.geotools.tutorial.quickstart;

import java.io.*;
import java.net.Socket;
import java.net.InetSocketAddress;

public class AsSocket{
    private String asnum;
    private int hostnum;

    //Constractor.
    public AsSocket(String asnum, int hostnum){
        this.asnum = asnum;
        this.hostnum = hostnum;
    }

    public static void AsDataGetter(String ASnumber, int HOSTnumber){
        int hostnumbercontrol = HOSTnumber;
        String[] hostname = getHostName();
        String host = hostname[hostnumbercontrol];
        int port = 43;
        boolean callresult = true;
        try{
            Socket sock = new Socket();
            sock.connect(new InetSocketAddress(host, port));
            BufferedWriter sockout;
            sockout = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
            BufferedReader sockre = new BufferedReader(new InputStreamReader(sock.getInputStream()));

            callresult = callRIR(host, sockout, sockre, ASnumber);

            if(callresult == false && hostnumbercontrol < 5){
                System.out.println("callresult: " + callresult);
                hostnumbercontrol = hostnumbercontrol + 1;
                // Call myself
                AsDataGetter(ASnumber, hostnumbercontrol);
            }
            else if(callresult == false && hostnumbercontrol == 5){
                System.out.println("no match any host server");
            }
            //Close socket
            closeConnection(sock);
        }catch(Exception e){
            System.out.println(e);
        }
    }

    /*
    * Call RIR.
    * Request AS number by String type.
    * If AS number does not exist host server, change number in "host[number]".
    *
    * Relation between number and host is below.
    * number = 0, host = IANA
    * number = 1, host = APNIC
    * number = 2, host = ARIN
    * number = 3, host = RIPE
    * number = 4, host = LACNIC
    * number = 5, host = AFRINIC
    *
    * Request format is "AS****" and variable type is String.
    * */
    private static boolean callRIR(String host, BufferedWriter sockout, BufferedReader sockre, String ASnumber){
        boolean result = false;
        try{
            sockout.write(ASnumber);
            sockout.newLine();
            sockout.flush();
            result = saparateHost(host,sockre,ASnumber);
        }catch(Exception e){
            System.out.println("Exception happen:" + e);
        }
        //System.out.println("callRIR: " + result);
        return result;
    }
    private static boolean saparateHost(String host, BufferedReader sockre, String ASnumber) {
        String[] hostname = getHostName();
        boolean result;
        int num = 0;
        for (int i = 1; i < 6; i++) {
            if (host == hostname[i]) {
                num = i;
                break;
            }
        }
        result = takeLocation(num, sockre, ASnumber);
        //System.out.println("saparateHost:" + result);
        return result;
    }
    private static boolean takeLocation(int hostnumber, BufferedReader sockre, String ASnumber){
        String str;
        String address = "";
        boolean result = true;
        System.out.println("\n");
        System.out.println("result of call: ASnumber: " + ASnumber);
        System.out.println("call host:" + hostnumber);
        try {
            switch (hostnumber) {
                //IANA
                case 0:
                    while((str = sockre.readLine()) != null){
                        if(true){
                            System.out.println(str);
                        }
                    }
                //APNIC
                case 1:
                    while ((str = sockre.readLine()) != null) {
                        if((str.indexOf("address:")) != -1){
                            System.out.println("result: " + str);
                            str = str.replaceAll("address:", "");
                            str = str.replaceAll("  ", "");
                            address += str + ",";
                        }
                        else if((str.indexOf("country:")) != -1){
                            System.out.println("result: " + str);
                            str = str.replaceAll("country:" , "");
                            str = str.replaceAll("  " , "");
                        }
                        else if((str.indexOf("as-block:")) != -1){
                            address = "";
                        }
                        if ((str.indexOf("ERROR")) != -1) {
                            result = false;
                        }
                    }
                //ARIN
                case 2:
                    while ((str = sockre.readLine()) != null) {
                        if (str.indexOf("Street:") != -1) {
                            System.out.println("result: " + str);
                            str = str.replaceAll("Street:", "");
                            str = str.replaceAll("  ", "");
                            address += str + ",";
                        }
                        else if((str.indexOf("Address:")) != -1){
                            System.out.println("result: " + str);
                            str = str.replaceAll("Address:" , "");
                            str = str.replaceAll("  ", "");
                            address += str + ",";
                        }
                        else if((str.indexOf("City:")) != -1){
                            System.out.println("result: " + str);
                            str = str.replaceAll("City:" , "");
                            str = str.replaceAll("  ", "");
                            address += str + ",";
                        }
                        else if((str.indexOf("State/Province:")) != -1){
                            System.out.println("result: " + str);
                            str = str.replaceAll("State/Province:" , "");
                            str = str.replaceAll("  " , "");
                            address += str + ",";
                        }
                        else if((str.indexOf("Country:")) != -1){
                            System.out.println("result: " + str);
                            str = str.replaceAll("Country:" , "");
                            str = str.replaceAll("  ", "");
                            address += str + ",";
                            break;
                        }
                        if((str.indexOf("No match found for")) != -1){
                            result = false;
                        }
                    }
                //RIPE
                case 3:
                    while ((str = sockre.readLine()) != null) {
                        if ((str.indexOf("address:")) != -1) {
                            System.out.println("result: " + str);
                            str = str.replaceAll("address:" , "");
                            str = str.replaceAll("  " , "");
                            address += str + ",";
                        }
                        else if((str.indexOf("abuse-c:")) != -1){
                            System.out.println("result: " + str);
                            break;
                        }
                        if((str.indexOf("ASN block not managed by the RIPE NCC")) != -1){
                            result = false;
                        }
                        if((str.indexOf("see http://www.")) != -1){
                            result = false;
                        }
                    }
                //AFRINIC
                case 4:
                    while((str = sockre.readLine()) != null){
                        if((str.indexOf("Address:")) != -1){
                            System.out.println("result: " + str);
                            str = str.replaceAll("Address:" , "");
                            str = str.replaceAll("  " , "");
                            address += str + ",";
                        }
                        else if((str.indexOf("City:")) != -1){
                            System.out.println("result: " + str);
                            str = str.replaceAll("City:" , "");
                            str = str.replaceAll("  " , "");
                            address += str + ",";
                        }
                        else if((str.indexOf("StateProv:")) != -1){
                            System.out.println("result: " + str);
                            str = str.replaceAll("StateProv" , "");
                            str = str.replaceAll("  " , "");
                            address += str + ",";
                        }
                        else if((str.indexOf("Country:")) != -1){
                            System.out.println("result: " + str);
                            str = str.replaceAll("Country:" , "");
                            str = str.replaceAll("  " , "");
                            address += str + ",";
                        }
                        if((str.indexOf("ERROR:101: no entries found")) != -1){
                            result = false;
                        }
                    }
                //LACNIC
                case 5:
                    while((str = sockre.readLine()) != null){
                        if((str.indexOf("Address:")) != -1){
                            System.out.println("result: " + str);
                            str = str.replaceAll("Address:" , "");
                            str = str.replaceAll("  " , "");
                            address += str + ",";
                        }
                        else if((str.indexOf("City:")) != -1){
                            System.out.println("result: " + str);
                            str = str.replaceAll("City:" , "");
                            str = str.replaceAll("  " , "");
                            address += str + ",";
                        }
                        else if((str.indexOf("StateProv:")) != -1){
                            System.out.println("result: " + str);
                            str = str.replaceAll("StateProv:" , "");
                            str = str.replaceAll("  " , "");
                            address += str + ",";
                        }
                        else if((str.indexOf("Country:")) != -1){
                            System.out.println("result: " + str);
                            str = str.replaceAll("Country:" , "");
                            str = str.replaceAll("  " , "");
                            address += str + ",";
                        }
                        else if((str.indexOf("address:")) != -1){
                            System.out.println("result: " + str);
                            str = str.replaceAll("address:" , "");
                            str = str.replaceAll("  " , "");
                            address += str + ",";
                        }
                        else{
                            result = false;
                        }
                    }
            }
        }catch(Exception e){
            System.out.println("exception happen:" + e);
        }
        //System.out.println("takelocation:" + result);
        System.out.println(address);
        setResult(address);
        return result;
    }

    public static String[] getHostName(){
        String[] host = {"whois.iana.org", "whois.apnic.net", "whois.arin.net", "whois.ripe.net", "whois.afrinic.net", "whois.lacnic.net"};
        return host;
    }
    public String getAsNumber(){
        return asnum;
    }
    public int getHostNumber(){
        return hostnum;
    }
    public static void setResult(String address){
        GlobalVariables instance = new GlobalVariables();
        instance.Address = address;
    }
    public static String getResult(){
        GlobalVariables instance = new GlobalVariables();
        return instance.Address;
    }
    public static void closeConnection(Socket sock){
        try{
            sock.close();
            System.out.println("Close connection correctry");
        }catch(Exception e){
            System.out.println(e);
        }
    }
}

class GlobalVariables{
    public static String Address;
}