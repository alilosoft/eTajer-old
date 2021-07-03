
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class LogTest {

    private static Logger errLogger = Logger.getLogger("Errors");
    private static Logger warnLogger = Logger.getLogger("Warnings");

    public LogTest() throws IOException {
        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        FileHandler errorhandler = new FileHandler("log/errors_" + date + ".log", true);
        FileHandler warninghandler = new FileHandler("log/warnings_" + date + ".log", true);
        errorhandler.setFormatter(new SimpleFormatter());
        warninghandler.setFormatter(new SimpleFormatter());
        
        errLogger.addHandler(errorhandler);
        warnLogger.addHandler(warninghandler);
        
        errLogger.setLevel(Level.SEVERE);
        warnLogger.setLevel(Level.WARNING);
    }
    
    

    public void test() {

        warnLogger.warning("A message logged to the file");
        try {
            String s = null;
            s.substring(6);
        } catch (Exception e) {
            errLogger.log(Level.SEVERE, "\nhummmmmmm!", e);
        }

        // getClass().getEnclosingMethod().getName()+
    }

    public static void main(String[] args) throws Exception {
           
        //logger.info(" A message logged to the file from");
        new LogTest().test();
    }
}