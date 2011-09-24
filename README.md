# Resources

* Home - http://hadoop.apache.org/
* Documentation - http://hadoop.apache.org/common/docs/r0.20.204.0/
* Setting up a single node cluster - http://www.michael-noll.com/tutorials/running-hadoop-on-ubuntu-linux-single-node-cluster/
* Map Reduce tutorial - http://hadoop.apache.org/common/docs/stable/mapred_tutorial.html
* Wiki - http://wiki.apache.org/hadoop/

# Setup

This site http://www.michael-noll.com/tutorials/running-hadoop-on-ubuntu-linux-single-node-cluster/
has a great step by step guide to how you configure your system which may be helpful to look at when
you follow our setup guide.


Prereq: Java6 + JAVA_HOME satta

 - Skapa en användare som heter hduser (MANDATORY) och en grupp som heter hadoop.
  
 - Ladda ner hadoop 0.20.204.X - current beta version från http://hadoop.apache.org/common/releases.html#Download

 - Packa upp (installera) hadoop under /usr/local/hadoop-0.20.204.x och gör en symlänk (ln -s hadoop-0.20.204.x hadoop).
    Det är VIKTIGT att hadoop ligger under '/usr/local/hadoop

  "Anledningen till att hduser + en symlänk till /usr/local/hadoop (byt ej namn på katalogen) är att
  när vi kör igång klustret så kommer en (min) maskin att vara master så den kan ssh:a in på era
  maskiner och köra igång det som behövs köras igång."
  
  "Om någon kör vmware/virtualbox så är det viktigt att den konfas så att den kan nås via andra maskiner i klustret"

 - I /usr/local/hadoop/conf/hadoop-env.sh behöver du sätta JAVA_HOME igen.

 - Skapa katalogen /app/hadoop/tmp och ge hduser access till tmp mappen. Du kan även använda tex /tmp/hadoop men isf
   får du ej glömma att ändra i conf/core-site.xml nedan.
 
```shell
sudo mkdir -p /app/hadoop/tmp
   
sudo chown hduser:hadoop /app/hadoop/tmp
   
sudo chmod 750 /app/hadoop/tmp
```
  - Edit .bashrc (.profile)

```shell
export HADOOP_HOME=/usr/local/hadoop

export JAVA_HOME=/usr/lib/jvm/java-6-sun (you should have done this already)

export PATH=$PATH:$HADOOP_HOME/bin
```

 -  Konfiguration

I have checked in a conf directory that is a replacement for the ./hadoop/conf directory. 

Remove/Rename ./hadoop/conf directory

Make a symbolic link from /usr/local/hadoop/conf to hadoop-lab/conf-git. If you use another user than hduser for git,use

```shell
sudo cp -a conf-git/ /usr/local/conf-git
sudo chown -R hduser:staff /usr/local/conf-git/
cd /usr/local/hadoop
ln -s ../conf-git conf
```


 - Add my (johans) public key to hdusers .ssh/authorized_keys
 - NOTE: To make it work when you start the cluster you MUST create and add your hduser key as well

```shell
ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDq/nCxgEsn1MgAevEopbbTnCET9wxqf9iyv0FbisFw/XgXAAk4jOdDPknxYlcNwNt80dC+nyOoqtMOzgHhBPR53h6IJz8aVx0+R/z1igU/MuBJLQ0XpkTNVwcNiN+KeDFILq6I8YGe0ekKgX1Wicdk4WvxjQK4rOrK7affhILbIqZEV6uWxr/DzY202AASPuwJWlPq9ZVKAIAD+E0odDjo+vNGWaA9iW0ifZPVcrAPE/iEMKtL2fptNa4uzFpDHE82uGmt+TmVubhTTdrXm6dDeqV7IX8l8iOm6nTZKF7gVxCnfEEgeGpsJaHLQUHG8ibPTS27/1BchdtmkXVTWKqP hduser@rask.lan
```

Finally, sudo nano /etc/hosts and add

```shell
sudo nano /etc/hosts and add

your_ip	master
```

You should now be able to start a datanode and tasktracker on your computer with

```shell
cd /usr/local/hadoop

rask:hadoop hduser$ bin/hadoop-daemon.sh start datanode
starting datanode, logging to /usr/local/hadoop-0.20.204.0/libexec/../logs/hadoop-hduser-datanode-rask.local.out

rask:hadoop hduser$ bin/hadoop-daemon.sh start tasktracker
starting tasktracker, logging to /usr/local/hadoop-0.20.204.0/libexec/../logs/hadoop-hduser-tasktracker-rask.local.out

rask:hadoop hduser$ jps
3767 DataNode
3850 TaskTracker
3939 Jps

```

#Lab instructions

##HDFS

### Get to know HDFS command utils

Run ./bin/hadoop to get a list of commands that you can use. Test some of the to get a feeling for
how it works.

Files are located under /user/hduser

