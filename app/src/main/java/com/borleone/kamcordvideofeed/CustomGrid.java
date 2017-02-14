package com.borleone.kamcordvideofeed;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by Borle on 12/18/2016.
 */
public class CustomGrid extends BaseAdapter {
    private Context mContext;
    ArrayList<Integer> heartCount = new ArrayList<Integer>();
    ArrayList<String> shotThumbnail = new ArrayList<String>();
    ArrayList<String> play = new ArrayList<String>();

    public CustomGrid(Context context, ArrayList heartCount, ArrayList shotThumbnail, ArrayList play) {
        mContext = context;
        this.heartCount = heartCount;
        this.shotThumbnail = shotThumbnail;
        this.play = play;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return heartCount.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            grid = new View(mContext);
            grid = inflater.inflate(R.layout.grid_single, null);
        } else {
            grid = (View) convertView;
        }

        TextView textView = (TextView) grid.findViewById(R.id.grid_text);
        Log.d("HEART COUNT", position + "+" + heartCount.get(position) + " - " + heartCount.toString());
        String mHeartCount = "\u2764" + heartCount.get(position).toString();    //unicode character for heart
        textView.setText(mHeartCount);

        DownloadImageTask task = new DownloadImageTask((ImageView) grid.findViewById(R.id.grid_image));
        DownloadedDrawable downloadedDrawable = new DownloadedDrawable(task);
        ((ImageView) grid.findViewById(R.id.grid_image)).setImageDrawable(downloadedDrawable);
        task.execute(shotThumbnail.get(position));

        return grid;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bmp = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bmp = BitmapFactory.decodeStream(in);
                in.close();
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bmp;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    static class DownloadedDrawable extends ColorDrawable {
        private final WeakReference<DownloadImageTask> bitmapDownloaderTaskReference;

        public DownloadedDrawable(DownloadImageTask bitmapDownloaderTask) {
            super(Color.BLACK);
            bitmapDownloaderTaskReference = new WeakReference<DownloadImageTask>(bitmapDownloaderTask);
        }

        public DownloadImageTask getBitmapDownloaderTask() {
            return bitmapDownloaderTaskReference.get();
        }
    }
}