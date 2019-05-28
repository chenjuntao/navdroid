package cjt.navdroid;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.cocoahero.android.geojson.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class ReadGeoJson extends AsyncTask<Void, Integer, Void> {
    private int current = 0;
    public MainActivity mainActivity;


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d("ReadGeoJson", "开始前的准备工作...");
        mainActivity.progress.setVisibility(View.VISIBLE);
        mainActivity.btnLoad.setEnabled(false);
        Toast.makeText(mainActivity, "开始读取数据，请稍侯。。。:", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        //这里params[0]和params[1]是execute传入的两个参数
        //这里的参数类型是 AsyncTask<Void, Integer, Void>中的Integer决定的，在onProgressUpdate中可以得到这个值去更新UI主线程，这里是异步线程
        Log.d("ReadGeoJson", "id:" + Thread.currentThread().getId() + " name:" + Thread.currentThread().getName());

//        readGeoJson();
        readJson();
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
        int nodeCount = mainActivity.cjtGraph.nodes.size();
        int edgeCount = mainActivity.cjtGraph.edges.size();
        Toast.makeText(mainActivity, "执行完成，一共读取" + nodeCount
                + "个节点，读取" + edgeCount + "条边。", Toast.LENGTH_LONG).show();
        mainActivity.txtMsg.setText("一共" + nodeCount + "个节点，" + edgeCount + "条边");
        mainActivity.progress.setVisibility(View.GONE);
        mainActivity.btnNav.setEnabled(true);
        AStar.astarReadGraph(mainActivity.cjtGraph);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        Log.i("ReadGeoJson", "执行取消!, Thread name: " + Thread.currentThread().getName());
    }

    public void readGeoJson() {

        try {
            InputStream is = mainActivity.getResources().openRawResource(R.raw.changsha_road);
            GeoJSONObject geoJSON = GeoJSON.parse(is);
            String geojsonType = geoJSON.getType();
            Log.i("ttt", geojsonType);
            List<Feature> fcs = ((FeatureCollection) geoJSON).getFeatures();

            for (int i = 0; i < fcs.size(); i++) {
                Feature feature = fcs.get(i);
                Geometry geometry = feature.getGeometry();
                LineString lineString = (LineString) geometry;
                List<Position> positions = lineString.getPositions();
                mainActivity.cjtGraph.addLine(positions);

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

    public void readJson() {
        try {
            StringBuffer jsonStr = new StringBuffer();
            InputStream stream = mainActivity.getResources().openRawResource(R.raw.changsha);
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line = "";
            while ((line = reader.readLine()) != null) {
                jsonStr.append(line);
            }
            JSONObject jsonObj = new JSONObject(jsonStr.toString());
            JSONArray elements = jsonObj.getJSONArray("elements");
            int eleCount = elements.length();
            for (int i = 0; i < elements.length(); i++) {
                JSONObject ele = (JSONObject) elements.get(i);
                String eleType = ele.getString("type");
                if (eleType.equals("node")) {
                    mainActivity.cjtGraph.addNode(ele);
                } else if (eleType.equals("way")) {
                    mainActivity.cjtGraph.addWay(ele);
                }

                int newProcess = i * 100 / eleCount;
                if (newProcess > current) {
                    current = newProcess;
                    publishProgress(current);
                }
            }
        } catch (
                IOException | JSONException e) {
            e.printStackTrace();

        }
    }
}
