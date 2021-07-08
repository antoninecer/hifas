<?php
$myServer = "localhost";
$myUser = "sa";
$myPass = "orea";
$myDB = "ipex1";

//connection to the database
$dbhandle = mssql_connect($myServer, $myUser, $myPass)
  or die("Couldn't connect to SQL Server on $myServer");

//select a database to work with
$selected = mssql_select_db($myDB, $dbhandle)
  or die("Couldn't open database $myDB");

//declare the SQL statement that will query the database
$query = "SELECT v.id, v.akce,a.popis, v.linka, v.cas, v.info, d.popis, v.cas2, v.jmeno ";
$query .= "FROM vstup v, akce a, done d ";
$query .= "WHERE  v.akce=a.akce and v.done=d.done and isnull(v.done,0) in(0,2,3) and cas > getdate()-1";

//execute the SQL query and return records
$result = mssql_query($query);

$numRows = mssql_num_rows($result);
echo "<h1>" . $numRows . " Row" . ($numRows == 1 ? "" : "s") . " Returned </h1>";

//display the results
while($row = mssql_fetch_array($result))
{
  echo "<li>" . $row["v.id"] . $row["v.akce"] . $row["a.popis"] . $row["v.linka"] . $row["v.cas"] . $row["v.info"] . $row["d.popis"] . $row["v.cas2"] . $row["v.jmeno"] . "</li>";
}
//close the connection
mssql_close($dbhandle);
?> 