package com.chefmenu.nami.controllers.services;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

public class Http {

    private final static String SSL = "SSL";

    private static OkHttpClient InsecureHttpClient;

    public static OkHttpClient client () {
        if (InsecureHttpClient == null) {
            try {
                InsecureHttpClient = insecureOkHttpClient ();
            } catch (Exception e) {
                e.printStackTrace ();
            }
        }

        return InsecureHttpClient;
    }

    private static OkHttpClient insecureOkHttpClient () throws Exception {
        TrustManager [] trustAllCerts       = new TrustManager [] { trustManager () };

        SSLContext sslContext               = SSLContext.getInstance (SSL);
        sslContext.init (null, trustAllCerts, new SecureRandom ());

        SSLSocketFactory sslSocketFactory   = sslContext.getSocketFactory ();

        OkHttpClient.Builder builder        = new OkHttpClient.Builder ();
        builder.sslSocketFactory (sslSocketFactory, (X509TrustManager)trustAllCerts [0]);
        builder.hostnameVerifier (hostnameVerifier ());

        return builder.build ();
    }

    private static TrustManager trustManager () {
        return new X509TrustManager () {

            @Override
            public void checkClientTrusted (X509Certificate [] chain, String authType) throws CertificateException {  }

            @Override
            public void checkServerTrusted (X509Certificate[] chain, String authType) throws CertificateException {  }

            @Override
            public X509Certificate [] getAcceptedIssuers () {
                return new X509Certificate [] {  };
            }
        };
    }

    private static HostnameVerifier hostnameVerifier () {
        return new HostnameVerifier () {

            @Override
            public boolean verify (String hostname, SSLSession session) {
                return true;
            }
        };
    }
}
