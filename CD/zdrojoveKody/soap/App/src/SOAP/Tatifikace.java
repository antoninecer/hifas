package SOAP;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 * Trida zajistujici nacteni tarifikace z IPEX ustredny
 * za dane obdobi, pro pripad vypadku, ci davkoveho vyctu hovoru.
 * @author ecer
 */
public class Tatifikace extends JFrame {
    private JTextField _od;
    private JTextField _do;

    /**
     * Konstruktor s vykreslenim formulare pro nacteni tarifikace.
     */
    public Tatifikace(){
        JFrame ram = new JFrame("Tarifikace");
        ram.setSize(500,75);
        ram.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ram.setLayout(new GridLayout(1,3));
        _od = new JTextField("20100601");
        _do = new JTextField("20100701");
        JButton tl_ok = new JButton("Preved do DB");
        tl_ok.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try{
                   // DB d = new DB();
                    //SoapFce sf = new SoapFce();
                    new SoapSpust().soapKomunikace(new SoapFce().vypisHovoru("", _od.getText(), _do.getText()));
                }catch(Exception ex){
                    Menu.log1.severe("Soap: problem komunikace. "+new SoapFce().vypisHovoru("", _od.getText(), _do.getText()));
                    }
                JOptionPane.showMessageDialog(null, "Tarifikační data byla vyzískána");
            }
        });

        ram.add(_od);
        ram.add(_do);
        ram.add(tl_ok);
        ram.setVisible(true);

    }
}
