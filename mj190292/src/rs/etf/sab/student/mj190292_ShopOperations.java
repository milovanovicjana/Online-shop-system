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
import rs.etf.sab.operations.ShopOperations;

/**
 *
 * @author HS
 */
public class mj190292_ShopOperations implements ShopOperations{
    
    private static boolean cityExists(String CityName){
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement("select * from city where Name=?");) {
            stmt.setString(1, CityName);
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

    private static boolean shopExists(String ShopName){
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement("select * from Shop s join Member m on m.IdM=s.IdShop and m.Name=?");) {
            stmt.setString(1, ShopName);
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
            stmt.executeQuery(); 
        } catch (SQLException ex) {
            Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    private static int getCityId(String CityName){
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement("select IdCity from City where Name=?");) {
            stmt.setString(1, CityName);
            try(ResultSet rs=stmt.executeQuery();) {
                if(rs.next()){
                    return rs.getInt(1);
                }
            } catch (SQLException ex) {
                Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            stmt.executeQuery(); 
        } catch (SQLException ex) {
            Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }
    
    private static boolean articleExists(int IdArticle){
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement("select * from Article where IdArticle=?");) {
            stmt.setInt(1, IdArticle);
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
    public int createShop(String ShopName, String CityName) {
        if(!cityExists(CityName)){
            return -1;
        }
        if(shopExists(ShopName)){ //shops must have unique name
            return -1;
        }
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement("insert into Member(Name) values(?)",Statement.RETURN_GENERATED_KEYS);) {
            stmt.setString(1, ShopName);
            stmt.executeUpdate(); 
            try(ResultSet rs=stmt.getGeneratedKeys();){
                if(rs.next()){
                    int IdShop=rs.getInt(1);
                    try(PreparedStatement stmt1=conn.prepareStatement("insert into Shop(IdShop,IdCity,Discount) values(?,?,?);"
                            + "insert into Account(IdClient,Credit) values(?,?)");) {
                        stmt1.setInt(1, IdShop);
                        stmt1.setInt(2, getCityId(CityName)); //sigurno ce naci IdCity jer sam gore proverila da li postoji grad sa tim imenom
                        stmt1.setInt(3, 0);
                        stmt1.setInt(4, IdShop);
                        stmt1.setInt(5, 0);
                        stmt1.executeUpdate(); 
                        return IdShop;
                        
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
        return -1;
    }

    @Override
    public int setCity(int IdShop, String cityName) {
        if(!shopExists(IdShop) || !cityExists(cityName)){
            return -1;
        }
        int IdCity=getCityId(cityName); //sigurno postoji
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement("update Shop set IdCity=? where IdShop=?");) {
            stmt.setInt(1, IdCity);
            stmt.setInt(2, IdShop);
            stmt.executeUpdate(); 
            return 1;
        } catch (SQLException ex) {
            Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    @Override
    public int getCity(int IdShop) {
        if(!shopExists(IdShop)){
            return -1;
        }
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement("select IdCity from Shop where IdShop=?");) {
            stmt.setInt(1, IdShop);
            try(ResultSet rs=stmt.executeQuery();) {
                if(rs.next()){
                    return rs.getInt(1);
                }
            } catch (SQLException ex) {
                Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            stmt.executeQuery(); 
        } catch (SQLException ex) {
            Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    @Override
    public int setDiscount(int IdShop, int discountPercentage) {
        if(!shopExists(IdShop)){
            return -1;
        }
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement("update Shop set discount=? where IdShop=?");) {
            stmt.setInt(1, discountPercentage);
            stmt.setInt(2, IdShop);
            stmt.executeUpdate(); 
            return 1;
        } catch (SQLException ex) {
            Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    @Override
    public int increaseArticleCount(int IdArticle, int increment) {
        if(!articleExists(IdArticle)){
            return -1;
        }
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement("update Article set count=count+? where IdArticle=?;"
                + "select * from Article where IdArticle=?");) {
            stmt.setInt(1, increment);
            stmt.setInt(2, IdArticle);
            stmt.setInt(3, IdArticle);
            
            try(ResultSet rs=stmt.executeQuery();) {
                if(rs.next()){
                    return rs.getInt("Count");
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
    public int getArticleCount(int IdArticle) {
        if(!articleExists(IdArticle)){
            return -1;
        }
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement("select * from Article where IdArticle=?");) {
            stmt.setInt(1, IdArticle);
            try(ResultSet rs=stmt.executeQuery();) {
                if(rs.next()){
                    return rs.getInt("Count");
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
    public List<Integer> getArticles(int IdShop) {
        if(!shopExists(IdShop)){
            return null;
        }
        ArrayList<Integer> articles=new ArrayList<>();

        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement("select IdArticle from Article where IdShop=?");) {
            stmt.setInt(1, IdShop);
            try(ResultSet rs=stmt.executeQuery();) {
                while(rs.next()){
                    articles.add(rs.getInt(1));
                }
                return articles;
            } catch (SQLException ex) {
                Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return articles;
    }

    @Override
    public int getDiscount(int IdShop) {
        if(!shopExists(IdShop)){
            return -1;
        }
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement("select discount from Shop where IdShop=?");) {
            stmt.setInt(1, IdShop);
            try(ResultSet rs=stmt.executeQuery();) {
                if(rs.next()){
                    return rs.getInt(1);
                }
            } catch (SQLException ex) {
                Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            stmt.executeQuery(); 
        } catch (SQLException ex) {
            Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }
    
}
