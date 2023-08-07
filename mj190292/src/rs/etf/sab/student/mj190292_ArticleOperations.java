
package rs.etf.sab.student;import rs.etf.sab.operations.*;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class mj190292_ArticleOperations implements ArticleOperations{
    
    private static boolean shopExists(int IdShop){
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement("select * from shop where IdShop=?");) {
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

    @Override
    public int createArticle(int IdShop, String Name, int Price) {
        if(!shopExists(IdShop)){
            return -1;
        }
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement("insert into article(Name,Count,Price,IdShop) values(?,?,?,?)",Statement.RETURN_GENERATED_KEYS);) {
            stmt.setString(1, Name);
            stmt.setInt(2, 0);
            stmt.setInt(3, Price);
            stmt.setInt(4, IdShop);
            stmt.executeUpdate(); 
            try(ResultSet rs=stmt.getGeneratedKeys();){
                 if(rs.next()){
                    return rs.getInt(1);
                }
            } catch (SQLException ex) {
                Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }
    
}
