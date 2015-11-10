package com.example.christoffer.ssl;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;


public class MainActivity extends AppCompatActivity {


    static String site1 = "https://www.liu.se";
    static String site2 = "https://tal-front.itn.liu.se";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        final Button button1 = (Button) findViewById(R.id.BTN1);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Connect(site1, -1);
            }
        });

        final Button button2 = (Button) findViewById(R.id.BTN2);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Connect(site2, -1);
            }
        });

        final Button button3 = (Button) findViewById(R.id.BTN1);
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Connect(site2, 4016);
            }
        });

        final Button button4 = (Button) findViewById(R.id.BTN2);
        button4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Connect(site2, 4047);
            }
        });

    }

    public void Connect(String site, int port) {

        URL url = null;
        if (port != -1) {
            try {
                url = new URL(site);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            URLConnection urlConnection = null;
            InputStream in = null;
            try {
                urlConnection = url.openConnection();
                in = urlConnection.getInputStream();
                copyInputStreamToOutputStream(in, System.out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            SSLconnection(site, port);
        }
    }


    public void SSLconnection(String site, int port) {
        // Create a KeyStore containing our trusted CAs
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance("BSK");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        InputStream fis = getResources().openRawResource(R.raw.keystore);

        try {
            keyStore.load(fis, "123456".toCharArray());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        //keyStore.setCertificateEntry("ca", ca);

        // Create a TrustManager that trusts the CAs in our KeyStore
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = null;
        try {
            tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            tmf.init(keyStore);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        // Create an SSLContext that uses our TrustManager
        SSLContext context = null;
        try {
            context = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            context.init(null, tmf.getTrustManagers(), null);
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        // Tell the URLConnection to use a SocketFactory from our SSLContext
        URL url = null;
        try {
            url = new URL(site + String.valueOf(':') + String.valueOf(port));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpsURLConnection urlConnection = null;
        urlConnection.setSSLSocketFactory(context.getSocketFactory());
        InputStream in = null;
        try {
            urlConnection = (HttpsURLConnection) url.openConnection();
            in = urlConnection.getInputStream();
            copyInputStreamToOutputStream(in, System.out);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void copyInputStreamToOutputStream(final InputStream in,
                                                     final java.io.PrintStream out) throws IOException {
        try {
            try {
                final byte[] buffer = new byte[1024];
                int n;
                while ((n = in.read(buffer)) != -1)
                    out.write(buffer, 0, n);
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }

}
