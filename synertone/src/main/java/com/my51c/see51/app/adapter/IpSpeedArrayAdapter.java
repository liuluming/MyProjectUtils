package com.my51c.see51.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.my51c.see51.app.domian.ListItemObject;
import com.synertone.netAssistant.R;

import java.util.HashMap;
import java.util.List;

public class IpSpeedArrayAdapter extends ArrayAdapter<ListItemObject> {

    HashMap<ListItemObject, Integer> mIdMap = new HashMap<ListItemObject, Integer>();
    List<ListItemObject> mData;
    Context mContext;
    int mLayoutViewResourceId;
    int mCounter;
    Boolean tagOnClick = false;
    int mPag;

    public IpSpeedArrayAdapter(Context context, int layoutViewResourceId,
                               List<ListItemObject> data, int pag) {
        super(context, layoutViewResourceId, data);
        mData = data;
        mContext = context;
        mLayoutViewResourceId = layoutViewResourceId;
        mPag = pag;
        updateStableIds();
    }

    /**
     * Returns a circular cropped version of the bitmap passed in.
     */
    public static Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);

        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);

        int halfWidth = bitmap.getWidth() / 2;
        int halfHeight = bitmap.getHeight() / 2;

        canvas.drawCircle(halfWidth, halfHeight,
                Math.max(halfWidth, halfHeight), paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public long getItemId(int position) {
        ListItemObject item = getItem(position);
        if (mIdMap.containsKey(item)) {
            return mIdMap.get(item);
        }
        return -1;
    }

    public void updateStableIds() {
        mIdMap.clear();
        mCounter = 0;
        for (int i = 0; i < mData.size(); ++i) {
            mIdMap.put(mData.get(i), mCounter++);
        }
    }

    public void addStableIdForDataAtPosition(int position) {
        mIdMap.put(mData.get(position), ++mCounter);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ListItemObject obj = mData.get(position);
        final ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater
                    .inflate(mLayoutViewResourceId, parent, false);
            vh.imgView = (ImageView) convertView.findViewById(R.id.ipspeed_image_view);
            vh.textView = (TextView) convertView.findViewById(R.id.ipspeed_text_view);
            //vh.bwplan_delebtn=(Button) convertView.findViewById(R.id.bwplan_delebtn);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        convertView.setLayoutParams(new ListView.LayoutParams(
                LayoutParams.MATCH_PARENT, obj.getHeight()));

        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(),
                obj.getImgResource(), null);

        vh.textView.setText(obj.getTitle());
//		vh.imgView.setImageBitmap(PlanArrayAdapter.getCroppedBitmap(bitmap));

	/*	vh.bwplan_delebtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mData.remove(position);
				notifyDataSetChanged();
			}
		});*/

        return convertView;
    }

    class ViewHolder {
        ImageView imgView;
        TextView textView;
       /* EditText bwplan_num01,bwplan_menber_num;
        Button bwplan_delebtn;
        Spinner bwplan_end;*/
    }
}
