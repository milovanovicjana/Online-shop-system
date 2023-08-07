package rs.etf.sab.student;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.List;
import org.junit.Assert;
import rs.etf.sab.operations.TransactionOperations;
import rs.etf.sab.operations.OrderOperations;
import rs.etf.sab.operations.BuyerOperations;
import rs.etf.sab.operations.ArticleOperations;
import rs.etf.sab.operations.CityOperations;
import rs.etf.sab.operations.ShopOperations;
import rs.etf.sab.operations.GeneralOperations;

public class Test {
    
        ArticleOperations articleOperations = new mj190292_ArticleOperations(); // Change this for your implementation (points will be negative if interfaces are not implemented).
        BuyerOperations buyerOperations = new mj190292_BuyerOperations();
        CityOperations cityOperations = new mj190292_CityOperations();
        GeneralOperations generalOperations = new mj190292_GeneralOperations();
        OrderOperations orderOperations = new mj190292_OrderOperations();
        ShopOperations shopOperations = new mj190292_ShopOperations();
        TransactionOperations transactionOperations = new mj190292_TransactionOperations();

        //njihov javni test
        public  void publicTest() {

            generalOperations.eraseAll();
            final Calendar initialTime = Calendar.getInstance();
            initialTime.clear();
            initialTime.set(2018, 0, 1);
            this.generalOperations.setInitialTime(initialTime);
            final Calendar receivedTime = Calendar.getInstance();
            receivedTime.clear();
            receivedTime.set(2018, 0, 22);
            int cityB = this.cityOperations.createCity("B");
            int cityC1 = this.cityOperations.createCity("C1");
            int cityA = this.cityOperations.createCity("A");
            int cityC2 = this.cityOperations.createCity("C2");
            int cityC3 = this.cityOperations.createCity("C3");
            int cityC4 = this.cityOperations.createCity("C4");
            int cityC5 = this.cityOperations.createCity("C5");
            this.cityOperations.connectCities(cityB, cityC1, 8);
            this.cityOperations.connectCities(cityC1, cityA, 10);
            this.cityOperations.connectCities(cityA, cityC2, 3);
            this.cityOperations.connectCities(cityC2, cityC3, 2);
            this.cityOperations.connectCities(cityC3, cityC4, 1);
            this.cityOperations.connectCities(cityC4, cityA, 3);
            this.cityOperations.connectCities(cityA, cityC5, 15);
            this.cityOperations.connectCities(cityC5, cityB, 2);
            final int shopA = this.shopOperations.createShop("shopA", "A");
            final int shopC2 = this.shopOperations.createShop("shopC2", "C2");
            final int shopC3 = this.shopOperations.createShop("shopC3", "C3");
            this.shopOperations.setDiscount(shopA, 20);
            this.shopOperations.setDiscount(shopC2, 50);
            final int laptop = this.articleOperations.createArticle(shopA, "laptop", 1000);
            final int monitor = this.articleOperations.createArticle(shopC2, "monitor", 200);
            final int stolica = this.articleOperations.createArticle(shopC3, "stolica", 100);
            final int sto = this.articleOperations.createArticle(shopC3, "sto", 200);
            this.shopOperations.increaseArticleCount(laptop, 10);
            this.shopOperations.increaseArticleCount(monitor, 10);
            this.shopOperations.increaseArticleCount(stolica, 10);
            this.shopOperations.increaseArticleCount(sto, 10);
            final int buyer = this.buyerOperations.createBuyer("kupac", cityB);
            this.buyerOperations.increaseCredit(buyer, new BigDecimal("20000"));
            final int order = this.buyerOperations.createOrder(buyer);
            this.orderOperations.addArticle(order, laptop, 5);
            this.orderOperations.addArticle(order, monitor, 4);
            this.orderOperations.addArticle(order, stolica, 10);
            this.orderOperations.addArticle(order, sto, 4);
            Assert.assertNull((Object)this.orderOperations.getSentTime(order));
            Assert.assertTrue("created".equals(this.orderOperations.getState(order)));

            this.orderOperations.completeOrder(order);
            Assert.assertTrue("sent".equals(this.orderOperations.getState(order)));
            final int buyerTransactionId = this.transactionOperations.getTransationsForBuyer(buyer).get(0);
            Assert.assertEquals((Object)initialTime, (Object)this.transactionOperations.getTimeOfExecution(buyerTransactionId));
            Assert.assertNull((Object)this.transactionOperations.getTransationsForShop(shopA));
            final BigDecimal shopAAmount = new BigDecimal("5").multiply(new BigDecimal("1000")).setScale(3);
            final BigDecimal shopAAmountWithDiscount = new BigDecimal("0.8").multiply(shopAAmount).setScale(3);
            final BigDecimal shopC2Amount = new BigDecimal("4").multiply(new BigDecimal("200")).setScale(3);
            final BigDecimal shopC2AmountWithDiscount = new BigDecimal("0.5").multiply(shopC2Amount).setScale(3);
            final BigDecimal shopC3AmountWithDiscount;
            final BigDecimal shopC3Amount = shopC3AmountWithDiscount = new BigDecimal("10").multiply(new BigDecimal("100")).add(new BigDecimal("4").multiply(new BigDecimal("200"))).setScale(3);
            final BigDecimal amountWithoutDiscounts = shopAAmount.add(shopC2Amount).add(shopC3Amount).setScale(3);
            final BigDecimal amountWithDiscounts = shopAAmountWithDiscount.add(shopC2AmountWithDiscount).add(shopC3AmountWithDiscount).setScale(3);
            final BigDecimal systemProfit = amountWithDiscounts.multiply(new BigDecimal("0.05")).setScale(3);
            final BigDecimal shopAAmountReal = shopAAmountWithDiscount.multiply(new BigDecimal("0.95")).setScale(3);
            final BigDecimal shopC2AmountReal = shopC2AmountWithDiscount.multiply(new BigDecimal("0.95")).setScale(3);
            final BigDecimal shopC3AmountReal = shopC3AmountWithDiscount.multiply(new BigDecimal("0.95")).setScale(3);
            
            Assert.assertEquals((Object)amountWithDiscounts, (Object)this.orderOperations.getFinalPrice(order));
            Assert.assertEquals((Object)amountWithoutDiscounts.subtract(amountWithDiscounts), (Object)this.orderOperations.getDiscountSum(order));
            Assert.assertEquals((Object)amountWithDiscounts, (Object)this.transactionOperations.getBuyerTransactionsAmmount(buyer));
            Assert.assertEquals((Object)this.transactionOperations.getShopTransactionsAmmount(shopA), (Object)new BigDecimal("0").setScale(3));
            Assert.assertEquals((Object)this.transactionOperations.getShopTransactionsAmmount(shopC2), (Object)new BigDecimal("0").setScale(3));
            Assert.assertEquals((Object)this.transactionOperations.getShopTransactionsAmmount(shopC3), (Object)new BigDecimal("0").setScale(3));
         
            Assert.assertEquals((Object)new BigDecimal("0").setScale(3), (Object)this.transactionOperations.getSystemProfit());
            this.generalOperations.time(2);
            Assert.assertEquals((Object)initialTime, (Object)this.orderOperations.getSentTime(order));
            Assert.assertNull((Object)this.orderOperations.getRecievedTime(order));
            
            Assert.assertEquals((long)this.orderOperations.getLocation(order), (long)cityA);
            
            this.generalOperations.time(9);
            Assert.assertEquals((long)this.orderOperations.getLocation(order), (long)cityA);
            this.generalOperations.time(8);
            Assert.assertEquals((long)this.orderOperations.getLocation(order), (long)cityC5);
            this.generalOperations.time(5);
            Assert.assertEquals((long)this.orderOperations.getLocation(order), (long)cityB);
          
            Assert.assertEquals((Object)receivedTime, (Object)this.orderOperations.getRecievedTime(order));
          
            Assert.assertEquals((Object)shopAAmountReal, (Object)this.transactionOperations.getShopTransactionsAmmount(shopA));
           
            Assert.assertEquals((Object)shopC2AmountReal, (Object)this.transactionOperations.getShopTransactionsAmmount(shopC2));
          
            Assert.assertEquals((Object)shopC3AmountReal, (Object)this.transactionOperations.getShopTransactionsAmmount(shopC3));
          
            Assert.assertEquals((Object)systemProfit, (Object)this.transactionOperations.getSystemProfit());
         
            final int shopATransactionId = this.transactionOperations.getTransactionForShopAndOrder(order, shopA);
            
            Assert.assertNotEquals(-1L, (long)shopATransactionId);
            Assert.assertEquals((Object)receivedTime, (Object)this.transactionOperations.getTimeOfExecution(shopATransactionId));
        
        }
        
