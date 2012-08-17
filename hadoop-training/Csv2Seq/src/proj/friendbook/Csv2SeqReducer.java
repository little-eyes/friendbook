package proj.friendbook;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.mahout.math.VectorWritable;

public class Csv2SeqReducer extends
		Reducer<Text, VectorWritable, Text, VectorWritable> {
	@Override
	protected void reduce(Text key, Iterable<VectorWritable> values,
			Context context) throws IOException, InterruptedException {
		for (VectorWritable value : values) {
			context.write(key, value);
		}
	}

}
