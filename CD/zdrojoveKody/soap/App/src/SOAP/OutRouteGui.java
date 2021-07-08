package SOAP;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 * Trida pro nastaveni odchozi routy pro linky
 * @author ecer
 */
public class OutRouteGui extends JFrame {
    private DB d = new DB();
    private ArrayList<Linka> linka;
    private ButtonGroup skupinaRadio;
    private ArrayList<String> listRoutu = new ArrayList<String>(d.nactiRouty());
    private JPanel routy;
    private ArrayList<String> extension = new ArrayList<String>();
    private ArrayList<String> id = new ArrayList<String>();
    private ArrayList<String> outroute = new ArrayList<String>();
    private ArrayList<JRadioButton> listButonu;
    private JFrame ram;
    private SoapFce sf = new SoapFce();
    private SoapSpust soap = new SoapSpust();

    /**
     * konstruktor - graficke zobrazeni
     * @throws SQLException
     */public OutRouteGui() throws SQLException {
        ram = new JFrame("Zamknuti/odemknuti linky");
        ram.setSize(300, 300);
        ram.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JButton aplikuj = new JButton("Aplikuj");
        linka = new ArrayList<Linka>(d.nactiOpravneniLinek());
        int vybrano;
        for (int i = 0; i < linka.size(); i++) {
            extension.add(linka.get(i).getExtension());
            id.add(linka.get(i).getId());
            //System.out.println("ha " + id.get(i));
            outroute.add(linka.get(i).getOutRoute());
        }
        final JComboBox select = new JComboBox(extension.toArray());
        select.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setRout(outroute.get(select.getSelectedIndex()));
            }
        });

        aplikuj.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String tempId = id.get(select.getSelectedIndex());
                //System.out.println("tak hajzele ukaz tempid:" + tempId);
                String tempExtension = extension.get(select.getSelectedIndex());
                String tempOutroute = outroute.get(select.getSelectedIndex());
                String prikaz = sf.updateLinky(tempId, getRout());
                soap.setID(tempId);
                try {

                    soap.soapKomunikace(prikaz);
                } catch (Exception ex) {
                    Menu.log1.log(Level.SEVERE, "Linka: Problem se zmenou opravneni linky{0} routa: {1} - {2}", new Object[]{tempExtension, tempOutroute, ex.toString()});
                    Logger.getLogger(OutRouteGui.class.getName()).log(Level.SEVERE, null, ex);
                }
                prikaz = sf.vypisNastaveniLinky(tempId);
                try {
                    soap.soapKomunikace(prikaz);
                } catch (Exception ex) {
                    Menu.log1.log(Level.SEVERE, "Problem s nactenim opravneni linky{0} - {1}", new Object[]{tempExtension, ex.toString()});
                    Logger.getLogger(OutRouteGui.class.getName()).log(Level.SEVERE, null, ex);
                }
                // potvrzeni zmen ustredne
                prikaz = sf.potvrd();
                try {
                    soap.soapKomunikace(prikaz);
                } catch(Exception ex) {
                    Menu.log1.log(Level.SEVERE, "nemohu ziniciovat nove nastaveni ustredny{0}", ex.toString());
                    Logger.getLogger(OutRouteGui.class.getName()).log(Level.SEVERE, null, ex);
                }

                try {
                    outroute.set(select.getSelectedIndex(), d.nactiRouty(tempId));
                    //System.out.println("tady posi co je v outroute po zmene :" + outroute.get(select.getSelectedIndex()));
                } catch (SQLException ex) {
                    Menu.log1.log(Level.SEVERE, "Problem se zapisem opravneni linek do DB {0}", ex.toString());
                    Logger.getLogger(OutRouteGui.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        //select.addItem(extension.toArray());
        ram.setLayout(new GridLayout(3, 1));
        ram.add(select);
        listButonu = new ArrayList<JRadioButton>();
        routy = new JPanel();
        routy.setLayout(new GridLayout(listRoutu.size(), 1));
        skupinaRadio = new ButtonGroup();
        for (int i = 0; i < listRoutu.size(); i++) {
            listButonu.add(new JRadioButton(listRoutu.get(i)));
            skupinaRadio.add(listButonu.get(i));
            routy.add(listButonu.get(i));
        }
        vybrano = select.getSelectedIndex();
        //System.out.println("vybrana linka:" + extension.get(vybrano) + " routa: " + outroute.get(vybrano));

        setRout(outroute.get(vybrano));
        ram.add(routy);
        ram.add(aplikuj);
        ram.setVisible(true);
    }

     /**
      * metoda pro nastaveni routy
      * @param s nazev routy
      */
    public void setRout(String s) {
        for (int i = 0; i < listRoutu.size(); i++) {
            String[] temp;
            temp = listRoutu.get(i).split(":");
            if (temp[0].equals(s)) {
                listButonu.get(i).setSelected(true);
            }
        }
    }

    /**
     * metoda pro vypis rout
     * @return
     */
    public String getRout() {
        String vystup = "";
        for (int i = 0; i < listRoutu.size(); i++) {
            String[] temp;
            temp = listRoutu.get(i).split(":");
            if (listButonu.get(i).isSelected()) {
                vystup = temp[0];
            }
        }
        return vystup;
    }
}
