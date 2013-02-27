-	INTRODUCTION
Friendbook2 is the advanced version of Friendbook which integrated more advanced algorithm and techniques in the system that achieves much better results in friend recommendation.

It takes advantages of human life style recognized from smartphone sensors and acquires the "query-and-recommendation" mode from search engine. For more details, please read:
	http://code.google.com/p/friendbook2/

---------------------------------
-	SOURCE DIRECTORY
1. android-ant
	This is the raw data collecting system used to take the initial raw data from a user's android phone, then the training system can train a good classifier.

2. server-ant
	This is the training server system that accept raw data uploaded from android-ant and the first level data clean. The cleaned data are imported into the MySQL database.

3. android-client
	The client app running on the Android smartphone where a user tries to request friend recommendation.

4. recommendation-kernel
	The kernel source directory of the recommendation system, including training and service.

5. database
	The directory that stores all related database involved in the project. One can do the simple import then replicate the system's database structure.

6. code-review.py
	This is a highly recommendation step BEFORE we are trying to commit code. We strongly suggest everyone to use this python script to upload your change issue, then invite teammates to review your code. Afterwards, revise your code, then check in.

push
