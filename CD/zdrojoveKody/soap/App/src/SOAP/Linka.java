package SOAP;

/**
 * Trida pro definici promenne linky (Linka)
 * @author ecer
 */
public class Linka {
//id,name,dialplan,extension,globalExten,ccid1,ccid2,outRoute,ringDuration,ringType,username,password

    private String id;
    private String name;
    private String dialPlanName;
    private String extension;
    private String globalExten;
    private String CID1;
    private String CID2;
    private String outRoute;
    private String ringDuration;
    private String ringType;
    private String username;
    private String password;
    private String note;

    /**
     * metoda pro nastaveni id linky
     * @param id id linky
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * metoda pro nastaveni jmena linky
     * @param name jmeno
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * metoda pro nastaveni volaciho planu
     * @param id volaci plan
     */
    public void setDialPlanName(String id) {
        this.dialPlanName = id;
    }

    /**
     * metoda k nastaveni cisla linky
     * @param id cislo linky
     */
    public void setExtension(String id) {
        this.extension = id;
    }

    /**
     * metoda pro nastaveni globalniho cisla linky
     * @param id globalni cislo
     */
    public void setGlobalExten(String id) {
        this.globalExten = id;
    }

    /**
     * metoda pro nastaveni CID1
     * @param id CID1
     */
    public void setCID1(String id) {
        this.CID1 = id;
    }

    /**
     * metoda pro nastaveni CID2
     * @param id CID2
     */
    public void setCID2(String id) {
        this.CID2 = id;
    }

    /**
     * metoda pro nastaveni odchozi routy
     * @param id odchozi routa
     */
    public void setOutRoute(String id) {
        this.outRoute = id;
    }

     /**
     * metoda pro nastaveni delky vyzvaneni k lince
     * @param id delka vyzvaneni
     */
    public void setRingDuration(String id) {
        this.ringDuration = id;
    }

     /**
     * metoda pro nastaveni vyzvaneni k lince
     * @param vyzvaneni k lince
     */

    public void setRingType(String id) {
        this.ringType = id;
    }

    /**
     * metoda pro nastaveni prihlasovaciho jmena k lince
     * @param id prihlasovaci jmeno k lince
     */
    public void setUsername(String id) {
        this.username = id;
    }

    /**
     * metoda pro nastaveni hesla k lince
     * @param id heslo k lince
     */
    public void setPassword(String id) {
        this.password = id;
    }

    /**
     * metoda pro poznamky linky
     * @param id poznamka k lince
     */
    public void setNote(String id) {
        this.note = id;
    }

     /**
     * metoda pro ziskani id linky
     * @return id linky
     */
    public String getId() {
        return id;
    }

        /**
     * metoda pro ziskani nazvu linky
     * @return nazev linky
     */
    public String getName() {
        return name;
    }

    /**
     * metoda pro ziskani volaciho planu linky
     * @return volaci plan
     */
    public String getDialPlanName() {
        return dialPlanName;
    }

    /**
     * metoda pro ziskani  cisla linky
     * @return  cislo linky
     */
    public String getExtension() {
        return extension;
    }

    /**
     * metoda pro ziskani globalniho cisla linky
     * @return globalni cislo linky
     */
    public String getGlobalExten() {
        return globalExten;
    }

    /**
     * metoda pro ziskani CID1 linky
     * @return CID1
     */
    public String getCID1() {
        return CID1;
    }

    /**
     * metoda pro ziskani CID2 linky
     * @return CID2
     */
    public String getCID2() {
        return CID2;
    }

    /**
     * metoda pro ziskani odchozi routy linky
     * @return odchozi routa
     */
    public String getOutRoute() {
        return outRoute;
    }

    /**
     * metoda pro ziskani delky vyzvaneni linky
     * @return delka vyzvaneni
     */
    public String getRingDuration() {
        return ringDuration;
    }

    /**
     * metoda pro ziskani vyzvaneni linky
     * @return vyzvaneni
     */
    public String getRingType() {
        return ringType;
    }

    /**
     * metoda pro ziskani prihlasovaciho jmena linky
     * @return prihlasovaci jmeno
     */
    public String getUsername() {
        return username;
    }

    /**
     * metoda pro ziskani prihlasovaciho hesla linky
     * @return prihlasovaci heslo
     */
    public String getPassword() {
        return password;
    }

    /**
     * metoda pro ziskani popisu linky
     * @return popis linky
     */
    public String getNote() {
        return note;
    }
}
