import csv
import math
import sys

def getMeanValue(vector):
	if len(vector) == 0:
		return
	return sum(vector) / len(vector)


# A fast std calculation: std = sqrt(EX^2 - (EX)^2)
def getStdValue(vector):
	if len(vector) == 0:
		return
	mean = getMeanValue(vector)
	return math.sqrt(sum([(x - mean)**2 for x in vector]))


# normalize a vector data
def getNormVector(data):
	nWidth = len(data)
	if (nWidth == 0):
		return
	meanVector = [14.479200823612667, 0.0020299812913130077, -0.00394638490302047, -0.01707181403018664, 0.02186330976773779, 0.007398993257810107, -0.006261374164440122]
	stdVector = [5.731461818000807, 0.7383419047929148, 0.7300122877314742, 0.7779778604783759, 0.47491686137469136, 0.4627368531414916, 0.26195897850032046]
	normVector = [(data[i] - meanVector[i])/stdVector[i] for i in range(nWidth)]
	return normVector

def getDistance(va, vb):
	if len(va) != len(vb):
		return
	sumDistance = sum([(va[i] - vb[i])**2 for i in range(len(va))])
	return math.sqrt(sumDistance)


# extract a feature vector from data chunk
def getFeatureVector(data):
	nSize = len(data)
	if (nSize == 0):
		return
	nWidth = len(data[0])
	featureVector = [0.0 for x in range(nWidth)]
	nWidth = len(data[0])
	for index in range(nWidth):
		vector = []
		for i in range(nSize):
			vector.append(data[i][index])
		if index == 0:
			featureVector[index] = getMeanValue(vector)
		else:
			featureVector[index] = getStdValue(vector)
	return featureVector


# kmeans classification
def classify(data):
	centroids = [[0.748456,14.708913,8.068699,8.525011,9.09386,11.943502,10.862649],
				 [0.007258,0.406512,0.540875,0.540096,0.507164,0.496289,0.51968],
				 [-2.311677,0.058579,0.033388,0.043472,0.143591,0.699729,0.077384],
				 [-0.081859,11.353061,7.580691,8.457156,7.756749,9.599028,8.20283],
				 [0.700406,0.5765,0.75581,0.770671,0.808063,0.670314,0.70497],
				 [-0.257834,0.938461,1.354903,1.223121,1.242737,1.205735,1.16795],
				 [-0.89436,0.508973,0.610307,0.578942,0.485694,0.489455,0.560052],
				 [-0.607266,1.017115,1.275919,1.202842,1.202362,1.145397,1.108045],
				 [-0.432534,1.317496,1.644737,1.519063,1.539822,1.510225,1.526299],
				 [1.045754,0.89793,0.947056,0.911609,1.016113,0.966098,0.937912],
				 [0.440001,1.224705,1.111222,1.222184,1.174176,1.158568,1.041785],
				 [-1.588784,0.002676,0.003449,0.003219,0.001206,0.001195,0.001211],
				 [0.265084,0.77343,0.739888,0.835033,0.751285,0.78236,0.715366],
				 [1.386746,0.474941,0.450967,0.486053,0.473852,0.482918,0.53293],
				 [-0.73049,19.702222,20.384851,16.235003,10.615717,11.532199,17.708576]]
	classId = -1
	minDistance = -1
	for index in range(15):
		dist = getDistance(data, centroids[index])
		if minDistance < 0 or dist < minDistance:
			minDistance = dist
			classId = index
	return classId


def convert(path, interval=100):
	inStream = csv.reader(open(path, "r"), delimiter=",")
	chunk = []
	lastTimeStamp = -1
	activitySequence = []
	for line in inStream:
		timestamp = eval(line[2])
		chunk.append([eval(line[o]) for o in range(3, 10)])
		if lastTimeStamp < 0 or timestamp - lastTimeStamp > 100 * 1000:
			feature = getFeatureVector(chunk)
			if feature != None:
				normFeature = getNormVector(feature)
				activitySequence.append(classify(normFeature))
			chunk = []
			lastTimeStamp = timestamp
	
	return activitySequence


if __name__ == "__main__":
	if len(sys.argv) < 3:
		print "need to provide input and output file names."
	sequence = convert(sys.argv[1])
	outStream = csv.writer(open(sys.argv[2], "w"), delimiter=",")
	for seq in sequence:
		outStream.writerow([seq])
	
