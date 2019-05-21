package cjt.navdroid;

public class CjtNode {
    public Integer nodeId;
    public double lon;
    public double lat;

    public CjtNode(Integer nid, double lon, double lat){
        this.nodeId = nid;
        this.lon = lon;
        this.lat = lat;
    }
}
