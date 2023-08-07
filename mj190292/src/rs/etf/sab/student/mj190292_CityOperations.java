/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.etf.sab.student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.CityOperations;

/**
 *
 * @author HS
 */
public class mj190292_CityOperations implements CityOperations{
    
    private static boolean cityExists(int IdCity){
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement("select * from city where IdCity=?");) {
            stmt.setInt(1, IdCity);
            try(ResultSet rs=stmt.executeQuery();) {
                if(rs.next()){
                    return true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            stmt.executeQuery(); 
        } catch (SQLException ex) {
            Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private static boolean cityExists(String cityName){
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement("select * from city where name=?");) {
            stmt.setString(1, cityName);
            try(ResultSet rs=stmt.executeQuery();) {
                if(rs.next()){
                    return true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            stmt.executeQuery(); 
        } catch (SQLException ex) {
            Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    private static boolean lineBetweenCitiesExists(int IdCity1,int IdCity2){
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement("select * from CityLines where ((IdCity1=? and IdCity2=?) or (IdCity2=? and IdCity1=?))");) {
            stmt.setInt(1, IdCity1);
            stmt.setInt(2, IdCity2);
            stmt.setInt(3, IdCity1);
            stmt.setInt(4, IdCity2);
           
            try(ResultSet rs=stmt.executeQuery();){
                if(rs.next()){
                    return true;
                }
            }
            catch (SQLException ex) {
                Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
            }   
           
        } catch (SQLException ex) {
            Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    @Override
    public int createCity(String Name) {
        if(cityExists(Name)){
            return -1;
        }
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement("insert into City(Name) values(?)",Statement.RETURN_GENERATED_KEYS);) {
            stmt.setString(1, Name);
            stmt.executeUpdate(); 
            try(ResultSet rs=stmt.getGeneratedKeys();){
                if(rs.next()){
                    return rs.getInt(1);
                }
            }
            catch (SQLException ex) {
                Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
            }   
           
        } catch (SQLException ex) {
            Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    @Override
    public List<Integer> getCities() {
        Connection conn=DB.getInstance().getConnection();
        ArrayList<Integer> cities=new ArrayList<>();
        try(PreparedStatement stmt=conn.prepareStatement("select IdCity from City");) {
            try(ResultSet rs=stmt.executeQuery();){
                while(rs.next()){
                    cities.add(rs.getInt(1));
                }
                return cities;
            }
            catch (SQLException ex) {
                Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
            }   
           
        } catch (SQLException ex) {
            Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public int connectCities(int IdCity1, int IdCity2, int distance) {
        
        if(!cityExists(IdCity1) || !cityExists(IdCity2) || lineBetweenCitiesExists(IdCity1, IdCity2)){
            return -1;
        }
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement("insert into CityLines(IdCity1,IdCity2,Distance) values(?,?,?)",Statement.RETURN_GENERATED_KEYS);) {
            stmt.setInt(1, IdCity1);
            stmt.setInt(2, IdCity2);
            stmt.setInt(3, distance);
            stmt.executeUpdate(); 
            try(ResultSet rs=stmt.getGeneratedKeys();){
                if(rs.next()){
                    return rs.getInt(1);
                }
            }
            catch (SQLException ex) {
                Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
            }   
           
        } catch (SQLException ex) {
            Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    @Override
    public List<Integer> getConnectedCities(int IdCity) {
        
        if(!cityExists(IdCity)){
            return null;
        }
        Connection conn=DB.getInstance().getConnection();
        ArrayList<Integer> cities=new ArrayList<>();
        try(PreparedStatement stmt=conn.prepareStatement("select * from CityLines where IdCity1=? or IdCity2=?");) {
            stmt.setInt(1, IdCity);
            stmt.setInt(2, IdCity);
            try(ResultSet rs=stmt.executeQuery();){
                while(rs.next()){
                    if(rs.getInt("IdCity1")!=IdCity){
                        cities.add(rs.getInt("IdCity1"));
                    }
                    else{
                        cities.add(rs.getInt("IdCity2"));
                    }
                }
                return cities;
            }
            catch (SQLException ex) {
                Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
            }   
        } catch (SQLException ex) {
            Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List<Integer> getShops(int IdCity) {
        if(!cityExists(IdCity)){
            return null;
        }
        Connection conn=DB.getInstance().getConnection();
        ArrayList<Integer> shops=new ArrayList<>();
        try(PreparedStatement stmt=conn.prepareStatement("select * from Shop where IdCity=?");) {
            stmt.setInt(1, IdCity);
            try(ResultSet rs=stmt.executeQuery();){
                while(rs.next()){
                    shops.add(rs.getInt("IdShop"));
                }
                return shops;
            }
            catch (SQLException ex) {
                Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
            }   
        } catch (SQLException ex) {
            Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
}
