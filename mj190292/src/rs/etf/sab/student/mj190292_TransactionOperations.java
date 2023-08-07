/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.etf.sab.student;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.TransactionOperations;

/**
 *
 * @author HS
 */
public class mj190292_TransactionOperations implements TransactionOperations{
    
    
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
        } catch (SQLException ex) {
            Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    private static boolean shopExists(int IdShop){
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement("select * from Shop where IdShop=?");) {
            stmt.setInt(1, IdShop);
            try(ResultSet rs=stmt.executeQuery();) {
                if(rs.next()){
                    return true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private static boolean orderExists(int IdOrder){
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement("select * from [Order] where IdOrder=?");) {
            stmt.setInt(1, IdOrder);
            try(ResultSet rs=stmt.executeQuery();) {
                if(rs.next()){
                    return true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    @Override
    public BigDecimal getBuyerTransactionsAmmount(int IdBuyer) {
        if(!buyerExists(IdBuyer)){
            return new BigDecimal("-1");
        }
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement("select coalesce(sum(Amount),0) as  BuyerAmount from [Transaction] where IdClientFrom=?");) {
            stmt.setInt(1, IdBuyer);
            try(ResultSet rs=stmt.executeQuery();) {
                if(rs.next()){
                    return rs.getBigDecimal("BuyerAmount");
                }
            } catch (SQLException ex) {
                Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new BigDecimal("-1");
    }

    @Override
    public BigDecimal getShopTransactionsAmmount(int IdShop) {
        if(!shopExists(IdShop)){
            return new BigDecimal("-1");
        }
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement("select coalesce(sum(Amount),0) as  ShopAmount from [Transaction] where IdClientTo=?");) {
            stmt.setInt(1, IdShop);
            try(ResultSet rs=stmt.executeQuery();) {
                if(rs.next()){
                    return rs.getBigDecimal("ShopAmount");
                }
            } catch (SQLException ex) {
                Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new BigDecimal("-1");    
    }

    @Override
    public List<Integer> getTransationsForBuyer(int IdBuyer) {
        if(!buyerExists(IdBuyer)){
            return null;
        }
        Connection conn=DB.getInstance().getConnection();
        ArrayList<Integer> transactions=new ArrayList<>();
        try(PreparedStatement stmt=conn.prepareStatement("select IdTransaction from [Transaction] where IdClientFrom=?");) {
            stmt.setInt(1, IdBuyer);
            try(ResultSet rs=stmt.executeQuery();){
                while(rs.next()){
                    transactions.add(rs.getInt(1));
                }
                return transactions;
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
    public int getTransactionForBuyersOrder(int IdOrder) {
        if(!orderExists(IdOrder)){
            return -1;
        }
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement("select IdTransaction \n" +
                                                        "from [Transaction] t join [order] o on o.idOrder=t.IdOrder\n" +
                                                        "where t.IdClientFrom=o.IdBuyer and o.IdOrder=?");) {
            stmt.setInt(1, IdOrder);
            try(ResultSet rs=stmt.executeQuery();){
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
    public int getTransactionForShopAndOrder(int IdOrder, int IdShop) {
        if(!orderExists(IdOrder) || !shopExists(IdShop)){
            return -1;
        }
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement("select IdTransaction \n" +
                                                        "from [Transaction] t\n" +
                                                        "where t.IdClientTo=? and t.IdOrder=?");) {
            stmt.setInt(1, IdShop);
            stmt.setInt(2, IdOrder);
            try(ResultSet rs=stmt.executeQuery();){
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
    public List<Integer> getTransationsForShop(int IdShop) {
        if(!shopExists(IdShop)){
            return null;
        }
        Connection conn=DB.getInstance().getConnection();
        ArrayList<Integer> transactions=new ArrayList<>();
        try(PreparedStatement stmt=conn.prepareStatement("select IdTransaction from [Transaction] where IdClientTo=?");) {
            stmt.setInt(1, IdShop);
            try(ResultSet rs=stmt.executeQuery();){
                while(rs.next()){
                    transactions.add(rs.getInt(1));
                }
                if(transactions.size()==0)return null;
                return transactions;
            }
            catch (SQLException ex) {
                Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
            }   
           
        } catch (SQLException ex) {
            Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    
    //proveriti za null
    @Override
    public Calendar getTimeOfExecution(int IdTransaction) {
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement("select TimeOfExecution from [Transaction] where IdTransaction=?");) {
            stmt.setInt(1, IdTransaction);
            try(ResultSet rs=stmt.executeQuery();) {
                if(rs.next()){
                    Date d=rs.getDate("TimeOfExecution");
                    Calendar c=Calendar.getInstance();
                    c.setTime(d);
                    return c;
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
    public BigDecimal getAmmountThatBuyerPayedForOrder(int IdOrder) {  
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement("select t.Amount \n" +
                                                        "from [Transaction] t join [order] o on o.idOrder=t.IdOrder\n" +
                                                        "where t.IdClientFrom=o.IdBuyer and o.IdOrder=?");) {
            stmt.setInt(1, IdOrder);
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
        return new BigDecimal("-1");
    }

    @Override
    public BigDecimal getAmmountThatShopRecievedForOrder(int IdShop, int IdOrder) {
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement("select t.Amount \n" +
                                                        "from [Transaction] t \n" +
                                                        "where t.IdClientTo=? and o.IdOrder=?");) {
            stmt.setInt(1, IdShop);
            stmt.setInt(2, IdOrder);
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
        return new BigDecimal("-1");
    }

    @Override
    public BigDecimal getTransactionAmount(int IdTransaction) {
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement("select t.Amount \n" +
                                                        "from [Transaction] t \n" +
                                                        "where t.IdTransaction=?");) {
            stmt.setInt(1, IdTransaction);
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
        return new BigDecimal("-1");
    }

    @Override
    public BigDecimal getSystemProfit() {
        
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement("select Credit from Account a where a.IdClient=\n" +
                                                        "(select top 1 IdSystem from System)");
            PreparedStatement stmt1=conn.prepareStatement("select coalesce(sum((a.Price-a.Price*s.Discount/100)*i.count),0) as Sum\n" +
                            "	from item i join article a on a.IdArticle=i.IdArticle \n" +
                            "	join  shop s on s.IdShop=a.IdShop join [order] o on o.IdOrder=i.IdOrder \n" +
                            "	where o.Status='sent'");  
            ) {
            try(ResultSet rs=stmt.executeQuery();
                ResultSet rs1=stmt1.executeQuery();){
                if(rs.next()){
                    BigDecimal credit=rs.getBigDecimal("Credit").setScale(3);
                    if(rs1.next()){
                        BigDecimal sumOfNotArrivedOrder= rs1.getBigDecimal("Sum").setScale(3);
                        return credit.subtract(sumOfNotArrivedOrder).setScale(3);
                    }
                }
            }
            catch (SQLException ex) {
                Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
            }   
           
        } catch (SQLException ex) {
            Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new BigDecimal("-1");
        
    }
    
}
