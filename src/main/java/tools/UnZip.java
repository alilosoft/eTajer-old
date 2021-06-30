package tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class UnZip {

    public static void main(String[] args) throws Exception {
        String zipname = "/home/alilo/genymotion-log.zip";
        ZipFile zipFile = new ZipFile(zipname);
        Enumeration enumeration = zipFile.entries();
        while (enumeration.hasMoreElements()) {
            ZipEntry zipEntry = (ZipEntry) enumeration.nextElement();
            System.out.println("Unzipping: " + zipEntry.getName());
            BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(zipEntry));
            int size;
            byte[] buffer = new byte[2048];
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("/home/alilo/genymotion_unzip/"+zipEntry.getName()), buffer.length);
            while ((size = bis.read(buffer, 0, buffer.length)) != -1) {
                bos.write(buffer, 0, size);
            }
            bos.flush();
            bos.close();
            bis.close();
        }
    }
}
