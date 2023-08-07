/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.etf.sab.student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import rs.etf.sab.operations.GeneralOperations;

import java.sql.*;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

public class mj190292_GeneralOperations implements GeneralOperations{
    
    private static Calendar currTime = Calendar.getInstance();

    @Override
    public void setInitialTime(Calendar calendar) {
        java.util.Date calTime = calendar.getTime();
        currTime.setTime(calTime); 
    }

    //proveriti za status sent da li treba uslov
    @Override
    public Calendar time(int days) {
        //set arrived status of order if receive date is in the past
        Connection conn=DB.getInstance().getConnection();
        currTime.add(Calendar.DATE, days);
        try(PreparedStatement stmt=conn.prepareStatement("update [order] set status='arrived' where RecievedTime<=?");) {
            stmt.setDate(1, new Date(currTime.getTimeInMillis()));
            stmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return currTime;
    }

    @Override
    public Calendar getCurrentTime() {
        return currTime;
    }
    
    public void createSystem() {
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement("insert into Member(Name) values(?)",Statement.RETURN_GENERATED_KEYS);) {
            stmt.setString(1, "System");
            stmt.executeUpdate(); 
            try(ResultSet rs=stmt.getGeneratedKeys();){
                if(rs.next()){
                    int IdSystem=rs.getInt(1);
                    try(PreparedStatement stmt1=conn.prepareStatement("insert into System(IdSystem) values(?);"
                            + "insert into Account(IdClient,Credit) values(?,?);");) {
                        stmt1.setInt(1, IdSystem);
                        stmt1.setInt(2, IdSystem);
                        stmt1.setInt(3, 0);
                        stmt1.executeUpdate(); 
                        
                    } catch (SQLException ex) {
                        Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            catch (SQLException ex) {
                Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
            }  
        } catch (SQLException ex) {
            Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    @Override
    public void eraseAll() {
        //disable triggers, disable all constraints, delete each table pg+f the database
        //enable triggers, enable all constraints
        Connection connection=DB.getInstance().getConnection();

        try(PreparedStatement stmt = connection.prepareStatement(
                        "EXEC sp_MSForEachTable 'DISABLE TRIGGER ALL ON ?'\n" +
                        "EXEC sp_MSForEachTable 'ALTER TABLE ? NOCHECK CONSTRAINT ALL'\n" +
                        "EXEC sp_MSForEachTable 'DELETE FROM ?'\n" +
                        "EXEC sp_MSForEachTable 'ALTER TABLE ? CHECK CONSTRAINT ALL'\n" +
                        "EXEC sp_MSForEachTable 'ENABLE TRIGGER ALL ON ?'\n" )
        ){
            stmt.executeUpdate();
            createSystem();  
        }catch (SQLException s) {
            s.printStackTrace();
        }
        
    }
    
}
