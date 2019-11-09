package ch.ethz.netsec.fimeier.iptables.configuration;

import java.util.ArrayList;

import javax.json.JsonObject;
import javax.json.JsonValue;


public class Network {
	JsonObject networkJson;

	public ArrayList<Subnet> subnets = new ArrayList<Subnet>();
	public ArrayList<Link> links = new ArrayList<Link>();
	public ArrayList<Router> routers = new ArrayList<Router>();


	public class Subnet {
		public int id;
		public String address;
		public int prefix;

		//Relations
		public ArrayList<Link> subnetLinks = new ArrayList<>();
		

		Subnet(JsonObject subnet) {
			id = subnet.getInt("id");
			address = subnet.getString("address");
			prefix = subnet.getInt("prefix");
		}
	}

	public class Link {
		public int routerId;
		public String interfaceId;
		public String ip;
		public int subnetId;

		//Relations
		public Subnet subnet = null;
		public Router router = null;


		Link(JsonObject link) {
			routerId = link.getInt("routerId");
			interfaceId = link.getString("interfaceId");
			ip = link.getString("ip");
			subnetId = link.getInt("subnetId");
		}
	}

	public class Router {
		public int id;

		//Relations
		public ArrayList<Link> routerLinks = new ArrayList<>();
		
		Router(int _id) {
			id = _id;
		}
	}

	Network(JsonObject _networkJson) {
		networkJson = _networkJson;

		// get router id
		for (JsonValue routerID : networkJson.getJsonArray("routers")) {
			routers.add(new Router(routerID.asJsonObject().getInt("id")));
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