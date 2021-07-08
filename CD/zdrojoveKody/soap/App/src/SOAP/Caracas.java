package SOAP;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Trida pro komunikaci s PMS emulujici system Caracas
 * pracujici na bazy vymeny souboru
 * @author ecer
 */
public class Caracas implements Runnable {
    /*
     * Do ustredny:
    ------------
    nastaveni buzeni na linku 202  7.9.2010 11:55 jednorazove
    031202   0000000000070910115510
    otevri linku 358 jmeno a zrus buzeni na tuto linku
    011101   000000000000101BRANDT, GEORG
    otevri linku 358 jmeno bez zruseni buzeni
    091101   000000000000901BRANDT, GEORG
    uzamkni linku 804
    011804   000000000000201GYNA, TOMAS
    do Hotelaku:
    -----------
    linka 202 7.9.2010 11:55 jednorazove  byla vzbuzena
    171202   000000000007091011551
    poklizeno - nebude se implementovat !!!
    131354   000000000001
    tarifikacni veta linka 835 24.10.2009 15:30:02 do 15:30:51 nekaky kod '0010' znovu linka 6mist,trunk '0   '
    19,11,SF ,Tarifikace,,111%linka,sL06 %0000000000%isystem,d01-99%10%datum,yymmdd%%cas1,hhmmss%%cas2,hhmmss%00%skupinaid,d00-99%%linka2,sL06 %%trunk,sL04 %%impuls,d00000-99999%%target,sL16 %%destinace,d0-9%0  %cena,d0000000-9999999%0
    111835   000000000021100910241530021530510010835   0   00000481548111       00  00000003500
     */

    final String lineSeparator = System.getProperty("line.separator");
    final String CARACAS = Menu.nastaveni.getProperty("hifas_file_in", "D:\\work\\interface\\caracas.ftp");
    final String HOST = Menu.nastaveni.getProperty("hifas_file_out", "D:\\work\\interface\\host.ftp");
    final String HOST1 = Menu.nastaveni.getProperty("hifas_file_out_safe", "D:\\work\\interface\\host.ft#");
    File caracas;
    File host;
    File host1;

    /**
     * prazdny konstruktor
     */
    public Caracas() {
    }

    /**
     * konstruktor pro pouziti v single threadu
     * @param s
     * @throws java.io.IOException
     */
    public Caracas(String s) throws IOException {
        zapisHost(s);
    }

    /**
     * Metoda vraci caracas vetu odpovedi na vzbuzeni
     * @param linka linka, ktera byla buzena
     * @param cas cas buzeni
     * @param jak vysledek true - vzbuzeno, false - nevzbuzeno
     * @return caracas veta o buzeni dane linky v dany cas
     */
    public String vzbuzeno(String linka, String cas, boolean jak) {
        // linka 202 7.9.2010 11:55 jednorazove  byla vzbuzena
//171202   000000000007091011551
//2010-09-10 21:05:00.000
        int i = linka.length();
        for (int j = i; j < 6; j++) {
            linka = linka + " ";
        }
        String uspech = jak ? "1" : "0";
        //System.out.println("tady je linka:" + linka + ":" + linka.length());
        String vysledek = "171" + linka + "0000000000" + cas.substring(8, 10) + cas.substring(5, 7) + cas.substring(2, 4)
                + cas.substring(11, 13) + cas.substring(14, 16) + uspech;
        return vysledek;
    }

