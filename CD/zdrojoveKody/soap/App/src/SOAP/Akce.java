package SOAP;

/**
 * Trida definujici strukturu interni promenne Akce 
 * ktera je jiz dale volana jako ArrayList(Akce)
 * @author ecer
 */
public class Akce {

    /**
     * id akce podle id v databazi
     */
    private int id;
    /**
     * typ akce (0101 in,0102 out,0301 buzeni)
     */
    private String akce;
    /**
     * cislo linky
     */
    private String linka;
    /**
     * priznak stavu dane akce 
     * (1 hotovo, 2 neuspesne vykonani prikazu, 3 probihajici buzeni)
     */
    private String done;
    /**
     * v ustredne se vse identifikuje dle id linky,
     * ktera zastupuje dane cislo linky
     */
    private String idLinky;
    /**
     * cas kdy se ma dana akce vykonat (pri pokusech o buzeni se posouva
     * o nastaveny interval)
     */
    private String cas;
    /**
     * cas2 pro puvodni dobu buzeni
     */
    private String cas2;
    /**
     * Jmeno , prozatim nepouzito
     */
    private String jmeno;
    /**
     * metoda pro ziskani promenne
     * @return promenna
     */
    public String getJmeno() {
        return jmeno;
    }

    /**
     * metoda pro nastaveni promenne
     */
    public void setJmeno(String jmeno) {
        this.jmeno = jmeno;
    }

    /**
     * metoda pro nastaveni promenne
     */
    public void setId(int i) {
        id = i;
    }

    /**
     * metoda pro nastaveni promenne
     */
    public void setAkce(String s) {
        akce = s;
    }

    /**
     * metoda pro nastaveni promenne
     */
    public void setLinka(String s) {
        linka = s;
    }

    /**
     * metoda pro nastaveni promenne
     */
    public void setDone(String s) {
        done = s;
    }

    /**
     * metoda pro nastaveni promenne
     */
    public void setIdLinky(String s) {
        idLinky = s;
    }

    /**
     * metoda pro nastaveni promenne
     */
    public void setCas(String s) {
        cas = s;
    }

    /**
     * metoda pro nastaveni promenne
     */
    public void setCas2(String s) {
        cas2 = s;
    }

     /**
     * metoda pro ziskani promenne
     * @return promenna
     */

    public int getId() {
        return (id);
    }

     /**
     * metoda pro ziskani promenne
     * @return promenna
     */
    public String getAkce() {
        return (akce);
    }

    /**
     * metoda pro ziskani promenne
     * @return promenna
     */
    public String getLinka() {
        return (linka);
    }

    /**
     * metoda pro ziskani promenne
     * @return promenna
     */
    public String getDone() {
        return (done);
    }

    /**
     * metoda pro ziskani promenne
     * @return promenna
     */
    public String getIdLinky() {
        return (idLinky);
    }

    /**
     * metoda pro ziskani promenne
     * @return promenna
     */
    public String getCas() {
        return (cas);
    }

    /**
     * metoda pro ziskani promenne
     * @return promenna
     */
    public String getCas2() {
        return (cas2);
    }

    /**
     * Konstruktor, iniciuje promenne
     * @return promenna
     */
    public Akce() {
        id = 0;
        akce = "";
        linka = "";
        done = "";
        idLinky = "";
        cas = "";
        cas2 = "";
    }
}
