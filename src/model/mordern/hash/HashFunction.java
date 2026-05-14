package model.mordern.hash;

public interface HashFunction {
 
    String hashString(String input);

    String hashFile(String filePath) throws Exception;

    String getAlgorithmName();
}