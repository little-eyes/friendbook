package proj.friendbook;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.mahout.classifier.sgd.CsvRecordFactory;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.VectorWritable;

import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class Csv2SeqMapper extends
		Mapper<LongWritable, Text, Text, VectorWritable> {
	private static final Splitter COMMA = Splitter.on(',').trimResults(CharMatcher.is(' '));

	private CsvRecordFactory csv;
	private String targetVariable;
	private String csvHeader;
	private String keyVariable;
	private Map<String, String> typeMap;
	private int maxTargetCategories;
	private int numFeatures;

	protected void map(LongWritable key, Text value,Context context) throws IOException, InterruptedException{
		Vector input = new RandomAccessSparseVector(numFeatures);
		String line = value.toString();
		csv.processLine(line, input);
		String keyValue = csv.getIdString(line);
		context.write(new Text(keyValue), new VectorWritable(input));
	}

	@Override
	protected void setup(Context context) throws IOException,
			InterruptedException {
		super.setup(context);
		Configuration conf = context.getConfiguration();
		targetVariable = conf.get("targetVariable");
		maxTargetCategories = Integer.valueOf(conf.get("maxTargetCategories")).intValue();
		numFeatures = Integer.valueOf(conf.get("numFeatures")).intValue();
		csvHeader = conf.get("csvHeader");
		keyVariable = conf.get("keyVariable");
		String predictores = conf.get("predictorListString");
		String types = conf.get("typeListString");
		List<String> typeList = Lists.newArrayList(COMMA.split(types));
		List<String> predictorList = Lists.newArrayList(COMMA.split(predictores));
		setTypeMap(predictorList, typeList);
		getCsvRecordFactory().firstLine(csvHeader);
	}

	
	private CsvRecordFactory getCsvRecordFactory() {
		if (csv == null) {
			csv = new CsvRecordFactory(getTargetVariable(), keyVariable,
					getTypeMap()).maxTargetValue(getMaxTargetCategories());
		}
		return csv;
	}

	/**
	 * Sets the types of the predictors. This will later be used when reading
	 * CSV data. If you don't use the CSV data and convert to vectors on your
	 * own, you don't need to call this.
	 * 
	 * @param predictorList
	 *            The list of variable names.
	 * @param typeList
	 *            The list of types in the format preferred by CsvRecordFactory.
	 */
	private void setTypeMap(Iterable<String> predictorList,
			List<String> typeList) {
		Preconditions.checkArgument(!typeList.isEmpty(),
				"Must have at least one type specifier");
		typeMap = Maps.newHashMap();
		Iterator<String> iTypes = typeList.iterator();
		String lastType = null;
		for (Object x : predictorList) {
			// type list can be short .. we just repeat last spec
			if (iTypes.hasNext()) {
				lastType = iTypes.next();
			}
			typeMap.put(x.toString(), lastType);
		}
	}

	private String getTargetVariable() {
		return targetVariable;
	}

	private Map<String, String> getTypeMap() {
		return typeMap;
	}

	private int getMaxTargetCategories() {
		return maxTargetCategories;
	}
}