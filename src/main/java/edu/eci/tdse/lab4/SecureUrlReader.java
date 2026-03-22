package edu.eci.tdse.lab4;

import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.security.KeyStore;

public class SecureUrlReader {

    public static String readSecureURL(String urlString,
                                       String trustStorePath,
                                       String trustStorePassword) throws Exception {

        // Cargar TrustStore con el certificado del otro servidor
        File trustStoreFile = new File(trustStorePath);
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(new FileInputStream(trustStoreFile),
                trustStorePassword.toCharArray());

        TrustManagerFactory tmf = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);
        SSLContext.setDefault(sslContext);

        // Hacer la petición HTTPS al otro servidor
        URL url = new URL(urlString);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setSSLSocketFactory(sslContext.getSocketFactory());

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));

        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        return response.toString();
    }
}