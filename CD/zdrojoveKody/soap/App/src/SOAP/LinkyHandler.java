package SOAP;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;

/**
 * Trida LinkyHandler pro nacteni XML dokumentu a naplneni hodnot
 * @author ecer
 */
class LinkyHandler extends DefaultHandler {

    DB d = new DB();
    Caracas c = new Caracas();
    public ArrayList<String> number = new ArrayList<String>();
    public ArrayList<String> id = new ArrayList<String>();
    int cislo = 0;
    private String currentField = "";
    private Linka linka = new Linka();
    private String ide;
    private ArrayList<String> cdr = new ArrayList<String>(); //pro vypis zodpovezeni buzeni
    private boolean jeCdr = false; //tu odchytim, zda-li jde o stejnou vetu, mam tu prvni konflikt ns2:item
    private int vysledek; // kolik vet updatovano, nakonec nevyuzito
    private StringBuffer stringBuffer;
    private Scanner scanner;

    /**
     * Metoda pro nastaveni id zaznamu
     * @param id id zaznamu
     */
    public void setId(String id) {
        this.ide = id;
        //System.out.println("linkyhandler setid " + ide);
    }

    /**
     * Metoda pro ziskani linky
     * @return linka
     */
    public Linka getLinka() {
        return linka;
    }

