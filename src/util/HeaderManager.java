package util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;

public class HeaderManager {
	public static void writeHeader(BufferedOutputStream bos, String fileName) throws Exception {
	    byte[] nameBytes = fileName.getBytes(StandardCharsets.UTF_8);
	    bos.write((nameBytes.length >> 8) & 0xFF);
	    bos.write(nameBytes.length & 0xFF);
	    bos.write(nameBytes);
	}

	public static String readHeader(BufferedInputStream bis, String destFile) throws Exception {
	    int hi = bis.read(), lo = bis.read();
	    if (hi == -1 || lo == -1) throw new Exception("File bị hỏng: không đọc được header.");
	    int nameLen = (hi << 8) | lo;
	    byte[] nameBytes = new byte[nameLen];
	    int total = 0;
	    while (total < nameLen) {
	        int n = bis.read(nameBytes, total, nameLen - total);
	        if (n == -1) throw new Exception("File bị hỏng: không đọc được tên file.");
	        total += n;
	    }
	    String originalName = new String(nameBytes, StandardCharsets.UTF_8);
	    File destDir = new File(destFile).getParentFile();
	    return new File(destDir, originalName).getAbsolutePath();
	}
}
