package SOAP;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.Timer;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Trida pro vypis nastavenych buzeni
 * @author ecer
 */
public class Fronta extends JFrame {

    private String newline = System.getProperty("line.separator");
    private ArrayList<String> al = new ArrayList<String>();
    private DB d = new DB();
    private final JTextArea ta = new JTextArea();

    /**
     * Konstruktor, ktery zabezpeci zobrazeni vypisu aktualnich buzeni
     * s obnovovacim intervalem 20 sekund, nebo dle property souboru,
     * konkretne jde o udaj v milisekundach program_interval
     */ 
    public Fronta() throws SQLException {
        ActionListener spoustej = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                delej();
            }
        };
        final Timer test = new Timer(Integer.parseInt(Menu.nastaveni.getProperty("program_interval", "20000")), spoustej);
        String s;
        test.start();

        JFrame frame = new JFrame("Vypis nastavenych buzeni");
        frame.setSize(350, 200);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JScrollPane scrolpanel = new JScrollPane(ta,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        frame.add(scrolpanel, BorderLayout.CENTER);

        delej();
        JButton tl = new JButton("Aktualizuj");
        tl.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                delej();
            }
        });
        frame.add(tl, BorderLayout.NORTH);
        frame.setVisible(true);
    }

    
    /**
     * Metoda, ktera vypisuje aktualni stav buzeni  
     */
     private void delej() {
        ta.setAutoscrolls(true);
        ta.setText("");
        ta.setToolTipText("Informace o buzeni");
        try {
            al = d.kontrolaBuzeni();
            for (int i = 0; i < al.size(); i++) {
                ta.append(al.get(i) + newline);
            }
        } catch (SQLException ex) {
        }
        ta.setEditable(false);
    }
}
