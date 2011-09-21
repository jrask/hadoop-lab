TODO - Get more logs or create fake logs

Lab instructions

1.a)  Create a program that calculates the maximum length of a row for each file in a directory

Under /user/hduser/gutenberg are three text books that we will use in this example

 - Open com.jayway.hadoop.demo.RowCounter.java
 
 Finish the mapper and reducer code so that it produces this output:
 
 notebooks.txt	95
 outline_of_science.txt	80
 ulysses.txt	73

 
Execute: bin/hadoop jar ¤{path.to}/hadoop-lab-1.0-SNAPSHOT.jar com.jayway.hadoop.demo.RowCounter /user/hduser/gutenberg /user/hduser/¤{you}/gutenberg-out

NOTE: You cannot use the same result directory twice, remove it or use a new directory.  
  
  
1.b) Extra assignment: Write a program that counts the occurrence of each word in the files.

Should produce something like this:

and		345
if		434
or		343 


2.a)  Write a program that outputs Logtype and count

 ERROR	678
 WARN	115150
 
Edit com.jayway.hadoop.ikealog.LogTypeCounter

There is a logfile that you should use under  /user/hduser/ikealogs

 bin/hadoop jar ${path.to}/hadoop-lab-1.0-SNAPSHOT.jar com.jayway.hadoop.ikealog.LogTypeCounter /user/hduser/ikealogs /user/hduser/{you}/ikealogs-out 
 
2.b) This assignment includes two MapReduce jobs;
     First, open LogTypePerDateCounter. Finish this program so that it outputs date, logtype and count.
     
    20110519	ERROR	29
	20110519	WARN	29
	20110520	ERROR	203
	20110520	WARN	44333
	20110521	ERROR	190
	20110521	WARN	52872
	20110522	ERROR	184
	20110522	WARN	1144
	20110523	ERROR	72
	20110523	WARN	16772
	
	Next, open LogTypePerWeekdayCounter. Finish the program so that it can process the output from the previous program
	and calculate the number of errors and warnings per weekday.
	
	Fri ERROR	203
	Fri WARN	44333
	Mon ERROR	72
	Mon WARN	16772
	Sat ERROR	190
	Sat WARN	52872
	Sun ERROR	184
	Sun WARN	1144
	Thu ERROR	29
	Thu WARN	29 