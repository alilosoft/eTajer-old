/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import java.io.IOException;
import tools.ExceptionReporting;

/**
 *
 * @author alilo
 */
public class MainTools {

    private static final ProcessBuilder calcProcess = new ProcessBuilder("calc.exe");

    public static void runSystemCalc() {
        try {
            System.out.println("OS: "+System.getProperty("os.name"));
            if (System.getProperty("os.name").equalsIgnoreCase("Linux")) {
                calcProcess.command("mate-calc");
            }else{
                if (System.getProperty("os.name").equalsIgnoreCase("Windows")) {
                    calcProcess.command("calc.exe");
                }
            }
            calcProcess.start();
        } catch (IOException ex) {
            ExceptionReporting.showException(ex);
        }
    }

    public static void main(String args[]) {
        MainTools.runSystemCalc();
    }
 
}
