package SOAP;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.net.*;

/**
 * Trida, ktera sklada pozadavky do spravnych XML kodu 
 * pro dotazy do usredny IPEX
 * @author ecer
 */
public class SoapFce {
    //definice hlavicky a paticky pozadavku
    private String requestHead = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ipbx=\"http://ipbx.namespace\">"
            + "<soapenv:Header/>"
            + "<soapenv:Body>";
    private String requestBottom = "</soapenv:Body>"
            + "</soapenv:Envelope>";
    private String request = "";
    private String plan = Menu.soap_plan;



    /**
     * Metoda vraci datum v pozadovanem xl formatu
     * @param d zadejte datum ve formatu RRRRMMDD ale pokousi se na YYYYMMDD hh:mm
     * @return datum v xl podobe
     */
    public String xlDatum(String d) {
        String datum;
        try {
        datum = d.substring(0, 4) + "-" + d.substring(4, 6) + "-" + d.substring(6, 8) + "T"+ d.substring(9, 14)+":00.000+01:00";
        } catch(Exception e){
        datum = d.substring(0, 4) + "-" + d.substring(4, 6) + "-" + d.substring(6, 8) + "T00:00:00.000+01:00";
        }
        return datum;
    }


    /**
     * Metoda vypise vse co bylo za poslednich 5 minut volano.
     * Je nutne zajistit synchronizaci casu PC se stejnym NTP serverem,
     * ktery pouziva ustredna: ntp1.ipex.cz
     * @return vypis volani
     */
    public String vypisHovoru(){
    DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm");
        Date date = new Date();
        String _do = dateFormat.format(date);
        //System.out.println("tak jake je datum: "+_do);
        //pro minuty tu budu muset udelat nejaky udelatko, ktery mi rekne je vic, nez 4 minuty, pak odecti 5 minut
        //je- min jak 4 minuty, koukni jestli je vic jak 5 minut, kdyz ne je kousek po pulnoci a mel bych to osetrit, nebo nedelat nic
        //optimalne oriznout hodiny na 23 a minuty dat na 55... treba

        int minut = Integer.parseInt(_do.substring(12,14));
        //System.out.println("minut:"+minut);
        if (minut >4) {
            date = new Date(date.getYear(), date.getMonth(), date.getDate(), date.getHours(),date.getMinutes()-5);

        } else {
            date = new Date(date.getYear(), date.getMonth(), date.getDate(), date.getHours()-1,55);
        }
        String _od = dateFormat.format(date);
        //System.out.println("tak jake je datum: pred hodinou "+_od);
        return vypisHovoru("",_od,_do);

    }


    /**
     * Metoda pro vypis hovoru
     * @param linka cislo existujici linky, nebo vynechat
     * @param _od  YYYYMMDD
     * @param _do  YYYYMMDD
     * @return
     */
    public String vypisHovoru(String linka, String _od, String _do) {
        request = requestHead
                + "<ipbx:VoipexVypisHovoru>"
                + "<filtr>"
                + linka
                + "</filtr>"
                + "<vypisOd>"
                + xlDatum(_od)
                + "</vypisOd>"
                + "<vypisDo>"
                + xlDatum(_do)
                + "</vypisDo>"
                + "<typVypisu>1</typVypisu>" //1 odchozi hovory , 7 vse
                + "</ipbx:VoipexVypisHovoru>"
                + requestBottom;
        return request;
    }

    /**
     * Metoda urcena pro buzeni. 
     * Supluje funkci spojovatelky.
     * @param _z volat z cisla ( cislo pro prehrani hlasky )
     * @param _na volat na cislo ( budit )
     * @return retezec xml dotazu
     */
    public String vytocLinku(String _z, String _na) {
        request = requestHead
                + "<ipbx:originateExten>"
                + "<from>"
                + _z // z jakeho cisla se vytaci
                + "</from>"
                + "<to>"
                + _na // na jake cislo je volano
                + "</to>"
                + "<callerID>"
                + _z
                + "</callerID>"
                + "</ipbx:originateExten>"
                + requestBottom;
        return request;
    }

    /**
     * Metoda vypise vsechny nastavene linky.
     * @return retezec xml dotazu
     */
    public String vypisLinek() {
        request = requestHead
                + "<ipbx:getAllExten>"
                + "<numplan>"+plan+"</numplan>" //cislovaci plan kam cislo patri prazdno taky funguje
                + "</ipbx:getAllExten>"
                + requestBottom;
        return request;
    }

