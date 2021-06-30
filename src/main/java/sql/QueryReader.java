/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sql;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import dao.UniteDAO;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import tools.ExceptionReporting;
import tools.MessageReporting;

/**
 *
 * @author alilo
 */
public class QueryReader {

    public static String[] getQueries(String queryFile, boolean externalFile) {
        StringBuilder qb = new StringBuilder();
        try {
            InputStreamReader reader;
            if (externalFile) {
                reader = new InputStreamReader(new FileInputStream(queryFile), Charset.forName("UTF8"));
            } else {
                reader = new InputStreamReader(QueryReader.class.getResourceAsStream(queryFile), Charset.forName("UTF8"));
            }
            BufferedReader br = new BufferedReader(reader);
            String line;
            while ((line = br.readLine()) != null) {
                qb.append(line);
            }
        } catch (IOException ex) {
            MessageReporting.showMessage(Level.SEVERE, QueryReader.class, "getQuery(String queryName)", ex.getMessage());
            ExceptionReporting.showException(ex);
        }
        //System.out.println(queryFile+"->"+qb.toString());
        return qb.toString().split(";");
    }

    public static void main(String args[]) throws Exception {
        QueryReader.getQueries(UniteDAO.SQL_FILES_PATH + UniteDAO.GET_ALL, false);
        QueryReader.getQueries(UniteDAO.SQL_FILES_PATH + UniteDAO.INSERT, false);
    }
}
