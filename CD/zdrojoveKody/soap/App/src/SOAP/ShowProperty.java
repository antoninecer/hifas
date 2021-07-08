package SOAP;

import java.util.*;
import java.lang.*;
import java.net.*;

/**
 * Trida pro ziskani informaci o PC, na kterem program bezi
 * @author tonda
 */
public class  ShowProperty
{
    /**
     * Metoda pro nacteni vlastnosti PC
     * @return nazev PC a MAC adresu sitove karty
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