    /**
     * Metoda vypise nastaveni jedne linky.
     * @param linka kterou vypise
     * @return retezec xml dotazu
     */
    public String vypisNastaveniLinky(String id){
                request = requestHead
                + "<bin:extenInfo>"
                + "<ExtenName>"+id+"</ExtenName>"
                + "</bin:extenInfo>"
                + requestBottom;
        return request;
    }

       
    /**
     * Metoda pro upravu nastaveni linky (zatim jen popis a odchozi smerovani).
     * @param linka linka
     * @param plan odchozi smerovani - viz Odchozí směrování
     * @param popis popis linky
     * @return retezec xml dotazu
     */
    public String updateLinky(String id, String plan, String popis){
                      request = requestHead
                + "<bin:updateExten>" //<ipbx:updateExten>"
                + "<id>"+id+"</id>" //+ "<callerID>"+linka+"</callerID>"
                + "<params>"
                + "<ipbx:outRoute>"+plan+"</ipbx:outRoute>"
                + "<ipbx:note>"+popis+"</ipbx:note>"
                + "</params>"
               // + "<nameChannel - unbounded>"+linka+"</nameChannel - unbounded>"
                + "</bin:updateExten>"
                + requestBottom;
                      System.out.println("--******----******----******----******--");
        return request;
    }

    /**
     * Metoda pro upravu nastaveni linky (zatim jen odchozi smerovani)
     * hodlam ji pouzit na ukonceni moznosti odchoziho volani
     * @param linka linka
     * @param plan odchozi smerovani - viz Odchozí směrování
     * @param popis popis linky
     * @return retezec xml dotazu
     */
    public String updateLinky(String linka, String plan){
                      request = requestHead
                + "<bin:updateExten>" //<ipbx:updateExten>"
                + "<id>"+linka+"</id>" //+ "<callerID>"+linka+"</callerID>"
                + "<params>"
                + "<ipbx:outRoute>"+plan+"</ipbx:outRoute>"
                + "</params>"
               // + "<note>"+popis+"</note>"
               // + "<nameChannel - unbounded>"+linka+"</nameChannel - unbounded>"
                + "</bin:updateExten>"
                + requestBottom;
        return request;
    }


    /**
     * Metoda pro vypis vsech aktualne probihajicich volani.
     * @return retezec xml dotazu
     */
    public String vsecnhyVolani(){
         request = requestHead
                + "<ipbx:getAllExtenCalls/>"
                + requestBottom;
         return request;
    }

     /**
     * Metoda pro vypis nastaveni jedne linky.
     * @param linka kterou vypise
     * @return retezec xml dotazu
     */
    public String getExtenInfo(String linka){
                request = requestHead
                + "<ipbx:extenInfo>"
                + "<name>"+linka+"</name>"
                + "<dialPlanName>"+plan+"</dialPlanName> "
                + "</ipbx:extenInfo>"
                + requestBottom;
        return request;
    }



/**
 * Metoda pro ziskani stavu linky, jestli bylo vzbuzeno, ci nikoliv.
 * Pouziti pro kontrolu buzeni dane linky, koukne se x minut nazpet.
 * (definovano v properties souboru)
 * @param line cislo linky pro ktere zjistuji stav buzeni.
 * @return retezec xml dotazu
 */
   public String getCDRlist(String line){
    DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm");
        Date date = new Date();
        String _do = dateFormat.format(date);
        _do = _do.substring(0, 4)+"-"+_do.substring(4,6)+"-"+_do.substring(6,8)
                +"T"+_do.substring(9,14)+":00.000+01:00";
        //System.out.println("tak jake je datum: do "+_do);
        int minut = Integer.parseInt(_do.substring(14,16));
        if (minut >9) {
            date = new Date(date.getYear(), date.getMonth(), date.getDate(), date.getHours(),date.getMinutes()-10);
        } else {
            date = new Date(date.getYear(), date.getMonth(), date.getDate(), date.getHours()-1,60+date.getMinutes()-10);
        }
        String _od = dateFormat.format(date);
        _od = _od.substring(0, 4)+"-"+_od.substring(4,6)+"-"+_od.substring(6,8)
                +"T"+_od.substring(9,14)+":00.000+01:00";
        //System.out.println("tak jake je datum: od "+_od);
       request = requestHead +  "<bin:getCDRList>"
         + "<fromDate>" + _od + "</fromDate>"
         + "<toDate>" + _do + "</toDate>"
         + "<conditions>" 
         + "<ipbx:dstMatch>=</ipbx:dstMatch>"
         + "<ipbx:dstNumber>"+line+"</ipbx:dstNumber>"
         +"</conditions>"
      + "</bin:getCDRList>"
                + requestBottom;
        return request;

   }

    /**
     * Metoda pro aktualizaci zmen v ustredne
     * @return retezec xml dotazu
     */
    public String potvrd(){
         request = requestHead
                + "<ipbx:reloadConfiguration/>"
                + requestBottom;
         return request;
    }

    /**
     * Metoda pro ziskani nazvu PC a MAC adresy
     * @return nazev PC a MAC v desitkovem tvaru
     * @throws UnknownHostException 
     */
    public String readProperties() throws UnknownHostException
     {
      String vysledek=InetAddress.getLocalHost().getCanonicalHostName();
      try {
            InetAddress address = InetAddress.getLocalHost();
            NetworkInterface ni = NetworkInterface.getByInetAddress(address);
            if (ni != null) {
                byte[] mac = ni.getHardwareAddress();
                if (mac != null) {

                    for (int i = 0; i < mac.length; i++) {
                        vysledek+=mac[i];
                    }
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        }
      return vysledek;
     }

}
