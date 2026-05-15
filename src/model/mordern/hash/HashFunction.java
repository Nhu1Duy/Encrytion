package model.mordern.hash;

public interface HashFunction {
 
    String hashString(String text);

    String hashFile(String path) throws Exception;

    String getAlgorithmName();
}