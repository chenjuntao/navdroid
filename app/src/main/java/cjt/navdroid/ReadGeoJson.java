package cjt.navdroid;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.cocoahero.android.geojson.*;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ReadGeoJson {

    public void readGeoJson(Context context, Handler mHandler) {
        CjtGraph cjtGraph = new CjtGraph();
        try {
            InputStream is = context.getResources().openRawResource(R.raw.cs_roadl);
            GeoJSONObject geoJSON = GeoJSON.parse(is);
            String geojsonType = geoJSON.getType();
            Log.i("ttt", geojsonType);
            List<Feature> fcs = ((FeatureCollection) geoJSON).getFeatures();

            int process = 0;
            for (int i = 0; i < fcs.size(); i++) {
                Feature feature = fcs.get(i);
                Geometry geometry = feature.getGeometry();
                LineString lineString = (LineString) geometry;
                List<Position> positions = lineString.getPositions();
                cjtGraph.addLine(positions);

                int newProcess = i * 100 / fcs.size();
                if (newProcess > process) {
                    // 发送进度消息到Handler
                    Message msg = new Message();
                    msg.what = 0x111;
                    msg.arg1 = newProcess;
                    mHandler.sendMessage(msg);
                    process = newProcess;
                }
            }
            Log.i("", "read cjtGraph complete!");
        } catch (
                IOException | JSONException e) {
            e.printStackTrace();
        }

//        private class DownloadAsyncTask extends AsyncTask<String, Integer, Boolean> {
//            private String mFilePath;//下载文件的保存路径
//            @Override
//            protected Boolean doInBackground(String... params) {
//                if (params != null && params.length > 0) {
//                    String pdfUrl = params[0];
//                    try {
//                        URL url = new URL(pdfUrl);
//                        URLConnection urlConnection = url.openConnection();
//                        InputStream in = urlConnection.getInputStream();
//                        int contentLength = urlConnection.getContentLength();//获取内容总长度
//                        mFilePath = Environment.getExternalStorageDirectory() + File.separator + FILE_NAME;
//                        //若存在同名文件则删除
//                        File pdfFile = new File(mFilePath);
//                        if (pdfFile.exists()) {
//                            boolean result = pdfFile.delete();
//                            if (!result) {
//                                return false;
//                            }
//                        }
//                        int downloadSize = 0;//已经下载的大小
//                        byte[] bytes = new byte[1024];
//                        int length = 0;
//                        OutputStream out = new FileOutputStream(mFilePath);
//                        while ((length = in.read(bytes)) != -1) {
//                            out.write(bytes, 0, length);
//                            downloadSize += length;
//                            publishProgress(downloadSize / contentLength * 100);
//                        }
//                        in.close();
//                        out.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        return false;
//                    }
//                } else {
//                    return false;
//                }
//                return true;
//            }
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//                mDownloadBtn.setText("下载中");
//                mDownloadBtn.setEnabled(false);
//                mStatus.setText("下载中");
//                mProgressBar.setProgress(0);
//            }
//            @Override
//            protected void onPostExecute(Boolean aBoolean) {
//                super.onPostExecute(aBoolean);
//                mDownloadBtn.setText("下载完成");
//                mStatus.setText(aBoolean ? "下载完成" + mFilePath : "下载失败");
//            }
//            @Override
//            protected void onProgressUpdate(Integer... values) {
//                super.onProgressUpdate(values);
//                if (values != null && values.length > 0) {
//                    mProgressBar.setProgress(values[0]);
//                }
//            }
//        }
    }
}
