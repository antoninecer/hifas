CREATE DATABASE [ipex]  
 COLLATE Czech_CI_AS
GO
use [ipex]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[akce]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[akce]
GO

CREATE TABLE [dbo].[akce] (
	[akce] [varchar] (11) COLLATE Czech_CI_AS NULL ,
	[popis] [varchar] (150) COLLATE Czech_CI_AS NULL ,
	[prikaz] [varchar] (50) COLLATE Czech_CI_AS NULL 
) ON [PRIMARY]
GO


if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[detail_linky]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[detail_linky]
GO

CREATE TABLE [dbo].[detail_linky] (
	[id] [varchar] (6) COLLATE Czech_CI_AS NOT NULL ,
	[name] [varchar] (30) COLLATE Czech_CI_AS NULL ,
	[dialplan] [varchar] (12) COLLATE Czech_CI_AS NULL ,
	[extension] [varchar] (20) COLLATE Czech_CI_AS NULL ,
	[globalExten] [varchar] (20) COLLATE Czech_CI_AS NULL ,
	[ccid1] [varchar] (20) COLLATE Czech_CI_AS NULL ,
	[ccid2] [varchar] (20) COLLATE Czech_CI_AS NULL ,
	[outRoute] [varchar] (20) COLLATE Czech_CI_AS NULL ,
	[ringDuration] [varchar] (6) COLLATE Czech_CI_AS NULL ,
	[ringType] [varchar] (20) COLLATE Czech_CI_AS NULL ,
	[username] [varchar] (20) COLLATE Czech_CI_AS NULL ,
	[password] [varchar] (30) COLLATE Czech_CI_AS NULL ,
	[note] [varchar] (50) COLLATE Czech_CI_AS NULL 
) ON [PRIMARY]
GO


if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[linky]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[linky]
GO

CREATE TABLE [dbo].[linky] (
	[number] [varchar] (4) COLLATE Czech_CI_AS NULL ,
	[id] [varchar] (4) COLLATE Czech_CI_AS NOT NULL 
) ON [PRIMARY]
GO


if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[FK_detail_linky_smerovani]') and OBJECTPROPERTY(id, N'IsForeignKey') = 1)
ALTER TABLE [dbo].[detail_linky] DROP CONSTRAINT FK_detail_linky_smerovani
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[smerovani]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[smerovani]
GO

CREATE TABLE [dbo].[smerovani] (
	[nazev] [varchar] (20) COLLATE Czech_CI_AS NOT NULL ,
	[popis] [varchar] (100) COLLATE Czech_CI_AS NULL ,
	[uziti] [varchar] (50) COLLATE Czech_CI_AS NULL 
) ON [PRIMARY]
GO

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[cislovani]') AND OBJECTPROPERTY(id, N'IsUserTable') = 1)
DROP TABLE [dbo].[cislovani]
CREATE TABLE [dbo].[cislovani](
	[nazev] [varchar](30) NULL
) ON [PRIMARY]

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[tarifikace]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[tarifikace]
GO

CREATE TABLE [dbo].[tarifikace] (
	[_z] [varchar] (30) COLLATE Czech_CI_AS NOT NULL ,
	[_na] [varchar] (30) COLLATE Czech_CI_AS NOT NULL ,
	[pole2] [varchar] (30) COLLATE Czech_CI_AS NULL ,
	[kdy] [datetime] NOT NULL ,
	[priznak1] [varchar] (1) COLLATE Czech_CI_AS NULL ,
	[priznak2] [varchar] (1) COLLATE Czech_CI_AS NULL ,
	[popis] [varchar] (20) COLLATE Czech_CI_AS NULL ,
	[pole7] [varchar] (30) COLLATE Czech_CI_AS NULL ,
	[delka] [int] NULL ,
	[pole10] [varchar] (30) COLLATE Czech_CI_AS NULL ,
	[pole11] [varchar] (30) COLLATE Czech_CI_AS NULL ,
	[pole12] [varchar] (30) COLLATE Czech_CI_AS NULL ,
	[pole13] [varchar] (30) COLLATE Czech_CI_AS NULL 
) ON [PRIMARY]
GO

if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[vstup]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[vstup]
GO

CREATE TABLE [dbo].[vstup] (
	[id] [int] IDENTITY (1, 1) NOT NULL ,
	[akce] [varchar] (11) COLLATE Czech_CI_AS NULL ,
	[linka] [varchar] (6) COLLATE Czech_CI_AS NULL ,
	[cas] [datetime] NULL ,
	[info] [varchar] (50) COLLATE Czech_CI_AS NULL ,
	[done] [varchar] (1) COLLATE Czech_CI_AS NULL ,
	[cas2] [datetime] NULL 
) ON [PRIMARY]
GO


ALTER TABLE [dbo].[smerovani] ADD 
	CONSTRAINT [PK_smerovani] PRIMARY KEY  CLUSTERED 
	(
		[nazev]
	)  ON [PRIMARY] 
GO


ALTER TABLE [dbo].[linky] ADD 
	CONSTRAINT [PK_linky] PRIMARY KEY  CLUSTERED 
	(
		[id]
	)  ON [PRIMARY] 
GO


ALTER TABLE [dbo].[detail_linky] ADD 
	CONSTRAINT [PK_detail_linky] PRIMARY KEY  CLUSTERED 
	(
		[id]
	)  ON [PRIMARY] 
GO


ALTER TABLE [dbo].[detail_linky] ADD 
	CONSTRAINT [FK_detail_linky_smerovani] FOREIGN KEY 
	(
		[outRoute]
	) REFERENCES [dbo].[smerovani] (
		[nazev]
	)
GO

ALTER TABLE [dbo].[tarifikace] ADD 
	CONSTRAINT [unikatni] PRIMARY KEY  CLUSTERED 
	(
		[_z],
		[_na],
		[kdy]
	)  ON [PRIMARY] 
GO

