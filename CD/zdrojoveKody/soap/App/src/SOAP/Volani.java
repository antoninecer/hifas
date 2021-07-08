package SOAP;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTextField;

/**
 * Trida grafickeho ovladani pro vynuceni volani z linky na linku
 * @author ecer
 */
public class Volani extends JFrame {

    private DB d = new DB();
    private ArrayList<Linka> linka;
    private ArrayList<String> extension = new ArrayList<String>();
    private JTextField kam;

    /**
     * konstruktor - vykresleni ovladani
     * @throws SQLException
     */
    public Volani() throws SQLException {
        linka = new ArrayList<Linka>(d.nactiOpravneniLinek());
        for (int i = 0; i < linka.size(); i++) {
            extension.add(linka.get(i).getExtension());
            System.out.println("Linka :" + linka.get(i).getExtension());
        }
        JFrame ram = new JFrame("Vynuceni volani");
        ram.setSize(300, 75);
        ram.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ram.setLayout(new GridLayout(1, 3));
        final JComboBox vyber = new JComboBox(extension.toArray());
        ram.add(vyber);
        kam = new JTextField(Menu.nastaveni.getProperty("buzeni_linka", "899"));
        ram.add(kam);
        JButton volej = new JButton("Volej");
        volej.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    if (kam.getText().equals(Menu.nastaveni.getProperty("buzeni_linka", "899"))) {
                        Menu.app.execute(new Buzeni(0, vyber.getSelectedItem().toString()));
                    } else {
                        Menu.app.execute(new Buzeni(vyber.getSelectedItem().toString(), kam.getText()));
                    }
                } catch (Exception ex) {
                    Logger.getLogger(Volani.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        ram.add(volej);
        ram.setVisible(true);
    }
}
