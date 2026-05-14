package util;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class SecurityProvider {

    public static void init() {

        if (Security.getProvider("BC") == null) {

            Security.addProvider(
                    new BouncyCastleProvider());

            System.out.println(
                    "BouncyCastle loaded!");
        }
    }
}