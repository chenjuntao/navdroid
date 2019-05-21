package cjt.navdroid;

import android.util.Log;
import com.cocoahero.android.geojson.Position;

import java.util.ArrayList;
import java.util.List;

public class CjtGraph {
    public List<CjtNode> nodes = new ArrayList<CjtNode>();
    public List<CjtEdge> edges = new ArrayList<CjtEdge>();

    public void addLine(List<Position> points){
        Integer[] nodeIds = new Integer[points.size()];
        for (int i=0;i<points.size();i++){
            Position p = points.get(i);
            nodeIds[i] = addNode(p.getLongitude(), p.getLatitude());

            if (i>0){
                addEdge(nodeIds[i-1], nodeIds[i]);
            }
        }
    }

    private Integer addNode(double lon, double lat){
        Integer nodeId = containsNode(lon, lat);
        if (nodeId.equals(-1)){
            nodeId = nodes.size();
            CjtNode newNode = new CjtNode(nodes.size(), lon, lat);
            nodes.add(newNode);
        }
        return nodeId;
    }

    private Integer containsNode(double lon, double lat){
        for (CjtNode cjtNode : nodes){
            if(cjtNode.lat == lat){
                if(cjtNode.lon == lon){
                    return cjtNode.nodeId;
                }
            }
        }
        return -1;
    }

    private void addEdge(Integer nodeId1, Integer nodeId2){
        CjtEdge newEdge = new CjtEdge(nodeId1, nodeId2);
        edges.add(newEdge);
    }
}
