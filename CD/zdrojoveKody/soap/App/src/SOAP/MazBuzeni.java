package SOAP;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;

/**
 * Trida pro manualni mazani buzeni z DB
 * tedy k deaktivaci dojde odmazanim zaznamu 
 * @author ecer
 */
public class MazBuzeni extends JFrame {

    private DB d = new DB();
    private JComboBox vyber;
    private JFrame ram;

    /**
     * konstruktor vykresli GUI komponentu pro mazani zaznamu o buzeni z DB
     * @throws SQLException 
     */
    public MazBuzeni() throws SQLException {
        ram = new JFrame("Odmazani buzeni z DB");
        ram.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        ram.setSize(450, 70);
        ram.setLayout(new BorderLayout());
        vyber = new JComboBox(d.select("select linka,id,cas2,isnull(info,'') from vstup where akce='0301' order by cas2 desc").toArray());
        JButton smaz = new JButton("Smaz");
        smaz.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    String[] pole = vyber.getSelectedItem().toString().split(" , ");
                    // id je na 2 pozici, je to pole, tak 1
                    //for(int i=0;i<pole.length;i++) System.out.println("pole"+i+" je >"+pole[i]+"<");

                    String prikaz = "delete from vstup where id=" + pole[1];
                    System.out.println(":" + prikaz);
                    try {
                        d.update(prikaz);
                        ArrayList<String> a = new ArrayList<String>(d.select("select linka,id,cas2,isnull(info,'') from vstup where akce='0301' order by cas2 desc"));
                        for (int i = 0; i < a.size(); i++) {
                            System.out.println(">" + a.get(i));
                        }
                        vyber = new JComboBox(a.toArray());

                        ram.dispose();
                        ram.add(vyber, BorderLayout.WEST);
                    } catch (SQLException ex) {
                        System.out.println("Prikaz >" + prikaz + "< se neprovedl");
                    }


                } catch (NullPointerException npe) {
                    System.out.println("tady nic neni");
                    ram.dispose();
                }
            }
        });

        ram.add(vyber, BorderLayout.WEST);
        ram.add(smaz, BorderLayout.EAST);
        ram.setVisible(true);

    }
}
