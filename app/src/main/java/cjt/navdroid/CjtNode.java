package cjt.navdroid;

public class CjtNode {
    public String nodeId;
    public double lon;
    public double lat;

    public CjtNode(String nid, double lon, double lat){
        this.nodeId = nid;
        this.lon = lon;
        this.lat = lat;
    }
}
