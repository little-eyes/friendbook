import sys
import csv

def feature(chunk):
	n = len(chunk)
	sum_x = [0. for i in range(len(chunk[0]))]
	sum_x2 = [0.for i in range(len(chunk[0]))]
	for line in chunk:
		for j in range(len(line)):
			sum_x[j] += line[j]
			sum_x2[j] += line[j]**2
	mean = [0. for i in range(len(sum_x))]
	std = [0. for i in range(len(sum_x))]
	for i in range(len(sum_x)):
		mean[i] = sum_x[i]/n
		std[i] = sum_x2[i]/n - mean[i]**2
	return [mean[0]]+std[1:]


if __name__ == "__main__":
	filename = ""
	fin = csv.reader(open(filename, "rb"))
	fout = csv.writer(open("feature.csv", "ab"))
	count = 0
	chunk = []
	for line in fin:
		dataline = [eval(line[i]) for i in range(1, 9)]
		if count == 200 or dataline[0] - chunk[len(chunk)-1][0] > 10000:
			res = feature(chunk)
			print "-->	",res
			fout.writerow(res)
			chunk = []
			count = 0
		count += 1
		chunk.append(dataline)
