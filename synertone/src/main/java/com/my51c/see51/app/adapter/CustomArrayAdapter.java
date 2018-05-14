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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.my51c.see51.app.domian.ListItemObject;
import com.synertone.netAssistant.R;

import java.util.HashMap;
import java.util.List;

//
public class CustomArrayAdapter extends ArrayAdapter<ListItemObject> {

    HashMap<ListItemObject, Integer> mIdMap = new HashMap<ListItemObject, Integer>();
    List<ListItemObject> mData;
    Context mContext;
    int mLayoutViewResourceId;
    int mCounter;
    Boolean tagOnClick = false;
    int mPag;

    public CustomArrayAdapter(Context context, int layoutViewResourceId,
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
//			vh.imgView = (ImageView) convertView.findViewById(R.id.bw_image_view);
            vh.textView = (TextView) convertView.findViewById(R.id.bw_text_view);
//		   	vh.bwinterfer_onoff = (ToggleButton) convertView.findViewById(R.id.bwinterfer_onoff);
            vh.bwinterfer_ed01 = (EditText) convertView.findViewById(R.id.bwinterfer_ed01);
            vh.bwinterfer_ipbtn = (Button) convertView.findViewById(R.id.bwinterfer_ipbtn);
            vh.bwinterfer_ed02 = (EditText) convertView.findViewById(R.id.bwinterfer_ed02);
            vh.bwinterfer_ipcount = (EditText) convertView.findViewById(R.id.bwinterfer_ipcount);
            vh.bwinterfer_group_count = (EditText) convertView.findViewById(R.id.bwinterfer_group_count);
            vh.bwinterfer_time_out = (EditText) convertView.findViewById(R.id.bwinterfer_time_out);
            vh.bwinterfer_time_space = (EditText) convertView.findViewById(R.id.bwinterfer_time_space);
            vh.bwinterfer_notnet_num = (EditText) convertView.findViewById(R.id.bwinterfer_notnet_num);
            vh.bwinterfer_net_num = (EditText) convertView.findViewById(R.id.bwinterfer_net_num);
            vh.bwinterfer_ip_add01 = (LinearLayout) convertView.findViewById(R.id.bwinterfer_ip_add01);
            vh.bwinterfer_ip_add02 = (LinearLayout) convertView.findViewById(R.id.bwinterfer_ip_add02);
            vh.bwinterfer_ip_add03 = (LinearLayout) convertView.findViewById(R.id.bwinterfer_ip_add03);
            vh.bwinterfer_ip_add04 = (LinearLayout) convertView.findViewById(R.id.bwinterfer_ip_add04);
            vh.bwinterfer_ip_add05 = (LinearLayout) convertView.findViewById(R.id.bwinterfer_ip_add05);
            vh.bwinterfer_ipbtndele01 = (Button) convertView.findViewById(R.id.bwinterfer_ipbtndele01);
            vh.bwinterfer_ipbtndele02 = (Button) convertView.findViewById(R.id.bwinterfer_ipbtndele02);
            vh.bwinterfer_ipbtndele03 = (Button) convertView.findViewById(R.id.bwinterfer_ipbtndele03);
            vh.bwinterfer_ipbtndele04 = (Button) convertView.findViewById(R.id.bwinterfer_ipbtndele04);
            vh.bwinterfer_ipbtndele05 = (Button) convertView.findViewById(R.id.bwinterfer_ipbtndele05);
            vh.btDelete = (Button) convertView.findViewById(R.id.bwinterfer_dianji_dele);


            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        convertView.setLayoutParams(new ListView.LayoutParams(
                LayoutParams.MATCH_PARENT, obj.getHeight()));

        //点击添加里面的小Item
        vh.bwinterfer_ipbtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPag <= 6) {
                    mPag++;
                    if (mPag == 1) {
                        vh.bwinterfer_ip_add01.setVisibility(View.VISIBLE);
                    } else if (mPag == 2) {
                        vh.bwinterfer_ip_add02.setVisibility(View.VISIBLE);
                    } else if (mPag == 3) {
                        vh.bwinterfer_ip_add03.setVisibility(View.VISIBLE);
                    } else if (mPag == 4) {
                        vh.bwinterfer_ip_add04.setVisibility(View.VISIBLE);
                    } else if (mPag == 5) {
                        vh.bwinterfer_ip_add05.setVisibility(View.VISIBLE);
                    }
                } else {
                    return;
                }
            }
        });

        //点击删除ListView Item的按钮
        vh.btDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mData.remove(position);
                notifyDataSetChanged();
            }
        });

        //点击删除Ip里面的小条目
        vh.bwinterfer_ipbtndele01.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                vh.bwinterfer_ip_add01.setVisibility(View.GONE);
                mPag--;
            }
        });
        vh.bwinterfer_ipbtndele02.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                vh.bwinterfer_ip_add02.setVisibility(View.GONE);
                mPag--;
            }
        });
        vh.bwinterfer_ipbtndele03.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                vh.bwinterfer_ip_add03.setVisibility(View.GONE);
                mPag--;
            }
        });
        vh.bwinterfer_ipbtndele04.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                vh.bwinterfer_ip_add04.setVisibility(View.GONE);
                mPag--;
            }
        });
        vh.bwinterfer_ipbtndele05.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                vh.bwinterfer_ip_add05.setVisibility(View.GONE);
                mPag--;
            }
        });

        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(),
                obj.getImgResource(), null);

        vh.textView.setText(obj.getTitle());
        vh.imgView.setImageBitmap(CustomArrayAdapter.getCroppedBitmap(bitmap));
        return convertView;
    }

    class ViewHolder {
        ImageView imgView;
        TextView textView;
        ToggleButton bwinterfer_onoff;
        EditText bwinterfer_ed01, bwinterfer_ed02, bwinterfer_ipcount, bwinterfer_group_count,
                bwinterfer_time_out, bwinterfer_time_space, bwinterfer_notnet_num, bwinterfer_net_num;
        LinearLayout bwinterfer_ip_add01, bwinterfer_ip_add02, bwinterfer_ip_add03, bwinterfer_ip_add04, bwinterfer_ip_add05;
        Button bwinterfer_ipbtndele01, bwinterfer_ipbtndele02, bwinterfer_ipbtndele03, bwinterfer_ipbtndele04, bwinterfer_ipbtndele05, bwinterfer_ipbtn, btDelete;
    }


}