        // kada postoji popust od 2 posto
        public  void myTest1WithAdditionalDiscount() {
            generalOperations.eraseAll();

            final Calendar initialTime = Calendar.getInstance();
            initialTime.clear();
            initialTime.set(2023, 6, 8);
            this.generalOperations.setInitialTime(initialTime);
            final Calendar receivedTime = Calendar.getInstance();
            receivedTime.clear();
            receivedTime.set(2023, 6, 29);
            int cityB = this.cityOperations.createCity("B");
            int cityC1 = this.cityOperations.createCity("C1");
            int cityA = this.cityOperations.createCity("A");
            int cityC2 = this.cityOperations.createCity("C2");
            int cityC3 = this.cityOperations.createCity("C3");
            int cityC4 = this.cityOperations.createCity("C4");
            int cityC5 = this.cityOperations.createCity("C5");
            int cityC6 = this.cityOperations.createCity("C6");
            this.cityOperations.connectCities(cityB, cityC1, 8);
            this.cityOperations.connectCities(cityC1, cityA, 10);
            this.cityOperations.connectCities(cityA, cityC2, 3);
            this.cityOperations.connectCities(cityC2, cityC3, 2);
            this.cityOperations.connectCities(cityC3, cityC4, 1);
            this.cityOperations.connectCities(cityC4, cityA, 3);
            this.cityOperations.connectCities(cityA, cityC5, 15);
            this.cityOperations.connectCities(cityC5, cityB, 2);
            this.cityOperations.connectCities(cityC6, cityB, 5);
            final int shopA = this.shopOperations.createShop("shopA", "A");
            final int shopC2 = this.shopOperations.createShop("shopC2", "C2");
            final int shopC3 = this.shopOperations.createShop("shopC3", "C3");
            final int shopC5 = this.shopOperations.createShop("shopC5", "C5");
            this.shopOperations.setDiscount(shopA, 20);
            this.shopOperations.setDiscount(shopC2, 50);
            final int laptop = this.articleOperations.createArticle(shopA, "laptop", 1000);
            final int monitor = this.articleOperations.createArticle(shopC2, "monitor", 200);
            final int stolica = this.articleOperations.createArticle(shopC3, "stolica", 100);
            final int sto = this.articleOperations.createArticle(shopC3, "sto", 200);
            this.shopOperations.increaseArticleCount(laptop, 10);
            this.shopOperations.increaseArticleCount(monitor, 10);
            this.shopOperations.increaseArticleCount(stolica, 10);
            this.shopOperations.increaseArticleCount(sto, 10);
            final int buyer = this.buyerOperations.createBuyer("kupac", cityB);
            this.buyerOperations.increaseCredit(buyer, new BigDecimal("20000"));
            final int order = this.buyerOperations.createOrder(buyer);
            this.orderOperations.addArticle(order, laptop, 10);
            this.orderOperations.addArticle(order, monitor, 4);
            this.orderOperations.addArticle(order, stolica, 10);
            this.orderOperations.addArticle(order, sto, 4);
            Assert.assertNull((Object)this.orderOperations.getSentTime(order));
            Assert.assertTrue("created".equals(this.orderOperations.getState(order)));
            
            this.orderOperations.completeOrder(order);
            
            Assert.assertTrue("sent".equals(this.orderOperations.getState(order)));
            final int buyerTransactionId = this.transactionOperations.getTransationsForBuyer(buyer).get(0);
            Assert.assertEquals((Object)initialTime, (Object)this.transactionOperations.getTimeOfExecution(buyerTransactionId));
            Assert.assertNull((Object)this.transactionOperations.getTransationsForShop(shopA));
            final BigDecimal shopAAmount = new BigDecimal("10").multiply(new BigDecimal("1000")).setScale(3);
            final BigDecimal shopAAmountWithDiscount = new BigDecimal("0.8").multiply(shopAAmount).setScale(3);
            final BigDecimal shopC2Amount = new BigDecimal("4").multiply(new BigDecimal("200")).setScale(3);
            final BigDecimal shopC2AmountWithDiscount = new BigDecimal("0.5").multiply(shopC2Amount).setScale(3);
            final BigDecimal shopC3AmountWithDiscount;
            final BigDecimal shopC3Amount = shopC3AmountWithDiscount = new BigDecimal("10").multiply(new BigDecimal("100")).add(new BigDecimal("4").multiply(new BigDecimal("200"))).setScale(3);
            final BigDecimal amountWithoutDiscounts = shopAAmount.add(shopC2Amount).add(shopC3Amount).setScale(3);
            final BigDecimal amountWithDiscounts = shopAAmountWithDiscount.add(shopC2AmountWithDiscount).add(shopC3AmountWithDiscount).setScale(3);
            final BigDecimal systemProfit = amountWithDiscounts.multiply(new BigDecimal("0.05")).setScale(3);
            final BigDecimal shopAAmountReal = shopAAmountWithDiscount.multiply(new BigDecimal("0.95")).setScale(3);
            final BigDecimal shopC2AmountReal = shopC2AmountWithDiscount.multiply(new BigDecimal("0.95")).setScale(3);
            final BigDecimal shopC3AmountReal = shopC3AmountWithDiscount.multiply(new BigDecimal("0.95")).setScale(3);
//            
            Assert.assertEquals((Object)amountWithDiscounts, (Object)this.orderOperations.getFinalPrice(order));
            Assert.assertEquals((Object)amountWithoutDiscounts.subtract(amountWithDiscounts), (Object)this.orderOperations.getDiscountSum(order));
            Assert.assertEquals((Object)amountWithDiscounts, (Object)this.transactionOperations.getBuyerTransactionsAmmount(buyer));
            Assert.assertEquals((Object)this.transactionOperations.getShopTransactionsAmmount(shopA), (Object)new BigDecimal("0").setScale(3));
            Assert.assertEquals((Object)this.transactionOperations.getShopTransactionsAmmount(shopC2), (Object)new BigDecimal("0").setScale(3));
            Assert.assertEquals((Object)this.transactionOperations.getShopTransactionsAmmount(shopC3), (Object)new BigDecimal("0").setScale(3));
//         
            Assert.assertEquals((Object)new BigDecimal("0").setScale(3), (Object)this.transactionOperations.getSystemProfit());
            this.generalOperations.time(2);
            Assert.assertEquals((Object)initialTime, (Object)this.orderOperations.getSentTime(order));
            Assert.assertNull((Object)this.orderOperations.getRecievedTime(order));
//            
            Assert.assertEquals((long)this.orderOperations.getLocation(order), (long)cityC5);
//            
            this.generalOperations.time(9);
            Assert.assertEquals((long)this.orderOperations.getLocation(order), (long)cityC5);
            this.generalOperations.time(8);
            Assert.assertEquals((long)this.orderOperations.getLocation(order), (long)cityC5);
            this.generalOperations.time(5);
            Assert.assertEquals((long)this.orderOperations.getLocation(order), (long)cityB);
          
            Assert.assertEquals((Object)receivedTime, (Object)this.orderOperations.getRecievedTime(order));
            Assert.assertEquals((Object)shopAAmountReal, (Object)this.transactionOperations.getShopTransactionsAmmount(shopA));
            Assert.assertEquals((Object)shopC2AmountReal, (Object)this.transactionOperations.getShopTransactionsAmmount(shopC2));
            Assert.assertEquals((Object)shopC3AmountReal, (Object)this.transactionOperations.getShopTransactionsAmmount(shopC3));
               System.out.println(systemProfit);
                 System.out.println(this.transactionOperations.getSystemProfit());
            Assert.assertEquals((Object)systemProfit, (Object)this.transactionOperations.getSystemProfit());
         
            final int shopATransactionId = this.transactionOperations.getTransactionForShopAndOrder(order, shopA);
            Assert.assertNotEquals(-1L, (long)shopATransactionId);
            Assert.assertEquals((Object)receivedTime, (Object)this.transactionOperations.getTimeOfExecution(shopATransactionId));
            
            
            //dodavanje nove porudzbine za istog kupca tako da se ostvari dodatnih 2% popusta jer je prethodna porudzbina bila preko 10 000
           
            final Calendar initialTime2 = Calendar.getInstance();
            initialTime2.setTimeInMillis(generalOperations.getCurrentTime().getTimeInMillis());
             
            final int order2 = this.buyerOperations.createOrder(buyer);
            this.orderOperations.addArticle(order2, monitor, 4);
            this.orderOperations.addArticle(order2, sto, 4);
            Assert.assertNull((Object)this.orderOperations.getSentTime(order2));
            Assert.assertTrue("created".equals(this.orderOperations.getState(order2)));
            
            this.orderOperations.completeOrder(order2);
            
            Assert.assertTrue("sent".equals(this.orderOperations.getState(order2)));
            final int buyerTransactionId2 = this.transactionOperations.getTransationsForBuyer(buyer).get(1);//sad ima 2 transakcije
            Assert.assertEquals((Object)initialTime2, (Object)this.transactionOperations.getTimeOfExecution(buyerTransactionId2));
            
            //Assert.assertNull((Object)this.transactionOperations.getTransationsForShop(shopA));
            final BigDecimal shopC3Amount2 = new BigDecimal("4").multiply(new BigDecimal("200")).setScale(3);
            final BigDecimal shopC3AmountWithDiscount2 = shopC3Amount2.multiply(new BigDecimal("0.98")).setScale(3);
            final BigDecimal shopC2Amount2 = new BigDecimal("4").multiply(new BigDecimal("200")).setScale(3);
            final BigDecimal shopC2AmountWithDiscount2 = new BigDecimal("0.5").multiply(shopC2Amount2).multiply(new BigDecimal("0.98")).setScale(3);
            final BigDecimal amountWithoutDiscounts2 = shopC3Amount2.add(shopC2Amount2).setScale(3);
            
            final BigDecimal amountWithDiscounts2= shopC3AmountWithDiscount2.add(shopC2AmountWithDiscount2).setScale(3);
            final BigDecimal systemProfit2 =new BigDecimal("546.000").setScale(3);
            final BigDecimal shopC3AmountReal2 = shopC3Amount2.multiply(new BigDecimal("0.95")).setScale(3);
            final BigDecimal shopC2AmountReal2 = shopC2Amount2.multiply(new BigDecimal("0.5")).multiply(new BigDecimal("0.95")).setScale(3);
            
            Assert.assertEquals((Object)amountWithDiscounts2, (Object)this.orderOperations.getFinalPrice(order2));
            Assert.assertEquals((Object)amountWithoutDiscounts2.subtract(amountWithDiscounts2), (Object)this.orderOperations.getDiscountSum(order2));
            Assert.assertEquals((Object)(amountWithDiscounts2.add(amountWithDiscounts)), (Object)this.transactionOperations.getBuyerTransactionsAmmount(buyer));
            Assert.assertEquals((Object)this.transactionOperations.getShopTransactionsAmmount(shopC3), (Object)new BigDecimal("1710").setScale(3));
            Assert.assertEquals((Object)this.transactionOperations.getShopTransactionsAmmount(shopC2), (Object)new BigDecimal("380").setScale(3));

            Assert.assertEquals((Object)new BigDecimal("510").setScale(3), (Object)this.transactionOperations.getSystemProfit());
            this.generalOperations.time(2);
            Assert.assertEquals((Object)initialTime2, (Object)this.orderOperations.getSentTime(order2));
            Assert.assertNull((Object)this.orderOperations.getRecievedTime(order2));
//            
            Assert.assertEquals((long)this.orderOperations.getLocation(order2), (long)cityC5);
//            
            this.generalOperations.time(9);
            Assert.assertEquals((long)this.orderOperations.getLocation(order2), (long)cityC5);
            this.generalOperations.time(8);
            Assert.assertEquals((long)this.orderOperations.getLocation(order2), (long)cityC5);
            this.generalOperations.time(5);
            Assert.assertEquals((long)this.orderOperations.getLocation(order2), (long)cityB);
            
            final Calendar receivedTime2 = Calendar.getInstance();
            receivedTime2.clear();
            receivedTime2.set(2023, 7, 22);
    
            Assert.assertEquals((Object)receivedTime2, (Object)this.orderOperations.getRecievedTime(order2));
            Assert.assertEquals((Object)(shopC3AmountReal.add(shopC3AmountReal2)), (Object)this.transactionOperations.getShopTransactionsAmmount(shopC3));
            Assert.assertEquals((Object)(shopC2AmountReal.add(shopC2AmountReal2)), (Object)this.transactionOperations.getShopTransactionsAmmount(shopC2));
            Assert.assertEquals((Object)(systemProfit2), (Object)this.transactionOperations.getSystemProfit());
            final int shopATransactionId2 = this.transactionOperations.getTransactionForShopAndOrder(order2, shopC3);
            Assert.assertNotEquals(-1L, (long)shopATransactionId2);
            Assert.assertEquals((Object)receivedTime2, (Object)this.transactionOperations.getTimeOfExecution(shopATransactionId2));

        }
        
