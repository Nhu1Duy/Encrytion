package model.mordern.hash;

public class HashFactory {

    public enum HashAlgorithm {

        MD2,
        MD5,
        SHA1,
        SHA224,
        SHA256,
        SHA384,
        SHA512,
        SHA512_224,
        SHA512_256,
        RIPEMD160,
        BLAKE2B
    }

    public static HashFunction createHashFunction(
            HashAlgorithm algorithm) {

        switch (algorithm) {

            case MD2:
                return new MD2();

            case MD5:
                return new MD5();

            case SHA1:
                return new SHA1();

            case SHA224:
                return new SHA224();

            case SHA256:
                return new SHA256();

            case SHA384:
                return new SHA384();

            case SHA512:
                return new SHA512();

            case RIPEMD160:
                return new RIPEMD160();

            case BLAKE2B:
                return new BLAKE2B();
                
            default:
                throw new IllegalArgumentException(
                        "Thuật toán không hỗ trợ");
        }
    }
}