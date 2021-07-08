package SOAP;

import hidden.org.apache.commons.logging.Log;
import hidden.org.apache.commons.logging.impl.Log4jFactory;
import java.awt.GridLayout;
import java.io.FileNotFoundException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.Timer;
import java.awt.event.*;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.SimpleFormatter;
import javax.swing.JOptionPane;

/**
 * Trida pro spousteni a menu aplikace
 * @author ecer
 */
public class Menu extends JFrame {

    private static boolean kyvacka = false;
    private static int delay;
    public static Properties nastaveni = new Properties();
    public static final Logger log1 = Logger.getLogger("Hifas");
    public static Log l1 = Log4jFactory.getLog("Log1");
    static Handler handler;
    public static ExecutorService app = Executors.newCachedThreadPool();
    public static ExecutorService single = Executors.newSingleThreadScheduledExecutor();
    public static String soap_plan;
    private static String pc = "";
    //public static BasicTextEncryptor textEncryptor = new BasicTextEncryptor();

    /**
     * metoda main - spustitelna metoda
     * @param args
     * @throws SQLException
     */
    public static void main(String[] args) throws SQLException {


        try {
            handler = new FileHandler("Program.log", true);
            handler.setFormatter(new SimpleFormatter());  //pokud toto neudelam, bude mit vystup xml podobu
        } catch (IOException ex) {
            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
        }
        log1.addHandler(handler);
        log1.info("Spusteni programu");

        try {
            pc = new SoapFce().readProperties();

        } catch (UnknownHostException ex) {
            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
        }

        File f = new File("serial.ser");
        if (!f.exists()) {
            JOptionPane.showMessageDialog(null, "Opiste dukladne nasledujici text a zaslete jej emailem, bude Vam vygenerovan soubor s klicem");
            JOptionPane.showMessageDialog(null, pc);
            //System.out.println("Soubor serial.ser s licencnim cislem neexistuje, ukoncuji program");
            //tady to chce nejaky kontrolni mechanizmus, ktery by pri neexistenci souboru vygeneruje nejakou zpravu, kterou nam zaslou a my jim dame na zaklade teto zpravy spravny serial.ser soubor
            String zprava="Soubor serial.ser s licencnim cislem neexistuje, ukoncuji program, PC:"+pc;
            log1.severe(zprava);
            System.exit(1);

        } else {
            try {



                //tedy soubor existuje, otevru jej a nactu a zkontroluji
                BufferedReader br = new BufferedReader(new FileReader(f));
                String kontrola = br.readLine();
                //System.out.println("kontrola:" + kontrola);
                //System.out.println("dekodovana kontrola" + new Caesar().decode(kontrola));
                br.close();
                // if (!encryptor.checkPassword(pc, kontrola)) {
                if (!new Caesar().decode(kontrola).equals(pc)) {
                    log1.severe("klic se neshoduje");
                    JOptionPane.showMessageDialog(null, "klic se neshoduje");
                    System.out.println("klic se neshoduje");
                    System.exit(99);
                }

            } catch (FileNotFoundException ex) {
                Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception e) {
                System.out.println("asi je trabl se souborem, ale todle se nikdy nestane");
            }
        }

        // nacte nastaveni ze souboru properties
        try {
            InputStream in = /*Menu.class.getResourceAsStream("/soap.properties");*/
                    new FileInputStream("soap.properties");
            // načtení obsahu souboru properties
            nastaveni.load(in);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "nemohu nacist soap.properties");
            System.out.println("nemohu nacist soap.properties");
        }
        soap_plan = new DB().cislovani();
        delay = Integer.parseInt(nastaveni.getProperty("program_interval", "20000"));
        log1.info("Spousteni po " + delay + " milisekundach");
        ActionListener spoustej = new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                if (kyvacka) {
                    //nazcte vstup z db vstup
                    nacti();
                } else {
                    //nacti caracas
                    Caracas c = new Caracas();

                    try {
                        c.nacti();
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                kyvacka = !kyvacka; //prehozeni stavu aby se tak casto nedelal vypis hovoru, bojim se prevelike zateze
            }
        };
        final Timer test = new Timer(delay, spoustej);
        // pokud najde v soap.properties nacti_start="yes" spusti test.start();
        if (nastaveni.getProperty("nacti_start", "ne").toUpperCase().equals("YES")) {
            test.start();
            log1.info("Spusteni interface dle soap.properties");
        }

