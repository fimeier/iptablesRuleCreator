package ch.ethz.netsec.fimeier.iptables;

import ch.ethz.netsec.fimeier.iptables.configuration.Communication;
import ch.ethz.netsec.fimeier.iptables.configuration.IPTableConfiguration;
import ch.ethz.netsec.fimeier.iptables.configuration.Network;
import ch.ethz.netsec.fimeier.iptables.configuration.Communication.ComDetails;
import ch.ethz.netsec.fimeier.iptables.configuration.Network.Subnet;
import ch.ethz.netsec.fimeier.iptables.configuration.Network.Link;
import ch.ethz.netsec.fimeier.iptables.configuration.Network.Router;
import ch.ethz.netsec.fimeier.iptables.helper.Pair;
import ch.ethz.netsec.fimeier.iptables.helper.Tripple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class IPTableRules {
    public ArrayList<Tripple<String, String, String>> rules = new ArrayList<>();

    // some shortcuts
    Communication comm;
    Network net;
    ArrayList<Router> routers;
    ArrayList<Subnet> subnets;
    ArrayList<Link> links;
    ArrayList<ComDetails> communications;
    String testID;

    // Indices
    HashMap<Integer, Subnet> subnetIndex = new HashMap<>();
    HashMap<Integer, Router> routerIndex = new HashMap<>();

    IPTableRules(IPTableConfiguration conf) {
        // some shortcuts
        comm = conf.comm;
        net = conf.net;
        routers = net.routers;
        subnets = net.subnets;
        links = net.links;
        communications = comm.communications;

        String testID = ((Integer) conf.testID).toString();

        createRelations();

        createIndices();

        findPath();

        //debugPrintPath();

        collectAllRules();

        for (Router r : routers) {
            String routerID = ((Integer) r.id).toString();

            String rule = "* nat" + "\n"
            +":OUTPUT ACCEPT [0:0]"+ "\n"
            +":PREROUTING ACCEPT [0:0]"+ "\n"
            +":POSTROUTING ACCEPT [0:0]"+ "\n"+ "\n"
            
            +"COMMIT"+ "\n"+ "\n"
            
            +"* filter"+ "\n"
            +":INPUT DROP [0:0]"+ "\n"
            +":OUTPUT DROP [0:0]"+ "\n"
            +":FORWARD DROP [0:0]"+ "\n"

            + r.rules+ "\n"
            + "COMMIT" + "\n";
            rules.add(new Tripple<String, String, String>(testID, routerID, rule));
        }
    }

    

    private void collectAllRules() {
        HashMap<Router,String> routerRules = new HashMap<>();
        for (ComDetails c : communications){
            for(Pair<Router,String> routerRule: c.getRules()){
                Router r = routerRule.getKey();
                String newRule = routerRule.getValue1();
                String existingRules = "";
                if (routerRules.containsKey(r)){
                    existingRules = routerRules.get(r);

                }
                existingRules += newRule;
                routerRules.put(r, existingRules); 
            }
        }

        for (Router r: routerRules.keySet()){
            r.rules = routerRules.get(r);
        }

    }

    private void debugPrintPath() {
        for (ComDetails c : communications){
            int pathLength = 0;
            String output = "Net "+c.path.get(0).subnetId;
            for (Link l: c.path){
                pathLength++;
                boolean nextIsSubnet = (pathLength%2 == 0) ? true: false;
                if (nextIsSubnet){
                    output += " -->Net "+l.subnetId;
                }
                if (!nextIsSubnet){
                    output += " -->Router "+l.routerId;
                }

            }
            System.out.println("**************************************");
            System.out.println("sourceSubnetId"+ c.sourceSubnetId +"\n"
                        +"targetSubnetId"+ c.targetSubnetId +"\n"
           + "protocol"+c.protocol +"\n"
            +"sourcePortStart"+c.sourcePortStart +"\n"
           + "sourcePortEnd"+c.sourcePortEnd +"\n"
           + "targetPortStart"+c.targetPortStart +"\n"
            +"targetPortEnd"+c.targetPortEnd +"\n"
           + "direction"+c.direction +"\n\n"
           + output);

            System.out.println("**************************************");

        }
    }

    private void createIndices() {

        // index for subnets
        for (Subnet s : subnets) {
            subnetIndex.put(s.id, s);
        }

        // index for routers
        for (Router r : routers) {
            routerIndex.put(r.id, r);
        }

    }

    private void findPath() {

        for (ComDetails c : communications) {
            // store all paths until correct has been found
            ArrayList<ArrayList<Link>> candidatePaths = new ArrayList<>();

            int srcNet = c.sourceSubnetId;
            Subnet srcNetObject = subnetIndex.get(srcNet);
            int targetNet = c.targetSubnetId;
            Subnet targetNetObject = subnetIndex.get(targetNet);

            for (Link l : srcNetObject.subnetLinks) {
                ArrayList<Link> path = new ArrayList<Link>();
                path.add(l);
                candidatePaths.add(path);
            }

            HashSet<Subnet> checkedSubnets = new HashSet<>();
            HashSet<Router> checkedRouters = new HashSet<>();
            boolean pathHasBeenFound = false;
            int pathLength = 0;
            ArrayList<ArrayList<Link>> candidatePathsNextRun = candidatePaths;
            while (!pathHasBeenFound) {
                candidatePaths = new ArrayList<>(candidatePathsNextRun);
                candidatePathsNextRun = new ArrayList<>();

                pathLength++;
                //pathLengt % 2 == 0 -> next is Subnet
                boolean nextIsSubnet = (pathLength%2 == 0) ? true: false;


                for (ArrayList<Link> path : candidatePaths) {
                    Link lastLink = path.get(path.size()-1);
                    //do subnet things
                    if (nextIsSubnet){
                        Subnet s = lastLink.subnet;
                        //if the Subnet has already been checked do not check it again
                        //this will also "remove" the path from the candidates
                        if (!checkedSubnets.add(s))
                            continue;
                        for (Link l: s.subnetLinks){
                            ArrayList<Link> newPath = new ArrayList<>(path);
                            newPath.add(l);
                            candidatePathsNextRun.add(newPath);
                        }
                        

                    }

                    //do router things
                    if (!nextIsSubnet && !pathHasBeenFound) {
                        Router r = lastLink.router;
                        //if the router has already been checked do not check it again
                        //this will also "remove" the path from the candidates
                        if (!checkedRouters.add(r))
                            continue;
                        for (Link l: r.routerLinks){
                            ArrayList<Link> newPath = new ArrayList<>(path);
                            newPath.add(l);
                            if (l.subnet.equals(targetNetObject)){
                                pathHasBeenFound = true;
                                //store the correct path into the ComDetails object
                                c.path = newPath;
                                break; 
                            }
                            candidatePathsNextRun.add(newPath);
                        }
                    }

                }
            }

          
        }

    }
    /*
     * private void findPath() { for (ComDetails c : communications) { int srcNet =
     * c.sourceSubnetId; Subnet srcNetObject = subnetIndex.get(srcNet); int
     * targetNet = c.targetSubnetId; Subnet targetObject =
     * subnetIndex.get(targetNet);
     * 
     * ArrayList<Link> path = new ArrayList<>(); HashMap<Router, Tripple<Link, Link,
     * Boolean>> linkLinkRouter = new HashMap<>(); /* erster Teil geht nicht... ich
     * weiss ja nicht welchen Link ich nehmen soll vom subnet!!!
     * 
     * 1 subnet => link2 => router
     * 
     * tree <=> unique path, no diamonds,...
     * 
     * 0.a) add srcLink
     * 
     * 0.b) add srcRouter(set fromLink=srcLink)
     * 
     * 0.c) is subnet reachable? => "end"
     * 
     * Iter: for all r: routersOnPath
     * 
     * 1. check all routerLinks execept for fromLink
     * 
     * a) routerLinks -> targetNet? => "end"
     * 
     * b) for all link1: routerLinks
     * 
     * i) get subnetId => get subnetLinks == link2 => get "new Routers"
     * 
     * 
     * Store: HashMap<routerId, Pair<link1, link2>>
     * 
     * n. "add targetLink"
     */

    /*
     * boolean firstRun = true; boolean pathFound = false; ArrayList<Link>
     * linksToCheck; ArrayList<Subnet> subnetsToCheck;
     * 
     * ArrayList<Link> nextRunLinks = srcNetObject.subnetLinks; ArrayList<Subnet>
     * nextRunSubnets = new ArrayList<>(); nextRunSubnets.add(srcNetObject);
     * 
     * HashSet<Link> checkedLinks = new HashSet<>();
     * 
     * while (!pathFound) { //linksToCheck = nextRunLinks; //nextRunLinks = new
     * ArrayList<>();
     * 
     * subnetsToCheck = nextRunSubnets; nextRunSubnets = new ArrayList<>();
     * 
     * for (Subnet s : nextRunSubnets) { for (Link l : s.subnetLinks) { if
     * (checkedLinks.contains(l)) continue; checkedLinks.add(l); Router r =
     * l.router; Link link2 = subnetLink; Tripple<Link, Link, Boolean> pathPart =
     * new Tripple<Network.Link, Network.Link, Boolean>(null, link2, true);
     * linkLinkRouter.put(r, pathPart); } } firstRun = false; }
     * 
     * } }
     */

    private void createRelations() {

        // create routerLink
        for (Router r : routers) {
            ArrayList<Link> routerLinks = new ArrayList<>();
            for (Link l : links) {
                if (l.routerId == r.id) {
                    l.router = r;
                    routerLinks.add(l);
                }
            }
            r.routerLinks = routerLinks;
        }

        // create subnetLink
        for (Subnet s : subnets) {
            ArrayList<Link> subnetLinks = new ArrayList<>();
            for (Link l : links) {
                if (l.subnetId == s.id) {
                    l.subnet = s;
                    subnetLinks.add(l);
                }
            }
            s.subnetLinks = subnetLinks;
        }
    }

}