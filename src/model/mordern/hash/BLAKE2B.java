package model.mordern.hash;

public class BLAKE2B extends AbstractHash {

    @Override
    protected String getAlgorithm() {
        return "BLAKE2B-512";
    }
}