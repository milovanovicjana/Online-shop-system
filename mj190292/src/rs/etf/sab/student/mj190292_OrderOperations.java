/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.etf.sab.student;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.sql.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.CityOperations;
import rs.etf.sab.operations.GeneralOperations;
import rs.etf.sab.operations.OrderOperations;


public class mj190292_OrderOperations implements OrderOperations{
    
    private static boolean articleExistsInArticleTable(int IdArticle){
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
    
    private static boolean orderHasAdditionalDiscount(int IdOrder){
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement("select hasAdditionalDiscount from [order] where IdOrder=?");) {
            stmt.setInt(1, IdOrder);
            try(ResultSet rs=stmt.executeQuery();) {
                if(rs.next()){
                    if(rs.getInt(1)==1){
                        return true;
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
     
    private static boolean articleExistsInOrder(int IdArticle,int IdOrder){
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement("select * from Item where IdArticle=? and IdOrder=?");) {
            stmt.setInt(1, IdArticle);
            stmt.setInt(2, IdOrder);
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
    
    private static int getIdArticle(int IdItem){
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement("select IdArticle from Item where IdItem=?");) {
            stmt.setInt(1, IdItem);
            try(ResultSet rs=stmt.executeQuery();) {
                if(rs.next()){
                    return rs.getInt("IdArticle");
                }
            } catch (SQLException ex) {
                Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }
    
    private static int getCount(int IdItem){
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement("select Count from Item where IdItem=?");) {
            stmt.setInt(1, IdItem);
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
    
    private static int getNumberOfCities(){
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement("select coalesce(count(*),0) as NumberOfCities from city");) {
           
            try(ResultSet rs=stmt.executeQuery();) {
                if(rs.next()){
                    return rs.getInt("NumberOfCities");
                }
            } catch (SQLException ex) {
                Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
        
    private static boolean articleIsAvailable(int IdArticle,int count){
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement("select Count from Article where IdArticle=?");) {
            stmt.setInt(1, IdArticle);
            try(ResultSet rs=stmt.executeQuery();) {
                if(rs.next()){
                    int available=rs.getInt(1);
                    return available>=count;
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
    public int addArticle(int IdOrder, int IdArticle, int count) {
        if(!articleExistsInArticleTable(IdArticle) || !orderExists(IdOrder) || count<=0){
            return -1;
        }
        String orderStatus=getState(IdOrder);
        if(!orderStatus.equals("created")){
            return -1;
        }
        if(!articleIsAvailable(IdArticle, count)){
            return -1;
        }
        Connection conn=DB.getInstance().getConnection();
        if(articleExistsInOrder(IdArticle, IdOrder)){ //increment item count 
            try(PreparedStatement stmt=conn.prepareStatement("update Item set count=count+? where IdArticle=? and IdOrder=?;"
                    + "select IdItem from Item where IdArticle=? and IdOrder=?;");) {
                stmt.setInt(1, count);
                stmt.setInt(2, IdArticle);
                stmt.setInt(3, IdOrder);
                stmt.setInt(4, IdArticle);
                stmt.setInt(5, IdOrder);
                try(ResultSet rs=stmt.executeQuery();) {
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
        else{
            try(PreparedStatement stmt=conn.prepareStatement("insert into Item(IdOrder,IdArticle,Count) values(?,?,?);",Statement.RETURN_GENERATED_KEYS);) {
                stmt.setInt(1, IdOrder);
                stmt.setInt(2, IdArticle);
                stmt.setInt(3, count);
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

    @Override
    public int removeArticle(int IdOrder, int IdArticle) {
        if(!articleExistsInOrder(IdArticle,IdOrder)){
            return -1;
        }
        String orderStatus=getState(IdOrder);
        if(!orderStatus.equals("created")){
            return -1;
        }
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement("delete from Item where IdArticle=? and IdOrder=?");) {
                stmt.setInt(1, IdArticle);
                stmt.setInt(2, IdOrder);
                stmt.executeUpdate();
                return 1;
        } catch (SQLException ex) {
                Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    @Override
    public List<Integer> getItems(int IdOrder) {
        if(!orderExists(IdOrder)){
            return null;
        }
        Connection conn=DB.getInstance().getConnection();
        ArrayList<Integer> items=new ArrayList<>();
        try(PreparedStatement stmt=conn.prepareStatement("select IdItem from Item where IdOrder=?");) {
            stmt.setInt(1, IdOrder);
            try(ResultSet rs=stmt.executeQuery();){
                while(rs.next()){
                    items.add(rs.getInt(1));
                }
                return items;
            }
            catch (SQLException ex) {
                Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
            }   
           
        } catch (SQLException ex) {
            Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public BigDecimal getBuyerOrderPrice(int IdOrder) {
       
        Connection conn=DB.getInstance().getConnection();
     
        try(PreparedStatement stmt=conn.prepareStatement("select sum((a.Price-a.Price*s.Discount/100)*i.count) as FinalPrice\n" +
                        "	from item i join article a on a.IdArticle=i.IdArticle \n" +
                        "	join  shop s on s.IdShop=a.IdShop\n" +
                        "	where i.IdOrder=?");) {
                stmt.setInt(1, IdOrder);
               
                try(ResultSet rs=stmt.executeQuery();){
                   rs.next();
                   return rs.getBigDecimal(1);
                }
                catch (SQLException ex) {
                    Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
                }  
        } catch (SQLException ex) {
                Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new BigDecimal("-1");
    }
    
    private static boolean itemsAreAvailable(List<Integer> items){
        for(Integer item:items){
            int IdArticle=getIdArticle(item);
            int Count=getCount(item);
            if(!articleIsAvailable(IdArticle, Count)){
                return false;
            } 
        }
        return true;
    }

    private static void updateArticleCountInShop(List<Integer> items){
        for(Integer item:items){
            int IdArticle=getIdArticle(item);
            int Count=getCount(item);
            Connection conn=DB.getInstance().getConnection();
            try(PreparedStatement stmt=conn.prepareStatement("update article set count=count-? where IdArticle=?");) {
                stmt.setInt(1, Count);
                stmt.setInt(2, IdArticle);
                stmt.executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        }  
    }
   
    private static BigDecimal getSumOfTransactionInPrevious30Days(int IdBuyer){
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement("select coalesce(sum(t.amount),0) as Ukupno\n" +
                "from [Transaction]t join [Order]o on o.IdOrder=t.IdOrder\n" +
                "where datediff(day, o.SentTime, ?) < 30 and t.IdClientfrom=?");) {
            
            Date today=new Date(new mj190292_GeneralOperations().getCurrentTime().getTimeInMillis());
            stmt.setDate(1, today);
            stmt.setInt(2, IdBuyer);
            try(ResultSet rs=stmt.executeQuery();) {
                if(rs.next()){
                    return rs.getBigDecimal("Ukupno");
                }
            } catch (SQLException ex) {
                Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new BigDecimal("-1");
    }
    
    private static void updateOrder(String status,int HasAdditionalDiscount,int IdNearestCity,int TimeToAssemble,int IdOrder,int distanceBetweenBuyerAndNearestShop){
       
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement("update [order] set Status=?,IdNearestCity=?,SentTime=?,"
                                  + "RecievedTime=?,TimeToAsseble=?,HasAdditionalDiscount=? where IdOrder=?");) {
            GeneralOperations go=new mj190292_GeneralOperations();
            stmt.setString(1, status);
            stmt.setInt(2, IdNearestCity);
            stmt.setDate(3, new Date(go.getCurrentTime().getTimeInMillis()));
            Calendar date = Calendar.getInstance();
            date.setTime(go.getCurrentTime().getTime());
            date.add(Calendar.DATE, (TimeToAssemble + distanceBetweenBuyerAndNearestShop));
            stmt.setDate(4, new Date(date.getTimeInMillis()));
            stmt.setInt(5, TimeToAssemble);
            stmt.setInt(6, HasAdditionalDiscount);
            stmt.setInt(7, IdOrder);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
         
     }
    
    private static int distanceBetweenCities(int IdCity1,int IdCity2){
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement("select * from CityLines where ((IdCity1=? and IdCity2=?) or (IdCity2=? and IdCity1=?))");) {
            stmt.setInt(1, IdCity1);
            stmt.setInt(2, IdCity2);
            stmt.setInt(3, IdCity1);
            stmt.setInt(4, IdCity2);
           
            try(ResultSet rs=stmt.executeQuery();){
                if(rs.next()){
                    return rs.getInt("distance");
                }
            }
            catch (SQLException ex) {
                Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
            }   
           
        } catch (SQLException ex) {
            Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Integer.MAX_VALUE;
    }
    
    private static int[] findMinimunIndexAndDistance(int d[],List<Integer> S){
        int min=Integer.MAX_VALUE;
        int index=-1;
        for(int i=0;i<d.length;i++){ //nalazi prvi minimum
            if(d[i]<min && !S.contains(i)){
                min=d[i];
                index=i;
            }
        }
        int retArray[]=new int[2];
        retArray[0]=index;
        retArray[1]=min;
        return retArray;
    }
   
    private static  List<int[]> dijkstraAlgorithm(int IdCityFrom){
        
        int d[]=new int[getNumberOfCities()-1]; //distance
        int t[]=new int[getNumberOfCities()-1]; //path
        int citiesId[]=new int[getNumberOfCities()-1]; //cityId
        
        CityOperations co=new mj190292_CityOperations();
        List<Integer>cities=co.getCities();
        cities.remove(new Integer(IdCityFrom));
        int index=0;
        
        for(Integer city:cities){
            d[index]=distanceBetweenCities(IdCityFrom,city);
            citiesId[index]=city;
            if(d[index]!=Integer.MAX_VALUE){
                t[index]=IdCityFrom;
            }
            else{
                 t[index]=-1;
            }
            index++;
        }
        List<Integer> S=new ArrayList<>();
        for(int k=0;k<6;k++){
            int indexAndMin[]=findMinimunIndexAndDistance(d,S);
            int minIndex=indexAndMin[0];
            int min=indexAndMin[1];
            S.add(minIndex);
            for(int j=0;j<d.length;j++){
                if(!S.contains(j)){
                    int distance=distanceBetweenCities(citiesId[j], citiesId[minIndex]);
                    if(distance!=Integer.MAX_VALUE && min+distance<d[j]){
                       
                        d[j]=min+distance;
                        t[j]=minIndex;
                    } 
                } 
            } 
          
        }
        
//        System.out.println("D: ");
//        for(int i=0;i<d.length;i++){
//            System.out.print(d[i]+" ");
//        }
//        System.out.println("\nT:");
//        for(int i=0;i<t.length;i++){
//            System.out.print(t[i]+" ");
//        }
//        System.out.println("\nCitiyId: ");
//        for(int i=0;i<citiesId.length;i++){
//            System.out.print(citiesId[i]+" ");
//        }        
        
        List<int[]>l=new ArrayList<>();
        l.add(t);
        l.add(citiesId);
        l.add(d);
        return l;
    }
    
    private static void path(int i,int t[],int idCityFrom,int citiesId[],List<Integer>pathh){
        if(i==idCityFrom){
            pathh.add(idCityFrom);
        }
        else{
            if(t[i]==-1){
                System.out.print("Nema putanje");
            }
            else{
                path(t[i], t,idCityFrom,citiesId,pathh);
                pathh.add(citiesId[i]);
            }
        }
    }
   
    private static int getIndex(int []citiesId,int IdCity){
        for(int i=0;i<citiesId.length;i++){
            if(citiesId[i]==IdCity){
                return i;
            }
        }
        return -1;
    }
    
    private static int getBuyerCity(int IdBuyer){
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement("select IdCity from Buyer where IdBuyer=?");) {
            stmt.setInt(1, IdBuyer);
            try(ResultSet rs=stmt.executeQuery();) {
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
    
    private static boolean cityHasShop(int IdCity){
        
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement("select * from shop where IdCity=?");) {
            stmt.setInt(1, IdCity);
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
   
    private static int nearestCityWithShop(int d[],int citiesId[]){
        List<Integer> S=new ArrayList<>();
        int res[]=new int[2];
        res=findMinimunIndexAndDistance(d, S);
        int index=res[0];
        int min=res[1];
        
        while(S.size()<citiesId.length){
            S.add(index);
            int idNearestCity=citiesId[index];
            if(cityHasShop(idNearestCity)){
                return idNearestCity;
            }
            res=findMinimunIndexAndDistance(d, S);
            index=res[0];
            min=res[1];
        }
        return -1;  
    }
    
    private static int pathDistance(List<Integer> path){
        if(path.size()==0 || path.size()==1)return 0;
        
        int distance=0;
        
        int i=0;
        
        while(i!=(path.size()-1)){
            distance+=distanceBetweenCities(path.get(i), path.get(i+1));
            i++;
        }
        return distance;
        
    }
   
    public static List<Integer> getArticleOrderCities(int IdOrder) {
        
        Connection conn=DB.getInstance().getConnection();
        ArrayList<Integer> cities=new ArrayList<>();
        try(PreparedStatement stmt=conn.prepareStatement("select distinct(s.IdCity) from item i join article a on a.IdArticle=i.IdArticle join shop s on s.IdShop=a.IdShop where i.IdOrder=?");) {
            stmt.setInt(1, IdOrder);
            try(ResultSet rs=stmt.executeQuery();){
                while(rs.next()){
                    cities.add(rs.getInt("IdCity"));
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
   
    private static int daysToAssembleOrder(int IdOrder,int IdNearestCity){
        List<Integer> ArticleOrderCities=getArticleOrderCities(IdOrder); //gradovi iz kojih su artikli poruceni
        
        List<int[]> result=dijkstraAlgorithm(IdNearestCity);
       
        int t1[]=result.get(0);
        int citiesId1[]=result.get(1);
        int dist=0;
        for(Integer city:ArticleOrderCities){
            List<Integer> pathh=new ArrayList<>();
            if(city==IdNearestCity){ 
                continue;
            }
            int index=getIndex(citiesId1, city);
            path(index, t1,IdNearestCity,citiesId1,pathh);
            if(pathDistance(pathh)>dist){
                dist=pathDistance(pathh);
            }
        }
        
        return dist;
    }
   
    //TimeOfExecution mi ne valja!!!
    private static void insertTransactionsAndUpdateAccount(BigDecimal amountBuyer,BigDecimal amountSystem,int IdOrder,int IdBuyer){
            Connection conn=DB.getInstance().getConnection();
            try(PreparedStatement stmt=conn.prepareStatement("insert into [Transaction](Amount, TimeOfExecution,IdOrder, IdClientFrom, IdClientTo) values (?,?,?,?,(select top 1 IdSystem from System));\n" +
                                                            "update Account set Credit = Credit + ? where IdClient=(select top 1 IdSystem from System);\n" +
                                                            "update Account set Credit = Credit - ? where IdClient = ?");) {
                GeneralOperations go=new mj190292_GeneralOperations();
                stmt.setBigDecimal(1, amountBuyer);
                stmt.setDate(2, new Date(go.getCurrentTime().getTimeInMillis()));
                stmt.setInt(3, IdOrder);
                stmt.setInt(4, IdBuyer);
                stmt.setBigDecimal(5, amountSystem);
                stmt.setBigDecimal(6, amountBuyer);
                stmt.setInt(7, IdBuyer);
                stmt.executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
   
    private void insertPaths(List<Integer> pathBetweenBuyerAndNearestCity, int IdOrder) {
        int numOfPaths=pathBetweenBuyerAndNearestCity.size()-1;
        Connection conn=DB.getInstance().getConnection();
        
        int i=0;
        
        while(i!=(pathBetweenBuyerAndNearestCity.size()-1)){
            int idCity1=pathBetweenBuyerAndNearestCity.get(i);
            int idCity2=pathBetweenBuyerAndNearestCity.get(i+1);
            //System.out.println(idCity1+"-"+idCity2);
            try(PreparedStatement stmt=conn.prepareStatement("select IdLine from CityLines where (IdCity1=? and IdCity2=?) or (IdCity2=? and IdCity1=?)");) {
                stmt.setInt(1, idCity1);
                stmt.setInt(2, idCity2);
                stmt.setInt(3, idCity1);
                stmt.setInt(4, idCity2);
                ResultSet rs=stmt.executeQuery();
                rs.next();
                int IdLine=rs.getInt(1);
                try(PreparedStatement stmt1=conn.prepareStatement("insert into orderpath(Part,IdLine,IdOrder) values(?,?,?)");) {

                   stmt1.setInt(1, numOfPaths);
                   stmt1.setInt(2, IdLine);
                   stmt1.setInt(3, IdOrder);
                   stmt1.executeUpdate();

                } catch (SQLException ex) {
                    Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
                }

            } catch (SQLException ex) {
                Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            i++;
            numOfPaths--;
        }

    }
    
    private boolean allArticlesAreInBuyersCity(int IdOrder,int IdBuyerCity){
            Connection conn=DB.getInstance().getConnection();
            try(PreparedStatement stmt=conn.prepareStatement("select coalesce(count(*),0) as num1 from \n" +
                                                            "item i\n" +
                                                            "where i.idorder=?");
                    PreparedStatement stmt1=conn.prepareStatement("select coalesce(count(*),0) as num2 from \n" +
                                                            "item i join [order] o on o.idorder=i.idorder\n" +
                                                            "join article a on i.idarticle=a.idarticle join shop s on s.idshop=a.idshop join city c on s.idcity=c.idcity\n" +
                                                            "where o.idorder=? and c.idcity=?");) {
                stmt.setInt(1, IdOrder);
                stmt1.setInt(1, IdOrder);
                stmt1.setInt(2, IdBuyerCity);
                ResultSet rs=stmt.executeQuery();
                ResultSet rs1=stmt1.executeQuery();
                rs.next();
                rs1.next();
                if(rs.getInt(1)==rs1.getInt(1)){
                    return true;
                }
                } catch (SQLException ex) {
                    Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
                }
            return false;
        
    }
    
    private boolean buyerCityHasShop(int IdBuyerCity){
           Connection conn=DB.getInstance().getConnection();
            try(PreparedStatement stmt=conn.prepareStatement("select * from shop s where s.idcity=?");) {
                stmt.setInt(1, IdBuyerCity);
                ResultSet rs=stmt.executeQuery();
                if(rs.next()){
                    return true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            return false;
    }
    
    //proveriti edge case da li moze da narucuje iz prodavnice koja je u njegovom gradu
    @Override
    public int completeOrder(int IdOrder) {
     
        if(!orderExists(IdOrder)){
            System.out.println("ne postoji por");
            return -1;
        }
        String status=getState(IdOrder);
        if(!status.equals("created")){
            return -1;
        }
        
        //1.provera da li ima dovoljo item-a u shop-u koji su ubaceni u porudzbinu
        List<Integer> items=getItems(IdOrder);
        if(items.size()==0){//u porudzbini ne postoji nijedan item pa ne moze ni da se kompletira
            return -1;
        }
        if(!itemsAreAvailable(items)){
            return -1; //nema dovoljno artikala u prodavnicama
        }
        
        //2.update kolicine artikala u prodavnici
        updateArticleCountInShop(items);
        
        //3.racunanje da li postoji dodatni popust
        int IdBuyer=getBuyer(IdOrder);
        BigDecimal sum=getSumOfTransactionInPrevious30Days(IdBuyer);
        System.out.println("suma za zadnjih 30 dana: "+sum);
        int hasAdditionalDiscount=0;
        if((sum.compareTo(new BigDecimal("10000")))==1){
            hasAdditionalDiscount=1;
        } 
        
        //4. update porudzbine
        int IdBuyerCity=getBuyerCity(IdBuyer);
        //IdBuyerCity=268;
       //
        List<int[]> res=dijkstraAlgorithm(IdBuyerCity);
        int citiesId[]=res.get(1);
        int d[]=res.get(2);
        int t[]=res.get(0);
        
        int IdNearestCity;
        List <Integer>pathBetweenBuyerAndNearestCity=new ArrayList<>();
        int distanceBetweenBuyerAndNearestShop=0;
        int timeToAssemble=0;
        if(allArticlesAreInBuyersCity(IdOrder, IdBuyerCity)){  //svi artikli koje je kupac narucio su u njegovom gradu!
            IdNearestCity=IdBuyerCity;
            updateOrder("arrived",hasAdditionalDiscount,IdNearestCity,timeToAssemble,IdOrder,distanceBetweenBuyerAndNearestShop);
        }
        else {
            
            if(buyerCityHasShop(IdBuyerCity)){
                System.out.println("grad kupca ima prod"); 
                IdNearestCity=IdBuyerCity;//grad u kome se se sklapati porudzbina
                distanceBetweenBuyerAndNearestShop=0;
                timeToAssemble=daysToAssembleOrder(IdOrder,IdNearestCity);
                updateOrder("sent",hasAdditionalDiscount,IdNearestCity,timeToAssemble,IdOrder,distanceBetweenBuyerAndNearestShop);

                //6.dodavanje putanja u tabelu path zbog racunanja fje getLocation
               // insertPaths(pathBetweenBuyerAndNearestCity,IdOrder);
            }
            else{
                    IdNearestCity=nearestCityWithShop(d, citiesId);//grad u kome se se sklapati porudzbina
                    int index=getIndex(citiesId, IdNearestCity);
                    path(index, t,IdBuyerCity,citiesId,pathBetweenBuyerAndNearestCity); //putanja od kupca do najblize prodavnice
                    distanceBetweenBuyerAndNearestShop=pathDistance(pathBetweenBuyerAndNearestCity);
                    timeToAssemble=daysToAssembleOrder(IdOrder,IdNearestCity);
                    System.out.print("Putanja od kupca do najblize prodavnice: ");
                    for(Integer i:pathBetweenBuyerAndNearestCity){
                        System.out.print(i+" ");
                    }
                    updateOrder("sent",hasAdditionalDiscount,IdNearestCity,timeToAssemble,IdOrder,distanceBetweenBuyerAndNearestShop);

                    //6.dodavanje putanja u tabelu path zbog racunanja fje getLocation
                    insertPaths(pathBetweenBuyerAndNearestCity,IdOrder);
            }
       


        }
        if(IdNearestCity==-1)return -1;
        
      
        System.out.println("\nNearest city with shop: "+IdNearestCity);
        System.out.println("distanceBetweenBuyerAndNearestShop: "+distanceBetweenBuyerAndNearestShop);
        System.out.println("vreme sklapanja: "+timeToAssemble);
        
        //5.dodavanje transakcije od kupca ka sistemu i update racuna sistema i kupca
        BigDecimal amountBuyer=getFinalPrice(IdOrder);
        BigDecimal amountSystem=getBuyerOrderPrice(IdOrder);
        insertTransactionsAndUpdateAccount(amountBuyer,amountSystem,IdOrder,IdBuyer);
        
        return 1;
    }

    @Override
    public BigDecimal getFinalPrice(int IdOrder) {
        if(!orderExists(IdOrder)){ 
            return new BigDecimal("-1");
        }
        String status=getState(IdOrder);
        if(status.equals("created") ){ //porudzbina nije kompletirana
            return new BigDecimal("-1");
        }
        String sql="{call SP_FINAL_PRICE(?,?)}";
        Connection conn=DB.getInstance().getConnection();
        try (CallableStatement stmt=conn.prepareCall(sql);){
            stmt.setInt(1, IdOrder);
            stmt.registerOutParameter(2, java.sql.Types.DECIMAL);
            stmt.execute();
            return stmt.getBigDecimal(2).setScale(3);
        } catch (SQLException ex) {
             Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new BigDecimal("-1");
    }

    @Override
    public BigDecimal getDiscountSum(int IdOrder) {
        if(!orderExists(IdOrder)){ 
            return new BigDecimal("-1");
        }
        String status=getState(IdOrder);
        if(status.equals("created") ){ //porudzbina nije kompletirana
            return new BigDecimal("-1");
        }
        String query="select sum((a.Price)*i.count)as PriceWithoutDiscount, sum((a.Price-a.Price*s.Discount/100)*i.count) as PriceWithShopDiscount\n" +
                "	from item i join article a on a.IdArticle=i.IdArticle \n" +
                "	join  shop s on s.IdShop=a.IdShop\n" +
                "	where i.IdOrder=?";
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement(query);) {
            stmt.setInt(1, IdOrder);
            try(ResultSet rs=stmt.executeQuery();){
                if(rs.next()){
                    BigDecimal PriceWithoutDiscount=rs.getBigDecimal("PriceWithoutDiscount").setScale(3);
                    BigDecimal PriceWithShopDiscount=rs.getBigDecimal("PriceWithShopDiscount").setScale(3);
                   
                    boolean hasAdditionalDiscount=orderHasAdditionalDiscount(IdOrder);
                    if(hasAdditionalDiscount){
                        return PriceWithoutDiscount.subtract(PriceWithShopDiscount.multiply(new BigDecimal("0.98"))).setScale(3);
                    }
                    else return PriceWithoutDiscount.subtract(PriceWithShopDiscount).setScale(3);

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
    public String getState(int IdOrder) {
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement("select Status from [Order] where IdOrder=?");) {
            stmt.setInt(1, IdOrder);
            try(ResultSet rs=stmt.executeQuery();) {
                if(rs.next()){
                    return rs.getString(1);
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
    public Calendar getSentTime(int IdOrder) {
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement("select SentTime from [Order] where IdOrder=?");) {
            stmt.setInt(1, IdOrder);
            try(ResultSet rs=stmt.executeQuery();) {
                if(rs.next()){
                    Calendar c=Calendar.getInstance();
                    Date d=rs.getDate("SentTime");
                    if(d==null)return null;
                    else{
                       c.setTime(d);
                       return c; 
                    }
                    
                }
            } catch (SQLException ex) {
                Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    //proveriti za status
    @Override
    public Calendar getRecievedTime(int IdOrder) {
        String status=getState(IdOrder);
        if(!status.equals("arrived")){
            return null;
        }
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement("select RecievedTime from [Order] where IdOrder=?");) {
            stmt.setInt(1, IdOrder);
            try(ResultSet rs=stmt.executeQuery();) {
                if(rs.next()){
                    Calendar c=Calendar.getInstance();
                    Date d=rs.getDate("RecievedTime");
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
    public int getBuyer(int IdOrder) {
        if(!orderExists(IdOrder)){
            return -1;
        }
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement stmt=conn.prepareStatement("select IdBuyer from [Order] where IdOrder=?");) {
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
    public int getLocation(int IdOrder) {
        String status=getState(IdOrder);
        if(status.equals("created")){
            return -1;
        }
            
        Connection conn=DB.getInstance().getConnection();
        try(PreparedStatement ps = conn.prepareStatement(
                "select * from [Order] where IdOrder = ?" );
            PreparedStatement ps1 = conn.prepareStatement(
                    "select c.IdCity1 , c.IdCity2 , c.Distance from orderpath p  join cityLines c on p.IdLine = c.IdLine where p.IdOrder = ? order by p.Part asc"
            )
        ){
            ps.setInt(1, IdOrder);
            try(  ResultSet rs = ps.executeQuery();){
                if(rs.next()) {
                    if(status.equals("arrived")){
                        int buyerCity=getBuyerCity(rs.getInt("IdBuyer"));
                        return buyerCity;
                    }
                    Date sentTime = rs.getDate("SentTime");
                    Calendar currTime = Calendar.getInstance();
                    currTime.setTime(new mj190292_GeneralOperations().getCurrentTime().getTime());
                    
                    long sendingTime=sentTime.getTime();
                    long now=currTime.getTimeInMillis();

                    int IdNearestCity = rs.getInt("IdNearestCity");
                    int TimeToAssemble = rs.getInt("TimeToAsseble");
         
                    
                    long diff=0;
                    if(sendingTime-now<0){
                        diff = TimeUnit.DAYS.convert(now-sendingTime, TimeUnit.MILLISECONDS);
                    }
                    else{
                        diff = TimeUnit.DAYS.convert(sendingTime-now, TimeUnit.MILLISECONDS);
                    }
                    if(diff <= TimeToAssemble) {
                        return  IdNearestCity;
                    }else{
                        diff -= TimeToAssemble; 
                        int city = IdNearestCity;
                        ps1.setInt(1, IdOrder);
                        try( ResultSet rs1 = ps1.executeQuery();) {
                              while(rs1.next()) {
                                if(rs1.getInt("Distance") > diff) return city;
                                diff -= rs1.getInt("Distance");
                                if(rs1.getInt("IdCity1") != city){
                                    city=rs1.getInt("IdCity1");
                                }
                                else{
                                    city=rs1.getInt("IdCity2");
                                }
                             }
                            return city;
                            
                        } catch (SQLException e) {
                           Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, e);

                        }
                    }  
                }
            }
            catch (SQLException ex) {
                Logger.getLogger(mj190292_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
            }    
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }


    
}
