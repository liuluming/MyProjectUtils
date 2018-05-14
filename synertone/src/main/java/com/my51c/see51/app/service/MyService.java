package com.my51c.see51.app.service;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.my51c.see51.app.domian.Contact;

import java.util.ArrayList;
import java.util.List;

public class MyService {

    public static List<Contact> findContacts(Context context, String str) {

        ContentResolver resolver = context.getContentResolver();
        // 设置查找uri
        Uri uri = Uri.parse("content://com.android.contacts/data");
        // 获得所有的联系人信息
        Cursor cursor = resolver.query(uri, null, "mimetype_id = 1 and data4 like ?", new String[]{"%" + str + "%"}, null);
        System.out.println("mimetype_id = 1 and data4 like " + "'%" + str + "%'");
//		Cursor cursor = resolver.query(uri, null, "mimetype_id = 1", null, null);
        List<Contact> list = new ArrayList<Contact>();
        // 遍历每个联系人，根据联系人ID，获得它的相关数据，循环完毕，获得一个联系人的全部信息
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex("raw_contact_id"));
            ContentResolver resolvers = context.getContentResolver();
            Uri uri1 = Uri.parse("content://com.android.contacts/data");
            Cursor cur = resolvers.query(uri1, null, "raw_contact_id=?",
                    new String[]{id}, null);
            Contact con = new Contact(id);
            while (cur.moveToNext()) {
                changeData(cur, con);
            }
            list.add(con);
            cur.close();
        }
        cursor.close();
        return list;

    }

    public static void changeData(Cursor cur, Contact con) {
        String mimetype = cur.getString(cur.getColumnIndex("mimetype"));
        String data = cur.getString(cur.getColumnIndex("data1"));
        if ("vnd.android.cursor.item/name".equals(mimetype)) {
            con.setName(data);
        } else if ("vnd.android.cursor.item/phone_v2".equals(mimetype)) {
            con.setPhone(cur.getString(cur.getColumnIndex("data4")));
            con.setAddress(cur.getString(cur.getColumnIndex("data3")));
        }
    }
}