        // kada ne postoji popust od 2 posto
        public  void myTest2WithoutAdditionalDiscount() {
            generalOperations.eraseAll(); //brise sve iz tabela

            final Calendar initialTime = Calendar.getInstance();
            initialTime.clear();
            initialTime.set(2023, 6, 8);
            this.generalOperations.setInitialTime(initialTime);
            final Calendar receivedTime = Calendar.getInstance();
            receivedTime.clear();
            receivedTime.set(2023, 6, 29);
            int cityB = this.cityOperations.createCity("B");
            int cityC1 = this.cityOperations.createCity("C1");
            int cityA = this.cityOperations.createCity("A");
            int cityC2 = this.cityOperations.createCity("C2");
            int cityC3 = this.cityOperations.createCity("C3");
            int cityC4 = this.cityOperations.createCity("C4");
            int cityC5 = this.cityOperations.createCity("C5");
            int cityC6 = this.cityOperations.createCity("C6");
            this.cityOperations.connectCities(cityB, cityC1, 8);
            this.cityOperations.connectCities(cityC1, cityA, 10);
            this.cityOperations.connectCities(cityA, cityC2, 3);
            this.cityOperations.connectCities(cityC2, cityC3, 2);
            this.cityOperations.connectCities(cityC3, cityC4, 1);
            this.cityOperations.connectCities(cityC4, cityA, 3);
            this.cityOperations.connectCities(cityA, cityC5, 15);
            this.cityOperations.connectCities(cityC5, cityB, 2);
            this.cityOperations.connectCities(cityC6, cityB, 5); 
            final int shopA = this.shopOperations.createShop("shopA", "A");
            final int shopC2 = this.shopOperations.createShop("shopC2", "C2");
            final int shopC3 = this.shopOperations.createShop("shopC3", "C3");
            final int shopC5 = this.shopOperations.createShop("shopC5", "C5"); //ovo je grad u kome ce se sklapati porudzbina
            this.shopOperations.setDiscount(shopA, 20);
            this.shopOperations.setDiscount(shopC2, 50);
            final int laptop = this.articleOperations.createArticle(shopA, "laptop", 1000);
            final int monitor = this.articleOperations.createArticle(shopC2, "monitor", 200);
            final int stolica = this.articleOperations.createArticle(shopC3, "stolica", 100);
            final int sto = this.articleOperations.createArticle(shopC3, "sto", 200);
            this.shopOperations.increaseArticleCount(laptop, 10);
            this.shopOperations.increaseArticleCount(monitor, 10);
            this.shopOperations.increaseArticleCount(stolica, 10);
            this.shopOperations.increaseArticleCount(sto, 10);
            final int buyer = this.buyerOperations.createBuyer("kupac", cityB);
            this.buyerOperations.increaseCredit(buyer, new BigDecimal("20000"));
            final int order = this.buyerOperations.createOrder(buyer);
            this.orderOperations.addArticle(order, laptop, 5);
            this.orderOperations.addArticle(order, monitor, 4);
            this.orderOperations.addArticle(order, stolica, 10);
            this.orderOperations.addArticle(order, sto, 4);
            Assert.assertNull((Object)this.orderOperations.getSentTime(order));
            Assert.assertTrue("created".equals(this.orderOperations.getState(order)));
            
            this.orderOperations.completeOrder(order);
            
            Assert.assertTrue("sent".equals(this.orderOperations.getState(order)));
            final int buyerTransactionId = this.transactionOperations.getTransationsForBuyer(buyer).get(0);
            Assert.assertEquals((Object)initialTime, (Object)this.transactionOperations.getTimeOfExecution(buyerTransactionId));
            Assert.assertNull((Object)this.transactionOperations.getTransationsForShop(shopA));
            final BigDecimal shopAAmount = new BigDecimal("5").multiply(new BigDecimal("1000")).setScale(3);
            final BigDecimal shopAAmountWithDiscount = new BigDecimal("0.8").multiply(shopAAmount).setScale(3);
            final BigDecimal shopC2Amount = new BigDecimal("4").multiply(new BigDecimal("200")).setScale(3);
            final BigDecimal shopC2AmountWithDiscount = new BigDecimal("0.5").multiply(shopC2Amount).setScale(3);
            final BigDecimal shopC3AmountWithDiscount;
            final BigDecimal shopC3Amount = shopC3AmountWithDiscount = new BigDecimal("10").multiply(new BigDecimal("100")).add(new BigDecimal("4").multiply(new BigDecimal("200"))).setScale(3);
            final BigDecimal amountWithoutDiscounts = shopAAmount.add(shopC2Amount).add(shopC3Amount).setScale(3);
            final BigDecimal amountWithDiscounts = shopAAmountWithDiscount.add(shopC2AmountWithDiscount).add(shopC3AmountWithDiscount).setScale(3);
            final BigDecimal systemProfit = amountWithDiscounts.multiply(new BigDecimal("0.05")).setScale(3);
            final BigDecimal shopAAmountReal = shopAAmountWithDiscount.multiply(new BigDecimal("0.95")).setScale(3);
            final BigDecimal shopC2AmountReal = shopC2AmountWithDiscount.multiply(new BigDecimal("0.95")).setScale(3);
            final BigDecimal shopC3AmountReal = shopC3AmountWithDiscount.multiply(new BigDecimal("0.95")).setScale(3);
//            
            Assert.assertEquals((Object)amountWithDiscounts, (Object)this.orderOperations.getFinalPrice(order));
            Assert.assertEquals((Object)amountWithoutDiscounts.subtract(amountWithDiscounts), (Object)this.orderOperations.getDiscountSum(order));
            Assert.assertEquals((Object)amountWithDiscounts, (Object)this.transactionOperations.getBuyerTransactionsAmmount(buyer));
            Assert.assertEquals((Object)this.transactionOperations.getShopTransactionsAmmount(shopA), (Object)new BigDecimal("0").setScale(3));
            Assert.assertEquals((Object)this.transactionOperations.getShopTransactionsAmmount(shopC2), (Object)new BigDecimal("0").setScale(3));
            Assert.assertEquals((Object)this.transactionOperations.getShopTransactionsAmmount(shopC3), (Object)new BigDecimal("0").setScale(3));
//         
            Assert.assertEquals((Object)new BigDecimal("0").setScale(3), (Object)this.transactionOperations.getSystemProfit());
            this.generalOperations.time(2);
            Assert.assertEquals((Object)initialTime, (Object)this.orderOperations.getSentTime(order));
            Assert.assertNull((Object)this.orderOperations.getRecievedTime(order));
//            
            Assert.assertEquals((long)this.orderOperations.getLocation(order), (long)cityC5);
//            
            this.generalOperations.time(9);
            Assert.assertEquals((long)this.orderOperations.getLocation(order), (long)cityC5);
            this.generalOperations.time(8);
            Assert.assertEquals((long)this.orderOperations.getLocation(order), (long)cityC5);
            this.generalOperations.time(5);
            Assert.assertEquals((long)this.orderOperations.getLocation(order), (long)cityB);
          
            Assert.assertEquals((Object)receivedTime, (Object)this.orderOperations.getRecievedTime(order));
            Assert.assertEquals((Object)shopAAmountReal, (Object)this.transactionOperations.getShopTransactionsAmmount(shopA));
            Assert.assertEquals((Object)shopC2AmountReal, (Object)this.transactionOperations.getShopTransactionsAmmount(shopC2));
            Assert.assertEquals((Object)shopC3AmountReal, (Object)this.transactionOperations.getShopTransactionsAmmount(shopC3));
            Assert.assertEquals((Object)systemProfit, (Object)this.transactionOperations.getSystemProfit());
            final int shopATransactionId = this.transactionOperations.getTransactionForShopAndOrder(order, shopA);
            Assert.assertNotEquals(-1L, (long)shopATransactionId);
            Assert.assertEquals((Object)receivedTime, (Object)this.transactionOperations.getTimeOfExecution(shopATransactionId));
            
            
            //dodavanje nove porudzbine za istog kupca tako da NE ostvari dodatnih 2% popusta jer je prethodna kupovina bila manja od 10.000
            
            final Calendar initialTime2 = Calendar.getInstance();
            initialTime2.setTimeInMillis(generalOperations.getCurrentTime().getTimeInMillis());
             
            final int order2 = this.buyerOperations.createOrder(buyer);
            this.orderOperations.addArticle(order2, laptop, 5);
            this.orderOperations.addArticle(order2, monitor, 4);
            Assert.assertNull((Object)this.orderOperations.getSentTime(order2));
            Assert.assertTrue("created".equals(this.orderOperations.getState(order2)));
            
            this.orderOperations.completeOrder(order2);
            
            Assert.assertTrue("sent".equals(this.orderOperations.getState(order2)));
            final int buyerTransactionId2 = this.transactionOperations.getTransationsForBuyer(buyer).get(1);//sad ima 2 transakcije
            Assert.assertEquals((Object)initialTime2, (Object)this.transactionOperations.getTimeOfExecution(buyerTransactionId2));
            
            //Assert.assertNull((Object)this.transactionOperations.getTransationsForShop(shopA));
            final BigDecimal shopAAmount2 = new BigDecimal("5").multiply(new BigDecimal("1000")).setScale(3);
            final BigDecimal shopAAmountWithDiscount2 = new BigDecimal("0.8").multiply(shopAAmount2).setScale(3);
            final BigDecimal shopC2Amount2 = new BigDecimal("4").multiply(new BigDecimal("200")).setScale(3);
            final BigDecimal shopC2AmountWithDiscount2 = new BigDecimal("0.5").multiply(shopC2Amount2).setScale(3);
            final BigDecimal amountWithoutDiscounts2 = shopAAmount2.add(shopC2Amount2).setScale(3);
            
            final BigDecimal amountWithDiscounts2= shopAAmountWithDiscount2.add(shopC2AmountWithDiscount2).setScale(3);
            final BigDecimal systemProfit2 = amountWithDiscounts2.multiply(new BigDecimal("0.05")).setScale(3);
            final BigDecimal shopAAmountReal2 = shopAAmountWithDiscount2.multiply(new BigDecimal("0.95")).setScale(3);
            final BigDecimal shopC2AmountReal2 = shopC2AmountWithDiscount2.multiply(new BigDecimal("0.95")).setScale(3);
            
            Assert.assertEquals((Object)amountWithDiscounts2, (Object)this.orderOperations.getFinalPrice(order2));
            Assert.assertEquals((Object)amountWithoutDiscounts2.subtract(amountWithDiscounts2), (Object)this.orderOperations.getDiscountSum(order2));
            Assert.assertEquals((Object)(amountWithDiscounts2.add(amountWithDiscounts)), (Object)this.transactionOperations.getBuyerTransactionsAmmount(buyer));
            Assert.assertEquals((Object)this.transactionOperations.getShopTransactionsAmmount(shopA), (Object)new BigDecimal("3800").setScale(3));
            Assert.assertEquals((Object)this.transactionOperations.getShopTransactionsAmmount(shopC2), (Object)new BigDecimal("380").setScale(3));

            Assert.assertEquals((Object)new BigDecimal("310").setScale(3), (Object)this.transactionOperations.getSystemProfit());
            this.generalOperations.time(2);
            Assert.assertEquals((Object)initialTime2, (Object)this.orderOperations.getSentTime(order2));
            Assert.assertNull((Object)this.orderOperations.getRecievedTime(order2));
//            
            Assert.assertEquals((long)this.orderOperations.getLocation(order2), (long)cityC5);
//            
            this.generalOperations.time(9);
            Assert.assertEquals((long)this.orderOperations.getLocation(order2), (long)cityC5);
            this.generalOperations.time(8);
            Assert.assertEquals((long)this.orderOperations.getLocation(order2), (long)cityC5);
            this.generalOperations.time(5);
            Assert.assertEquals((long)this.orderOperations.getLocation(order2), (long)cityB);
            
            final Calendar receivedTime2 = Calendar.getInstance();
            receivedTime2.clear();
            receivedTime2.set(2023, 7, 21);
    
            Assert.assertEquals((Object)receivedTime2, (Object)this.orderOperations.getRecievedTime(order2));
            Assert.assertEquals((Object)(shopAAmountReal2.add(shopAAmountReal)), (Object)this.transactionOperations.getShopTransactionsAmmount(shopA));
            Assert.assertEquals((Object)(shopC2AmountReal.add(shopC2AmountReal2)), (Object)this.transactionOperations.getShopTransactionsAmmount(shopC2));
            Assert.assertEquals((Object)(systemProfit.add(systemProfit2)), (Object)this.transactionOperations.getSystemProfit());
            final int shopATransactionId2 = this.transactionOperations.getTransactionForShopAndOrder(order2, shopA);
            Assert.assertNotEquals(-1L, (long)shopATransactionId2);
            Assert.assertEquals((Object)receivedTime2, (Object)this.transactionOperations.getTimeOfExecution(shopATransactionId2));

        }
        
