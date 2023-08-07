/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.etf.sab.student;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.*;


public class mj190292_BuyerOperations implements BuyerOperations{
    
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

    private static boolean buyerExists(int IdBuyer){
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement("select * from Buyer where IdBuyer=?");) {
            stmt.setInt(1, IdBuyer);
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
    
    @Override
    public int createBuyer(String Name, int IdCity) {
        if(!cityExists(IdCity)){
            return -1;
        }
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement("insert into Member(Name) values(?)",Statement.RETURN_GENERATED_KEYS);) {
            stmt.setString(1, Name);
            stmt.executeUpdate(); 
            try(ResultSet rs=stmt.getGeneratedKeys();){
                 if(rs.next()){
                    int IdBuyer=rs.getInt(1);
                    try(PreparedStatement stmt1=conn.prepareStatement("insert into Buyer(IdBuyer,IdCity) values(?,?)");
                        PreparedStatement stmt2=conn.prepareStatement("insert into Account(IdClient,Credit) values(?,?)");) {
                        stmt1.setInt(1, IdBuyer);
                        stmt1.setInt(2, IdCity);
                        
                        stmt2.setInt(1, IdBuyer);
                        stmt2.setInt(2, 0);
                        
                        stmt1.executeUpdate(); 
                        stmt2.executeUpdate(); 
                        
                        return IdBuyer;
                    } catch (SQLException ex) {
                        Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    @Override
    public int setCity(int IdBuyer, int IdCity) {
        if(!cityExists(IdCity) || !buyerExists(IdBuyer)){
            return -1;
        }
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement("update Buyer set IdCity=? where IdBuyer=?");) {
            stmt.setInt(1, IdCity);
            stmt.setInt(2, IdBuyer);
            stmt.executeUpdate(); 
            return 1;
        } catch (SQLException ex) {
            Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    @Override
    public int getCity(int IdBuyer) {
        if(!buyerExists(IdBuyer)){
            return -1;
        }
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement("select * from Buyer where IdBuyer=?");) {
            stmt.setInt(1, IdBuyer);
            try(ResultSet rs=stmt.executeQuery();){
                if(rs.next()){
                    return rs.getInt("IdCity");
                }
            } catch (SQLException ex) {
                Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    @Override
    public BigDecimal increaseCredit(int IdBuyer, BigDecimal credit) {
        if( !buyerExists(IdBuyer)){
            return null;
        }
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement("update Account set Credit=Credit+? where IdClient=?; "
                + "select Credit from Account where IdClient=?");) {
            stmt.setBigDecimal(1, credit);
            stmt.setInt(2, IdBuyer);
            stmt.setInt(3, IdBuyer);
            try(ResultSet rs=stmt.executeQuery();){
                if(rs.next()){
                    return rs.getBigDecimal(1);
                }
            } catch (SQLException ex) {
                Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public int createOrder(int IdBuyer) {
        if(!buyerExists(IdBuyer)){
            return -1;
        }
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement("insert into [Order](IdBuyer,Status) values(?,?)",Statement.RETURN_GENERATED_KEYS);) {
            stmt.setInt(1, IdBuyer);
            stmt.setString(2, "created");
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
    public List<Integer> getOrders(int IdBuyer) {
        if(!buyerExists(IdBuyer)){
            return null;
        }
        Connection conn=DB.getInstance().getConnection();
        ArrayList<Integer> orders=new ArrayList<>();
        try(PreparedStatement stmt=conn.prepareStatement("select IdOrder from [Order] where IdBuyer=?");) {
            stmt.setInt(1, IdBuyer);
          
            try(ResultSet rs=stmt.executeQuery();){
                while(rs.next()){
                    orders.add(rs.getInt(1));
                }
                return orders;
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
    public BigDecimal getCredit(int IdBuyer) {
        if(!buyerExists(IdBuyer)){
            return null;
        }
        Connection conn=DB.getInstance().getConnection();
      
        try(PreparedStatement stmt=conn.prepareStatement("select Credit from Account where IdClient=?");) {
            stmt.setInt(1, IdBuyer);
          
            try(ResultSet rs=stmt.executeQuery();){
                if(rs.next()){
                    return rs.getBigDecimal(1);
                }
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
