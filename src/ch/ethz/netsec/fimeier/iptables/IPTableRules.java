package ch.ethz.netsec.fimeier.iptables;

import ch.ethz.netsec.fimeier.iptables.configuration.Communications;
import ch.ethz.netsec.fimeier.iptables.configuration.IPTableConfiguration;
import ch.ethz.netsec.fimeier.iptables.configuration.Network;
import ch.ethz.netsec.fimeier.iptables.configuration.Communications.ComDetails;
import ch.ethz.netsec.fimeier.iptables.configuration.Network.Link;
import ch.ethz.netsec.fimeier.iptables.configuration.Network.Subnet;
import ch.ethz.netsec.fimeier.iptables.helper.Tripple;

import java.util.ArrayList;


public class IPTableRules {
    public ArrayList<Tripple<String,String,String>> rules = new ArrayList<>();
    


	IPTableRules(IPTableConfiguration conf) {
        //some shortcuts
        Communications comm = conf.comm;
        Network net = conf.net;
        ArrayList<Integer> routers = net.routers;
	    ArrayList<Subnet> subnets = net.subnets;
	    ArrayList<Link> links = net.links;
        ArrayList<ComDetails> comDetails = comm.communications;
      
        

        String testID = ((Integer)conf.testID).toString();
        
        for (Integer routerId: routers){
            String rule = "blubs";
            rules.add(new Tripple<String,String,String>(testID, routerId.toString(), rule));
        }

	}
}