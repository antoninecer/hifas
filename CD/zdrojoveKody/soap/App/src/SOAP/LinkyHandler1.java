package SOAP;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;

/**
 * Trida MyHandler1 pro nacteni XML dokumentu a naplneni hodnot
 * tady jedu pro cdr list, jelikoz je ns2:item atd jiz pouzito 
 * a musim kontrolu buzeni udelat jinak
 * @author ecer
 */
class LinkyHandler1 extends DefaultHandler {

    Caracas c = new Caracas();
    DB d = new DB();
    public ArrayList<String> number = new ArrayList<String>();
    public ArrayList<String> id = new ArrayList<String>();
    int cislo = 0;
    private String currentField = "";
    private Linka linka = new Linka();
    private String ide;
    private ArrayList<String> cdr = new ArrayList<String>(); //pro vypis zodpovezeni buzeni
    private boolean jeCdr = false; //tu odchytim, zda-li jde o stejnou vetu, mam tu prvni konflikt ns2:item
    //private int vysledek; // kolik vet updatovano, nakonec nevyuzito
    //private boolean answer = false;  //resim jinak nez promenou v enddocument
    private int idVstupu;
    private String source = "";
    private String target = "";
    private String kdy="";

    public LinkyHandler1(int i) {
        idVstupu = i;
    }

    public void setId(String id) {
        this.ide = id;
        //System.out.println("linkyhandler setid " + ide);
    }

    public Linka getLinka() {
        return linka;
    }

    /**
     * Prepsana metoda characters , ktera zajisti zpracovani elementu
     * a odpovi vetu o vysledku buzeni
     * @param ch
     * @param start
     * @param length
     * @throws SAXException
     */
    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        String value = new String(ch, start, length);
        //System.err.println("Value:"+value);
        if (currentField != null) {
            if (currentField.equals("ns1:getCDRListResponse")) {
                jeCdr = true;
                //System.out.println("jeCDR veta ? " + jeCdr);
            }


            if (currentField.equals("ns2:calldate")) { //jde o datum cdr zaznamu
                cdr.add(value);
                //System.out.println("field:" + currentField + ":" + value);
                kdy=value;
            }
            if (currentField.equals("ns2:disposition")) { //jde o cdr jak zodpovezeno
                cdr.add(value);
                //System.out.println("field:" + currentField + ":" + value);
            }


            if (currentField.equals("ns2:id")) {
                //cdr.add(value);
                //System.out.println("field:" + currentField + ":" + value);
            }
            if (currentField.equals("ns2:srcNumber")) {
                source = value;
            }
            if (currentField.equals("ns2:dstNumber")) {
                target = value;

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
        currentField = null;


    }

    /**     * Prepsana metoda startElement , ktera zajisti nastaveni nazvu elementu
     * @param uri
     * @param localName
     * @param qName
     * @throws SAXException
     */
    @Override
    public void startElement(String uri, String localName,
            String qName, Attributes attributes)
            throws SAXException {
        currentField = qName;
        //System.out.println("element>>" + currentField + "<<");


    }

    /**     * Prepsana metoda endDocument , ktera zajisti zapis do SQL
     * @param uri
     * @param localName
     * @param qName
     * @throws SAXException
     */
    @Override
    public void endDocument() {
        //System.out.println("velikost cdr zaznamu:" + cdr.size());
        for (int i = 0; i < cdr.size(); i++) {
            //System.out.println("i:" + i + ":" + cdr.get(i));
            if (i == cdr.size() - 1) {
                String sql = "";
                String odpoved = "";
                if (cdr.get(i).equals("ANSWERED")) {
                    Menu.log1.info("Buzeni: linka "+source+" uspesne vzbuzena");
                    sql = "update vstup set done=1,info='Uspesne vzbuzeno' where id=" + idVstupu;
                    odpoved = c.vzbuzeno(source, kdy, true);
                } else {
                    //nezodpovezeno zmeni cas pro buzeni
                    String kolik = Menu.nastaveni.getProperty("buzeni_opakovani", "5");
                    Menu.log1.info("Buzeni: linku "+source+" se nepodarilo vzbudit, nastavuji novy termin o "+kolik+" pozdeji");
                    sql = "update vstup set cas=dateadd(mi,"+kolik+",getdate()),done='3' where id=" + idVstupu;
                   // pri neuspesnem buzeni to nic nepise, proc ja odpoved = c.vzbuzeno(source, kdy, false);
                    //tady to chce nejaky rozdil cas a cas2, kdyz > 15

                }
                try {
                    System.out.println("sql: " + sql);
                    d.update(sql);
                    try {
                       if(odpoved.length()>1) Menu.single.execute(new Caracas(odpoved));
                    } catch (IOException ex) {
                        Menu.log1.severe("hifas: nepodarilo se zapsat. "+ex.toString());
                        //Logger.getLogger(LinkyHandler1.class.getName()).log(Level.SEVERE, null, ex);
                    }

                } catch (SQLException ex) {
                    Menu.log1.severe("DB: Nepodarilo se updatovat zaznam. "+ex.toString());
                    //Logger.getLogger(LinkyHandler1.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    @Override
    public void startDocument() {
       //System.out.println("\nzacatek dokumentu:");
    }
}