        //narucuje se iz shop-a C1 i C2, kupac u gradu B, u gradu B postoji prodavnica znaci porudzbina se sklapa u gradu B
        public  void myTest3() {
              
            generalOperations.eraseAll();
            final Calendar initialTime = Calendar.getInstance();
            initialTime.clear();
            initialTime.set(2018, 0, 1);
            this.generalOperations.setInitialTime(initialTime);
            final Calendar receivedTime = Calendar.getInstance();
            receivedTime.clear();
            receivedTime.set(2018, 0, 21);
            int cityB = this.cityOperations.createCity("B");
            int cityC1 = this.cityOperations.createCity("C1");
            int cityA = this.cityOperations.createCity("A");
            int cityC2 = this.cityOperations.createCity("C2");
            int cityC3 = this.cityOperations.createCity("C3");
            int cityC4 = this.cityOperations.createCity("C4");
            int cityC5 = this.cityOperations.createCity("C5");
            this.cityOperations.connectCities(cityB, cityC1, 8);
            this.cityOperations.connectCities(cityC1, cityA, 10);
            this.cityOperations.connectCities(cityA, cityC2, 3);
            this.cityOperations.connectCities(cityC2, cityC3, 2);
            this.cityOperations.connectCities(cityC3, cityC4, 1);
            this.cityOperations.connectCities(cityC4, cityA, 3);
            this.cityOperations.connectCities(cityA, cityC5, 15);
            this.cityOperations.connectCities(cityC5, cityB, 2);
            final int shopA = this.shopOperations.createShop("shopA", "A");
            final int shopC2 = this.shopOperations.createShop("shopC2", "C2");
            final int shopC3 = this.shopOperations.createShop("shopC3", "C3");
            final int shopC1 = this.shopOperations.createShop("shopC1", "C1");
            final int shopC5 = this.shopOperations.createShop("shopC5", "C5");
            final int shopB = this.shopOperations.createShop("shopB", "B"); //i grad B ima prodavnicu
            this.shopOperations.setDiscount(shopA, 20);
            this.shopOperations.setDiscount(shopC2, 50);
            this.shopOperations.setDiscount(shopC1, 20);
            final int laptop = this.articleOperations.createArticle(shopA, "laptop", 1000);
            final int monitor = this.articleOperations.createArticle(shopC2, "monitor", 200);
            final int stolica = this.articleOperations.createArticle(shopC3, "stolica", 100);
            final int sto = this.articleOperations.createArticle(shopC1, "sto", 200);
            this.shopOperations.increaseArticleCount(laptop, 10);
            this.shopOperations.increaseArticleCount(monitor, 10);
            this.shopOperations.increaseArticleCount(stolica, 10);
            this.shopOperations.increaseArticleCount(sto, 10);
            
            final int buyer = this.buyerOperations.createBuyer("kupac", cityB);
            this.buyerOperations.increaseCredit(buyer, new BigDecimal("20000"));
            final int order = this.buyerOperations.createOrder(buyer);
            
            this.orderOperations.addArticle(order, sto, 4);//C1
             this.orderOperations.addArticle(order, monitor, 1);//C2

            Assert.assertNull((Object)this.orderOperations.getSentTime(order));
            Assert.assertTrue("created".equals(this.orderOperations.getState(order)));
            
            
            this.orderOperations.completeOrder(order);
            
            
            Assert.assertTrue("sent".equals(this.orderOperations.getState(order)));
            final int buyerTransactionId = this.transactionOperations.getTransationsForBuyer(buyer).get(0);
            Assert.assertEquals((Object)initialTime, (Object)this.transactionOperations.getTimeOfExecution(buyerTransactionId));
            Assert.assertNull((Object)this.transactionOperations.getTransationsForShop(shopA));
         
            final BigDecimal shopC2Amount = new BigDecimal("1").multiply(new BigDecimal("200")).setScale(3);
            final BigDecimal shopC2AmountWithDiscount = new BigDecimal("0.5").multiply(shopC2Amount).setScale(3);
            
            final BigDecimal shopC1Amount = new BigDecimal("4").multiply(new BigDecimal("200")).setScale(3);
            final BigDecimal shopC1AmountWithDiscount =new BigDecimal("0.8").multiply(shopC1Amount).setScale(3);
            final BigDecimal amountWithoutDiscounts = shopC1Amount.add(shopC2Amount).setScale(3);
            final BigDecimal amountWithDiscounts = shopC1AmountWithDiscount.add(shopC2AmountWithDiscount).setScale(3);
            final BigDecimal systemProfit = amountWithDiscounts.multiply(new BigDecimal("0.05")).setScale(3);

            final BigDecimal shopC2AmountReal = shopC2AmountWithDiscount.multiply(new BigDecimal("0.95")).setScale(3);
            final BigDecimal shopC1AmountReal = shopC1AmountWithDiscount.multiply(new BigDecimal("0.95")).setScale(3);
            
            Assert.assertEquals((Object)amountWithDiscounts, (Object)this.orderOperations.getFinalPrice(order));
            Assert.assertEquals((Object)amountWithoutDiscounts.subtract(amountWithDiscounts), (Object)this.orderOperations.getDiscountSum(order));
            Assert.assertEquals((Object)amountWithDiscounts, (Object)this.transactionOperations.getBuyerTransactionsAmmount(buyer));
            Assert.assertEquals((Object)this.transactionOperations.getShopTransactionsAmmount(shopC2), (Object)new BigDecimal("0").setScale(3));
            Assert.assertEquals((Object)this.transactionOperations.getShopTransactionsAmmount(shopC1), (Object)new BigDecimal("0").setScale(3));
         
            Assert.assertEquals((Object)new BigDecimal("0").setScale(3), (Object)this.transactionOperations.getSystemProfit());
            this.generalOperations.time(2);
            Assert.assertEquals((Object)initialTime, (Object)this.orderOperations.getSentTime(order));
            Assert.assertNull((Object)this.orderOperations.getRecievedTime(order));
            
            Assert.assertEquals((long)this.orderOperations.getLocation(order), (long)cityB);
            
            this.generalOperations.time(9);
            Assert.assertEquals((long)this.orderOperations.getLocation(order), (long)cityB);
            this.generalOperations.time(8);
            Assert.assertEquals((long)this.orderOperations.getLocation(order), (long)cityB);
            this.generalOperations.time(5);
            Assert.assertEquals((long)this.orderOperations.getLocation(order), (long)cityB);
          
            Assert.assertEquals((Object)receivedTime, (Object)this.orderOperations.getRecievedTime(order));
          
           
            Assert.assertEquals((Object)shopC2AmountReal, (Object)this.transactionOperations.getShopTransactionsAmmount(shopC2));
          
            Assert.assertEquals((Object)shopC1AmountReal, (Object)this.transactionOperations.getShopTransactionsAmmount(shopC1));
          
            Assert.assertEquals((Object)systemProfit, (Object)this.transactionOperations.getSystemProfit());
         
            final int shopC2TransactionId = this.transactionOperations.getTransactionForShopAndOrder(order, shopC2);
            
            Assert.assertNotEquals(-1L, (long)shopC2TransactionId);
            Assert.assertEquals((Object)receivedTime, (Object)this.transactionOperations.getTimeOfExecution(shopC2TransactionId));
     
      
        }
  
