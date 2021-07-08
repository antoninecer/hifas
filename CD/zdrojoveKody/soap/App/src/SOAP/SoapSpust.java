package SOAP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.protocol.Protocol;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Trida, ktera posila SOAP prikazy do telefonni ustredny
 * @author ecer
 */
public class SoapSpust {

    private String login;
    private String password;
    private String ustredna;
    LinkyHandler lh = new LinkyHandler();
    private String id;
   
    /**
     * konstruktor, ktery musim mit nadefinovan, kdyz uz mam parametricky konstruktor
     */
    public SoapSpust() throws SQLException {
        ustredna = Menu.nastaveni.getProperty("soap_ustredna", "nu.cz");
        login = Menu.nastaveni.getProperty("soap_login", "soap_app");
        password = new Caesar().decode(Menu.nastaveni.getProperty("soap_password", "Khvor"));
    }

    /**
     * Konstruktor , ktery ihned vykona prikaz k ustredne pri inicializaci
     * @param s SOAP prikaz ustredne
     */
    public SoapSpust(String s) {
        ustredna = Menu.nastaveni.getProperty("soap_usredna", "vyskocilova.ipex.cz");
        login = Menu.nastaveni.getProperty("soap_login", "soap_app");
        password = new Caesar().decode(Menu.nastaveni.getProperty("soap_password", "Khvor"));
        try {
            soapKomunikace(s);
        } catch (Exception ex) {
            Menu.log1.severe("Soap: komunikace se nezdarila. "+ex.toString());
            //System.out.println("Hele neco se nezvedlo, do ustredny nic nedoslo");
        }
    }

    /**
     * Metoda pro nastaveni id zaznamu
     * @param s id jako string
     */
    public void setID(String s) {
        id = s;
        System.out.println("souapspust setid :" + id);
    }

    /**
     * Metoda automaticky spusti komunikaci s ustrednou
     * se zadanym parametrem (XML zpravou)
     * @param request SOAP pozadavek na usrednu
     * @throws UnsupportedEncodingException
     * @throws IOException
     * @throws SAXException
     * @throws SQLException
     */
    public void soapKomunikace(String request) throws UnsupportedEncodingException, IOException, SAXException, SQLException {
        //ArrayList<String> id=new ArrayList<String>(d.nactiId());
        System.out.println("veta:" + request);
        // vytvoreni protokolu s vlatni socketfactory - aby to bralo neduveryhdony certifikat
        Protocol myhttps = new Protocol("https", new MySSLSocketFactory(), 443);
        HttpClient httpclient = new HttpClient();
        // posilani hesla uz pri prvnim pripojeni (neceka na vyzvu)
        httpclient.getParams().setAuthenticationPreemptive(true);
        // jemno a heslo pro prihlasovani
        Credentials creds = new UsernamePasswordCredentials(login, password);
        // domena a port na kterou se jemno a heslo posila
        AuthScope scope = new AuthScope(ustredna, 443);
        httpclient.getState().setCredentials(scope, creds);
        // nastaveni pripojeni klienta - domena, port a hlavne protokol
        httpclient.getHostConfiguration().setHost(ustredna, 443, myhttps);
        // vytvoreni post zadosti
        PostMethod method = new PostMethod("/ipbx/soap/soapserver.php");
        // nastaveni obsahu post zadosti
        // samo pak nastavi content type, kodovani a podle toho kodovani prevede zadany retezec na byty
        StringRequestEntity entity = new StringRequestEntity(request, "text/xml", "UTF-8");
        method.setRequestEntity(entity);
        // spusteni metody
        httpclient.executeMethod(method);
        // tisk kodu odpovedi
        System.out.println("Response code: " + method.getStatusCode());
        System.out.println(method.getStatusText());

                XMLReader reader = XMLReaderFactory.createXMLReader();
        // nastavení obsluhy
        reader.setContentHandler(lh);
        // vytvoření zdroje pro XML parser založeného na znakovém proudu
        BufferedReader br = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
//        InputSource source = new InputSource(method.getResponseBodyAsStream());
        lh.setId(id);
        reader.parse(new InputSource(br));

    }
}
