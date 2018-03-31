package com.jerry_mar.httputils;

import android.util.Log;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

public class CertificateManager implements X509TrustManager {
    static void bind(OkHttpClient.Builder builder, List<InputStream> list) {
        try {
            TrustManager[] managers = createTrustManager(list);
            SSLContext context = SSLContext.getInstance("TLS");
            CertificateManager manager = new CertificateManager(managers);
            context.init(null, new TrustManager[] {manager}, new SecureRandom());
            builder.sslSocketFactory(context.getSocketFactory(), manager);
        } catch (Exception e) {
            Log.d("CertificateManager", e.getMessage());
        }
    }

    private static TrustManager[] createTrustManager(List<InputStream> list) {
        TrustManager[] result = null;
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            for (int i = 0; i < list.size(); i++) {
                String alias = Integer.toString(i);
                InputStream stream = list.get(i);
                keyStore.setCertificateEntry(alias, certificateFactory.generateCertificate(stream));
            }
            TrustManagerFactory factory = TrustManagerFactory. getInstance(TrustManagerFactory.getDefaultAlgorithm());
            factory.init(keyStore);
            result = factory.getTrustManagers();
            for (InputStream stream : list) {
                stream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private X509TrustManager coreManager;

    CertificateManager(TrustManager[] managers) {
        for (TrustManager manager : managers) {
            if (manager instanceof X509TrustManager) {
                coreManager = (X509TrustManager) manager;
                break;
            }
        }
        if (coreManager == null) {
            throw new RuntimeException("CertificateManager 初始化异常");
        }
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        coreManager.checkClientTrusted(chain, authType);
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        coreManager.checkServerTrusted(chain, authType);
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return coreManager.getAcceptedIssuers();
    }
}
