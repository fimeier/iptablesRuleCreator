package ch.ethz.netsec.fimeier.iptables.configuration;

import java.util.ArrayList;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import ch.ethz.netsec.fimeier.iptables.configuration.Network.Link;


public class Communication {
	JsonArray communicationsJson;

	public ArrayList<ComDetails> communications = new ArrayList<ComDetails>();

	public class ComDetails {
		public int sourceSubnetId;
		public int targetSubnetId;
		public String protocol;
		public int sourcePortStart;
		public int sourcePortEnd;
		public int targetPortStart;
		public int targetPortEnd;
		public String direction;

		//Relations
		public ArrayList<Link> path = new ArrayList<>();


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

	Communication(JsonArray _communicationsJson) {
		communicationsJson = _communicationsJson;

		// get comDetails
		for (JsonValue com : communicationsJson) {
			communications.add(new ComDetails(com.asJsonObject()));
		}
	}

}