    /**
     * Metoda vraci tarifikacni vetu pro hotelovy interface
     * @param linka pro kterou linku
     * @param cil kam bylo volano
     * @param cas kdy bylo volano
     * @param delka jak dlouho bylo volano
     * @param trunk pres jaky trunk bylo volano
     * @return tarifikacni veta ve formatu caracas link
     */
    public String tatifikace(String linka, String cil, String cas, int delka, String trunk) {
        int i = linka.length();
        for (int j = i; j < 6; j++) {
            linka = linka + " ";
        }
        i = trunk.length();
        for (int j = i; j < 4; j++) {
            trunk = trunk + " ";
        }
        i = cil.length();
        for (int j = i; j < 16; j++) {
            cil = cil + " ";
        }
        int hodin = delka / 3600;
        int minut = (delka / 60) - (hodin * 3600);
        int vterin = delka - (hodin * 3600) - (minut * 60);

        int h1 = Integer.parseInt(cas.substring(11, 13));
        int m1 = Integer.parseInt(cas.substring(14, 16));
        int v1 = Integer.parseInt(cas.substring(17, 19));
        System.out.println("h1:" + h1 + "m1:" + m1 + "v1" + v1);
        //pridam vteriny
        if (vterin + v1 > 59) {
            if (minut + 1 > 59) {
                minut = 0;
                hodin = hodin + 1;
            } else {

                minut = minut + 1;
            }
        } else {
            vterin = vterin + v1;
        }
        System.out.println("po pridani vterin: hodin" + hodin + " minut" + minut + " sekund" + vterin);
        //pridam minuty
        if (minut + m1 > 59) {

            minut = minut + m1 - 60;
            hodin = hodin + 1;
        } else {
            minut = minut + m1;
        }
        System.out.println("po pridani minut: hodin" + hodin + " minut" + minut + " sekund" + vterin);
        //pridam hodiny
        if (hodin + h1 > 23) {
            hodin = 0;
        } else {
            hodin = hodin + h1;
        }
        System.out.println("po pridani hodin: hodin" + hodin + " minut" + minut + " sekund" + vterin);
        System.out.println("hod2:" + hodin + "min2:" + minut + "sec2:" + vterin);

        String hod = String.valueOf(hodin);
        if (hod.length() == 1) {
            hod = "0" + hod;
        }
        String min = String.valueOf(minut);
        if (min.length() == 1) {
            min = "0" + min;
        }
        String sec = String.valueOf(vterin);
        if (sec.length() == 1) {
            sec = "0" + sec;
        }
        System.out.println(hod + min + sec + "    tak je to");
//2010-07-01 14:36:47
//CVS format: cislo_int;cislo_ext;destinace;datetime;odchozi;placeny;stav;flags;delka;cena;pocet;soukromy hovor;trunk
//
//111997   000000000021101009102105230003130010997   0   00000602654814       00            00000000000
        //296831705;286883507;;2010-07-01 12:46:00;;N;;;172;;;0;
        String vysledek = "111" + linka + "00000000002110"
                + cas.substring(2, 4) + cas.substring(5, 7) + cas.substring(8, 10)
                + cas.substring(11, 13) + cas.substring(14, 16) + cas.substring(17, 19)
                + hod + min + sec + "0010" + linka + trunk + "00000" + cil + "00            00000000000";
        return vysledek;
    }

    /**
     * Metoda pro zapis do host.ftp
     * potvrzeni buzeni , tarifikace
     */
    public void zapisHost(String s) throws IOException {

        host = new File(HOST);
        host1 = new File(HOST1);

        // DB d = new DB();
        if (host.exists()) {
            //zapis do host.ft#    
            FileWriter fw = new FileWriter(host1, true);
            fw.write(s + lineSeparator);
            fw.flush();
            fw.close();

        } else {

            //host.ftp neexistuje
            if (host1.exists()) {
                host1.renameTo(host); //prejmenuje hos.ft# na host.ftp foufam
                FileWriter fw = new FileWriter(host1, true);
                fw.write(s + lineSeparator);
                fw.flush();
                fw.close();

                //otevre host.ft# pro prepis
            } else {
                //zapis do host.ftp
                FileWriter fw = new FileWriter(host);
                fw.write(s + lineSeparator);
                fw.flush();
                fw.close();

            }

        }

    }

