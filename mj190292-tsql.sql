
CREATE PROCEDURE SP_FINAL_PRICE
	@IdOrder int,
	@finalPrice int OUTPUT 
AS
BEGIN
	declare @hasAdditionalDiscount int
	set @finalPrice = (select sum((a.Price-a.Price*s.Discount/100)*i.count) as FinalPrice
	from item i join article a on a.IdArticle=i.IdArticle 
	join  shop s on s.IdShop=a.IdShop
	where i.IdOrder=@IdOrder)

	set @hasAdditionalDiscount = (select hasAdditionalDiscount from [order] where IdOrder=@IdOrder)
	if(@hasAdditionalDiscount=1)
	BEGIN
		set @finalPrice=@finalPrice*0.98
	END
END
GO



CREATE TRIGGER TR_TRANSFER_MONEY_TO_SHOPS
   ON  [Order]
   AFTER UPDATE
AS 
BEGIN
		--check if order status is changed from send to arrived 
		declare @kursorI cursor, @kursorD cursor
		declare @OldStatus varchar(30),@NewStatus varchar(30)

		set @kursorI = cursor for
		select Status
		from inserted

		set @kursorD = cursor for
		select Status
		from deleted

		open @kursorI
		open @kursorD

		fetch next from @kursorI
		into @NewStatus

		fetch next from @kursorD
		into @OldStatus

		close @kursorI
		deallocate @kursorI

		close @kursorD
		deallocate @kursorD

		
		if(@NewStatus='arrived' and @OldStatus<>'arrived')
		begin


			declare @IdShop int,@amount decimal(10,3),@IdOrder int,@IdSystem int,@orderHasAdditionalDiscount int,@totalOrderAmount decimal(10,3),@dateOfExecution date
			declare @shop_cursor cursor
			declare @amount_cursor cursor
			declare @IdOrder_cursor cursor
			declare @total_amount_cursor cursor
			declare @dateOfExecution_cursor cursor

			--find updated row and get IdOrder
			set @IdOrder_cursor = cursor for
			select IdOrder
			from inserted

			open @IdOrder_cursor

			fetch next from @IdOrder_cursor
			into @IdOrder
		
			close @IdOrder_cursor
			deallocate @IdOrder_cursor

			--find updated row and get dateOfExecution
			set @dateOfExecution_cursor = cursor for
			select RecievedTime
			from inserted

			open @dateOfExecution_cursor

			fetch next from @dateOfExecution_cursor
			into @dateOfExecution
		
			close @dateOfExecution_cursor
			deallocate @dateOfExecution_cursor

			--find updated row and get AdditionalDiscount
			set @IdOrder_cursor = cursor for
			select HasAdditionalDiscount
			from inserted

			open @IdOrder_cursor

			fetch next from @IdOrder_cursor
			into @orderHasAdditionalDiscount
		
			close @IdOrder_cursor
			deallocate @IdOrder_cursor

			--get IdSystem
			set @IdSystem =(select  top(1) IdSystem from system)

			--getTotalAmount
			set @total_amount_cursor =cursor for
			select cast((sum((a.price-a.price*s.discount/100)*i.count)) as decimal(10,3))
			from item i join article a on a.IdArticle=i.idArticle join shop s on s.IdShop=a.IdShop join [order] o on o.idorder=i.idorder
			where i.idOrder=@IdOrder

			open @total_amount_cursor

			fetch next from @total_amount_cursor
			into @totalOrderAmount
		
			close @total_amount_cursor
			deallocate @total_amount_cursor

			--curosor for all shops in order
			set @shop_cursor =cursor for
			select distinct(a.idshop)
			from item i join article a on a.IdArticle=i.idArticle join shop s on s.IdShop=a.IdShop join [order] o on o.idorder=i.idorder
			where i.idOrder=@IdOrder
			group by a.idshop

			--curosor for all amount for shops in order
			set @amount_cursor =cursor for
			select cast((sum((a.price-a.price*s.discount/100)*i.count))*0.95 as decimal(10,3))
			from item i join article a on a.IdArticle=i.idArticle join shop s on s.IdShop=a.IdShop join [order] o on o.idorder=i.idorder
			where i.idOrder=@IdOrder
			group by a.idshop
	

			open @shop_cursor
			open @amount_cursor

			fetch next from @shop_cursor
			into @IdShop
			fetch next from @amount_cursor
			into @amount

	
			while @@FETCH_STATUS = 0
				begin
						print @amount
						--insert transaction
						insert into [transaction](amount,timeofexecution,idorder,IdClientFrom,IdClientto) values(@amount,@dateOfExecution,@IdOrder,@IdSystem,@IdShop)
						--update shop credit
						update account set credit=credit+@amount where idClient=@IdShop

						fetch next from @shop_cursor
						into @IdShop
						fetch next from @amount_cursor
						into @amount
					end

			--update system credit
		
			print @totalOrderAmount
			if @orderHasAdditionalDiscount=1
				update account set credit=credit-@totalOrderAmount*0.97 where idClient=@IdSystem
			else
				update account set credit=credit-@totalOrderAmount*0.95 where idClient=@IdSystem

			close @shop_cursor
			deallocate @shop_cursor
			close @amount_cursor
			deallocate @amount_cursor

		end
	
END
GO



