package SOAP;
/**
 * Jednoducha metoda pro kodovani, takzvana Caesarova sifra
 * v nasem pripade jde o posun o 3 znaky
 * @author ecer
 */
public class Caesar {


 
    private int key=3;
    private char znak;
    //private String text;
    

    //konstruktor
    public Caesar() {

        //this.key = key;
        //this.text = text;
    }

    /**
     * metoda code k sifrovani - zasifruje dany text
     */
    public String code(String text) {
    StringBuffer sifra = new StringBuffer();
        for(int i = 0;i < text.length();i++) {
            znak = (char) (text.charAt(i) + key);
            //if(znak > 'y') znak = (char) (znak - 77);
            sifra.append(znak);
        }
        return sifra.toString();
    }

    /**
     * metoda decode k desifrovani - desifruje dany text
     */
    public String decode(String text) {
        StringBuffer dekode = new StringBuffer();
        for(int i = 0;i < text.length();i++) {
            znak = (char) (text.charAt(i) - key);
            //if(znak < '.') znak = (char) (znak + 77);
            dekode.append(znak);
        }
        return dekode.toString();
    }

}