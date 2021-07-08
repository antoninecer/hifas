package SOAP;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Trida pro obsluhu buzeni a navratu kontroly vzbuzeni
 * @author ecer
 */
public class Buzeni implements Runnable {

    private SoapFce sf = new SoapFce();
    private SoapSpust sp;//
    private SoapSpust1 sp1;//pro portvrzeni musim pouzit jinou tridu s jinym handlerem, skoda
    private String linka;
    private int id;
    private String buditko;
    private String cil = "";
    private boolean opakuj = false;
    private ArrayList al = new ArrayList();

    /**
     * Konstruktor nastavujici tridni promenne id zaznamu a linku
     * @param i - id zaznamu v tebulce vstup
     * @param line - linka, ktera se ma budit
     * @throws SQLException
     */
    public Buzeni(int i, String line) throws SQLException {
        linka = line;
        cil = Menu.nastaveni.getProperty("buzeni_linka", "899");
        id = i;
        sp = new SoapSpust();
        sp1 = new SoapSpust1(id);
        opakuj = true;
    }

    /**
     * Konstruktor nastavujici tridni promenne 
     * @param target - kam se bude volat
     * @param line - linka, ktera se ma budit
     * @throws SQLException
     */
    public Buzeni(String line, String target) throws SQLException {
        linka = line;
        cil = target;
        id = 0;
        sp = new SoapSpust();
        sp1 = new SoapSpust1(id);
    }

    /**
     * Metoda run, ktera pobezi v novem vlaknu.
     * Vytoci linku pro buzeni a za minutu vrati zpravu o vysledku,
     * ci prenastavi buzeni o x minut dle property souboru
     */
    public void run() {
        try {
            //if (cil.length() > 0) {
            if (id == 0) {
                buditko = cil;
                Menu.log1.info("Vytacim cilovou linku " + cil + " pro linku " + linka);
                sp.soapKomunikace(sf.vytocLinku(linka, buditko));
            } else {
                 //mel jsem problemy se vzbuzenim vsech nastavenych linek, dam tedy preklopeni do stavu buzeni sem
                 new DB().update("update vstup set done='3',info='V Buzeni'  where id ='" + id + "'");
                // nacteni cisla linky pro buzeni z properties, defaultni hodnota je 899
                buditko = Menu.nastaveni.getProperty("buzeni_linka", "899");
                //al = new DB().select("select * from vstup where id=" + id + " and isnull(done,'0')='3'");
                opakuj = (new DB().select("select * from vstup where id=" + id + " and isnull(done,'0')='3'").size() > 0) ? true : false;
                //opakuj = (al.size() > 0) ? true : false;
                System.out.println("Buzeni.run.opakuj " + opakuj + " Buzeni.ru.al.size()" + al.size());
                // vytoci linku pro buzeni z linky, ktera se ma vzbudit
                //dodelame cele buzeni
                while (opakuj) { //bud dokud nevzbudis
                    try {
                        //al = new DB().select("select * from vstup where id=" + id + " and isnull(done,'0')='3'");
                        opakuj = (new DB().select("select * from vstup where id=" + id + " and isnull(done,'0')='3'").size() > 0) ? true : false;
                        System.out.println("ID: " + id + " Opakuj: " + opakuj);
                        if (!opakuj) {
                            break;
                        }
                        Menu.log1.info("Budim " + linka + " volanim na " + buditko);
                        sp.soapKomunikace(sf.vytocLinku(linka, buditko));
                        // uspime vlakno na 1 minutu (overeno dle ustredny Vyskocilova)
                        Thread.sleep(60000);
                        // zjisteni, zda byla linka vzbuzena
                        sp1.soapKomunikace(sf.getCDRlist(linka));
                        int cekej = (Integer.parseInt(Menu.nastaveni.getProperty("buzeni_opakovani", "5")) * 1000 * 60) - 60000;
                        //System.out.println("Cekam na dalsi buzeni (ms):"+cekej);
                        Thread.sleep(cekej);
                    //if(pokusu==0){opakuj=false;}

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(Buzeni.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