        JFrame ram = new JFrame(nastaveni.getProperty("program_name", "HifAs"));
        JButton LockUnlock = new JButton("Zamknout Odemknout");
        LockUnlock.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    OutRouteGui org = new OutRouteGui();
                //org.setVisible(true);
                } catch (SQLException ex) {
                    Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        JButton NacteniParametru = new JButton("Nacteni parametru/POZOR nepta se, maze!!!");
        NacteniParametru.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    l1.info("Nacteni parametru z ustredny");
                    IniciaceDB i = new IniciaceDB();
                //dodelat !!Ini
                } catch (SQLException ex) {
                    Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        JButton Buzeni = new JButton("vynuceni volani/buzeni");
        Buzeni.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    Volani v = new Volani();
                //throw new UnsupportedOperationException("Not supported yet.");
                } catch (SQLException ex) {
                    Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        JButton Fronta = new JButton("Vypis buzeni");
        Fronta.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    Fronta f = new Fronta();
                } catch (SQLException ex) {
                    Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        JButton Tarifikace = new JButton("Tarifikace > DB");
        Tarifikace.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                new Tatifikace();
            }
        });
        JButton NactiVstup = new JButton("Nacti soubor");
        NactiVstup.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    //nacti();
                    new Caracas().nacti();
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NullPointerException ex) {
                    Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        JButton Odmaz = new JButton("Odmazani nastavenych buzeni");
        Odmaz.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    new MazBuzeni();
                } catch (SQLException ex) {
                    Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });


        JButton StartAutomat = new JButton("Start Automat");
        StartAutomat.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                test.start();
                log1.info("Automat nastartovan rucne");
                JOptionPane.showMessageDialog(null, "Automat nastartovan rucne");


            }
        });

        JButton StopAutomat = new JButton("Stop Automat");
        StopAutomat.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                test.stop();
                log1.info("Automat zastaven rucne");
                JOptionPane.showMessageDialog(null, "Automat zastaven rucne");
            }
        });

        JButton Exit = new JButton("Konec programu");
        Exit.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                log1.info("Ukoncuji Program");
                handler.close();
                try {
                    new DB().update("update vstup set done='0',info='Ukonceni programu' where done='3' and akce='0301'");
                } catch (SQLException ex) {
                    Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.exit(0);
            }
        });

        JButton prvotni_nastaveni = new JButton("Prvotní nastavení");
        prvotni_nastaveni.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    System.out.println("Tady smazu udaje z cislovani a smerovani a naplnim pres GUI");
                    NewPBX p = new NewPBX();
                    p.setVisible(true);
                //new NovaUstrednaGui().setVisible(true);
                } catch (Exception ex) {
                    //Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        JButton envir = new JButton("test sifry");
        envir.addActionListener(new ActionListener() {

            @SuppressWarnings("static-access")
            public void actionPerformed(ActionEvent e) {
                System.out.println("zkousim sifru");
                try {
                    //new ShowProperty().readProperties();
                    // objekt = new EncipherDecipher();
                //System.out.println("Code: " + objekt.code("Ecer_NTB.CimexGroup.cz0305534119116"));
                //System.out.println("Decode: "+ objekt.decode("HfhubQWE1Flph.Jurxs1f0363886744<449"));

                } catch (Exception ex) {
                    System.out.println("chyba:");
                    ex.printStackTrace();
                //Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        ram.setSize(300, 300);
        ram.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        ram.setLayout(new GridLayout(11, 1));

        ram.add(prvotni_nastaveni);
        ram.add(NacteniParametru);
        ram.add(Fronta);
        ram.add(Odmaz);
        ram.add(Tarifikace);
        ram.add(StartAutomat);
        ram.add(StopAutomat);
        ram.add(LockUnlock);
        ram.add(Buzeni);
        ram.add(NactiVstup);
        ram.add(Exit);
        //ram.add(envir);
        ram.setVisible(true);
    }

    /**
     * Metoda pro nacteni vstupu
     * Spousti se timerem
     */
    public static void nacti() {
        try {
            //NactiVstup nv =
            new NactiVstup();
        } catch (Exception ex) {
            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