        //svi proizvodi koji se narucuju su iz grada kupca, dan slanja i prijema su isti 
        public  void myTest4() {
              
            generalOperations.eraseAll();
            final Calendar initialTime = Calendar.getInstance();
            initialTime.clear();
            initialTime.set(2018, 0, 1);
            this.generalOperations.setInitialTime(initialTime);
            final Calendar receivedTime = Calendar.getInstance();
            receivedTime.clear();
            receivedTime.set(2018, 0, 1);
            int cityB = this.cityOperations.createCity("B");
            int Beograd = this.cityOperations.createCity("Beograd");
            int cityA = this.cityOperations.createCity("A");
            int cityC2 = this.cityOperations.createCity("C2");
            int cityC3 = this.cityOperations.createCity("C3");
            int cityC4 = this.cityOperations.createCity("C4");
            int cityC5 = this.cityOperations.createCity("C5");
            this.cityOperations.connectCities(cityB, Beograd, 8);
            this.cityOperations.connectCities(Beograd, cityA, 10);
            this.cityOperations.connectCities(cityA, cityC2, 3);
            this.cityOperations.connectCities(cityC2, cityC3, 2);
            this.cityOperations.connectCities(cityC3, cityC4, 1);
            this.cityOperations.connectCities(cityC4, cityA, 3);
            this.cityOperations.connectCities(cityA, cityC5, 15);
            this.cityOperations.connectCities(cityC5, cityB, 2);
            final int shopA = this.shopOperations.createShop("shopA", "A");
            final int shopC2 = this.shopOperations.createShop("shopC2", "C2");
            final int shopC3 = this.shopOperations.createShop("shopC3", "C3");
            final int shopBeograd1 = this.shopOperations.createShop("shopBeograd1", "Beograd");
            final int shopBeograd2 = this.shopOperations.createShop("shopBeograd2", "Beograd");
            final int shopC5 = this.shopOperations.createShop("shopC5", "C5");
            final int shopB = this.shopOperations.createShop("shopB", "B"); //i grad B ima prodavnicu
            this.shopOperations.setDiscount(shopA, 20);
            this.shopOperations.setDiscount(shopC2, 50);
            this.shopOperations.setDiscount(shopBeograd2, 20);
            final int laptop = this.articleOperations.createArticle(shopBeograd1, "laptop", 1000);
            final int monitor = this.articleOperations.createArticle(shopBeograd1, "monitor", 200);
            final int stolica = this.articleOperations.createArticle(shopBeograd2, "stolica", 100);
            final int sto = this.articleOperations.createArticle(shopBeograd2, "sto", 200);
            this.shopOperations.increaseArticleCount(laptop, 10);
            this.shopOperations.increaseArticleCount(monitor, 10);
            this.shopOperations.increaseArticleCount(stolica, 10);
            this.shopOperations.increaseArticleCount(sto, 10);
            
            final int buyer = this.buyerOperations.createBuyer("kupac", Beograd);
            this.buyerOperations.increaseCredit(buyer, new BigDecimal("20000"));
            final int order = this.buyerOperations.createOrder(buyer);
            
            this.orderOperations.addArticle(order, laptop, 4);//shopBeograd1
            this.orderOperations.addArticle(order, monitor, 1);//shopBeograd1
            this.orderOperations.addArticle(order, stolica, 1);//shopBeograd2
            this.orderOperations.addArticle(order, sto, 4);//shopBeograd2
            

            Assert.assertNull((Object)this.orderOperations.getSentTime(order));
            Assert.assertTrue("created".equals(this.orderOperations.getState(order)));
            
            
            this.orderOperations.completeOrder(order);
            
            
            Assert.assertTrue("arrived".equals(this.orderOperations.getState(order)));
            final int buyerTransactionId = this.transactionOperations.getTransationsForBuyer(buyer).get(0);
            Assert.assertEquals((Object)initialTime, (Object)this.transactionOperations.getTimeOfExecution(buyerTransactionId));
            Assert.assertNull((Object)this.transactionOperations.getTransationsForShop(shopA));
         
            final BigDecimal shopBeograd1Amount = (new BigDecimal("4").multiply(new BigDecimal("1000")).add(new BigDecimal("200"))).setScale(3);
            final BigDecimal shopBeograd1AmountWithDiscount = shopBeograd1Amount.setScale(3);
            
             BigDecimal shopBeograd2Amount = new BigDecimal("1").multiply(new BigDecimal("100")).setScale(3);
            shopBeograd2Amount=shopBeograd2Amount.add( new BigDecimal("4").multiply(new BigDecimal("200"))).setScale(3);
            final BigDecimal shopBeograd2AmountWithDiscount =new BigDecimal("0.8").multiply(shopBeograd2Amount).setScale(3);
            
            System.out.println(shopBeograd1AmountWithDiscount);
            System.out.println(shopBeograd2AmountWithDiscount);
            
            final BigDecimal amountWithoutDiscounts = shopBeograd1Amount.add(shopBeograd2Amount).setScale(3);
            final BigDecimal amountWithDiscounts = shopBeograd2AmountWithDiscount.add(shopBeograd1AmountWithDiscount).setScale(3);
            final BigDecimal systemProfit = amountWithDiscounts.multiply(new BigDecimal("0.05")).setScale(3);

            final BigDecimal shopBeograd1AmountReal = shopBeograd1AmountWithDiscount.multiply(new BigDecimal("0.95")).setScale(3);
            final BigDecimal shopBeograd2AmountReal = shopBeograd2AmountWithDiscount.multiply(new BigDecimal("0.95")).setScale(3);
            
            Assert.assertEquals((Object)amountWithDiscounts, (Object)this.orderOperations.getFinalPrice(order));
            Assert.assertEquals((Object)amountWithoutDiscounts.subtract(amountWithDiscounts), (Object)this.orderOperations.getDiscountSum(order));
            Assert.assertEquals((Object)amountWithDiscounts, (Object)this.transactionOperations.getBuyerTransactionsAmmount(buyer));
            Assert.assertEquals((Object)this.transactionOperations.getShopTransactionsAmmount(shopBeograd1), (Object)shopBeograd1AmountReal);
            Assert.assertEquals((Object)this.transactionOperations.getShopTransactionsAmmount(shopBeograd2), (Object)shopBeograd2AmountReal);
////         
           Assert.assertEquals((Object)systemProfit, (Object)this.transactionOperations.getSystemProfit());
//           
            Assert.assertEquals((long)this.orderOperations.getLocation(order), (long)Beograd);

             this.generalOperations.time(2);
            Assert.assertEquals((Object)initialTime, (Object)this.orderOperations.getSentTime(order));
//            
            Assert.assertEquals((long)this.orderOperations.getLocation(order), (long)Beograd);

//   //          
            Assert.assertEquals((Object)receivedTime, (Object)this.orderOperations.getRecievedTime(order));
//          
//           
//     
//      
        }

