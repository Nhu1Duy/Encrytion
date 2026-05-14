package model.mordern.hash;

public class SHA512 extends AbstractHash {

    @Override
    protected String getAlgorithm() {
        return "SHA-512";
    }
}