package appLogic;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;

public class Encryption {

    public static void createActivationRequestKey() {
        try {
            KeyGenerator kg = KeyGenerator.getInstance("DES");
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            Key key = kg.generateKey();
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte input[] = "Fellahi Alilo".getBytes();
            byte encrypted[] = cipher.doFinal(input);
            System.out.println(new String(encrypted));
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("./KeyRequest"));
            bos.write(encrypted);
            bos.flush();
            bos.close();
        } catch (Exception ex) {
        }
    }

    public static void readKeyRequestFile(String file) {
        try {
            KeyGenerator kgDES = KeyGenerator.getInstance("DES");
            Key key = kgDES.generateKey();
            Cipher c = Cipher.getInstance("DES/CBC/PKCS5Padding");
            byte iv[] = c.getIV();
            IvParameterSpec dps = new IvParameterSpec(iv);
            c.init(Cipher.DECRYPT_MODE, key, dps);
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream("./KeyRequest"));
            byte encrypted[] = new byte[bis.available()];
            byte readByte = (byte) bis.read();
            int i = 0;
            while (readByte != -1) {                
                encrypted[i] = readByte;
                i++;
            }
            System.out.println(new String(encrypted));
            byte output[] = c.doFinal(encrypted);
            System.out.println(new String(output));
        } catch (Exception ex) {
        }
    }

    public static void main(String args[]) throws Exception {
        //Encryption.createActivationRequestKey();
        Encryption.readKeyRequestFile(null);
    }
}