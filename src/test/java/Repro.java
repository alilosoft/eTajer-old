import java.sql.*;

public class Repro {
    public static void main(String[] args) throws Exception {
        Class.forName("org.apache.derby.jdbc.ClientDriver");
        Connection c = DriverManager.getConnection
            ("jdbc:derby://localhost/mydb;create=true;traceFile=trace.out");
        Statement s = c.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                                        ResultSet.CONCUR_UPDATABLE);
        try {
            s.executeUpdate("drop table t");
        } catch (SQLException e) { }
        s.executeUpdate("create table t (id int)");
        s.executeUpdate("insert into t values (1)");
        ResultSet rs = s.executeQuery("select * from t, t as t2");
        rs.beforeFirst();
        while(rs.next()){
            System.out.println(rs.getInt(1));
        }
    }
}
