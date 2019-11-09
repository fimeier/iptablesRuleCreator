package ch.ethz.netsec.fimeier.iptables;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.Reader;
import java.util.ArrayList;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import ch.ethz.netsec.fimeier.iptables.configuration.IPTableConfiguration;

public class runIPTABLES {

	public static String inputDir = "inputs/";
	public static String outputDir = "outputs/";
	
	public static ArrayList<JsonObject> allTestCasesJson = new ArrayList<JsonObject>();

	public static ArrayList<IPTableConfiguration> configIPTable = new ArrayList<IPTableConfiguration>();




	static void parseArguments(String[] args) {
		int i = 0;
		for (String arg: args) {

			if (i%2==0) {
				switch(arg) {
				case "-i":{
					inputDir = args[i+1];
					System.out.println("inputDir: "+inputDir);
					break;
				}
				case "-o":{
					outputDir = args[i+1];
					System.out.println("outputDir: "+outputDir);
					break;
				}

				default: {
					System.out.println("ERROR: Unknown argument given to runACME.main(), namely: " + arg);
				}
				}
			}
			i++;
		}
	}

	static FilenameFilter getFilter(String startsWith, String[] contains) {
		return new FilenameFilter() {

			public boolean accept(File dir, String fileName) {

				boolean result = false;

				if (startsWith != null){
					if (fileName.startsWith(startsWith)) {
						result = true;
					} else {
						return false; //ends here
					}
				}

				if (contains != null){
					for (String cont: contains) {

						if (fileName.contains(cont)) {
							result = true;
						} else {
							return false; //ends here
						}
					}
				}
				return result;
			}
		};
	}

	public static ArrayList<String> getFilesByFilter(final File folder, String startsWith, String[] contains) {

		ArrayList<String> filesFound = new ArrayList<>();

		File[] filesForAggregation = folder.listFiles(getFilter(startsWith, contains));


		for (final File fileEntry: filesForAggregation) {
			if (fileEntry.isDirectory()) {
				//listFilesForFolder(fileEntry);
				System.out.println("ERROR: I am a directory: "+fileEntry.getAbsoluteFile().toString());
			} else {
				System.out.println("Testcase: "+ fileEntry.getName() + " " +fileEntry.getAbsolutePath());

				filesFound.add(fileEntry.getAbsolutePath());
			}
		}
		return filesFound;
	}


	private static void getTestcases() {

		File folderContainingInputData = new File(inputDir);
		String contains[] = {".json"};
		ArrayList<String> allTestCases = getFilesByFilter(folderContainingInputData,null,contains);

		for(String testCase: allTestCases) {
			Reader fileReader = null;
			try {
				fileReader = new BufferedReader(new FileReader(testCase));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			JsonReader jsonReader = Json.createReader(fileReader);
			JsonObject testCaseJson = jsonReader.readObject();
			allTestCasesJson.add(testCaseJson);
			configIPTable.add(new IPTableConfiguration(testCase, testCaseJson));
		}
	}



	
	public static void main(String[] args) {

		System.out.println("Starting IPTABLES-Project....");

		parseArguments(args);

		getTestcases();
		

	}

}
