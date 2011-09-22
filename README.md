# Setup

This site http://www.michael-noll.com/tutorials/running-hadoop-on-ubuntu-linux-single-node-cluster/
has a great step by step guide to how you configure your system which may be helpful to look at when
you follow our setup guide.


Prereq: Java6 + JAVA_HOME satta

1. Skapa en användare som heter hduser (MANDATORY) och en grupp som heter hadoop.
  
2. Ladda ner hadoop 0.20.204.X - current beta version från http://hadoop.apache.org/common/releases.html#Download

3. Packa upp (installera) hadoop under /usr/local/hadoop-0.20.204.x och gör en symlänk (ln -s hadoop-0.20.204.x hadoop).
    Det är VIKTIGT att hadoop ligger under '/usr/local/hadoop

  "Anledningen till att hduser + en symlänk till /usr/local/hadoop (byt ej namn på katalogen) är att
  när vi kör igång klustret så kommer en (min) maskin att vara master så den kan ssh:a in på era
  maskiner och köra igång det som behövs köras igång."
  
  "Om någon kör vmware/virtualbox så är det viktigt att den konfas så att den kan nås via andra maskiner i klustret"

4. I /usr/local/hadoop/conf/hadoop-env.sh behöver du sätta JAVA_HOME igen.

5. Skapa katalogen /app/hadoop/tmp och ge hduser access till tmp mappen. Du kan även använda tex /tmp/hadoop men isf
   får du ej glömma att ändra i conf/core-site.xml nedan.
   
   $ sudo mkdir -p /app/hadoop/tmp
   $ sudo chown hduser:hadoop /app/hadoop/tmp
   $ sudo chmod 750 /app/hadoop/tmp

6. Konfiguration

6.1 Open and add the stuff below to your conf/core-site.xml

<property>
  <name>hadoop.tmp.dir</name>
  <value>/app/hadoop/tmp</value>
  <description>A base for other temporary directories.</description>
</property>

<property>
  <name>fs.default.name</name>
  <value>hdfs://master:54310</value>
  <description>The name of the default file system.  A URI whose
  scheme and authority determine the FileSystem implementation.  The
  uri's scheme determines the config property (fs.SCHEME.impl) naming
  the FileSystem implementation class.  The uri's authority is used to
  determine the host, port, etc. for a filesystem.</description>
</property>


6.2 Open and add the stuff below to your conf/hdfs-site.xml

<property>
  <name>dfs.replication</name>
  <value>2</value>
  <description>Default block replication.
  The actual number of replications can be specified when the file is created.
  The default is used if replication is not specified in create time.
  </description>
</property>
<property>
   <name>dfs.block.size</name>
   <value>10240000</value>
</property>
<property>
   <name>dfs.http.address</name>
   <value>master:50070</value>
</property>


6.3 Open and add the stuff below to your conf/mapred-site.xml

<property>
  <name>mapred.job.tracker</name>
  <value>master:54311</value>
  <description>The host and port that the MapReduce job tracker runs
  at.  If "local", then jobs are run in-process as a single map
  and reduce task.
  </description>
</property>

7. Edit .bashrc (.profile)

export HADOOP_HOME=/usr/local/hadoop
export JAVA_HOME=/usr/lib/jvm/java-6-sun (you should have done this already)
export PATH=$PATH:$HADOOP_HOME/bin

8. Work In Progress


# All lab instructions are in progress
#Lab instructions

##HDFS

### Get to know HDFS command utils

Run ./bin/hadoop to get a list of commands that you can use. Test some of the to get a feeling for
how it works.

Files are located under /user/hduser

Most frequently used are
 fs => commands used to manage the filesystem in a similar way as on *ix system.
 fsck => commands for checking the health of the filesystem
 
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

 - Open com.jayway.hadoop.demo.RowCounter.java
 
 Finish the mapper and reducer code so that it produces this output:
 
 notebooks.txt	95
 outline_of_science.txt	80
 ulysses.txt	73

 
Execute: bin/hadoop jar �{path.to}/hadoop-lab-1.0-SNAPSHOT.jar com.jayway.hadoop.demo.RowCounter /user/hduser/gutenberg /user/hduser/�{you}/lab1-out

NOTE: You cannot use the same result directory twice, remove it or use a new directory with i.e a number appended to dirname  
  
  
### 1.b) Extra assignment: Write a program that counts the occurrence of each word in the files.

Should produce something like this:

and		345
if		434
or		343 


### 2.a)  Write a program that outputs Logtype and count

 ERROR	678
 WARN	115150
 
Edit com.jayway.hadoop.ikealog.LogTypeCounter

There is a logfile that you should use under  /user/hduser/ikealogs

 bin/hadoop jar ${path.to}/hadoop-lab-1.0-SNAPSHOT.jar com.jayway.hadoop.ikealog.LogTypeCounter /user/hduser/ikealogs /user/hduser/{you}/ikealogs-out 
 
### 2.b) This assignment includes two MapReduce jobs;
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
	
	
### 2.c) Write a program that can be used to figure out the most problematic class from the logs, probably the one
generating the most errors.

