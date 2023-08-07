
CREATE TABLE [Account]
( 
	[Credit]             decimal(10,3)  NULL ,
	[IdClient]           integer  NOT NULL 
)
go

CREATE TABLE [Article]
( 
	[IdArticle]          integer  IDENTITY  NOT NULL ,
	[Name]               varchar(100)  NULL ,
	[Count]              integer  NULL ,
	[Price]              integer  NULL ,
	[IdShop]             integer  NULL 
)
go

CREATE TABLE [Buyer]
( 
	[IdCity]             integer  NULL ,
	[IdBuyer]            integer  NOT NULL 
)
go

CREATE TABLE [City]
( 
	[IdCity]             integer  IDENTITY  NOT NULL ,
	[Name]               varchar(100)  NULL 
)
go

CREATE TABLE [CityLines]
( 
	[Distance]           integer  NULL ,
	[IdCity1]            integer  NOT NULL ,
	[IdCity2]            integer  NOT NULL ,
	[IdLine]             integer  IDENTITY  NOT NULL 
)
go

CREATE TABLE [Item]
( 
	[IdItem]             integer  IDENTITY  NOT NULL ,
	[IdOrder]            integer  NULL ,
	[IdArticle]          integer  NULL ,
	[Count]              integer  NULL 
)
go

CREATE TABLE [Member]
( 
	[IdM]                integer  IDENTITY  NOT NULL ,
	[Name]               varchar(100)  NULL 
)
go

CREATE TABLE [Order]
( 
	[IdOrder]            integer  IDENTITY  NOT NULL ,
	[Status]             varchar(100)  NULL ,
	[IdBuyer]            integer  NULL ,
	[SentTime]           date  NULL ,
	[RecievedTime]       date  NULL ,
	[TimeToAsseble]      integer  NULL ,
	[IdNearestCity]      integer  NULL ,
	[HasAdditionalDiscount] int  NULL 
)
go

CREATE TABLE [OrderPath]
( 
	[Part]               integer  NULL ,
	[IdLine]             integer  NULL ,
	[IdOrder]            integer  NULL ,
	[IdPath]             integer  IDENTITY  NOT NULL 
)
go

CREATE TABLE [Shop]
( 
	[Discount]           integer  NULL ,
	[IdCity]             integer  NULL ,
	[IdShop]             integer  NOT NULL 
)
go

CREATE TABLE [System]
( 
	[IdSystem]           integer  NOT NULL 
)
go

CREATE TABLE [Transaction]
( 
	[IdTransaction]      integer  IDENTITY  NOT NULL ,
	[Amount]             decimal(10,3)  NULL ,
	[TimeOfExecution]    date  NULL ,
	[IdOrder]            integer  NULL ,
	[IdClientFrom]       integer  NULL ,
	[IdClientTo]         integer  NULL 
)
go

ALTER TABLE [Account]
	ADD CONSTRAINT [XPKAccount] PRIMARY KEY  CLUSTERED ([IdClient] ASC)
go

ALTER TABLE [Article]
	ADD CONSTRAINT [XPKArticle] PRIMARY KEY  CLUSTERED ([IdArticle] ASC)
go

ALTER TABLE [Buyer]
	ADD CONSTRAINT [XPKBuyer] PRIMARY KEY  CLUSTERED ([IdBuyer] ASC)
go

ALTER TABLE [City]
	ADD CONSTRAINT [XPKCity] PRIMARY KEY  CLUSTERED ([IdCity] ASC)
go

ALTER TABLE [City]
	ADD CONSTRAINT [XAK1City] UNIQUE ([Name]  ASC)
go

ALTER TABLE [CityLines]
	ADD CONSTRAINT [XPKCityLines] PRIMARY KEY  CLUSTERED ([IdLine] ASC)
go

ALTER TABLE [Item]
	ADD CONSTRAINT [XPKItem] PRIMARY KEY  CLUSTERED ([IdItem] ASC)
go

ALTER TABLE [Member]
	ADD CONSTRAINT [XPKMember] PRIMARY KEY  CLUSTERED ([IdM] ASC)
go

ALTER TABLE [Order]
	ADD CONSTRAINT [XPKOrder] PRIMARY KEY  CLUSTERED ([IdOrder] ASC)
go

ALTER TABLE [OrderPath]
	ADD CONSTRAINT [XPKOrderPath] PRIMARY KEY  CLUSTERED ([IdPath] ASC)
go

ALTER TABLE [Shop]
	ADD CONSTRAINT [XPKShop] PRIMARY KEY  CLUSTERED ([IdShop] ASC)
go

ALTER TABLE [System]
	ADD CONSTRAINT [XPKSystem] PRIMARY KEY  CLUSTERED ([IdSystem] ASC)
go

ALTER TABLE [Transaction]
	ADD CONSTRAINT [XPKTransaction] PRIMARY KEY  CLUSTERED ([IdTransaction] ASC)
go


ALTER TABLE [Account]
	ADD CONSTRAINT [R_16] FOREIGN KEY ([IdClient]) REFERENCES [Member]([IdM])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Article]
	ADD CONSTRAINT [R_2] FOREIGN KEY ([IdShop]) REFERENCES [Shop]([IdShop])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Buyer]
	ADD CONSTRAINT [R_14] FOREIGN KEY ([IdBuyer]) REFERENCES [Member]([IdM])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go

ALTER TABLE [Buyer]
	ADD CONSTRAINT [R_3] FOREIGN KEY ([IdCity]) REFERENCES [City]([IdCity])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [CityLines]
	ADD CONSTRAINT [R_4] FOREIGN KEY ([IdCity1]) REFERENCES [City]([IdCity])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [CityLines]
	ADD CONSTRAINT [R_5] FOREIGN KEY ([IdCity2]) REFERENCES [City]([IdCity])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Item]
	ADD CONSTRAINT [R_6] FOREIGN KEY ([IdOrder]) REFERENCES [Order]([IdOrder])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Item]
	ADD CONSTRAINT [R_7] FOREIGN KEY ([IdArticle]) REFERENCES [Article]([IdArticle])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Order]
	ADD CONSTRAINT [R_8] FOREIGN KEY ([IdBuyer]) REFERENCES [Buyer]([IdBuyer])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Order]
	ADD CONSTRAINT [R_19] FOREIGN KEY ([IdNearestCity]) REFERENCES [City]([IdCity])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [OrderPath]
	ADD CONSTRAINT [R_20] FOREIGN KEY ([IdLine]) REFERENCES [CityLines]([IdLine])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [OrderPath]
	ADD CONSTRAINT [R_21] FOREIGN KEY ([IdOrder]) REFERENCES [Order]([IdOrder])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Shop]
	ADD CONSTRAINT [R_13] FOREIGN KEY ([IdShop]) REFERENCES [Member]([IdM])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go

ALTER TABLE [Shop]
	ADD CONSTRAINT [R_1] FOREIGN KEY ([IdCity]) REFERENCES [City]([IdCity])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [System]
	ADD CONSTRAINT [R_15] FOREIGN KEY ([IdSystem]) REFERENCES [Member]([IdM])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go


ALTER TABLE [Transaction]
	ADD CONSTRAINT [R_11] FOREIGN KEY ([IdOrder]) REFERENCES [Order]([IdOrder])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Transaction]
	ADD CONSTRAINT [R_17] FOREIGN KEY ([IdClientFrom]) REFERENCES [Member]([IdM])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Transaction]
	ADD CONSTRAINT [R_18] FOREIGN KEY ([IdClientTo]) REFERENCES [Member]([IdM])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go
   
