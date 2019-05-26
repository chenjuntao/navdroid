package cjt.navdroid;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.FileInputStream;

public class ReadSDCard {
        public void readSD(Context context){
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            try {

                FileInputStream inputStream = new FileInputStream("/sdcard/changsha_road.geojson");
                byte[] b = new byte[inputStream.available()];
                inputStream.read(b);
                Toast.makeText(context, "读取文件成功",
                        Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(context, "读取失败",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // 此时SDcard不存在或者不能进行读写操作的
            Toast.makeText(context,
                    "此时SDcard不存在或者不能进行读写操作", Toast.LENGTH_SHORT).show();
        }
    }
}
