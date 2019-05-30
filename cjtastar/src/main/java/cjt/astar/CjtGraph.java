package cjt.astar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CjtGraph {
    public Map<String, CjtNode> nodes = new HashMap<String, CjtNode>();
    public List<CjtEdge> edges = new ArrayList<CjtEdge>();

    public void addNode(JSONObject node){
        try {
            String nodeId = node.getString("id");
            double lon = node.getDouble("lon");
            double lat = node.getDouble("lat");
            CjtNode newNode = new CjtNode(nodeId, lon, lat);
            nodes.put(newNode.nodeId ,newNode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addWay(JSONObject way){
        try {
            JSONArray nodes = way.getJSONArray("nodes");
            for (int i = 1; i < nodes.length(); i++) {
                String fromId = nodes.getString(i);
                String toId = nodes.getString(i-1);
                CjtEdge newEdge = new CjtEdge(fromId, toId);
                newEdge.distance = getDistance(this.nodes.get(fromId), this.nodes.get(toId));
                edges.add(newEdge);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public CjtNode getNearestNode(double lon, double lat){
        CjtNode nearestNode = null;
        double minDistance = 10000000;
        for (CjtNode cjtNode : nodes.values()){
            double dx = lon-cjtNode.lon;
            double dy = lat-cjtNode.lat;
            double distance = Math.sqrt(dx*dx+dy*dy);
            if (minDistance>distance){
                minDistance = distance;
                nearestNode = cjtNode;
            }
        }
        return nearestNode;
    }


    private double getDistance(CjtNode node1, CjtNode node2){
        double dx = node1.lon - node2.lon;
        double dy = node1.lat - node2.lat;

        return Math.sqrt(dx*dx + dy*dy);
    }
}
