/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.my51c.see51.app.domian.ListItemObject;
import com.synertone.netAssistant.R;

import java.util.HashMap;
import java.util.List;


public class MemberArrayAdapter extends ArrayAdapter<ListItemObject> {

    HashMap<ListItemObject, Integer> mIdMap = new HashMap<ListItemObject, Integer>();
    List<ListItemObject> mData;
    Context mContext;
    int mLayoutViewResourceId;
    int mCounter;
    Boolean tagOnClick = false;
    int mPag;

    public MemberArrayAdapter(Context context, int layoutViewResourceId,
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
            vh.imgView = (ImageView) convertView.findViewById(R.id.bwmember_image_view);
            vh.textView = (TextView) convertView.findViewById(R.id.bwmember_text_view);
            vh.bwmember_interfer = (Spinner) convertView.findViewById(R.id.bwmember_interfer);
            vh.bwmember_hopnum = (EditText) convertView.findViewById(R.id.bwmember_hopnum);
            vh.bwmember_weigth = (EditText) convertView.findViewById(R.id.bwmember_weigth);
            vh.bwmember_delebtn = (Button) convertView.findViewById(R.id.bwmember_delebtn);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        convertView.setLayoutParams(new ListView.LayoutParams(
                LayoutParams.MATCH_PARENT, obj.getHeight()));

        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(),
                obj.getImgResource(), null);

        vh.textView.setText(obj.getTitle());
        vh.imgView.setImageBitmap(MemberArrayAdapter.getCroppedBitmap(bitmap));

        vh.bwmember_delebtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mData.remove(position);
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    class ViewHolder {
        ImageView imgView;
        TextView textView;
        EditText bwmember_hopnum, bwmember_weigth;
        Button bwmember_delebtn;
        Spinner bwmember_interfer;
    }
}