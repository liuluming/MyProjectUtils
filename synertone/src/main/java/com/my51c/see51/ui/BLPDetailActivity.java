package com.my51c.see51.ui;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.my51c.see51.BaseActivity;
import com.synertone.netAssistant.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class BLPDetailActivity extends BaseActivity implements OnClickListener {

    private ArrayList<String> infoList = new ArrayList<String>();
    private LinearLayout backLayout;
    private ListView personList;
    private ArrayList<String> persons = new ArrayList<String>();
    private PersonAdapter personAdapter;
    private RelativeLayout detailLayout1;
    private LinearLayout dateL, hpressL, lpressL, heartL, resultL;
    private Button close;
    private TextView personName;
    private boolean isHide = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blpdetailacy);
        Bundle bundle = getIntent().getExtras();
        infoList = bundle.getStringArrayList("infoList");
        findViewById();
        setListView();
    }

    private void findViewById() {

        backLayout = (LinearLayout) findViewById(R.id.blp_back_layout);
        personList = (ListView) findViewById(R.id.personList);
        detailLayout1 = (RelativeLayout) findViewById(R.id.detailLayout1);
        close = (Button) findViewById(R.id.blp_closeBtn);
        dateL = (LinearLayout) findViewById(R.id.blp_date_layout);
        hpressL = (LinearLayout) findViewById(R.id.blp_hpress_layout);
        lpressL = (LinearLayout) findViewById(R.id.blp_lpress_layout);
        heartL = (LinearLayout) findViewById(R.id.blp_heart_layout);
        resultL = (LinearLayout) findViewById(R.id.blp_result_layout);
        personName = (TextView) findViewById(R.id.personName);

        close.setOnClickListener(this);
        backLayout.setOnClickListener(this);
    }

    public void setListView() {
        getPersonId();
        personAdapter = new PersonAdapter(persons);
        personList.setAdapter(personAdapter);
        personList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                ArrayList<String> personInfos = new ArrayList<String>();
                Toast.makeText(getApplicationContext(), persons.get(position), Toast.LENGTH_SHORT).show();
                personName.setText(persons.get(position));
                for (int i = 0; i < infoList.size(); i++) {
                    if (persons.get(position).equals(infoList.get(i).split(",")[2])) {
                        personInfos.add(infoList.get(i));
                    }
                }
                showDetail(true, personInfos);
            }
        });
    }

    private void getPersonId() {
        HashSet<String> personIdSet = new HashSet<String>();
        for (int i = 0; i < infoList.size(); i++) {
            String[] infos = infoList.get(i).split(",");
            personIdSet.add(infos[2]);
        }
        Iterator<String> it = personIdSet.iterator();
        while (it.hasNext()) {
            persons.add(it.next());
        }
    }

    public void showDetail(boolean isHide, ArrayList<String> personInfos) {

        Animation leftIn = AnimationUtils.loadAnimation(this, R.anim.left_in);
        Animation rightOut = AnimationUtils.loadAnimation(this, R.anim.right_out);
        if (isHide) {
            detailLayout1.setAnimation(leftIn);
            detailLayout1.setVisibility(View.VISIBLE);
            leftIn.start();

//			data
            dateL.removeAllViewsInLayout();
            hpressL.removeAllViewsInLayout();
            lpressL.removeAllViewsInLayout();
            heartL.removeAllViewsInLayout();
            resultL.removeAllViewsInLayout();
            LayoutParams lp = new LayoutParams(180, LayoutParams.WRAP_CONTENT);
            for (int i = 0; i < personInfos.size(); i++) {
                TextView dateTx = new TextView(getApplicationContext());
                TextView hpressTx = new TextView(getApplicationContext());
                TextView lpressTx = new TextView(getApplicationContext());
                TextView heartTx = new TextView(getApplicationContext());
                TextView resultTx = new TextView(getApplicationContext());

                dateTx.setText(formatDate(personInfos.get(i).split(",")[1]));
                dateTx.setGravity(Gravity.CENTER);
                dateL.addView(dateTx, lp);

                hpressTx.setText(transformate(personInfos, i, 3, 0, 2));
                hpressTx.setGravity(Gravity.CENTER);
                hpressTx.setTextSize(20);
                hpressL.addView(hpressTx, lp);

                lpressTx.setText(transformate(personInfos, i, 3, 2, 4));
                lpressTx.setGravity(Gravity.CENTER);
                lpressTx.setTextSize(20);
                lpressL.addView(lpressTx, lp);

                heartTx.setText(transformate(personInfos, i, 3, 4, 6));
                heartTx.setGravity(Gravity.CENTER);
                heartTx.setTextSize(20);
                heartL.addView(heartTx, lp);

                resultTx.setText(personInfos.get(i).split(",")[3].substring(6, 8));
                resultTx.setGravity(Gravity.CENTER);
                resultTx.setTextSize(20);
                resultL.addView(resultTx, lp);
            }

        } else {

            detailLayout1.setAnimation(rightOut);
            detailLayout1.setVisibility(View.GONE);
            rightOut.start();
        }
    }

    private String transformate(ArrayList<String> personInfos, int i, int a, int b, int c) {
        int value = 0;
        String s = personInfos.get(i).split(",")[a].substring(b, c);
        value = Integer.parseInt(s, 16);
        return value + "";
    }

    //	0000004,20150831195443,01000000,673d3f01,
    private String formatDate(String s) {
        String year = s.substring(0, 4);
        String moth = s.substring(4, 6);
        String day = s.substring(6, 8);
        String hour = s.substring(8, 10);
        String min = s.substring(10, 12);
        String second = s.substring(12, 14);
        String result = year + "-" + moth + "-" + day + "\n" + hour + ":" + min + ":" + second;
        return result;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.blp_back_layout:
                finish();
                break;
            case R.id.blp_closeBtn:
                showDetail(false, null);
                break;
            default:
                break;
        }
    }

    private class PersonAdapter extends BaseAdapter {

        private ArrayList<String> persons;

        public PersonAdapter(ArrayList<String> persons) {
            this.persons = persons;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return persons.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup arg2) {
            // TODO Auto-generated method stub
            convertView = LayoutInflater.from(BLPDetailActivity.this).inflate(R.layout.blppersonlist_item, null);
            TextView name = (TextView) convertView.findViewById(R.id.personId);
            name.setText(persons.get(position));
            return convertView;
        }

    }


}
