package model.mordern.asymmetric;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class RSAFileV1 {
    private static final Base64.Encoder encoder = Base64.getEncoder();
    private static final Base64.Decoder decoder = Base64.getDecoder();
    private static final SecureRandom srandom = new SecureRandom();


    private static void processFile(InputStream in, OutputStream out) throws IOException {
        byte[] ibuf = new byte[1024];
        int len;
        while ((len = in.read(ibuf)) != -1) {
            out.write(ibuf, 0, len);
        }
        out.flush();
    }

    public static void doGenKey(String path) throws Exception {
        File des = new File(path);
        if (!des.exists()) des.mkdirs();

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.generateKeyPair();

        try (FileOutputStream out = new FileOutputStream(new File(des, "private.key"))) {
            out.write(encoder.encodeToString(kp.getPrivate().getEncoded()).getBytes(StandardCharsets.UTF_8));
        }

        try (FileOutputStream out = new FileOutputStream(new File(des, "public.pub"))) {
            out.write(encoder.encodeToString(kp.getPublic().getEncoded()).getBytes(StandardCharsets.UTF_8));
        }
        System.out.println("Đã tạo khóa tại: " + des.getAbsolutePath());
    }

    public static PrivateKey readPrivateKey(String path) throws Exception {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        String priKeyString = new String(bytes, StandardCharsets.UTF_8);
        byte[] decoded = decoder.decode(priKeyString);
        PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePrivate(ks);
    }

    public static PublicKey readPublicKey(String path) throws Exception {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        String pubKeyString = new String(bytes, StandardCharsets.UTF_8);
        byte[] decoded = decoder.decode(pubKeyString);
        X509EncodedKeySpec ks = new X509EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePublic(ks);
    }

    public static void doEncryptRSAWithAES(PublicKey pub, String inputPath, String outputPath) throws Exception {
        String algorithm = "AES/CBC/PKCS5Padding";
        int keySize = 128;

        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(keySize);
        SecretKey skey = kgen.generateKey();
        byte[] iv = new byte[16]; 
        srandom.nextBytes(iv);

        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        rsaCipher.init(Cipher.ENCRYPT_MODE, pub);

        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outputPath)))) {
            out.writeInt(keySize);
            out.writeUTF(encoder.encodeToString(rsaCipher.doFinal(algorithm.getBytes(StandardCharsets.UTF_8))));
            out.writeUTF(encoder.encodeToString(rsaCipher.doFinal(skey.getEncoded())));
            out.writeUTF(encoder.encodeToString(rsaCipher.doFinal(iv)));

            Cipher aesCipher = Cipher.getInstance(algorithm);
            aesCipher.init(Cipher.ENCRYPT_MODE, skey, new IvParameterSpec(iv));

            try (InputStream in = new BufferedInputStream(new FileInputStream(inputPath));
                 CipherOutputStream cos = new CipherOutputStream(out, aesCipher)) {
                processFile(in, cos);
            }
        }
        System.out.println("Mã hóa thành công!");
    }

    public static void doDecryptRSAWithAES(PrivateKey priv, String inputPath, String outputPath) throws Exception {
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        rsaCipher.init(Cipher.DECRYPT_MODE, priv);

        try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(inputPath)))) {
            int keySize = in.readInt();
            String algorithm = new String(rsaCipher.doFinal(decoder.decode(in.readUTF())), StandardCharsets.UTF_8);
            byte[] keyBytes = rsaCipher.doFinal(decoder.decode(in.readUTF()));
            byte[] ivBytes = rsaCipher.doFinal(decoder.decode(in.readUTF()));

            SecretKeySpec skey = new SecretKeySpec(keyBytes, "AES");
            IvParameterSpec ivspec = new IvParameterSpec(ivBytes);

            Cipher aesCipher = Cipher.getInstance(algorithm);
            aesCipher.init(Cipher.DECRYPT_MODE, skey, ivspec);

            try (CipherInputStream cis = new CipherInputStream(in, aesCipher);
                 OutputStream out = new BufferedOutputStream(new FileOutputStream(outputPath))) {
                processFile(cis, out);
            }
        }
        System.out.println("Giải mã thành công!");
    }
}