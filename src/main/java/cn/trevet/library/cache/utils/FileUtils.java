package cn.trevet.library.cache.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;

import javax.servlet.ServletOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

@Slf4j
public class FileUtils extends org.apache.tomcat.util.http.fileupload.FileUtils {


    public static void downFile(ServletOutputStream outputStream, URLConnection con, File file) throws IOException {
        InputStream in;
        in = con.getInputStream();
        FileOutputStream fos = new FileOutputStream(file);
        byte[] bytes = new byte[1024];
        int bytereadState;
        while ((bytereadState = in.read(bytes)) != -1) {
            fos.write(bytes, 0, bytereadState);
            outputStream.write(bytes, 0, bytereadState);
        }
        IOUtils.closeQuietly(in);
        IOUtils.closeQuietly(fos);
    }
}
