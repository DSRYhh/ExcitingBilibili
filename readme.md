# Exciting Bilibili
![Build status](https://travis-ci.org/DSRYhh/ExcitingBilibili.svg?branch=master)

## Build

Build with `sbt 0.13.16`. Use `sbt` command and enter `compile` to build the project.

### Database
Use postgre to store data. The structure of the database can be seen in `/src/main/scala/ExcitingBilibili/Utility/Database/CreateTable.sql`

## Web 

### RESTful API

#### System status
`/api/status`: System status in JSON format


#### Fetching data
`/api/data`

Query parameters

|Parameter|Example|Remarks|Optional|
|:-------:|:-----:|:--|:--:|
|date|`date=2017-8-1`|Today by default|Optional|
|count|`count=50`|Return all data if not specified|Optional|    

# TODO List
- Duplicated primary key exception handling
- Change danmu table to unique index