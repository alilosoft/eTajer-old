
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.JarOutputStream;



public class MainClass {

    public static void main(String[] args) throws MalformedURLException, IOException {
        //URL url = ClassLoader.getSystemResource("panels/maj/MajProd_Defaults.properties");
          URL url = new URL("jar:file:/.//dist//GeCom_Standard_Edition.jar!/");
        System.out.println(url.getPath());
        System.out.println(url.getFile());
        
        //System.out.println(url.getPath());
        //File f = new File(url.getPath()+"alilo.txt");
        
       // System.out.println(f.getPath());
        //new FileWriter(f).write("hello");
        //JarOutputStream jos = new JarOutputStream(new FileOutputStream("./dist/GeCom_Standard_Edition.jar"));
        EnhancedJarFile ejf = new EnhancedJarFile("./dist/GeCom_Standard_Edition.jar");
        JarEntryOutputStream jeos = new JarEntryOutputStream(ejf, "alilo.txt");
        
        
    }
}