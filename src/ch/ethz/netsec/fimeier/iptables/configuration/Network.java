package ch.ethz.netsec.fimeier.iptables.configuration;

import java.util.ArrayList;

import javax.json.JsonObject;
import javax.json.JsonValue;


public class Network {
	JsonObject networkJson;

	ArrayList<Integer> routers = new ArrayList<Integer>();
	ArrayList<Subnet> subnets = new ArrayList<Subnet>();
	ArrayList<Link> links = new ArrayList<Link>();

	public class Subnet {
		int id;
		String address;
		int prefix;

		Subnet(JsonObject subnet) {
			id = subnet.getInt("id");
			address = subnet.getString("address");
			prefix = subnet.getInt("prefix");
		}
	}

	public class Link {
		int routerId;
		String interfaceId;
		String ip;
		int subnetId;

		Link(JsonObject link) {
			routerId = link.getInt("routerId");
			interfaceId = link.getString("interfaceId");
			ip = link.getString("ip");
			subnetId = link.getInt("subnetId");
		}
	}

	Network(JsonObject _networkJson) {
		networkJson = _networkJson;

		// get router id
		for (JsonValue routerID : networkJson.getJsonArray("routers")) {
			routers.add(routerID.asJsonObject().getInt("id"));
		}

		// get subnets
		for (JsonValue subnet : networkJson.getJsonArray("subnets")) {
			subnets.add(new Subnet(subnet.asJsonObject()));
		}

		// get links
		for (JsonValue link : networkJson.getJsonArray("links")) {
			links.add(new Link(link.asJsonObject()));
		}

	}
}