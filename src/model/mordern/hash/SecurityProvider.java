package model.mordern.hash;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;

public class SecurityProvider {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static void init() {
    }
}