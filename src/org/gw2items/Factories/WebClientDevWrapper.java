package org.gw2items.Factories;

//~--- non-JDK imports --------------------------------------------------------

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;

//~--- JDK imports ------------------------------------------------------------

/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
import java.io.IOException;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.JOptionPane;

public class WebClientDevWrapper {

	/**
	 *
	 * @param base
	 * @return
	 */
	public static HttpClient wrapClient(HttpClient base) {
        try {
            SSLContext       ctx = SSLContext.getInstance("TLS");
            X509TrustManager tm  = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {}
                @Override
                public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {}
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            X509HostnameVerifier verifier = new X509HostnameVerifier() {
                @Override
                public void verify(String string, X509Certificate xc) throws SSLException {}
                @Override
                public void verify(String string, String[] strings, String[] strings1) throws SSLException {}
                @Override
                public void verify(String string, SSLSocket ssls) throws IOException {

                    // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
                @Override
                public boolean verify(String string, SSLSession ssls) {

                    // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    return true;
                }
            };

            ctx.init(null, new TrustManager[] { tm }, null);

            SSLSocketFactory ssf = new SSLSocketFactory(ctx);

            ssf.setHostnameVerifier(verifier);

            ClientConnectionManager ccm = base.getConnectionManager();
            SchemeRegistry          sr  = ccm.getSchemeRegistry();

            sr.register(new Scheme("https", ssf, 443));

            return new DefaultHttpClient(ccm, base.getParams());
        } catch (NoSuchAlgorithmException | KeyManagementException ex) {
			JOptionPane.showMessageDialog(null, ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}
