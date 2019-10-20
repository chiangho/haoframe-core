package haoframe.core.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

public class FileUtils {

	
	public static String inputStreamToBase64(InputStream in) {
        byte[] data = null;
		try {
            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int rc = 0;
            while ((rc = in.read(buff, 0, 1024)) > 0) {
                swapStream.write(buff, 0, rc);
            }
            data = swapStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
		if(data==null) {
			return "";
		}
		return Base64.getEncoder().encodeToString(data);
	}
	
}
