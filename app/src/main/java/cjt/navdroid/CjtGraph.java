package cjt.navdroid;

import android.util.Log;
import com.cocoahero.android.geojson.Position;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CjtGraph {
    public List<CjtNode> nodes = new ArrayList<CjtNode>();
    public List<CjtEdge> edges = new ArrayList<CjtEdge>();

    public void addLine(List<Position> points){
        String[] nodeIds = new String[points.size()];
        for (int i=0;i<points.size();i++){
            Position p = points.get(i);
            nodeIds[i] = addNode(p.getLongitude(), p.getLatitude());

            if (i>0){
                addEdge(String.valueOf(nodeIds[i-1]), String.valueOf(nodeIds[i]));
            }
        }
    }

    private String addNode(double lon, double lat){
        String nodeId = containsNode(lon, lat);
        if (nodeId.equals("no")){
            nodeId = String.valueOf(nodes.size());
            CjtNode newNode = new CjtNode(String.valueOf(nodes.size()), lon, lat);
            nodes.add(newNode);
        }
        return nodeId;
    }

    private String containsNode(double lon, double lat){
        for (CjtNode cjtNode : nodes){
            if(cjtNode.lat == lat){
                if(cjtNode.lon == lon){
                    return cjtNode.nodeId;
                }
            }
        }
        return "no";
    }

    private void addEdge(String nodeId1, String nodeId2){
        CjtEdge newEdge = new CjtEdge(nodeId1, nodeId2);
        edges.add(newEdge);
    }

    //-------------------------------------


    public void addNode(JSONObject node){
        try {
            String nodeId = node.getString("id");
            double lon = node.getDouble("lon");
            double lat = node.getDouble("lat");
            CjtNode newNode = new CjtNode(nodeId, lon, lat);
            nodes.add(newNode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addWay(JSONObject way){
        try {
            JSONArray nodes = way.getJSONArray("nodes");
            for (int i = 1; i < nodes.length(); i++) {
                String fromId = nodes.getString(i-1);
                String toId = nodes.getString(i);
                CjtEdge newEdge = new CjtEdge(fromId, toId);
                edges.add(newEdge);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
