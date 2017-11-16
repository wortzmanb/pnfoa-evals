package pnfoa.util;

import com.opencsv.*;
import java.util.*;
import java.io.*;

public class CSVParser {
	private String[] fields;
//	private Scanner input;
	private CSVReader reader;
	private Iterator<String[]> iterator;
	
	// parses a CSV file
	// Assumes the first line contains a comma-separated list of field names, 
	//   and each subsequent line represents one record
	// Assumes no commas in field names or values
	public CSVParser(String fileName) throws FileNotFoundException {
		try {
			reader = new CSVReader(new FileReader(fileName));
			fields = reader.readNext();
			iterator = reader.iterator();
		} catch (FileNotFoundException e) {
			System.err.println("Error: file not found " + fileName);
			throw e;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public boolean hasNextRecord() {
		return iterator.hasNext();
	}
	
	public Map<String, String> nextRecord() {
		if (!hasNextRecord()) {
			return null;
		}
		
		String[] values;
		do {
			values = iterator.next();
		} while (values.length == 0 && hasNextRecord());
		
		if (values.length == 0)
			return null;
			
		if (values.length != fields.length) 
			throw new RuntimeException("Wrong number of values in a record.");

		Map<String, String> record = new HashMap<String, String>();
		for (int i = 0; i < values.length; i++) {
			record.put(fields[i], values[i]);
		}
		
		return record;
		/*
		String line;
		do {
			line = input.nextLine();
		} while (line.isEmpty() && input.hasNextLine());
		
		while (line.endsWith("<p>") || line.endsWith("<P>") || line.endsWith("<br>") || line.endsWith("<BR>")) {
			line += input.nextLine();
		}

		if (line.isEmpty() && !hasNextRecord()) {
			return null;
		}
		
		String[] values = line.split(",");
//		if (values.length != fields.length) throw new IllegalArgumentException("Wrong number of values in a record");
		
		Map<String, String> record = new HashMap<String, String>();
		for (int i = 0, valueIndex = 0; i < fields.length; i++, valueIndex++) {
			if (valueIndex < values.length)
				if (values[valueIndex].startsWith("\"")) {
					String value = "";
					while (!values[valueIndex].endsWith("\"")) {
						value += values[valueIndex] + ",";
						valueIndex++;
					}
					value += values[valueIndex];
					record.put(fields[i], value.substring(1, value.length() - 1));
				} else {
					record.put(fields[i], values[valueIndex]);
				}
			else
				record.put(fields[i], null);
		}
		
		return record;
		*/
	}
	
	public int getNumFields() { return fields.length; }
	public String[] getFields() { return fields; }
	
//	public int getNumRecords() { return records.size(); }
//	public List<Map<String, String>> getRecords() { return records; }
}
