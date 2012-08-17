import csv
import sys

if __name__ == "__main__":
	hwid = ["1fb5f5eb95abf677", \
			"37d1cc080bd90d61", \
			"58dae26e68c055b5", \
			"59a1aad2ac9e18cf", \
			"5fd01dd3c4253311", \
			"70cad1272ff9c13c", \
			"ee8a9b6e9e0deb89", \
			"fe411ac462309f62"]
	fo = {}
	for x in hwid:
		fo[x] = csv.writer(open(x, "wb"))
	
	f0 = csv.reader(open("norm-5gb.csv", "rb"))
	itr = 0
	for line in f0:
		fo[str(line[0])].writerow(line)
		sys.stdout.write(str(itr))
		sys.stdout.flush()
		itr += 1
	print "done!!"

