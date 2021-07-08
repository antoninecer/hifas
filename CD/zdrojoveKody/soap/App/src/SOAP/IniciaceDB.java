package SOAP;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.xml.sax.SAXException;

/**
 * Trida pro iniciaci databaze - informace o nastavenych linkach v ustredne 
 * prevede do DB
 * @author ecer
 */
public class IniciaceDB {
    private DB d = new DB();
    private ArrayList<String> id=new ArrayList<String>();


    /**
     * Metoda, ktera ma na starost obcerstveni,
     * tedy smazani a naplneni tabulek linky a detail_linky
     * @throws SQLException
     */
    public IniciaceDB() throws SQLException{
        //smaze linky(id,linka)
        Menu.log1.info("Iniciace Databaze - Maze linky a detaily linky");
        d.update("delete from linky");
        //smaze detail_linky (pro kazdou liku je cca 12 sledovanych veci, nas zajima hlavne id, exten,outroute)
        //System.out.println("smaze detail_linky");
        d.update("delete from detail_linky");
        try {
            spust();
        } catch (Exception ex) {
            Logger.getLogger(IniciaceDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Vykonna metoda pro zaplneni tabulek linky a linky_detail
     * @throws UnsupportedEncodingException
     * @throws IOException
     * @throws SAXException
     * @throws SQLException
     */
    public void spust() throws SQLException {
        SoapFce sf = new SoapFce();
        String request = "";
        SoapSpust soap = new SoapSpust();
        Menu.log1.info("Iniciace DB - zaplneni tabulky linky z ustredny");
        //zaplneni tabulky linky z ipex pbx pres xml , je tam trabl ale osetrenej, ne natolik, aby to nervalo
        request=sf.vypisLinek();
        try {
            soap.soapKomunikace(request);
        } catch (Exception ex) {
            String chyba=ex.toString();
            JOptionPane.showMessageDialog(null, chyba);
        }
        try {
            //nacteni id linek z db
            //System.out.println("nacteni id linek z db");
           id = new ArrayList<String>(d.nactiId());

        } catch (SQLException ex) {
            String chyba=ex.toString();
            JOptionPane.showMessageDialog(null, chyba);
        }
        //pro kazde id si necham z ipex pbx vypsat a do db tabulky detail_linky
         //System.out.println("pro kazde id si necham z ipex pbx vypsat a do db tabulky detail_linky");
        for (int i=0;i<id.size();i++){
        request=sf.vypisNastaveniLinky(id.get(i));
        soap.setID(id.get(i));
            try {
                soap.soapKomunikace(request);
            } catch (Exception ex) {
                String chyba=ex.toString();
            JOptionPane.showMessageDialog(null, chyba);
            }
       }
        JOptionPane.showMessageDialog(null, "Nastavení linek z ústředny načteno");
    }

}