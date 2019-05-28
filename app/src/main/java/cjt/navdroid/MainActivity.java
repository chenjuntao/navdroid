package cjt.navdroid;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.*;
import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Object;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.*;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    V8 runtime = null;
    MapView map = null;
    public ProgressBar progress = null;
    public Button btnLoad = null;
    public Button btnNav = null;
    public TextView txtMsg = null;

    public CjtGraph cjtGraph = new CjtGraph();

    private boolean isFromFocus, isToFocus;
    private ItemizedIconOverlay fromIdOverlay, toIdOverlay;

    private Polyline linePath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        runtime = V8.createV8Runtime();//创建 可放在onCreate()当中


        //handle permissions first, before map is created. not depicted here

        //load/initialize the osmdroid configuration, this can be done
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's tile servers will get you banned based on this string

        //inflate and create the map
        setContentView(R.layout.activity_main);

        final EditText txtFromId = findViewById(R.id.txtFromId);
        setFromToIdEvents(txtFromId, true);
        final EditText txtToId = findViewById(R.id.txtToId);
        setFromToIdEvents(txtToId, false);

        //获得界面布局里面的进度条组件
        progress =  findViewById(R.id.progress);
        btnLoad = findViewById(R.id.btnLoad);
        btnNav = findViewById(R.id.btnNav);
        txtMsg = findViewById(R.id.txtMsg);

        findViewById(R.id.btn1).setOnClickListener(onClickListener1);
        findViewById(R.id.btnLoad).setOnClickListener(onClickListenerLoad);
        findViewById(R.id.btnNav).setOnClickListener(onClickListenerNav);

        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        final MapEventsReceiver mReceive = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {

                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                ArrayList<OverlayItem> overlayItems = new ArrayList<OverlayItem>();
                CjtNode nearestNode = cjtGraph.getNearestNode(p.getLongitude(), p.getLatitude());
                GeoPoint nearestPoint = new GeoPoint(nearestNode.lat, nearestNode.lon);
                if(isFromFocus) {
                    OverlayItem fromItem = new OverlayItem("fromId", "fromId", nearestPoint);
                    overlayItems.add(fromItem);
                    if(map.getOverlays().contains(fromIdOverlay)){
                        map.getOverlays().remove(fromIdOverlay);
                    }
                    fromIdOverlay = new ItemizedIconOverlay(overlayItems, null, getApplicationContext());
                    map.getOverlays().add(fromIdOverlay);
                    txtFromId.setText(nearestNode.nodeId);
                }else if(isToFocus){
                    OverlayItem toItem = new OverlayItem("toId", "toId", nearestPoint);
                    overlayItems.add(toItem);
                    if(map.getOverlays().contains(toIdOverlay)){
                        map.getOverlays().remove(toIdOverlay);
                    }
                    toIdOverlay = new ItemizedIconOverlay(overlayItems, null, getApplicationContext());
                    map.getOverlays().add(toIdOverlay);
                    txtToId.setText(nearestNode.nodeId);
                }
                return false;
            }
        };
        MapEventsOverlay mo = new MapEventsOverlay(mReceive);
        map.getOverlayManager().add(mo);

        IMapController mapController = map.getController();
        mapController.setZoom(13.0);
        GeoPoint startPoint = new GeoPoint(28.2, 113);
//        GeoPoint startPoint = new GeoPoint(16.83, 112.33);
        mapController.setCenter(startPoint);
    }

    private void setFromToIdEvents(final EditText txtId, final boolean isFromId) {
        txtId.setInputType(InputType.TYPE_NULL);//强制不自动弹出软键盘
        txtId.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(isFromId){
                    isFromFocus = hasFocus;
                }else{
                    isToFocus = hasFocus;
                }

                if (hasFocus) {
                    txtId.setBackgroundColor(Color.parseColor("#e000FF00"));  //获取焦点后更改背景色
                } else {
                    txtId.setBackgroundColor(Color.parseColor("#01000000"));  //失去焦点后更改背景色
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        runtime.release();//释放  可放在onDestroy()当中
        super.onDestroy();
    }

    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    private View.OnClickListener onClickListener1 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int result = runtime.executeIntegerScript(""
                    + "var hello = 'hello, ';\n"
                    + "var world = 'world!';\n"
                    + "hello.concat(world).length;\n");
            Toast.makeText(MainActivity.this, "result:" + result, Toast.LENGTH_LONG).show();

            ReadRoadData.readRoadRawTest(MainActivity.this);
        }
    };

    private View.OnClickListener onClickListenerLoad = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ReadGeoJson readGeoJson = new ReadGeoJson();
            readGeoJson.mainActivity = MainActivity.this;
            readGeoJson.execute();
        }
    };

    private View.OnClickListener onClickListenerNav = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (!btnLoad.isEnabled()) {
                EditText txtFrom = (EditText) findViewById(R.id.txtFromId);
                String fromId = txtFrom.getText().toString();
                EditText txtTo = (EditText) findViewById(R.id.txtToId);
                String toId = txtTo.getText().toString();

                List<CjtNode> result = AStar.astarNavRoad(MainActivity.this.cjtGraph,  fromId, toId);

                if (result.size() == 0) {
                    Toast.makeText(MainActivity.this, "无结果！", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "结果数量为：" + result.size(), Toast.LENGTH_LONG).show();
                    drawLine(result);
                }
            }else{
                Toast.makeText(MainActivity.this, "请先加载数据！", Toast.LENGTH_LONG).show();
            }
        }
    };

    private void drawLine(List<CjtNode> lineNodes) {
        List<GeoPoint> points = new ArrayList<>();
        double xmin=180,xmax=-180,ymin=90,ymax=-90;
        for (int i = 0; i < lineNodes.size(); i++) {
            double lon = lineNodes.get(i).lon;
            double lat = lineNodes.get(i).lat;
            points.add(new GeoPoint(lat, lon));

            if(lon<xmin){
                xmin = lon;
            }else if(lon>xmax){
                xmax = lon;
            }

            if(lat<ymin){
                ymin=lat;
            }else if(lat>ymax){
                ymax=lat;
            }
        }

        if(linePath == null) {
            linePath = new Polyline();
            linePath.setWidth(10);
            linePath.setColor(0xFF1B7BCD);
            linePath.setPoints(points);
            map.getOverlays().add(linePath);
        }else {
            linePath.setPoints(points);
            map.refreshDrawableState();
        }

        //计算边界值，定位边界
//        final BoundingBox box = new BoundingBox(ymax,xmax,ymin,xmin);
//        map.zoomToBoundingBox(box, true);
    }
}
