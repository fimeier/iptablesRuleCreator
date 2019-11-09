package ch.ethz.netsec.fimeier.iptables.configuration;

import java.util.ArrayList;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;


public class Communications {
	JsonArray communicationsJson;

	public ArrayList<ComDetails> communications = new ArrayList<ComDetails>();

	public class ComDetails {
		int sourceSubnetId;
		int targetSubnetId;
		String protocol;
		int sourcePortStart;
		int sourcePortEnd;
		int targetPortStart;
		int targetPortEnd;
		String direction;

		ComDetails(JsonObject com) {
			sourceSubnetId = com.getInt("sourceSubnetId");
			targetSubnetId = com.getInt("targetSubnetId");
			protocol = com.getString("protocol");
			sourcePortStart = com.getInt("sourcePortStart");
			sourcePortEnd = com.getInt("sourcePortEnd");
			targetPortStart = com.getInt("targetPortStart");
			targetPortEnd = com.getInt("targetPortEnd");
			direction = com.getString("direction");
		}
	}

	Communications(JsonArray _communicationsJson) {
		communicationsJson = _communicationsJson;

		// get comDetails
		for (JsonValue com : communicationsJson) {
			communications.add(new ComDetails(com.asJsonObject()));
		}
	}

}