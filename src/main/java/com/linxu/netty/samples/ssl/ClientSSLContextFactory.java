package com.linxu.netty.samples.ssl;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;

/**
 * @author linxu
 * @date 2020/3/2
 * <tip>take care of yourself.everything is no in vain.</tip>
 * 1 生成自己的密钥库
 * 2 将服务器cer导入密钥库
 */
public class ClientSSLContextFactory {
    private static final String PROTOCOL = "TLS";
    private static SSLContext sslContext;

    /**
     * @param caPath    ca path
     * @param storepass psw
     * @return ssl context
     */
    public static SSLContext getClientContext(String caPath, String storepass) {
        if (sslContext != null) {
            return sslContext;
        }
        InputStream trustInput = null;

        try {
            //信任库
            TrustManagerFactory tf = null;
            if (caPath != null) {
                //密钥库KeyStore
                KeyStore ks = KeyStore.getInstance("JKS");
                //加载服务端证书
                trustInput = new FileInputStream(caPath);
                ks.load(trustInput, storepass.toCharArray());
                tf = TrustManagerFactory.getInstance("SunX509");
                // 初始化信任库
                tf.init(ks);
            }
            //双向认证时需要加载自己的证书
            /*KeyManagerFactory kmf = null;
            if (pkPath != null) {
                KeyStore ks = KeyStore.getInstance("JKS");
                keyIn = new FileInputStream(pkPath);
                ks.load(keyIn, storepass.toCharArray());
                kmf = KeyManagerFactory.getInstance("SunX509");
                kmf.init(ks, keypass.toCharArray());
            }*/
            sslContext = SSLContext.getInstance(PROTOCOL);
            //设置信任证书. 双向认证时，第一个参数kmf.getKeyManagers()
            sslContext.init(null, tf == null ? null : tf.getTrustManagers(), null);

        } catch (Exception e) {
            throw new Error("Failed to init the client-side SSLContext");
        } finally {
            if (trustInput != null) {
                try {
                    trustInput.close();
                } catch (IOException e) {
                    // log.info("close InputStream.", e);
                }
            }
        }
        return sslContext;
    }

    /**
     * @param caPath    ca path
     * @param storepass psw
     * @return engine
     */
    public static SSLEngine getSSLEngine(String caPath, String storepass) {
        SSLEngine engine = getClientContext(caPath, storepass).createSSLEngine();
        engine.setUseClientMode(true);
        return engine;
    }
}