    /**
     * Prepsana metoda characters , ktera zajisti zpracovani elementu
     * a ulozeni do statickych promennych
     * @param ch
     * @param start
     * @param length
     * @throws SAXException
     */
    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {

        stringBuffer.append(ch, start, length);

        String value = new String(ch, start, length);
        //System.err.println("Value:"+value);
        if (currentField != null) {
            if (currentField.equals("response")) { //jde o cdr jak zodpovezeno
                System.out.println("response:" + value);
            }
            if (currentField.equals("ns2:number")) {
                number.add(value);
            }

            if (currentField.equals("ns2:id")) {
                id.add(value);
                try {
                    if (number.size() < id.size()) {  //pravidelne na stejnem vzorku se vzdy stane po x desitkach radek, ze neni vse, posledni znak je utnut a vlozen jako jediny do dalsiho value, resim updatem
                        number.add("000");
                        id.set(cislo - 1, id.get(cislo - 1) + id.get(cislo));
                        Menu.log1.info("oprava:" + "update linky set id=" + id.get(cislo - 1) + " where number= " + number.get(cislo - 1) + " ");
                        d.update("update linky set id=" + id.get(cislo - 1) + " where number=" + number.get(cislo - 1) + " ");
                    } else {
                        System.out.println(cislo + ": " + number.get(cislo).toString() + " - " + id.get(cislo).toString());
                        d.update("insert into linky(number,id) values(" + number.get(cislo) + "," + id.get(cislo) + ")");
                    }
                } catch (Exception ex) {
                    Logger.getLogger(LinkyHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
                cislo++;
            }


            if (currentField.equals("ns2:name")) {
                linka.setName(value);
                //System.out.println("ns2:name" + value);
            }
            if (currentField.equals("ns2:dialPlanName")) {
                linka.setDialPlanName(value);
                //System.out.println("ns2:dialPlanName" + value);
            }
            if (currentField.equals("ns2:extension")) {
                linka.setExtension(value);
                //System.out.println("ns2:extension" + value);
            }
            if (currentField.equals("ns2:globalExten")) {
                linka.setGlobalExten(value);
                //System.out.println("ns2:globalExten" + value);
            }
            if (currentField.equals("ns2:CID1")) {
                linka.setCID1(value);
                //System.out.println("ns2:CID1" + value);
            }
            if (currentField.equals("ns2:CID2")) {
                linka.setCID2(value);
                //System.out.println("ns2:CID2" + value);
            }
            if (currentField.equals("ns2:outRoute")) {
                linka.setOutRoute(value);
                //System.out.println("ns2:outRoute" + value);
            }
            if (currentField.equals("ns2:ringDuration")) {
                linka.setRingDuration(value);
                //System.out.println("ns2:dialPlanName" + value);
            }
            if (currentField.equals("ns2:ringType")) {
                linka.setRingType(value);
                //System.out.println("ns2:ringType" + value);
            }
            if (currentField.equals("ns2:username")) {
                linka.setUsername(value);
                //System.out.println("ns2:username" + value);
            }
            if (currentField.equals("ns2:note")) {
                linka.setNote(value);
                //System.out.println("ns2:note" + value);
            }
            if (currentField.equals("ns2:password")) {
                linka.setPassword(value);
                //System.out.println("ns2:password" + value);
                //System.out.println("linkyhandler ide:" + ide + " ale linka.get :" + linka.getId());
                linka.setId(ide);
                String sql;
                //System.out.println("linkyhandler ide:" + ide + " a tu je menena linka.get :" + linka.getId());
                sql = "insert into detail_linky(id,name,dialplan,extension,globalExten,ccid1,ccid2,outRoute,ringDuration,ringType,username,password,note)"
                        + "values ('" + linka.getId() + "','" + linka.getName() + "','" + linka.getDialPlanName()
                        + "','" + linka.getExtension() + "','" + linka.getGlobalExten() + "','" + linka.getCID1() + "','" + linka.getCID1()
                        + "','" + linka.getOutRoute() + "','" + linka.getRingDuration() + "','" + linka.getRingType() + "','" + linka.getUsername() + "','" + linka.getPassword() + "','" + linka.getNote() + "')";
                //System.out.println(sql);
                try {
                    // nesmysl logovatMenu.log1.info("vkladam udaj")
                    vysledek = d.update(sql);
                } catch (SQLException ex) {
                    if (ex.getErrorCode() == 2627) {
                        sql = "update detail_linky set name='" + linka.getName() + "', outRoute='" + linka.getOutRoute() + "' where id='" + linka.getId() + "'";
                        System.out.println("zaznam jiz existuje, updatuji nazev a outroute");
                        //System.out.println("SQL: " + sql);
                        try {
                            d.update(sql);
                        } catch (SQLException ex1) {
                            Logger.getLogger(LinkyHandler.class.getName()).log(Level.SEVERE, null, ex1);
                        }
                    } else {
                       Menu.log1.severe("Problem s sql prikazem: "+sql);
                        // System.out.println("Problem se zapisem do DB");
                    }
                }
            } else if (currentField.equals("ns2:note")) {
                linka.setNote(value);
            } //cdr vysledek
            else if (currentField.equals("ns2:disposition")) {
                cdr.add(value);
                //System.out.println("value");
            } //cdr vysledek linky
            else if (currentField.equals("ns2:dstNumber")) {
                cdr.add(value);
                //System.out.println("value");
            } else if (currentField.equals("list")) {
                cdr.add(value);
                //System.out.println("tralala");
            }
        }
    }

    /**
     * Prepsana metoda endElement , ktera zajisti odmazani nazvu elementu
     * @param uri
     * @param localName
     * @param qName
     * @throws SAXException
     */
    @Override
    public void endElement(String uri, String localName,
            String qName) throws SAXException {
try{
        if (currentField.equals("vypis")) {
        String hodnota = stringBuffer.toString();
        scanner = new Scanner(hodnota);
        while (scanner.hasNextLine()) {
           String veta=scanner.nextLine();
         //tady otchytavam vety tarifikace

                System.out.println(veta);
                //String[] radky = veta.split("\n");
                //for (int radek=0;radek<radky.length;radek++){
                //    System.out.println("tak radek "+ radek + " : "+radky[radek]);
                String[] temp = veta.split(";");// radky[radek].split(";"); //rozdelim string po castech do pole sringu, oddelovac je strednik
                // System.out.println("velikost vety je:"+temp.length);
                if (temp.length == 13) { //pravidelne na stejnem vzorku se vzdy stane po x desitkach radek, ze neni vse, posledni znak je utnut a vlozen jako jediny do dalsiho value
                    String sql = "insert into tarifikace(_z,_na,pole2,kdy,priznak1,priznak2,popis,pole7,delka,pole10,pole11,pole12,pole13) values('" + temp[0].trim() + "','" + temp[1] + "','" + temp[2] + "','" + temp[3] + "','" + temp[4] + "','" + temp[5] + "','" + temp[6] + "','" + temp[7] + "'," + temp[8] + ",'" + temp[9] + "','" + temp[10] + "','" + temp[11] + "','" + temp[12] + "')";
                    
                    try {
                        if(d.existujeTarifikace(temp[0].trim(),temp[1], temp[3]))
                        {
                            //System.out.println("sql :" + sql);
                            Menu.log1.info("Tarifikace: linka "+temp[0]+" cil "+temp[1]+" datum "+temp[3]+" delka"+temp[8]);
                          d.update(sql);
                        if (temp[11].length() < 1) {
                            temp[11] = "0";// a mam tu problem trun se jmenuje "vyskocilova" tak dam natvrdo "0"
                        }
                        if (temp[5].contains("Y")) {
                            Menu.single.execute(new Caracas(c.tatifikace(temp[0].trim(), temp[1], temp[3], Integer.parseInt(temp[8]), "0")));//c.zapis(c.tatifikace(temp[0].trim(), temp[1], temp[3], Integer.parseInt(temp[8]), "0"));
                        }}else{
                            //System.out.println("zaznam jiz v tarifikaci existuje");
                        }
                    } catch (Exception ex) {
                        Menu.log1.severe("Chyba - sql: "+sql);
                        Logger.getLogger(LinkyHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    //System.out.println("Zaznam "+temp + "ma delku: " + temp.length);
                }//}
            }



        }
    }catch (NullPointerException ne){
        //System.out.println("Tak currenfield je null");
    }
        currentField = null;
    }

    /** Prepsana metoda startElement , ktera zajisti nastaveni nazvu elementu
     * @param uri
     * @param localName
     * @param qName
     * @param attributes
     * @throws SAXException
     */
    @Override
    public void startElement(String uri, String localName,
            String qName, Attributes attributes)
            throws SAXException {
        currentField = qName;
        stringBuffer=new StringBuffer();
    }

    /**
     * Prepsana metoda endDocument
     */
    @Override
    public void endDocument() {
        // System.out.println("velikost cdr zaznamu:" + cdr.size());
        for (int i = 0; i < cdr.size(); i++) {
            System.out.println("i:" + i + ":" + cdr.get(i));
        }
        //System.out.println("\nKonec dokumentu");
    }

    /**
     * Prepsana metoda startDocument
     */
    @Override
    public void startDocument() {
        //System.out.println("\nzacatek dokumentu:");
    }
}
