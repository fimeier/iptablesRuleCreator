package ch.ethz.netsec.fimeier.iptables.configuration;

import java.util.ArrayList;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import ch.ethz.netsec.fimeier.iptables.configuration.Network.Link;
import ch.ethz.netsec.fimeier.iptables.configuration.Network.Router;
import ch.ethz.netsec.fimeier.iptables.configuration.Network.Subnet;
import ch.ethz.netsec.fimeier.iptables.helper.Pair;

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

		// Relations
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

		/*
		 * 
		 * Überlege ob Links korrekt....
		 * 
		 * 
		 */
		public ArrayList<Pair<Router, String>> getRules() {
			ArrayList<Pair<Router, String>> result = new ArrayList<>();
			for (int i = 0; i < path.size(); i++) {
				String rulesForThisRouter = "";
				Link l = path.get(i);
				Subnet s = l.subnet;
				Router r = l.router;
				// pathLength++;
				boolean nextIsSubnet = (i % 2 == 1) ? true : false;

				// subnet stuff
				if (nextIsSubnet) {
					continue;
				}

				// router stuff
				if (!nextIsSubnet) {
					Link nextLink = path.get(i + 1);
					Subnet nextSubnet = nextLink.subnet;

					// TODO was ist mit unidirectional
					String state = ((direction.equals("bidirectional") || direction.equals("unidirectional"))
							? "NEW,ESTABLISHED"
							: "ESTABLISHED");

					String rule = "-A FORWARD";
					rule += " -p " + protocol;
					rule += (protocol.equals("icmp")) ? " --icmp-type any" : "";
					rule += (protocol.equals("icmp")) ? "" : " --sport " + sourcePortStart + ":" + sourcePortEnd;
					rule += (protocol.equals("icmp")) ? "" : " --dport " + targetPortStart + ":" + targetPortEnd;
					rule += " -s " + s.ipAndPrefix + " -d " + nextSubnet.ipAndPrefix;
					rule += " -i " + l.interfaceId + " -o " + nextLink.interfaceId;
					rule += " -m state" + " --state " + state + " -j ACCEPT" + "\n";

					rulesForThisRouter += rule;

					// "add inverse rule"
					if (direction.equals("bidirectional")) {
						rule = "-A FORWARD";
						rule += " -p " + protocol;
						rule += (protocol.equals("icmp")) ? " --icmp-type any" : "";
						rule += (protocol.equals("icmp")) ? "" : " --sport " + targetPortStart + ":" + targetPortEnd;
						rule += (protocol.equals("icmp")) ? "" : " --dport " + sourcePortStart + ":" + sourcePortEnd;
						rule += " -s " + nextSubnet.ipAndPrefix + " -d " + s.ipAndPrefix;
						rule += " -i " + nextLink.interfaceId + " -o " + l.interfaceId;
						rule += " -m state" + " --state ";
						rule += (protocol.equals("icmp")) ? "NEW,ESTABLISHED" : "ESTABLISHED";
						rule += " -j ACCEPT" + "\n";

						rulesForThisRouter += rule;

					}

				}

				result.add(new Pair<Router, String>(r, rulesForThisRouter));
			}

			return result;
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
