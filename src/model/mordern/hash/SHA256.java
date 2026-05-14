package model.mordern.hash;

public class SHA256 extends AbstractHash {

    @Override
    protected String getAlgorithm() {
        return "SHA-256";
    }
}