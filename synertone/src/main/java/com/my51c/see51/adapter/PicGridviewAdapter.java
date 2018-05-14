package com.my51c.see51.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.my51c.see51.widget.GridItem;

import java.util.ArrayList;

public class PicGridviewAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Bitmap> bitmaplist = new ArrayList<Bitmap>();
    //��� List
//	    private ArrayList<String> lstFile =Constant.setList();

    public PicGridviewAdapter(ArrayList<Bitmap> bitmaplist, Context context) {
        this.bitmaplist = bitmaplist;
        this.mContext = context;


    }

    public static Bitmap getImageThumbnail(String imagePath, int width, int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // ��ȡ���ͼƬ�Ŀ�͸ߣ�ע��˴���bitmapΪnull
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false; // ��Ϊ false
        // �������ű�
        int h = options.outHeight;
        int w = options.outWidth;
        int beWidth = w / width;
        int beHeight = h / height;
        int be = 1;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        // ���¶���ͼƬ����ȡ���ź��bitmap��ע�����Ҫ��options.inJustDecodeBounds ��Ϊ false
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        // ����ThumbnailUtils����������ͼ������Ҫָ��Ҫ�����ĸ�Bitmap����
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
//			return picPath.length;
        return bitmaplist.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @SuppressWarnings("unused")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        Bitmap bitmap = null;
        GridItem item;
        if (convertView == null) {

            item = new GridItem(mContext);
            item.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        } else {
            item = (GridItem) convertView;

        }
        item.setImgBitmap(bitmaplist.get(position));
//			Object c = selectMap.get(position);
//			item.setChecked(selectMap.get(position) == null ? false : selectMap.get(position));
        return item;
    }

}