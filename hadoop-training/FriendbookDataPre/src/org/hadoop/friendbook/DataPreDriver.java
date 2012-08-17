package org.hadoop.friendbook;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.mapred.JobClient;
import org.apache.mahout.math.SequentialAccessSparseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.VectorIterable;
import org.apache.mahout.math.VectorWritable;

public class DataPreDriver {

	/*
	 * @para input-path
	 * @para output-path
	 * @para number of columns
	 * */
	
	public static void main(String[] args) {
		int ncol = Integer.parseInt(args[2]);
		String inPath = args[0];
		String outPath = args[1];
		Configuration conf = new Configuration();
		try {
			FileSystem fs = FileSystem.get(conf);
			SequenceFile.Writer writer = SequenceFile.createWriter(
				fs, conf, new Path(outPath), IntWritable.class, 
				VectorWritable.class, CompressionType.BLOCK);

			IntWritable key = new IntWritable();
			VectorWritable value = new VectorWritable();
			String line = null;
			BufferedReader br = new BufferedReader(new FileReader(inPath));
			Vector vector = new SequentialAccessSparseVector(ncol);
			int id = 0;
			while ((line = br.readLine()) != null) {
				key.set(id);
				StringTokenizer st = new StringTokenizer(line, "\t");
				int pos = 0;
				while (st.hasMoreTokens()) {
					vector.setQuick(pos++, Double.parseDouble(st.nextToken()));
				}
				value.set(vector);
				writer.append(key, value);
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
