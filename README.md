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

 -  Konfiguration

Open and add the stuff below to your conf/core-site.xml

```xml
<property> 
	<name>hadoop.tmp.dir</name> 
	<value>/app/hadoop/tmp</value> 
	<description>A base for other temporary directories.</description>
</property>

<property> 
	<name>hadoop.tmp.dir</name> 
	<value>/app/hadoop/tmp</value> 
	<description>A base for other temporary directories.</description> 
</property>

<property> 
	<name>fs.default.name</name> 
	<value>hdfs://master:54310</value> 
	<description>The name of the default file system. A URI whose scheme and authority determine 
	the FileSystem implementation. The uri's scheme determines the config property (fs.SCHEME.impl) 
	naming the FileSystem implementation class. The uri's authority is used to determine the host,
	port, etc. for a filesystem.</description> 
</property>

```

&lt;property&gt;
  &lt;name&gt;hadoop.tmp.dir&lt;/name&gt;
  &lt;value&gt;/app/hadoop/tmp&lt;/value&gt;
  &lt;description&gt;A base for other temporary directories.&lt;/description&gt;
&lt;/property&gt;

&lt;property&gt;
  &lt;name&gt;fs.default.name&lt;/name&gt;
  &lt;value&gt;hdfs://master:54310&lt;/value&gt;
  &lt;description&gt;The name of the default file system.  A URI whose
  scheme and authority determine the FileSystem implementation.  The
  uri's scheme determines the config property (fs.SCHEME.impl) naming
  the FileSystem implementation class.  The uri's authority is used to
  determine the host, port, etc. for a filesystem.&lt;/description&gt;
&lt;/property&gt;


Open and add the stuff below to your conf/hdfs-site.xml

&lt;property&gt;
  &lt;name&gt;dfs.replication&lt;/name&gt;
  &lt;value&gt;2&lt;/value&gt;
  &lt;description&gt;Default block replication.
  The actual number of replications can be specified when the file is created.
  The default is used if replication is not specified in create time.
  &lt;/description&gt;
&lt;/property&gt;
&lt;property&gt;
   &lt;name&gt;dfs.block.size&lt;/name&gt;
   &lt;value&gt;10240000&lt;/value&gt;
&lt;/property&gt;
&lt;property&gt;
   &lt;name&gt;dfs.http.address&lt;/name&gt;
   &lt;value&gt;master:50070&lt;/value&gt;
&lt;/property&gt;


Open and add the stuff below to your conf/mapred-site.xml

&lt;property&gt;
  &lt;name&gt;mapred.job.tracker&lt;/name&gt;
  &lt;value&gt;master:54311&lt;/value&gt;
  &lt;description&gt;The host and port that the MapReduce job tracker runs
  at.  If "local", then jobs are run in-process as a single map
  and reduce task.
  &lt;/description&gt;
&lt;/property&gt;

 - Edit .bashrc (.profile)

export HADOOP_HOME=/usr/local/hadoop

export JAVA_HOME=/usr/lib/jvm/java-6-sun (you should have done this already)

export PATH=$PATH:$HADOOP_HOME/bin

 - Rest of setup is Work In Progress. 

The final setup will be done during friday and saturday. If you are at the office and have done all
steps please come by me.


# All lab instructions are in progress
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