package cjt.navdroid;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.cocoahero.android.geojson.*;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ReadGeoJson extends AsyncTask<Void, Integer, Void> {
    private int current = 0;
    public MainActivity mainActivity;
    private CjtGraph cjtGraph = new CjtGraph();

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        Log.d("ReadGeoJson","开始前的准备工作...");
        mainActivity.progress.setVisibility(View.VISIBLE);
        mainActivity.btnLoad.setEnabled(false);
        Toast.makeText(mainActivity, "开始读取数据，请稍侯。。。:", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        //这里params[0]和params[1]是execute传入的两个参数
        //这里的参数类型是 AsyncTask<Void, Integer, Void>中的Integer决定的，在onProgressUpdate中可以得到这个值去更新UI主线程，这里是异步线程
        Log.d("ReadGeoJson", "id:" + Thread.currentThread().getId() + " name:" + Thread.currentThread().getName());

        readGeoJson();
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        //这里是UI主线程
        System.out.println(values[0]);
        Log.d("ReadGeoJson", "显示进度");

        mainActivity.progress.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.i("ReadGeoJson", "执行完成!, Thread name: " + Thread.currentThread().getName());
        Toast.makeText(mainActivity, "执行完成，一共读取"+cjtGraph.nodes.size()+"个节点，读取"+cjtGraph.edges.size()+"条边。", Toast.LENGTH_LONG).show();
        mainActivity.progress.setVisibility(View.GONE);
        mainActivity.btnNav.setEnabled(true);
        AStar.astarReadGraph(cjtGraph);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        Log.i("ReadGeoJson", "执行取消!, Thread name: " + Thread.currentThread().getName());
    }

    public void readGeoJson() {

        try {
            InputStream is = mainActivity.getResources().openRawResource(R.raw.cs_roadl);
            GeoJSONObject geoJSON = GeoJSON.parse(is);
            String geojsonType = geoJSON.getType();
            Log.i("ttt", geojsonType);
            List<Feature> fcs = ((FeatureCollection) geoJSON).getFeatures();

            for (int i = 0; i < fcs.size(); i++) {
                Feature feature = fcs.get(i);
                Geometry geometry = feature.getGeometry();
                LineString lineString = (LineString) geometry;
                List<Position> positions = lineString.getPositions();
                cjtGraph.addLine(positions);

                int newProcess = i * 100 / fcs.size();
                if (newProcess > current) {
                    current = newProcess;
                    publishProgress(current);
                }
            }
            Log.i("", "read cjtGraph complete!");
        } catch (
                IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}
