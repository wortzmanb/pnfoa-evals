package pnfoa.util;

import java.util.*;
import java.io.*;

public class CSVReader_Test {

	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		Scanner kb = new Scanner(System.in);
		System.out.print("File to read? ");
		String fileName = kb.nextLine();
		
		Scanner input = new Scanner(new File(fileName));
		while (input.hasNextLine()) {
			String line = input.nextLine();
			System.out.println(line);
			String[] parts = line.split(",");
			System.out.println("   " + Arrays.toString(parts));
			kb.nextLine();
		}
	}

}