Most frequently used are

 fs =&gt; commands used to manage the filesystem in a similar way as on *ix system.

fsck =&gt; commands for checking the health of the filesystem
 
I.e ./bin/hadoop fsck /user/hduser -blocks -files


### Web UI:s

From here you can browse the filesystem, see each datanode, MapReduce status, result of jobs etc. 
Just browse through it to get a feeling of what you can find.


Namenode -  http://master:50070 
 
Jobtracker - http://master:50030

Tasktracker - http://localhost:50060 (eller n�gon annan dator i klustret, du n�r den �ven fr�n jobtrackern)

 But each tasktracker is also available from the jobtracker ui 


## Map-Reduce Labs

### 1.a)  Create a program that calculates the maximum length of a row for each file in a directory

Under /user/hduser/gutenberg are three text books that we will use in this example

Open com.jayway.hadoop.demo.RowLengthCounter.java and follow the instructions.
 
Finish the mapper and reducer code so that it produces this output:

	notebooks.txt	95
	outline_of_science.txt	80
	ulysses.txt	73


To run the job (due to permissions you might have to cp the jar file and chmod it)

```shell
mvn clean install
bin/hadoop jar ${path.to}/hadoop-lab-1.0-SNAPSHOT.jar com.jayway.hadoop.demo.RowLengthCounter /user/hduser/gutenberg /user/hduser/${you}/lab1-out
```

NOTE: You cannot use the same result directory twice, remove it or use a new directory with i.e a number appended to dirname  

Use command utils or Web UI:s to verify your results

  
### 1.b) Extra assignment (Skip this, goto next)

Write a program that counts the occurrence of each word in the files.

Should produce something like this:

	and		345
	if		434
	or		343 


### 2.a)  Write a program that outputs Logtype and count from a log4j file

	ERROR	678
 	WARN	115150
 
Open com.jayway.hadoop.ikealog.LogTypeCounter

There is a logfile that you should use under  /user/hduser/ikealogs

```shell
bin/hadoop jar ${path.to}/hadoop-lab-1.0-SNAPSHOT.jar com.jayway.hadoop.ikealog.LogTypeCounter /user/hduser/ikealogs /user/hduser/{you}/ikealogs-lab1-out 
```

### 2.b) Write two jobs where the second takes the output of the first as input

First, open LogTypePerDateCounter. Finish this program so that it outputs date, logtype and count.
     
   	20110519	ERROR	29
	20110519	WARN	29
	20110520	ERROR	203
	20110520	WARN	44333
	...
	...

Use the same directory as input as in 1.a but use a different output dir.

Next, open LogTypePerWeekdayCounter. Finish the program so that it can process the output from the previous program
and calculate the number of errors and warnings per weekday.
	
	Fri ERROR	203
	Fri WARN	44333
	Mon ERROR	72
	Mon WARN	16772
	...	
	...
	
Use the output file (part-r-00000) from LogTypePerDateCounter as input for this job.

### 2.c) Write a program that can be used to figure out the most problematic class 

The program should output each class with the number of errors

	com.somepackage.SomeClass	345
	com.somepackage.SomeClass2	200
	...
	...
	
There is no template for this assignment so you have to create a new och change an existing.

### 3.a) Count the number of words with the Hadoop Streaming utility

Hadoop Streaming is a utility that allows you to run map/reduce jobs with any kind of script or executable
that can be run on the cluster (or at least your own computer). The mapper should write tab-separated key-value
pairs to standard output. The streaming utility will provide the reducer with the key-value pairs
sorted by key, instead of providing a list of values. I.e., the reducer must compare the current key with the
last key to find the end of the value lists.

The biggest advantages with Hadoop Streaming is that it is fast to implement and test. You can also make use
of already existing executables on your cluster nodes. For example find, grep, sed, etc. The mapper and reducer
commands can even contain pipes.

To test your mapper and reducer, run

```shell
cat input | <mapper> | sort | <reducer>
```

In this assignment we will count the number of occurences of each word in the input. The mapper code in Ruby is:

```ruby
#!/usr/bin/ruby

STDIN.each_line do |l|

  line = l.strip
  
  unless line.empty?
    words = line.split(/\W+/)
    words.each do |word|
      puts "#{word}\t1"
    end
  end
end
```

Try to implement a reducer in your own favorite scripting language. There is no need for the reducer to be implemented
in Ruby just because the mapper is.

There is no hadoop command for the streaming utility. Instead you use a jar that comes with the distribution.
The -file argument is used to specify which executables that must be sent to the other cluster nodes. When
using already existing scripts and executables this argument can be omitted.

```shell
bin/hadoop jar $HADOOP_HOME/contrib/streaming/hadoop-streaming-0.20.204.0.jar -mapper <mapper absolute path> -reducer <reducer absolute path> -file <mapper absolute path> -file <reducer absolute path> -input /user/hduser/gutenberg -output <output dir>
```