
package rs.etf.sab.student;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DB {
    
    private static final String username="sa";
    private static final String password="123";
    private static final String database="SAB_project";
    private static final int port=1433;
    private static final String server="localhost";
    
    //url string za konekciju
    private static final String connectionUrl = "jdbc:sqlserver://"+server+":"+port+";encrypt=true;trustServerCertificate=true;databaseName="+database+";";
      
    private static DB db=null;
    private Connection connection;
    
    private DB(){
        try {
            connection=DriverManager.getConnection(connectionUrl,username,password);
        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Connection getConnection(){
        return connection;
    }
    
    public static DB getInstance(){
        if(db==null){
            db=new  DB();
        }
        return db;
    }

    
    
}
