package SOAP;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.xml.sax.SAXException;

/**
 * Nacteni novych akci z db tabulky vstup 
 * do ArrayList<Akce> a zpracuje 
 * @author ecer
 */
public class NactiVstup {

    public static ArrayList<Akce> vstup;
    ExecutorService ap = Executors.newCachedThreadPool();
    //presunu do menu ExecutorService app = Executors.newCachedThreadPool();
    //newSingleThreadScheduledExecutor();

    /**
     * konstruktor volajici metodu delej
     * @throws SQLException
     * @throws UnsupportedEncodingException
     * @throws IOException
     * @throws SAXException
     */
    public NactiVstup() throws SQLException, UnsupportedEncodingException, IOException, SAXException {
        //System.out.println("<");
        delej();
    //System.out.println(">");
    }

    /**
     * spousteni v novem threadu metodu delej
     */
    public void run() {
        try {
            delej();
        } catch (Exception e) {
        }
    }

    /**
     * Vykonna metoda pro nacteni z tabulky vstup a spusteni tarifikace
     * @throws SQLException
     * @throws UnsupportedEncodingException
     * @throws IOException
     * @throws SAXException
     */
    public void delej() throws SQLException, UnsupportedEncodingException, IOException, SAXException {
        DB d = new DB();
        SoapFce sf = new SoapFce();
        SoapSpust spust = new SoapSpust();
        d.dbConnect();
        vstup = d.nactiVstup();
        String prikaz = "";
        String jak = "";
        for (int i = 0; i < vstup.size(); i++) {
            // System.out.println("i:"+i+" ");

            //pokud jsou nezpracovane, pak udelej:
            if (Integer.parseInt(vstup.get(i).getDone()) == 0 || Integer.parseInt(vstup.get(i).getDone()) == 3) {
                System.out.println("ID: " + vstup.get(i).getId() + " Akce " + vstup.get(i).getAkce() + " linka " + vstup.get(i).getLinka() + " idLinky " + vstup.get(i).getIdLinky() + " done " + vstup.get(i).getDone());
                if (vstup.get(i).getAkce().startsWith("01")) //checkin
                {
                    if (vstup.get(i).getAkce().equals("0101")) //checkout
                    {
                        jak = Menu.nastaveni.getProperty("volani_povol", "MZN"); //nacte z properties
                        String jmeno = "";
                        try {jmeno = vstup.get(i).getJmeno();} catch(Exception e){System.out.println("Todle se doufam nestane, aby nebylo jmeno");}
                        if (jmeno.length()>1) {
                            prikaz = sf.updateLinky(vstup.get(i).getIdLinky(), jak,jmeno);
                        } else {
                           prikaz = sf.updateLinky(vstup.get(i).getIdLinky(), jak);
                        }
                        try {
                            Menu.log1.info("Linka: Opravneni Linky " + vstup.get(i).getLinka() + " nastaveno na " + jak);
                            d.update("update detail_linky set outRoute='" + jak + "' , note='"+vstup.get(i)+"' where id='" + vstup.get(i).getIdLinky() + "' ");
                        //System.out.println("Updatuji detail_linky - " + opravneni.get(i).getLinka() + " opravneni - " + jak + ". ");
                        } catch (Exception e) {
                            Menu.log1.severe("Linka: Opravneni Linky " + vstup.get(i).getLinka() + " nastaveni na " + jak + " se nepovedlo");
                        }
                    }
                    if (vstup.get(i).getAkce().equals("0102")) //checkout
                    {
                        jak = Menu.nastaveni.getProperty("volani_zakaz", "INT"); //nacte z properties
                        prikaz = sf.updateLinky(vstup.get(i).getIdLinky(), jak,"");
                        try {
                            Menu.log1.info("Linka: Opravneni Linky " + vstup.get(i).getLinka() + " nastaveno na " + jak);
                            d.update("update detail_linky set outRoute='" + jak + "' where id='" + vstup.get(i).getIdLinky() + "' ");
                        //System.out.println("Updatuji detail_linky - " + opravneni.get(i).getLinka() + " poravneni - " + jak + ". ");
                        } catch (Exception e) {
                            Menu.log1.severe("Linka: Opravneni Linky " + vstup.get(i).getLinka() + " nastaveni na " + jak + " se nepovedlo");
                        }
                    }
                    spust.soapKomunikace(prikaz);
                    vstup.get(i).setDone("1");
                    prikaz = "update vstup set done='1' where id ='" + vstup.get(i).getId() + "'";
                    d.update(prikaz);
                }
                if (vstup.get(i).getAkce().equals("0301")) //buzeni
                {

                    int pocet = Integer.parseInt(Menu.nastaveni.getProperty("buzeni_odmaz", "15"));
                    int interval = d.interval(vstup.get(i).getId());
                    System.out.println("interval:" + interval);
                    if (interval > pocet) {
                        prikaz = "update vstup set done='2',info='Nedoslo ke vzbuzeni (" + pocet + ")' where id ='" + vstup.get(i).getId() + "'";
                        Menu.log1.info("Buzeni: Ukoncuji buzeni na lince" + vstup.get(i).getLinka() + ", ktere jiz vice jak " + pocet + " minut proslo" + vstup.get(i).getId());
                        // dodelani, ze bude posilat info o neuspesnem buzeni
                        String[] nevzbuzeno = d.buzeno(vstup.get(i).getId()).split(",");
                        //pokud je hifas_wakeup_false_word=yes pak pri ruseni buzeni posle vetu o neuspechu buzeni
                        if (Menu.nastaveni.getProperty("hifas_wakeup_false_word", "no").toUpperCase().equals("YES")) {
                            //System.out.println("pocet vysledku:"+nevzbuzeno.length+" lika:"+nevzbuzeno[1]+"  cas:"+nevzbuzeno[0]);
                            Menu.log1.info("Hifas: odesilam zpravu o nevzbuzeni linky " + vstup.get(i).getLinka());
                            Menu.single.execute(new Caracas(new Caracas().vzbuzeno(nevzbuzeno[1], nevzbuzeno[0], false)));
                        }
                        //20101130 pridelani vyzvaneni na lince o tom, ze nebylo vzbuzeno
                        int covolat = Integer.parseInt(Menu.nastaveni.getProperty("buzeni_upozorneni","-1"));
                        int kamvolat = Integer.parseInt(Menu.nastaveni.getProperty("buzeni_oznam","-1"));
                        if (covolat != -1 && kamvolat != -1){
                            //je covolat (hlaska o nevzbuzeni) a kamvolat (telefon kde se ozve problem)
                            
                            Menu.app.execute(new Buzeni(""+kamvolat, ""+covolat));
                        }
                        d.update(prikaz);
                    } else {
                        String porovnej = d.getdatum();
                        if (Integer.parseInt(vstup.get(i).getDone()) == 0 && (interval >= 0)) {
                            System.out.println("spousti se buzeni linky: " + vstup.get(i).getLinka() + " s parametry : " + vstup.get(i).getId());
                            //pokusim se tu o 2 vterinu zpozdeni aby se budilo vsude
                            long t0, t1;
                            t0 = System.currentTimeMillis();
                            do {
                                t1 = System.currentTimeMillis();
                            } while (t1 - t0 < 3333);
                            Menu.app.execute(new Buzeni(vstup.get(i).getId(), vstup.get(i).getLinka()));
                            vstup.get(i).setDone("3");
                           // byl problem se vzbuzenim vsech linkek, tak jsem presunul toto prerazeni z 0 do 3 na buzeni
                            // prikaz = "update vstup set done='3',info='V Buzeni'  where id ='" + vstup.get(i).getId() + "'";
                           // d.update(prikaz);
                        }
                    }
                }
            }
        }
        //jeste trosku tarifikace.
        d.conn.close();
        spust.soapKomunikace(sf.vypisHovoru());
    }
}