        public  void myTest5() {
            generalOperations.eraseAll();

            final Calendar initialTime = Calendar.getInstance();
            initialTime.clear();
            initialTime.set(2023, 6, 8);
            this.generalOperations.setInitialTime(initialTime);
            final Calendar receivedTime = Calendar.getInstance();
            receivedTime.clear();
            receivedTime.set(2023, 6, 29);
            int cityB = this.cityOperations.createCity("B");
            int cityC1 = this.cityOperations.createCity("C1");
            int cityA = this.cityOperations.createCity("A");
            int cityC2 = this.cityOperations.createCity("C2");
            int cityC3 = this.cityOperations.createCity("C3");
            int cityC4 = this.cityOperations.createCity("C4");
            int cityC5 = this.cityOperations.createCity("C5");
            int cityC6 = this.cityOperations.createCity("C6");
            this.cityOperations.connectCities(cityB, cityC1, 8);
            this.cityOperations.connectCities(cityC1, cityA, 10);
            this.cityOperations.connectCities(cityA, cityC2, 3);
            this.cityOperations.connectCities(cityC2, cityC3, 2);
            this.cityOperations.connectCities(cityC3, cityC4, 1);
            this.cityOperations.connectCities(cityC4, cityA, 3);
            this.cityOperations.connectCities(cityA, cityC5, 15);
            this.cityOperations.connectCities(cityC5, cityB, 2);
            this.cityOperations.connectCities(cityC6, cityB, 5);
            final int shopA = this.shopOperations.createShop("shopA", "A");
            final int shopC2 = this.shopOperations.createShop("shopC2", "C2");
            final int shopC3 = this.shopOperations.createShop("shopC3", "C3");
          //  final int shopC5 = this.shopOperations.createShop("shopC5", "C5");
            this.shopOperations.setDiscount(shopA, 20);
            this.shopOperations.setDiscount(shopC2, 50);
            final int laptop = this.articleOperations.createArticle(shopA, "laptop", 1000);
            final int monitor = this.articleOperations.createArticle(shopC2, "monitor", 200);
            final int stolica = this.articleOperations.createArticle(shopC3, "stolica", 100);
            final int sto = this.articleOperations.createArticle(shopC3, "sto", 200);
            this.shopOperations.increaseArticleCount(laptop, 10);
            this.shopOperations.increaseArticleCount(monitor, 10);
            this.shopOperations.increaseArticleCount(stolica, 10);
            this.shopOperations.increaseArticleCount(sto, 10);
            final int buyer = this.buyerOperations.createBuyer("kupac", cityB);
            this.buyerOperations.increaseCredit(buyer, new BigDecimal("20000"));
            final int order = this.buyerOperations.createOrder(buyer);
            this.orderOperations.addArticle(order, laptop, 10);
            this.orderOperations.addArticle(order, monitor, 4);
            this.orderOperations.addArticle(order, stolica, 10);
            this.orderOperations.addArticle(order, sto, 4);
            Assert.assertNull((Object)this.orderOperations.getSentTime(order));
            Assert.assertTrue("created".equals(this.orderOperations.getState(order)));
            
            this.orderOperations.completeOrder(order);
            
            Assert.assertTrue("sent".equals(this.orderOperations.getState(order)));
            final int buyerTransactionId = this.transactionOperations.getTransationsForBuyer(buyer).get(0);
            Assert.assertEquals((Object)initialTime, (Object)this.transactionOperations.getTimeOfExecution(buyerTransactionId));
            Assert.assertNull((Object)this.transactionOperations.getTransationsForShop(shopA));
            final BigDecimal shopAAmount = new BigDecimal("10").multiply(new BigDecimal("1000")).setScale(3);
            final BigDecimal shopAAmountWithDiscount = new BigDecimal("0.8").multiply(shopAAmount).setScale(3);
            final BigDecimal shopC2Amount = new BigDecimal("4").multiply(new BigDecimal("200")).setScale(3);
            final BigDecimal shopC2AmountWithDiscount = new BigDecimal("0.5").multiply(shopC2Amount).setScale(3);
            final BigDecimal shopC3AmountWithDiscount;
            final BigDecimal shopC3Amount = shopC3AmountWithDiscount = new BigDecimal("10").multiply(new BigDecimal("100")).add(new BigDecimal("4").multiply(new BigDecimal("200"))).setScale(3);
            final BigDecimal amountWithoutDiscounts = shopAAmount.add(shopC2Amount).add(shopC3Amount).setScale(3);
            final BigDecimal amountWithDiscounts = shopAAmountWithDiscount.add(shopC2AmountWithDiscount).add(shopC3AmountWithDiscount).setScale(3);
            final BigDecimal systemProfit = amountWithDiscounts.multiply(new BigDecimal("0.05")).setScale(3);
            final BigDecimal shopAAmountReal = shopAAmountWithDiscount.multiply(new BigDecimal("0.95")).setScale(3);
            final BigDecimal shopC2AmountReal = shopC2AmountWithDiscount.multiply(new BigDecimal("0.95")).setScale(3);
            final BigDecimal shopC3AmountReal = shopC3AmountWithDiscount.multiply(new BigDecimal("0.95")).setScale(3);
//            
            Assert.assertEquals((Object)amountWithDiscounts, (Object)this.orderOperations.getFinalPrice(order));
            Assert.assertEquals((Object)amountWithoutDiscounts.subtract(amountWithDiscounts), (Object)this.orderOperations.getDiscountSum(order));
            Assert.assertEquals((Object)amountWithDiscounts, (Object)this.transactionOperations.getBuyerTransactionsAmmount(buyer));
            Assert.assertEquals((Object)this.transactionOperations.getShopTransactionsAmmount(shopA), (Object)new BigDecimal("0").setScale(3));
            Assert.assertEquals((Object)this.transactionOperations.getShopTransactionsAmmount(shopC2), (Object)new BigDecimal("0").setScale(3));
            Assert.assertEquals((Object)this.transactionOperations.getShopTransactionsAmmount(shopC3), (Object)new BigDecimal("0").setScale(3));
//         
            Assert.assertEquals((Object)new BigDecimal("0").setScale(3), (Object)this.transactionOperations.getSystemProfit());
            this.generalOperations.time(2);
            Assert.assertEquals((Object)initialTime, (Object)this.orderOperations.getSentTime(order));
            Assert.assertNull((Object)this.orderOperations.getRecievedTime(order));
//            
            Assert.assertEquals((long)this.orderOperations.getLocation(order), (long)cityA);
//            
            this.generalOperations.time(9);
            Assert.assertEquals((long)this.orderOperations.getLocation(order), (long)cityA);
            this.generalOperations.time(8);
            Assert.assertEquals((long)this.orderOperations.getLocation(order), (long)cityC5);
            this.generalOperations.time(5);
            Assert.assertEquals((long)this.orderOperations.getLocation(order), (long)cityB);
          
            Assert.assertEquals((Object)receivedTime, (Object)this.orderOperations.getRecievedTime(order));
            Assert.assertEquals((Object)shopAAmountReal, (Object)this.transactionOperations.getShopTransactionsAmmount(shopA));
            Assert.assertEquals((Object)shopC2AmountReal, (Object)this.transactionOperations.getShopTransactionsAmmount(shopC2));
            Assert.assertEquals((Object)shopC3AmountReal, (Object)this.transactionOperations.getShopTransactionsAmmount(shopC3));
            System.out.println(systemProfit);
            System.out.println(this.transactionOperations.getSystemProfit());
            Assert.assertEquals((Object)systemProfit, (Object)this.transactionOperations.getSystemProfit());
         
            final int shopATransactionId = this.transactionOperations.getTransactionForShopAndOrder(order, shopA);
            Assert.assertNotEquals(-1L, (long)shopATransactionId);
            Assert.assertEquals((Object)receivedTime, (Object)this.transactionOperations.getTimeOfExecution(shopATransactionId));
            
            this.generalOperations.time(10);
            //nova por
            final Calendar initialTime2 = Calendar.getInstance();
            initialTime2.setTimeInMillis(generalOperations.getCurrentTime().getTimeInMillis());
             
            final int order2 = this.buyerOperations.createOrder(buyer);
            this.orderOperations.addArticle(order2, monitor, 1);
            this.orderOperations.addArticle(order2, sto, 1);
            Assert.assertNull((Object)this.orderOperations.getSentTime(order2));
            Assert.assertTrue("created".equals(this.orderOperations.getState(order2)));
            
            this.orderOperations.completeOrder(order2);
            
            Assert.assertTrue("sent".equals(this.orderOperations.getState(order2)));
            final int buyerTransactionId2 = this.transactionOperations.getTransationsForBuyer(buyer).get(1);//sad ima 2 transakcije
            Assert.assertEquals((Object)initialTime2, (Object)this.transactionOperations.getTimeOfExecution(buyerTransactionId2));
            
            //Assert.assertNull((Object)this.transactionOperations.getTransationsForShop(shopA));
            final BigDecimal shopC2Amount2 = new BigDecimal("1").multiply(new BigDecimal("200")).setScale(3);
            final BigDecimal shopC2AmountWithDiscount2 = new BigDecimal("0.5").multiply(shopC2Amount2).setScale(3);
            final BigDecimal shopC3Amount2 = new BigDecimal("1").multiply(new BigDecimal("200")).setScale(3);
            final BigDecimal shopC3AmountWithDiscount2 = shopC3Amount2;
            final BigDecimal amountWithoutDiscounts2 = shopC2Amount2.add(shopC3Amount2).setScale(3);
            
            final BigDecimal amountWithDiscounts2= shopC2AmountWithDiscount2.add(shopC3AmountWithDiscount2).setScale(3);
            final BigDecimal systemProfit2 = shopC2AmountWithDiscount2.add(shopC3AmountWithDiscount2).multiply(new BigDecimal("0.05")).setScale(3);
            final BigDecimal shopC3AmountReal2 = shopC3AmountWithDiscount2.multiply(new BigDecimal("0.95")).setScale(3);
            final BigDecimal shopC2AmountReal2 = shopC2AmountWithDiscount2.multiply(new BigDecimal("0.95")).setScale(3);
            
            Assert.assertEquals((Object)amountWithDiscounts2, (Object)this.orderOperations.getFinalPrice(order2));
            Assert.assertEquals((Object)amountWithoutDiscounts2.subtract(amountWithDiscounts2), (Object)this.orderOperations.getDiscountSum(order2));
            Assert.assertEquals((Object)(amountWithDiscounts2.add(amountWithDiscounts)), (Object)this.transactionOperations.getBuyerTransactionsAmmount(buyer));
            Assert.assertEquals((Object)this.transactionOperations.getShopTransactionsAmmount(shopC3), (Object)new BigDecimal("1710").setScale(3));
            Assert.assertEquals((Object)this.transactionOperations.getShopTransactionsAmmount(shopC2), (Object)new BigDecimal("380").setScale(3));

            Assert.assertEquals((Object)new BigDecimal("510").setScale(3), (Object)this.transactionOperations.getSystemProfit());
            this.generalOperations.time(2);
            Assert.assertEquals((Object)initialTime2, (Object)this.orderOperations.getSentTime(order2));
            Assert.assertNull((Object)this.orderOperations.getRecievedTime(order2));
//            
            Assert.assertEquals((long)this.orderOperations.getLocation(order2), (long)cityA);
//            
            this.generalOperations.time(9);
            Assert.assertEquals((long)this.orderOperations.getLocation(order2), (long)cityA);
            this.generalOperations.time(8);
            Assert.assertEquals((long)this.orderOperations.getLocation(order2), (long)cityC5);
            this.generalOperations.time(5);
            Assert.assertEquals((long)this.orderOperations.getLocation(order2), (long)cityB);
            
            final Calendar receivedTime2 = Calendar.getInstance();
            receivedTime2.clear();
            receivedTime2.set(2023, 8, 1);
    
            Assert.assertEquals((Object)receivedTime2, (Object)this.orderOperations.getRecievedTime(order2));
            Assert.assertEquals((Object)(shopC3AmountReal2.add(shopC3AmountReal)), (Object)this.transactionOperations.getShopTransactionsAmmount(shopC3));
            Assert.assertEquals((Object)(shopC2AmountReal.add(shopC2AmountReal2)), (Object)this.transactionOperations.getShopTransactionsAmmount(shopC2));
            Assert.assertEquals((Object)(systemProfit.add(systemProfit2)), (Object)this.transactionOperations.getSystemProfit());
            final int shopC2TransactionId2 = this.transactionOperations.getTransactionForShopAndOrder(order2, shopC2);
            Assert.assertNotEquals(-1L, (long)shopC2TransactionId2);
            Assert.assertEquals((Object)receivedTime2, (Object)this.transactionOperations.getTimeOfExecution(shopC2TransactionId2));

            
            
           
        }
    
        public static void main(String[] args) {
            Test t=new Test();
            t.myTest5();
        }
}
