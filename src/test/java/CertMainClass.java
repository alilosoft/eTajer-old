 import java.io.FileInputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

public class CertMainClass {

  public static void main(String[] args) throws Exception {

    CertificateFactory certFactory = CertificateFactory.getInstance("X.509");

    FileInputStream fis = new FileInputStream("a.dat");

    Certificate cert = certFactory.generateCertificate(fis);
    fis.close();

    System.out.println(cert);
  }
}