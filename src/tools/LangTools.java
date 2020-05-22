/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import java.util.Locale;

/**
 *
 * @author alilo
 */
public class LangTools {

    public static Locale parseStr2Locale(String loc) throws MissFormatedLocal {
        String lang;
        String country;
        Locale l;
        if (loc.length() == 2) {
            lang = loc;
            l = new Locale(lang);
        } else {
            if (loc.length() == 5) {
                lang = loc.substring(0, 2);
                country = loc.substring(3);
                l = new Locale(lang, country);
            }else{
                throw new MissFormatedLocal("String "+ loc+" is not well formated locale.");
            }
        }
        return l;
    }
}