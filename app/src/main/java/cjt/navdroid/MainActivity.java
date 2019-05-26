package cjt.navdroid;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Object;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    V8 runtime = null;
    MapView map = null;
    public ProgressBar progress = null;
    public Button btnLoad = null;
    public Button btnNav = null;

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


        //获得界面布局里面的进度条组件
        progress = (ProgressBar) findViewById(R.id.progress);
        btnLoad = (Button) findViewById(R.id.btnLoad);
        btnNav = (Button) findViewById(R.id.btnNav);

        findViewById(R.id.btn1).setOnClickListener(onClickListener1);
        findViewById(R.id.btn2).setOnClickListener(onClickListener2);
        findViewById(R.id.btnLoad).setOnClickListener(onClickListenerLoad);
        findViewById(R.id.btnNav).setOnClickListener(onClickListenerNav);

        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);

        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        IMapController mapController = map.getController();
        mapController.setZoom(9.5);
        GeoPoint startPoint = new GeoPoint(28.1, 112.1);
        mapController.setCenter(startPoint);
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
        }
    };

    private View.OnClickListener onClickListener2 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            runtime.executeVoidScript(""
                    + "var person = {};\n"
                    + "var hockeyTeam = {name : 'WolfPack'};\n"
                    + "person.first = 'Ian';\n"
                    + "person['last'] = 'Bull';\n"
                    + "person.hockeyTeam = hockeyTeam;\n");

            V8Object person = runtime.getObject("person");
            V8Object hockeyTeam = person.getObject("hockeyTeam");
            String result = "JS result name = " + hockeyTeam.getString("name");
            Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
            person.release();
            hockeyTeam.release();
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

                List<CjtNode> result = AStar.astarNavRoad(fromId, toId);
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

        org.osmdroid.views.overlay.Polyline Polyline = new org.osmdroid.views.overlay.Polyline();
        Polyline.setWidth(10);
        Polyline.setColor(0xFF1B7BCD);
        Polyline.setPoints(points);
        map.getOverlays().clear();
        map.getOverlays().add(Polyline);


        //计算边界值，定位边界
        final BoundingBox box = new BoundingBox(ymax,xmax,ymin,xmin);
        map.getController().zoomTo(6);
        map.zoomToBoundingBox(box, true);
    }
}
