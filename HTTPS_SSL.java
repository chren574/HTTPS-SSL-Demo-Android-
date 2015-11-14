package com.example.christoffer.ssl;

import android.content.Context;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyStore;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;


public class HTTPS_SSL extends AppCompatActivity {

    static String site1 = "https://www.liu.se";
    static String site2 = "https://tal-front.itn.liu.se";
    static String site3 = "https://tal-front.itn.liu.se:4016";
    static String site4 = "https://tal-front.itn.liu.se:4047";

    static int duration = Toast.LENGTH_SHORT;
    Context toastContext;
    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toastContext = getApplicationContext();

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

        final Button button3 = (Button) findViewById(R.id.BTN3);
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Connect(site3, 1);
            }
        });

        final Button button4 = (Button) findViewById(R.id.BTN4);
        button4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Connect(site4, 1);
            }
        });

    }

    public void Connect(String site, int port) {
        try {
            if (port == -1) {
                URL url = new URL(site);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                int code = connection.getResponseCode();
                toast = Toast.makeText(toastContext, "Code:" + Integer.toString(code), duration);
                toast.show();
            } else {
                SSLconnection(site);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            toast = Toast.makeText(toastContext, e.getMessage(), duration);
            toast.show();
        } catch (ProtocolException e) {
            e.printStackTrace();
            toast = Toast.makeText(toastContext, e.getMessage(), duration);
            toast.show();
        } catch (IOException e) {
            e.printStackTrace();
            toast = Toast.makeText(toastContext, e.getMessage(), duration);
            toast.show();
        }
    }


    public void SSLconnection(String www) {
        try {
            // Bouncy castle-certifikat
            KeyStore keyStore = KeyStore.getInstance("BKS");

            // Put the keystore in a stream
            InputStream is = getResources().openRawResource(R.raw.keys);
            // No password
            keyStore.load(is, "".toCharArray());
            is.close();

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);

            tmf.init(keyStore);

            // Create an SSLContext that uses our TrustManager
            SSLContext context = SSLContext.getInstance("TLS");

            context.init(null, tmf.getTrustManagers(), null);

            // Tell the URLConnection to use a SocketFactory from our SSLContext
            URL url = new URL(www);

            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setSSLSocketFactory(context.getSocketFactory());

            // Toast to screen
            int code = urlConnection.getResponseCode();
            toast = Toast.makeText(toastContext, "Code:" + Integer.toString(code), duration);
            toast.show();
        } catch (Exception ex) {
            ex.printStackTrace();
            toast = Toast.makeText(toastContext, ex.getMessage(), duration);
            toast.show();
        }
    }

}