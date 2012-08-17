<?php

/*
* This file is used to accept import raw sensor recordings from smartphone,
* then clean the duplicate and insert all good data into database.
*/

// Open the database.
$conn = mysql_connect("localhost", "friendbook-dev", "2TMy2mHPmB5YmBd5") or die(mysql_error());
mysql_select_db("friendbook") or die(mysql_error());

// Get the handle of the buffered data storage directory.
$handle = opendir("/home/friendbook-data");
if ($handle) {
	echo "Open dir success......\n";

	// Process each file.
	while (($entry = readdir($handle)) != false) {
		if ($entry == "." or $entry == "..") continue;
		if (filesize("/home/friendbook-data/".$entry) == 0) continue;
		$var = explode("-", $entry);
		$user_id = $var[0];
		$lines = file("/home/friendbook-data/".$entry) or die("Open File failed: ".$entry."\n");
		echo "Start working on ".$entry."\n";
		foreach ($lines as $line_num => $line) {
			$rawdata = explode(",", $line);
		    $rawdata[11] = substr($rawdata[11], 0, -1);
			
			// Check to see if duplicate sensor record.
			$query = "SELECT * FROM `rawdata` WHERE `hwid`='{$user_id}' AND `timestamp`='{$rawdata[0]}';";
			$res = mysql_query($query);
			if (mysql_num_rows($res) > 0) continue;

			// Insert the new record. To be safe, a low efficiency one-by-one insert approach is used.
			$query = "INSERT INTO `rawdata` (`hwid`, `timestamp`, `hrs`, `acc-x`, `acc-y`, `acc-z`, `gyr-x`, `gyr-y`, `gyr-z`, `gps-lat`, `gps-lng`, `gps-sp`, `gps-acc`) VALUES ";
			$query .= "('{$user_id}', '{$rawdata[0]}', '{$rawdata[1]}', '{$rawdata[2]}', '{$rawdata[3]}', '{$rawdata[4]}', '{$rawdata[5]}', '{$rawdata[6]}', '{$rawdata[7]}', '{$rawdata[8]}', '{$rawdata[9]}', '{$rawdata[10]}', '{$rawdata[11]}');";
			mysql_query($query) or die(mysql_error());
			echo ".";
		}
		echo "\n";
		echo "File -> $entry: done!\n";
	}
}

?>
