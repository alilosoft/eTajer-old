/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package other;

import java.util.Iterator;
import java.util.UUID;

/**
 *
 * @author alilo
 */
public class GenerateUUID {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        //generate random UUIDs
        UUID idOne = UUID.randomUUID();
        UUID idTwo = UUID.randomUUID();
        
        
        
            Iterator<String>    it = System.getenv().keySet().iterator();
            while (it.hasNext()){
                String key =  it.next();
                String val = System.getenv().get(key);
                System.out.println(key+":  "+ val);
            }
        }


    }
    



