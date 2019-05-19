package cjt.navdroid;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.io.*;

public class MainActivity extends AppCompatActivity {
    V8 runtime = null;
    MapView map = null;

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

        findViewById(R.id.btn1).setOnClickListener(onClickListener1);
        findViewById(R.id.btn2).setOnClickListener(onClickListener2);
        findViewById(R.id.btn3).setOnClickListener(onClickListener3);

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

    private View.OnClickListener onClickListener3 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Integer[] nodex, nodey;
            try {
                InputStream is = getResources().openRawResource(R.raw.amsterdam_roads_nodes);
                BufferedInputStream bis = new BufferedInputStream(is);
                int len = bis.available();
                byte []b = new byte[len];
                bis.read(b, 0, len);

                int nodeCount = len/8;
                Log.i("node count", ""+nodeCount);
                nodex = new Integer[nodeCount];
                nodey = new Integer[nodeCount];
                for (int i=0;i<b.length;i+=4){
                    Integer item = byteToInt(new byte[]{b[i], b[i+1], b[i+2], b[i+3]});
                    if(i%8==0){
                        nodex[i/8] = item;
                    }else{
                        nodey[i/8] = item;
                    }
                }
                Log.i("","sd");
            } catch (IOException e) {
                e.printStackTrace();
            }

//            try {
//                InputStream is = getResources().openRawResource(R.raw.amsterdam_roads_edges);
//                BufferedInputStream bis = new BufferedInputStream(is);
//                int len = bis.available();
//                byte []b = new byte[len];
//                int size = bis.read(b, 0, len);
//
//                Log.i("", ""+size);
//                for (int i=0;i<b.length;i+=4){
//                    Integer item = byteToInt(new byte[]{b[i], b[i+1], b[i+2], b[i+3]});
//                    Log.i("edge", ""+item);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            String result = AStar.test2().toString();
            Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
        }
    };

    public static float getFloat(byte[] b) {
        // 4 bytes
        int accum = 0;
        for ( int shiftBy = 0; shiftBy < 4; shiftBy++ ) {
            accum |= (b[shiftBy] & 0xff) << shiftBy * 8;
        }
        return Float.intBitsToFloat(accum);
    }

    public static int byteToInt(byte[] b) {
        int s = 0;
        int s0 = b[0] & 0xff;// 最低位
        int s1 = b[1] & 0xff;
        int s2 = b[2] & 0xff;
        int s3 = b[3] & 0xff;
        s3 <<= 24;
        s2 <<= 16;
        s1 <<= 8;
        s = s0 | s1 | s2 | s3;
        return s;
    }

}