    /**
     * Metoda, ktera podle nactene vety zjisti co se ma vykonat a to ulozi 
     * do DB do tabulky vstup
     * @param nacteno
     * @throws SQLException 
     */
    public void kontroluj(String nacteno) throws SQLException {
        DB d = new DB();
        if (nacteno.startsWith("011")) {
            String linka = nacteno.substring(3, 9).trim();
            String jmeno = "";
            if (nacteno.length() > 25) {
                try {
                    jmeno = nacteno.substring(24, nacteno.length()).trim();
                    System.out.println("Jmeno linky: " + jmeno);
                } catch (Exception e) {
                    System.out.println("problem s prirazenim jmena ");
                }
            }
            String sql = "insert into vstup (akce,linka,cas,info,jmeno) values ";
            if (nacteno.substring(21, 22).equals("1")) {
                Menu.log1.info("Caracas vstup - otevri linku: " + linka);
                sql = sql + "('0101','" + linka + "','" + d.getdatum() + "','Otevri linku','" + jmeno + "')";
                d.update(sql);
                if (Menu.nastaveni.getProperty("buzeni_zrus_pri_checkin", "no").toUpperCase().equals("YES")) {
                    d.update("update vstup set done='2', info='Novy Check-in' where akce='0301' and cas > getdate() and isnull(done,'0') in ('0','3') and linka='" + linka + "'");
                    System.out.println("Ukoncuji nastavene buzeni na linku" + linka);
                }
            }
            if (nacteno.substring(21, 22).equals("2")) {
                Menu.log1.info("Caracas vstup - uzavri linku: " + linka);
                sql = sql + "('0102','" + linka + "','" + d.getdatum() + "','Uzavri linku','" + jmeno + "')";
                System.out.println("SQL :" + sql);
                d.update(sql);
            }
            System.out.println("sql:>" + sql + "<");

        }
        if (nacteno.startsWith("091")) {
            String linka = nacteno.substring(3, 9).trim();
            String sql = "insert into vstup (akce,linka,cas,info) values ";
            if (nacteno.substring(21, 22).equals("9")) {
                Menu.log1.info("Caracas vstup - otevri linku: " + linka);
                sql = sql + "('0101','" + linka + "','" + d.getdatum() + "','Otevri linku')";
                d.update(sql);
            }
            System.out.println("sql:>" + sql + "<");
        }
        // nastav buzeni
        if (nacteno.startsWith("031")) {
            //031997   0000000000250810190010
            System.out.println(nacteno);
            int linka = Integer.parseInt(nacteno.substring(3, 9).trim());
            // System.out.println("linka:" + linka);
            String dd = nacteno.substring(19, 21);
            // System.out.println("dd:" + dd);
            String mm = nacteno.substring(21, 23);
            // System.out.println("dd:" + mm);
            String rr = nacteno.substring(23, 25);
            // System.out.println("rr:" + rr);
            String hod = nacteno.substring(25, 27);
            // System.out.println("hod:" + hod);
            String min = nacteno.substring(27, 29);
            // System.out.println("min:" + min);
            String stav = nacteno.substring(29, 31);
            // System.out.println("stav:" + stav);
            String sql = "";
            if (stav.startsWith("1")) {
                String datum = "20" + rr + "-" + mm + "-" + dd + " " + hod + ":" + min + ":00.000";
                Menu.log1.info("Caracas vstup - nastaveni buzeni. Linka: " + linka + " datum:" + datum);
                sql = "insert into vstup(akce,linka,cas,cas2,info) values('0301','" + linka + "' ,'" + datum + "' ,'" + datum + "','Nastav buzeni' )";
                System.out.println("dotaz:" + sql);

            }
            if (stav.startsWith("2")) {
                String datum = "20" + rr + "-" + mm + "-" + dd + " " + hod + ":" + min + ":00.000";
                Menu.log1.info("Caracas vstup - rusim buzeni. Linka: " + linka + " datum:" + datum);
                sql = "update vstup set done=2, info='Zruseno prikazem z caracasu' where akce='0301' and linka='" + linka + "' and cas2='" + datum + "'";
                //System.out.println("dotaz:" + sql);
            }
            if (sql.length() > 1) {
                //System.out.println(sql);
                d.update(sql);
            }
        }
    }

    /**
     * Metoda pro nacteni souboru caracas.ftp
     * umi nacist nastav buzeni a odemkni a uzamkni linku
     * V teto metode je pouzita jednoducha ochrana souboroveho prenosu
     * pokud existuje vystupni soubor .ftp, zapisuj do .ft#
     * kdyz neni .ftp a je .ft# prejmenuj ft# > ftp
     * kdyz je caracas.ftp , nacti jej a smaz, to same dela protistrana
     * @throws FileNotFoundException
     */
    public void nacti() throws FileNotFoundException, NullPointerException {
        caracas = new File(CARACAS);

        host = new File(HOST);
        host1 = new File(HOST1);
        if (host.exists()) {
            //nic nedelej host.ftp existuje a dal se bude zapisovat do host.ft#
        } else {
            if (host1.exists()) { // no jestli existuje host.ft# , prejmenuj na hos.ftp
                host1.renameTo(host);
            }
        }
        if (caracas.exists()) {
            Menu.log1.info("Nacitam soubor " + CARACAS);
            BufferedReader br = new BufferedReader(new FileReader(caracas));
            try {
                String nacteno;
                while ((nacteno = br.readLine()) != null) {
                    kontroluj(nacteno);
                }
                br.close();
                //System.out.println("mazu soubor");
                caracas.delete(); //smazani souboru

            } catch (Exception e) {
                Menu.log1.severe("chyba v nacteni souboru caracas");
            }
        } else {
        }
    }

    /**
     * Metoda automaticky generovana pri pouziti volani vlaken
     */
    public void run() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
