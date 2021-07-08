package SOAP;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.security.KeyStore;
import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;

/**
 * Trida pro praci s SSL protokolem stazena z webu
 * @author ecer
 */
public class MySSLSocketFactory implements ProtocolSocketFactory {

    private SSLContext sslcontext = null;

    private static SSLContext createEasySSLContext() {
        char[] passphrase = "changeit".toCharArray();
        File file = new File("jssecacerts");
        KeyStore ks;
        try {
            InputStream in = new FileInputStream(file);
            ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(in, passphrase);
            in.close();
        } catch (Exception e1) {
            System.out.println("Chyba cteni souboru: " + file.getAbsolutePath());
            throw new RuntimeException(e1);
        }

        try {
            SSLContext context = SSLContext.getInstance("SSL");
            TrustManagerFactory tmf =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ks);
            context.init(null, tmf.getTrustManagers(), null);
            return context;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private SSLContext getSSLContext() {
        if (this.sslcontext == null) {
            this.sslcontext = createEasySSLContext();
        }
        return this.sslcontext;
    }

    @Override
    public Socket createSocket(String host, int port)
            throws IOException, UnknownHostException {
        return getSSLContext().getSocketFactory().createSocket(host,
                port);
    }

    @Override
    public Socket createSocket(String host, int port,
            InetAddress clientHost, int clientPort)
            throws IOException, UnknownHostException {
        return getSSLContext().getSocketFactory().createSocket(host,
                port, clientHost, clientPort);

    }

    @Override
    public Socket createSocket(String host, int port,
            InetAddress localAddress, int localPort,
            HttpConnectionParams params) throws IOException,
            UnknownHostException, ConnectTimeoutException {
        if (params == null) {
            throw new IllegalArgumentException(
                    "Parameters may not be null");
        }
        int timeout = params.getConnectionTimeout();
        SocketFactory socketfactory =
                getSSLContext().getSocketFactory();
        if (timeout == 0) {
            return socketfactory.createSocket(host, port,
                    localAddress, localPort);
        } else {
            Socket socket = socketfactory.createSocket();
            SocketAddress localaddr =
                    new InetSocketAddress(localAddress, localPort);
            SocketAddress remoteaddr =
                    new InetSocketAddress(host, port);
            socket.bind(localaddr);
            socket.connect(remoteaddr, timeout);
            return socket;
        }
    }
}
