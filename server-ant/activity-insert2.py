import pythondb
import time
import sys

db_handler = pythondb.MyDatabaseHandler("localhost", \
	"root", "123", "friendbook")

if __name__ == "__main__":
	if len(sys.argv) < 3:
		print "provide a file name and an ID"
	filename = sys.argv[1]
	hwid = sys.argv[2]
	inFile = open(filename, "r")
	document = inFile.readline()	
	query = "INSERT INTO `rawdoc` (`hwid`, `timestamp`, `entity`) \
		VALUES ('"+hwid+"', '"+str(int(time.time()))+"', '"+ \
		document+"')"
	db_handler.update_db(query)
