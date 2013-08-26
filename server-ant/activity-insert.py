import pythondb
import time
import csv
import sys

db_handler = pythondb.MyDatabaseHandler("localhost", \
	"root", "123", "friendbook")

if __name__ == "__main__":
	if len(sys.argv) < 3:
		print "provide a file name"
	filename = sys.argv[1]
	inStream = csv.reader(open(filename, "r"), delimiter=",")
	document = ""
	for activity in inStream:
		document += str(activity[0]) + ";"
	hwid = sys.argv[1].split("-")[0]
	query = "INSERT INTO `rawdoc` (`hwid`, `timestamp`, `entity`) \
		VALUES ('"+sys.argv[2]+"', '"+str(int(time.time()))+"', '"+ \
		document[0:len(document)-1]+"')"
	db_handler.update_db(query)
