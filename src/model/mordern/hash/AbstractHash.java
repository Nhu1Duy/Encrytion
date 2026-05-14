package model.mordern.hash;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.MessageDigest;

public abstract class AbstractHash implements HashFunction {

    protected abstract String getAlgorithm();

    @Override
    public String hashString(String input) {

        try {

            MessageDigest md =
                    MessageDigest.getInstance(
                            getAlgorithm(),
                            "BC");

            byte[] digest =
                    md.digest(input.getBytes());

            BigInteger number =
                    new BigInteger(1, digest);

            return number.toString(16);

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }

    @Override
    public String hashFile(String filePath)
            throws Exception {

        try {

            MessageDigest md =
                    MessageDigest.getInstance(
                            getAlgorithm(),
                            "BC");

            InputStream is =
                    new BufferedInputStream(
                            new FileInputStream(filePath));

            DigestInputStream dis =
                    new DigestInputStream(is, md);

            byte[] buffer = new byte[1024];

            while (dis.read(buffer) != -1) {
            }

            dis.close();
            is.close();

            BigInteger number =
                    new BigInteger(
                            1,
                            md.digest());

            return number.toString(16);

        } catch (Exception e) {

            throw new Exception(e);
        }
    }

    @Override
    public String getAlgorithmName() {
        return getAlgorithm();
    }
}