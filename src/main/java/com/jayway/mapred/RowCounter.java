package com.jayway.mapred;

import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * Very simple map/reduce program that counts the maximum length that a row can have for each file.
 * 
 * The result is:
 * 
 * rask:hadoop hduser$ bin/hadoop dfs -cat /Users/hduser/gutenberg-output13/part-r-00000
 * 2011-09-16 13:39:37.086 java[17816:1b03] Unable to load realm info from SCDynamicStore
 * notebooks.txt	95
 * ouline_of_science.txt	80
 * ulysses.txt	73
 * 
 * @author johanrask
 *
 */
public class RowCounter {

	public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
		
		Job job = new Job();
		job.setJarByClass(RowCounter.class);
		
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		job.setMapperClass(RowCountMapper.class);
		job.setReducerClass(RowCountReducer.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
	
	static class RowCountMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
		
		protected void map(LongWritable key, Text row, Mapper<LongWritable,Text,Text,IntWritable>.Context context) throws IOException ,InterruptedException {
			
			FileSplit split = (FileSplit)context.getInputSplit();
			int length = row.getLength();
			context.write(new Text(split.getPath().getName()),new IntWritable(length));
		};
		
	}
	

	static class RowCountReducer extends Reducer<Text, IntWritable, Text,IntWritable> {

		protected void reduce(Text fileName, Iterable<IntWritable> arg1, Reducer<Text,IntWritable,Text,IntWritable>.Context context) throws IOException ,InterruptedException {
			
			int maxValue = Integer.MIN_VALUE;
			
			for (IntWritable rowCount : arg1) {
				maxValue = Math.max(maxValue, rowCount.get());				
			}

			context.write(fileName, new IntWritable(maxValue));
		};
	
	}
}
