package org.openflamingo.hadoop.filter;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * Mapper for Filter
 * EQ, NOT EQ, GT, LT, GTE, LTE, EMPTY,
 * NOT EMPTY, START, END 등등 지원
 *
 * @author Hyunje
 */
public class FilterMapper extends Mapper<LongWritable, Text, NullWritable, Text> {
	String inDelimiter;
	String outDelimiter;
	String targetColumn;

	String commandName;
	String commandValue;

	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		Configuration configuration = context.getConfiguration();

		inDelimiter = configuration.get("indelimiter");
		outDelimiter = configuration.get("outdelimiter");
		targetColumn = configuration.get("targetColumn");

		//변수명의 규칙에 대한 고민
		commandName = configuration.get("commandName");
		commandValue = configuration.get("value");

		//hash map을 이용해서 한번에 하나씩 꺼내는 방식으로.
		//Filter의 핵심은 확장성.
	}


	@Override
	protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		String[] line = value.toString().split(inDelimiter); //
		int target = Integer.parseInt(targetColumn); //
		String outputLine = "";

		/*
			연산자의 종류에 따라서 interface 뽑는다.
		 */

		if (commandName.equals("EMPTY".toLowerCase())) {
			if (line[target].isEmpty())
				outputLine = value.toString();
		} else if (commandName.equals("NEMPTY".toLowerCase())) {
			if (!line[target].isEmpty())
				outputLine = value.toString();
		} else if (commandName.equals("EQ".toLowerCase())) {
			if (line[target].equals(commandValue))
				outputLine = value.toString();
		} else if (commandName.equals("NEQ".toLowerCase())) {
			if (!line[target].equals(commandValue))
				outputLine = value.toString();
		} else if (commandName.equals("GT".toLowerCase())) {
			if (Double.parseDouble(line[target]) > Double.parseDouble(commandValue))
				outputLine = value.toString();
		} else if (commandName.equals("LT".toLowerCase())) {
			if (Double.parseDouble(line[target]) < Double.parseDouble(commandValue))
				outputLine = value.toString();
		} else if (commandName.equals("GTE".toLowerCase())) {
			if (Double.parseDouble(line[target]) >= Double.parseDouble(commandValue))
				outputLine = value.toString();
		} else if (commandName.equals("LTE".toLowerCase())) {
			if (Double.parseDouble(line[target]) <= Double.parseDouble(commandValue))
				outputLine = value.toString();
		} else if (commandName.equals("START".toLowerCase())) {
			if (line[target].endsWith(commandValue))
				outputLine = value.toString();
		} else if (commandName.equals("END".toLowerCase())) {
			if (line[target].endsWith(commandValue))
				outputLine = value.toString();
		}
		if (outputLine.length() > 0)
			context.write(NullWritable.get(), new Text(outputLine));
	}
}
