package SOAP;

import java.sql.*;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 * Trida pro praci s databazi.
 * Obsahuje pripravene metody pro chod aplikace
 * @author ecer
 */
public class DB {

    private String db_connect_string;
    private String db_userid;
    private String db_password;
    Connection conn;
    private Statement statement;

    /**
     * konstruktor, nacte hodnoty z properties
     */
    public DB() {
        db_userid = Menu.nastaveni.getProperty("db_userid", "sa");
        db_connect_string = Menu.nastaveni.getProperty("db_connect_string", "jdbc:jtds:sqlserver://triss:1433/ipex");
        db_password = new Caesar().decode(Menu.nastaveni.getProperty("db_password", "ruhd"));
        //System.out.println("user : "+db_userid+" sring : "+db_connect_string+" heslo :"+db_password);
    }

    /**
     * Metoda pripoji k databazi
     * @param db_connect_string
     * @param db_userid
     * @param db_password
     */
    public void dbConnect() {
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            conn = DriverManager.getConnection(db_connect_string, db_userid, db_password);
        //System.out.println("connected");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,"Nepodarilo se napojit do databaze"+db_connect_string);
            Menu.log1.severe("Nepodarilo se napojit do databaze");
        }
    }

    /**
     * Metoda, ktera zpusobi sql update/insert/delete podle sql povelu sqlString
     * @param sqlString sql: update/insert/delete
     * @return kolik radku ovlivnil
     * @throws SQLException
     */
    public int update(String sqlString) throws SQLException {
        dbConnect();
        ResultSet rs = null;
        int updateQuery = 0;
        statement = conn.createStatement();
        int vysledek = 0;
        vysledek = statement.executeUpdate(sqlString);
        statement.close();
        conn.close();
        return vysledek;
    }

    /**
     * Metoda pro nacteni vsech id linek z tabulky linky.
     * Usredna identifikuje vse dle id, ne cisel linek
     * @return arraylist ID linek
     * @throws SQLException
     */
    public ArrayList<String> nactiId() throws SQLException {
        ArrayList<String> id = new ArrayList<String>();
        dbConnect();
        ResultSet rs = null;
        statement = conn.createStatement();
        rs = statement.executeQuery("select id from linky order by id");
        int sloupcu = rs.getMetaData().getColumnCount();
        String[] sloupce = new String[sloupcu];
        for (int i = 0; i < sloupce.length; i++) {
            System.out.println(rs.getMetaData().getColumnName(i + 1));
            sloupce[i] = rs.getMetaData().getColumnName(i + 1);
        }
        while (rs.next()) {
            for (int j = 0; j < sloupcu; j++) {
            }
            for (int j = 1; j <= sloupcu; j++) {
                System.out.print(rs.getString(j) + " ");
                id.add(rs.getString(j));
            }
            System.out.println();
        }
        statement.close();
        conn.close();
        return id;
    }

    /**
     * Metoda pro navraceni id,extension a outroute z databaze
     * @return arraylist linek jen id, extension, outroute
     * @throws SQLException
     */
    public ArrayList<Linka> nactiOpravneniLinek() throws SQLException {
        ArrayList<Linka> linka = new ArrayList<Linka>();
        dbConnect();
        ResultSet rs = null;
        statement = conn.createStatement();
        rs = statement.executeQuery("select id,extension,outRoute from detail_linky order by extension");
        int sloupcu = rs.getMetaData().getColumnCount();
        String[] sloupce = new String[sloupcu];
        for (int i = 0; i < sloupce.length; i++) {
            System.out.print(rs.getMetaData().getColumnName(i + 1));
            sloupce[i] = rs.getMetaData().getColumnName(i + 1);
        }
        System.out.println();
        int r = 0;
        while (rs.next()) {

            linka.add(new Linka());
            for (int j = 1; j <= sloupcu; j++) {
                //id 1
                if (j == 1) {
                    linka.get(r).setId(rs.getString(j));
                    System.out.print(rs.getString(j) + " ");
                }
                if (j == 2) {
                    linka.get(r).setExtension(rs.getString(j));

                    System.out.print(rs.getString(j) + " ");
                }
                if (j == 3) {
                    linka.get(r).setOutRoute(rs.getString(j));
                    System.out.print(rs.getString(j) + " ");
                }
            }
            System.out.println();
            r++;
        }
        statement.close();
        conn.close();
        return linka;

    }

    /**
     * Metoda pro navraceni rout z DB pro radiobutony
     * @return Routa linek jen id, extension, outroute
     * @throws SQLException
     */
    public ArrayList<String> nactiRouty() throws SQLException {
        ArrayList<String> pole = new ArrayList<String>();
        dbConnect();
        ResultSet rs = null;
        statement = conn.createStatement();
        rs = statement.executeQuery("select nazev +':'+ popis from smerovani order by nazev");
        int sloupcu = rs.getMetaData().getColumnCount();
        String[] sloupce = new String[sloupcu];
        for (int i = 0; i < sloupce.length; i++) {
            System.out.print(rs.getMetaData().getColumnName(i + 1));
            sloupce[i] = rs.getMetaData().getColumnName(i + 1);
        }
        System.out.println();
        int r = 0;
        while (rs.next()) {
            System.out.println(" > :" + rs.getString(sloupcu));
            pole.add(rs.getString(1));
            System.out.println();
            r++;
        }
        statement.close();
        conn.close();
        return pole;

    }

    /**
     * Metoda pro nacteni odchozi routy z db z tabulky detail_linky
     * @param id id linky
     * @return stringove vyjadreni kam se dovola dana linka
     * @throws SQLException
     */
    public String nactiRouty(String id) throws SQLException {
        ArrayList<String> pole = new ArrayList<String>();
        dbConnect();
        ResultSet rs = null;
        statement = conn.createStatement();
        rs = statement.executeQuery("select outRoute from detail_linky where id='" + id + "'");
        int sloupcu = rs.getMetaData().getColumnCount();
        String[] sloupce = new String[sloupcu];
        for (int i = 0; i < sloupce.length; i++) {
            System.out.print(rs.getMetaData().getColumnName(i + 1));
            sloupce[i] = rs.getMetaData().getColumnName(i + 1);
        }
        //System.out.println();
        int r = 0;
        String temp = "";
        while (rs.next()) {
            //System.out.println("db nactirouty > :" + rs.getString(sloupcu));
            temp = rs.getString(1);
            //System.out.println();
            r++;
        }
        statement.close();
        conn.close();
        return temp;

    }

    /**
     * Metoda pro nacteni novych akci v tabulce vstup a nacteni hovoru
     * @return ArrayList akci(id,akce,linka,done)
     * @throws SQLException
     */
    public ArrayList<Akce> nactiVstup() throws SQLException {
        ArrayList<Akce> a = new ArrayList<Akce>();
        dbConnect();
        ResultSet rs = null;
        statement = conn.createStatement();
        rs = statement.executeQuery("select v.id,v.akce,v.linka,l.id as idLinky,isnull(v.done,'0') as done,v.cas,v.cas2,v.jmeno from vstup v inner join linky l on l.number=v.linka where isnull(v.done,'0') ='0' or v.done='3' order by v.id");
        int sloupcu = rs.getMetaData().getColumnCount();
        String[] sloupce = new String[sloupcu];
        for (int i = 0; i < sloupce.length; i++) {
            System.out.print(rs.getMetaData().getColumnName(i + 1) + " ");
            sloupce[i] = rs.getMetaData().getColumnName(i + 1);
        }
        System.out.println();
        int r = 0;
        while (rs.next()) {

            a.add(new Akce());
            for (int j = 1; j <= sloupcu; j++) {
                //id 1
                if (j == 1) {
                    a.get(r).setId(Integer.parseInt(rs.getString(j)));
                    System.out.print(rs.getString(j) + " ");
                }
                if (j == 2) {
                    a.get(r).setAkce(rs.getString(j));

                    System.out.print(rs.getString(j) + " ");
                }
                if (j == 3) {
                    a.get(r).setLinka(rs.getString(j));
                    System.out.print(rs.getString(j) + " ");
                }
                if (j == 4) {
                    a.get(r).setIdLinky(rs.getString(j));
                    System.out.print(rs.getString(j) + " ");
                }
                if (j == 5) {
                    a.get(r).setDone(rs.getString(j));
                    System.out.print(rs.getString(j) + " ");
                }
                if (j == 6) {
                    a.get(r).setCas(rs.getString(j));
                    System.out.print(rs.getString(j) + " ");
                }
                if (j == 7) {
                    a.get(r).setCas2(rs.getString(j));
                    System.out.print(rs.getString(j) + " ");
                }
                if (j == 8) {
                    a.get(r).setJmeno(rs.getString(j));
                    System.out.print(rs.getString(j) + " ");
                }
            }
            System.out.println();
            r++;
        }
        statement.close();
        conn.close();
        return a;

    }

    /**
     * Metoda pro navraceni aktualniho datumu ve spravnem formatu
     * @return aktualni datum ve formatu yyyy-MM-dd HH:mm
     */
    public String getdatum() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date();
        return dateFormat.format(date);
    }

    /**
     * Metoda pro navraceni poctu minut od pozadavku na buzeni
     * @param id -id z tabulky vstup
     * @return minut
     * @throws SQLException
     */
    public int interval(int id) throws SQLException {
        String sql = "select datediff(mi,cas2,getdate()) from vstup where id=" + String.valueOf(id);
        String vysledek = "";
        dbConnect();
        ResultSet rs = null;
        statement = conn.createStatement();
        rs = statement.executeQuery(sql);
        int sloupcu = rs.getMetaData().getColumnCount();
        String[] sloupce = new String[sloupcu];
        for (int i = 0; i < sloupce.length; i++) {
            System.out.print(rs.getMetaData().getColumnName(i + 1) + " ");
            sloupce[i] = rs.getMetaData().getColumnName(i + 1);
        }
        //System.out.println();
        //System.out.println(sloupce);
        int r = 0;
        while (rs.next()) {
            for (int j = 1; j <= sloupcu; j++) {
                //id 1
                if (j == 1) {
                    vysledek = rs.getString(j);
                }
            }
            System.out.println();
            r++;
        }
        statement.close();
        conn.close();
        return Integer.parseInt(vysledek);
    }

    /**
     * Metoda pro navraceni retezce pro odeslani nevzbuzeni
     * @param id -id z tabulky vstup
     * @return vysledek=kdy+","+linka
     * @throws SQLException
     */
    public String buzeno(int id) throws SQLException {
        String sql = "select cas2,linka from vstup where id=" + String.valueOf(id);
        String kdy = "";
        String linka = "";
        dbConnect();
        ResultSet rs = null;
        statement = conn.createStatement();
        rs = statement.executeQuery(sql);
        int sloupcu = rs.getMetaData().getColumnCount();
        String[] sloupce = new String[sloupcu];
        for (int i = 0; i < sloupce.length; i++) {
            System.out.print(rs.getMetaData().getColumnName(i + 1) + " ");
            sloupce[i] = rs.getMetaData().getColumnName(i + 1);
        }
        System.out.println();
        //System.out.println(sloupce);
        int r = 0;
        while (rs.next()) {
            for (int j = 1; j <= sloupcu; j++) {
                //id 1
                if (j == 1) {
                    kdy = rs.getString(j);
                }
                if (j == 2) {
                    linka = rs.getString(j);
                }
            }
            //System.out.println("id:"+id+" cas:"+kdy+" linka"+linka);
            r++;
        }
        statement.close();
        conn.close();
        String vysledek = kdy + "," + linka;
        return (vysledek);
    }

    /**
     * Metoda pro osetreni duplicit tarifikace,
     * vraci false, pokud zaznam v tarifikaci existuje
     * @param _z odkud volano
     *  @param _na kam volano
     * @param  kdy kdy volano
     * @return boolean true, kdyz zaznam neexistuje(muzeme zapsat do DB)
     * @throws SQLException
     */
    public boolean existujeTarifikace(String _z, String _na, String kdy) throws SQLException {
        String sql = "select count(*) from tarifikace where _z='" + _z + "' and _na='" + _na + "' and kdy='" + kdy + "'";
        String vysledek = "";
        dbConnect();
        ResultSet rs = null;
        statement = conn.createStatement();
        rs = statement.executeQuery(sql);
        int sloupcu = rs.getMetaData().getColumnCount();
        String[] sloupce = new String[sloupcu];
        for (int i = 0; i < sloupce.length; i++) {
            System.out.print(rs.getMetaData().getColumnName(i + 1) + " ");
            sloupce[i] = rs.getMetaData().getColumnName(i + 1);
        }
        System.out.println();
        //System.out.println(sloupce);
        int r = 0;
        while (rs.next()) {
            for (int j = 1; j <= sloupcu; j++) {
                //id 1
                if (j == 1) {
                    vysledek = rs.getString(j);
                }
            }
            System.out.println();
            r++;
        }
        statement.close();
        conn.close();
        return (Integer.parseInt(vysledek) > 0) ? false : true;
    }


    /**
     * Metoda pro nacteni novych akci v tabulce vstup
     * @return ArrayList akci(id,akce,linka,done)
     * @throws SQLException
     */
    public ArrayList<String> kontrolaBuzeni() throws SQLException {
        ArrayList<String> a = new ArrayList<String>();
        dbConnect();
        ResultSet rs = null;
        statement = conn.createStatement();
        rs = statement.executeQuery(Menu.nastaveni.getProperty("buzeni_vypis_sql","select linka,cas2,isnull(info,'') from vstup where akce='0301' and isnull(done,'0') in('0','2','3') or( isnull(done,'0')='2' and datediff(mi,cas2,getdate())< 60) order by cas2 desc"));
               // "where akce='0301' and isnull(done,'0') in('0','3') order by cas2 desc");
        int sloupcu = rs.getMetaData().getColumnCount();
        String sloupce = "";// = new String[sloupcu];
        for (int i = 0; i < sloupcu; i++) {
            //System.out.print(rs.getMetaData().getColumnName(i + 1) + " ");
            sloupce = sloupce + " " + rs.getMetaData().getColumnName(i + 1);
        }
        //a.add(sloupce);
        //System.out.println(sloupce);
        int r = 0;
        while (rs.next()) {
            String radek = "";
            //a.add(new Akce());
            for (int j = 1; j <= sloupcu; j++) {
                if (j==(sloupcu)){
                    radek = radek + rs.getString(j);
                }
                else {
                radek = radek + rs.getString(j) +" , ";}
            }
            //System.out.println(radek);
            a.add(radek);
            r++;
        }
        statement.close();
        conn.close();
        return a;

    }
     /**
     * Metoda pro moznost odeslani sveho selectu do BD
     * pokud je parametr prazdny, vysledkem je vypis buzeni srovnanych dle casu desc
     * @return ArrayList akci(id,akce,linka,done)
     * @throws SQLException
     */
    public ArrayList<String> select(String s) throws SQLException {
        if (s.length()<1) s="select linka,id,cas2,isnull(info,'') from vstup where akce='0301' order by cas2 desc";
        ArrayList<String> a = new ArrayList<String>();
        dbConnect();
        ResultSet rs = null;
        statement = conn.createStatement();
        rs = statement.executeQuery(s);
               // "where akce='0301' and isnull(done,'0') in('0','3') order by cas2 desc");
        int sloupcu = rs.getMetaData().getColumnCount();
        String sloupce = "";// = new String[sloupcu];
        for (int i = 0; i < sloupcu; i++) {
            //System.out.print(rs.getMetaData().getColumnName(i + 1) + " ");
            sloupce = sloupce + " " + rs.getMetaData().getColumnName(i + 1);
        }
        //a.add(sloupce);
        //System.out.println(sloupce);
        int r = 0;
        while (rs.next()) {
            String radek = "";
            //a.add(new Akce());
            for (int j = 1; j <= sloupcu; j++) {
                if (j==(sloupcu)){
                    radek = radek + rs.getString(j);
                }
                else {
                radek = radek + rs.getString(j) +" , ";}
            }
            //System.out.println(radek);
            a.add(radek);
            r++;
        }
        statement.close();
        conn.close();
        return a;

    }

     /**
     * Metoda pro navraceni retezce pro zjisteni nazvu cislovaciho planu
     * dulezite pro sestaveni XML hlavicky SOAP zpravy
     * @return vysledek nazev cislovaciho planu
     * @throws SQLException
     */
    public String cislovani() throws SQLException {
        String sql = "select top 1 nazev from cislovani";
        dbConnect();
        ResultSet rs = null;
        statement = conn.createStatement();
        rs = statement.executeQuery(sql);
        int sloupcu = rs.getMetaData().getColumnCount();
        String vysledek="" ;
        //System.out.println(sloupce);
        while (rs.next()) {
            for (int j = 1; j <= sloupcu; j++) {
                if (j==1) vysledek = rs.getString(j);
            }
        }
        statement.close();
        conn.close();
        return (vysledek);
    }

}
