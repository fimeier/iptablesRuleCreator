package ch.ethz.netsec.fimeier.iptables;

public class runIPTABLES {
	
	public static String inputDir = "inputs/";
	public static String outputDir = "outputs/";

	
	
	
	private static void parseArguments(String[] args) {
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


	public static void main(String[] args) {


		System.out.println("Starting IPTABLES-Project....");
		parseArguments(args);
	}

}
