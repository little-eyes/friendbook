<?php

/*
* This file is used to accept data from network and temporary stored in a
* server's local path. 
*/

$target_path  = "/home/friendbook-data/";
$target_path = $target_path.basename( $_FILES['uploadedfile']['name']);

if(move_uploaded_file($_FILES['uploadedfile']['tmp_name'], $target_path)) {
	echo "The file ".  basename( $_FILES['uploadedfile']['name'])." has been uploaded";
} else {
	echo "There was an error uploading the file, please try again!";
}

$user_ip = $_SERVER['REMOTE_ADDR'];
$var = explode("-", $_FILES['uploadedfile']['name']);

$hwid = $var[0];
$record_time = $var[1];

// -----------------------------
// The following is to update the checkins in the database.

$conn = mysql_connect("localhost", "friendbook-dev", "2TMy2mHPmB5YmBd5") or die(mysql_error());
mysql_select_db("friendbook") or die(mysql_error());

$query = "SELECT * FROM `users` WHERE `hwid`='".$hwid."';";

$res = mysql_query($query);
if (mysql_num_rows($res) == 0) {
	$query = "INSERT INTO `users` (`hwid`, `uip`, `checkin`) VALUES ('".$hwid."', '".$user_ip."', '".date("m/d/Y h:i:s a", time())."');";
	//echo $query;
	mysql_query($query);
}
else {
	$query = "UPDATE `users` SET `uip`='".$user_ip."', `checkin`='".date("m/d/Y h:i:s a", time())."' WHERE `hwid`='".$hwid."';";
	//echo $query;
	mysql_query($query);
}

$query = "INSERT INTO `checkin` (`hwid`, `timestamp` ,`path`, `status`) VALUES ('{$hwid}','".date("m/d/Y h;i:s a", time())."', '{$target_path}', '0');";
mysql_query($query);

mysql_close($conn);
?>
