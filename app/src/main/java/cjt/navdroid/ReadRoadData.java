package cjt.navdroid;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ReadRoadData {

    public void readRoadRawTest(Context context){
        Integer[] nodex, nodey;
            try {
                InputStream is = context.getResources().openRawResource(R.raw.amsterdam_roads_co);
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

        try {
                InputStream is = context.getResources().openRawResource(R.raw.amsterdam_roads_gr);
                BufferedInputStream bis = new BufferedInputStream(is);
                int len = bis.available();
                byte []b = new byte[len];
                int size = bis.read(b, 0, len);

                Log.i("", ""+size);
                for (int i=0;i<b.length;i+=4){
                    Integer item = byteToInt(new byte[]{b[i], b[i+1], b[i+2], b[i+3]});
                    Log.i("edge", ""+item);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public int byteToInt(byte[] b) {
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

    public float getFloat(byte[] b) {
        // 4 bytes
        int accum = 0;
        for ( int shiftBy = 0; shiftBy < 4; shiftBy++ ) {
            accum |= (b[shiftBy] & 0xff) << shiftBy * 8;
        }
        return Float.intBitsToFloat(accum);
    }
}
