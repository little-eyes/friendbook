package proj.friendbook;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.util.List;

import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.Option;
import org.apache.commons.cli2.builder.ArgumentBuilder;
import org.apache.commons.cli2.builder.DefaultOptionBuilder;
import org.apache.commons.cli2.builder.GroupBuilder;
import org.apache.commons.cli2.commandline.Parser;
import org.apache.commons.cli2.util.HelpFormatter;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.mahout.common.HadoopUtil;
import org.apache.mahout.math.VectorWritable;

import com.google.common.collect.Lists;

public class Csv2Seq {
	private static String inputPath;
	private static String outputPath;
	private static String targetVariable;
	private static String csvHeaderPath;
	private static String csvHeader;
	private static String keyVariable;
	private static String predictorListString;
	private static String typeListString;
	private static int	  maxTargetCategories;
	private static int 	  numFeatures;

	public static void main(String[] args) {
		if (!parseArgs(args))
			return;

		InputStream in = null;
		try {
			in = new FileInputStream(csvHeaderPath);
			csvHeader = Csv2Seq.convertStreamToString(in);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeStream(in);
		}
		
		try {
			runJob();
		} catch (IOException e) {			
			e.printStackTrace();
		} catch (InterruptedException e) {			
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void runJob( ) throws IOException, InterruptedException, ClassNotFoundException {


		Configuration conf = new Configuration();
		conf.set("targetVariable", targetVariable);
		conf.set("csvHeader", csvHeader);
		conf.set("keyVariable", keyVariable == null ? targetVariable : keyVariable);
		conf.set("predictorListString", predictorListString);
		conf.set("typeListString", typeListString);
		conf.set("maxTargetCategories", String.valueOf(maxTargetCategories));
		conf.set("numFeatures", String.valueOf(numFeatures));
		
		Job job = new Job(conf);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(VectorWritable.class);
		FileInputFormat.setInputPaths(job, new Path(inputPath));
		Path outPath = new Path(outputPath);
		FileOutputFormat.setOutputPath(job, outPath);
		
		job.setMapperClass(Csv2SeqMapper.class);
		job.setInputFormatClass(TextInputFormat.class);
		job.setReducerClass(Csv2SeqReducer.class);
		job.setOutputFormatClass(SequenceFileOutputFormat.class);
		job.setJarByClass(Csv2Seq.class);
		
		HadoopUtil.delete(conf, outPath);		
		job.waitForCompletion(true);
		
	}
	
	private static boolean parseArgs(String[] args) {
	    DefaultOptionBuilder builder = new DefaultOptionBuilder();

	    Option help = builder.withLongName("help")
	        .withDescription("print this list").create();

	    Option quiet = builder.withLongName("quiet")
	        .withDescription("be extra quiet").create();
	    
	   
	    ArgumentBuilder argumentBuilder = new ArgumentBuilder();
	    

	    Option inputFile = builder
	        .withLongName("input")
	        .withRequired(true)
	        .withArgument(
	            argumentBuilder.withName("input").withMaximum(1)
	                .create())
	        .withDescription("the HDFS path of the csv files to convert").create();

	    Option outputFile = builder
	        .withLongName("output")
	        .withRequired(true)
	        .withArgument(
	            argumentBuilder.withName("output").withMaximum(1)
	                .create())
	        .withDescription("the target HDFS directory to write the target sequence file ").create();
	    
	    Option target = builder
        .withLongName("target")
        .withDescription("the name of the target/label variable/column")    
        .withArgument(
            argumentBuilder.withName("target").withMaximum(1)
                .create())
         .create();

	    Option header = builder.withLongName("header")
	        .withArgument(
	            argumentBuilder.withName("header").withMaximum(1).create())
	        .withDescription("ninga local file path contai the csv header content")
	        .create();
	    
	    Option key = builder
    	.withLongName("key")
    	.withDescription("the name of key variable/column")
    	.withArgument(
    		argumentBuilder.withName("key").withMaximum(1).create()).create();
    
	    
	    Option predictors = builder.withLongName("predictors")
	        .withRequired(true)
	        .withArgument(argumentBuilder.withName("predictors").create())
	        .withDescription("a list of predictor variables").create();

	    Option types = builder
	        .withLongName("types")
	        .withRequired(true)
	        .withArgument(argumentBuilder.withName("types").create())
	        .withDescription(
	            "a list of predictor variable types (numeric, word, or text)")
	        .create();
	   
	    Option targetCategories = builder
	    .withLongName("categories")
	    .withDescription("the number of target categories to be considered")
	    .withArgument(
	        argumentBuilder.withName("categories").withMaximum(1)
	            .create())        
	    .create();

	    Option features = builder
	        .withLongName("features")
	        .withDescription("the number of internal hashed features to use")
	        .withArgument(
	            argumentBuilder.withName("features")
	                .withDefault("1000").withMaximum(1).create())        
	        .create();

	    Group normalArgs = new GroupBuilder().withOption(help).withOption(quiet)
	    	.withOption(inputFile).withOption(outputFile)
	        .withOption(target).withOption(header).withOption(key)	        
	        .withOption(predictors).withOption(types)
	        .withOption(targetCategories).withOption(features)
	        .create();

	    Parser parser = new Parser();
	    parser.setHelpOption(help);
	    parser.setHelpTrigger("--help");
	    parser.setGroup(normalArgs);
	    parser.setHelpFormatter(new HelpFormatter(" ", "", " ", 130));
	    CommandLine cmdLine = parser.parseAndHelp(args);

	    if (cmdLine == null) {
	      return false;
	    }

	    List<String> typeList = Lists.newArrayList();
	    for (Object x : cmdLine.getValues(types)) {
	      typeList.add(x.toString());
	    }

	    List<String> predictorList = Lists.newArrayList();
	    for (Object x : cmdLine.getValues(predictors)) {
	      predictorList.add(x.toString());
	    }
	    
	    inputPath = getStringArgument(cmdLine, inputFile);
	    outputPath = getStringArgument(cmdLine, outputFile);
	    targetVariable = getStringArgument(cmdLine, target);
	    csvHeaderPath = getStringArgument(cmdLine, header);
	    keyVariable = getStringArgument(cmdLine, key);
	    
	    predictorListString = predictorList.toString();
	    predictorListString = predictorListString.substring(1, predictorListString.length() - 1);
	    
	    typeListString = typeList.toString();
	    typeListString = typeListString.substring(1, typeListString.length() - 1);   

	    maxTargetCategories = getIntegerArgument(cmdLine, targetCategories);
	    numFeatures = getIntegerArgument(cmdLine, features);
	    
	    return true;
	  }

	private static String getStringArgument(CommandLine cmdLine,
			Option inputFile) {
		return (String) cmdLine.getValue(inputFile);
	}

	private static int getIntegerArgument(CommandLine cmdLine, Option features) {
		return Integer.parseInt((String) cmdLine.getValue(features));
	}

	public static String convertStreamToString(InputStream is)
	throws IOException {
		if (is != null) {
			Writer writer = new StringWriter();
			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(
				new InputStreamReader(is, "UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} finally {
				is.close();
			}
			return writer.toString();
		} else {
			return "";
		}
	}
}
