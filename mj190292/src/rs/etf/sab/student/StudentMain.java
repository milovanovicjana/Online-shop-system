package rs.etf.sab.student;

import rs.etf.sab.tests.*;
import rs.etf.sab.operations.TransactionOperations;
import rs.etf.sab.operations.OrderOperations;
import rs.etf.sab.operations.BuyerOperations;
import rs.etf.sab.operations.ArticleOperations;
import rs.etf.sab.operations.CityOperations;
import rs.etf.sab.operations.ShopOperations;
import rs.etf.sab.operations.GeneralOperations;
public class StudentMain {
    
  
    public static void main(String[] args) {

        ArticleOperations articleOperations = new mj190292_ArticleOperations(); // Change this for your implementation (points will be negative if interfaces are not implemented).
        BuyerOperations buyerOperations = new mj190292_BuyerOperations();
        CityOperations cityOperations = new mj190292_CityOperations();
        GeneralOperations generalOperations = new mj190292_GeneralOperations();
        OrderOperations orderOperations = new mj190292_OrderOperations();
        ShopOperations shopOperations = new mj190292_ShopOperations();
        TransactionOperations transactionOperations = new mj190292_TransactionOperations();

        TestHandler.createInstance(
                articleOperations,
                buyerOperations,
                cityOperations,
                generalOperations,
                orderOperations,
                shopOperations,
                transactionOperations
        );

        TestRunner.runTests();


    }
}
