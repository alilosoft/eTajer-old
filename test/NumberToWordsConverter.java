
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * {{{ http://code.activestate.com/recipes/577312/ (r1)
 */
/**
 *
 */
/**
 * @author st0le
 *
 */
public class NumberToWordsConverter {

    final private static String[] units = {"Zero", "One", "Two", "Three", "Four",
        "Five", "Six", "Seven", "Eight", "Nine", "Ten",
        "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen",
        "Sixteen", "Seventeen", "Eighteen", "Nineteen"};
    final private static String[] tens = {"", "", "Twenty", "Thirty", "Forty", "Fifty",
        "Sixty", "Seventy", "Eighty", "Ninety"};
    final private static String[] unitsAr = {"صفر",
        "واحد",
        "إثنان",
        "ثلاثة",
        "أربعة",
        "خمسة",
        "ستة",
        "سبعة",
        "ثمانية",
        "تسعة",
        "عشرة",
        "إحدى عشر",
        "إثنى عشر",
        "ثلاثة عشر",
        "أربعة عشر",
        "خمسة عشر",
        "ستة عشر",
        "سبعة عشر",
        "ثمانية عشر",
        "تسعة عشر"};
    final private static String[] tensAr = {"", "",
        "عشرون",
        "ثلاثون",
        "أربعون",
        "خمسون",
        "ستون",
        "سبعون",
        "ثمانون",
        "تسعون"};
    final private static String[] hundreds = {"",
        "مائة", 
        "مائتين", 
        "ثلاثمائة", 
        "أربعمائة", 
        "خمسمائة", 
        "ستمائة",
        "سبعمائة", 
        "ثمانمائة", 
        "تسعمائة"};

    public static String convert(Integer input, java.lang.String lang) {
        //
        if (lang.equalsIgnoreCase("AR")) {
            if (input < 20) {
                return unitsAr[input];
            }
            if (input < 100) {
                return ((input % 10 > 0) ? convert(input % 10, lang) + " و " : "") + tensAr[input / 10];
            }
            if (input < 1000) {
                return hundreds[input / 100] + ((input % 100 > 0) ? " و " + convert(input % 100, lang) : "");
            }
            if (input < 1000000) {
                return convert(input / 1000, lang) + " آلاف " + ((input % 1000 > 0) ? " " + convert(input % 1000, lang) : "");
            }
            return convert(input / 1000000, lang) + " Million " + ((input % 1000000 > 0) ? " " + convert(input % 1000000, lang) : "");
        } else {
            if (input < 20) {

                return units[input];
            }
            if (input < 100) {
                return tens[input / 10] + ((input % 10 > 0) ? " " + convert(input % 10, lang) : "");
            }
            if (input < 1000) {
                return units[input / 100] + " Hundred" + ((input % 100 > 0) ? " and " + convert(input % 100, lang) : "");
            }
            if (input < 1000000) {
                return convert(input / 1000, lang) + " Thousand " + ((input % 1000 > 0) ? " " + convert(input % 1000, lang) : "");
            }
            return convert(input / 1000000, lang) + " Million " + ((input % 1000000 > 0) ? " " + convert(input % 1000000, lang) : "");
        }

    }

    public static void main(String[] args) {
        String inputStr = "";
        Scanner  s = new Scanner(System.in);
        do {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("scaner input: "+ s.nextInt());
            
            try {
                inputStr = br.readLine();
                int input = Integer.valueOf(inputStr);
                System.out.println(NumberToWordsConverter.convert(input, "ar"));
            } catch (IOException ex) {
                Logger.getLogger(NumberToWordsConverter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NumberFormatException ex) {
                System.out.println("Entre un chiffre correct SVP! pour terminer entre 'end'");
            }
        } while (!inputStr.equalsIgnoreCase("end"));

    }
}
/**
 * end of http://code.activestate.com/recipes/577312/ }}}
 */
