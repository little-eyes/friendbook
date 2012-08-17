import csv
import os

if __name__ == "__main__":
	f0 = csv.reader(open("raw-5gb.csv", "rb"))
	sum_x2 = [0. for i in range(7)]
	sum_x = [0. for i in range(7)]
	count = 0
	for line in f0:
		dataline = [eval(line[i]) for i in range(1, 8)]
		for i in range(7):
			sum_x2[i] += dataline[i]**2
			sum_x[i] += dataline[i]
		count += 1
		sys.stdout.write(str(count)+"\r")
		sys.stdout.flush()
	mean = [0. for i in range(7)]
	std = [0. for i in range(7)]
	for i in range(7):
		mean[i] = sum_x[i]/count
		std[i] = (sum_x2[i]/count - mean[i]**2)**0.5
	
	print "-----------------"
	print mean
	print std
	print "-----------------"

	f1 = csv.reader(open("raw-5gb.csv", "rb"))
	fo = csv.writer(open("norm-5gb.csv", "rb"))
	itr = 0
	for line in f1:
		dataline = [eval(line[i]) for i in range(1, 8)]
		for i in range(7):
			dataline[i] = (dataline[i]-mean[i])/std[i]
		row = [line[0]] + dataline
		fo.writerow(row)
		sys.stdout.write(str(itr)+"\r")
		sys.stdout.flush()
	
	print
	print "done!"

