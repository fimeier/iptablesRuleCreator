package ch.ethz.netsec.fimeier.iptables.configuration;

import javax.json.JsonObject;

public class IPTableConfiguration {
	String testName = "";
	JsonObject testCaseJson = null;

	int testID = 0;
	Communications comm;
	Network net;

	public IPTableConfiguration(String _testName, JsonObject _testCaseJson) {
		testName = _testName;
		testCaseJson = _testCaseJson;

		int l = testName.split("/").length;
		testID = Integer.parseInt(testName.split("/")[l - 1].split("\\.")[0]);

		// get network part
		net = new Network(testCaseJson.getJsonObject("network"));

		// get network part
		comm = new Communications(testCaseJson.getJsonArray("communications"));

		return;
	}

}
