package com.my51c.see51.app;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.my51c.see51.BaseActivity;
import com.my51c.see51.app.activity.AcContactsDetails;
import com.my51c.see51.app.activity.PhoneActivity;
import com.my51c.see51.app.view.IndexBarView;
import com.my51c.see51.app.view.PinnedHeaderListView;
import com.my51c.see51.app.voipphone.PinnedHeaderAdapter;
import com.synertone.netAssistant.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class VoipActivity extends BaseActivity {
    // an array of countries to display in the list
    static final String[] ITEMS = new String[]{"East Timor", "Ecuador",
            "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea", "Estonia",
            "Ethiopia", "Faeroe Islands", "Falkland Islands", "Fiji",
            "Finland", "Afghanistan", "Albania", "Algeria", "American Samoa",
            "Andorra", "Angola", "Anguilla", "Antarctica",
            "Antigua and Barbuda", "Argentina", "Armenia", "Aruba",
            "Australia", "Austria", "Azerbaijan", "Bahrain", "Bangladesh",
            "Barbados", "Belarus", "Belgium", "Monaco", "Mongolia",
            "Montserrat", "Morocco", "Mozambique", "Myanmar", "Namibia",
            "Nauru", "Nepal", "Netherlands", "Netherlands Antilles",
            "New Caledonia", "New Zealand", "Guyana", "Haiti",
            "Heard Island and McDonald Islands", "Honduras", "Hong Kong",
            "Hungary", "Iceland", "India", "Indonesia", "Iran", "Iraq",
            "Ireland", "Israel", "Italy", "积极", "Kazakhstan", "Kenya",
            "Kiribati", "Kuwait", "Kyrgyzstan", "Laos", "Latvia", "Lebanon",
            "Lesotho", "Liberia", "Libya", "Liechtenstein", "Lithuania",
            "Luxembourg", "Nicaragua", "Niger", "Nigeria", "Niue",
            "Norfolk Island", "North Korea", "Northern Marianas", "Norway",
            "Oman", "Pakistan", "Palau", "Panama", "Papua New Guinea",
            "Paraguay", "皮城", "皮皮", "Pitcairn Islands", "Poland", "Portugal",
            "Puerto Rico", "Qatar", "French Southern Territories", "Gabon",
            "Georgia", "Germany", "Ghana", "Gibraltar", "Greece", "Greenland",
            "Grenada", "Guadeloupe", "Guam", "Guatemala", "Guinea",
            "Guinea-Bissau", "Martinique", "Mauritania", "Mauritius",
            "Mayotte", "Mexico", "Micronesia", "Moldova",
            "Bosnia and Herzegovina", "Botswana", "Bouvet Island", "Brazil",
            "British Indian Ocean Territory",
            "Saint Vincent and the Grenadines", "Samoa", "San Marino",
            "Saudi Arabia", "Senegal", "Seychelles", "Sierra Leone",
            "Singapore", "Slovakia", "Slovenia", "Solomon Islands", "Somalia",
            "South Africa", "South Georgia and the South Sandwich Islands",
            "South Korea", "Spain", "Sri Lanka", "Sudan", "Suriname",
            "Svalbard and Jan Mayen", "Swaziland", "Sweden", "Switzerland",
            "Syria", "Taiwan", "Tajikistan", "Tanzania", "Thailand",
            "The Bahamas", "The Gambia", "Togo", "Tokelau", "Tonga",
            "Trinidad and Tobago", "Tunisia", "Turkey", "Turkmenistan",
            "Turks and Caicos Islands", "Tuvalu", "Uganda", "Ukraine",
            "United Arab Emirates", "United Kingdom", "United States",
            "United States Minor Outlying Islands", "Uruguay", "Uzbekistan",
            "Vanuatu", "Vatican City", "Venezuela", "Vietnam",
            "Virgin Islands", "Wallis and Futuna", "Western Sahara",
            "British Virgin Islands", "Brunei", "Bulgaria", "Burkina Faso",
            "Burundi", "Cote d'Ivoire", "Cambodia", "Cameroon", "Canada",
            "Cape Verde", "Cayman Islands", "Central African Republic", "Chad",
            "Chile", "China", "Reunion", "Romania", "Russia", "Rwanda",
            "Sqo Tome and Principe", "Saint Helena", "Saint Kitts and Nevis",
            "Saint Lucia", "Saint Pierre and Miquelon", "Belize", "Benin",
            "Bermuda", "Bhutan", "Bolivia", "Christmas Island",
            "Cocos (Keeling) Islands", "Colombia", "Comoros", "Congo",
            "Cook Islands", "Costa Rica", "Croatia", "Cuba", "Cyprus",
            "Czech Republic", "Democratic Republic of the Congo", "Denmark",
            "Djibouti", "Dominica", "Dominican Republic",
            "Former Yugoslav Republic of Macedonia", "France", "French Guiana",
            "French Polynesia", "Macau", "Madagascar", "Malawi", "Malaysia",
            "Maldives", "Mali", "Malta", "Marshall Islands", "Yemen",
            "Yugoslavia", "Zambia", "Zimbabwe"};

    // unsorted list items
    ArrayList<String> mItems;

    // array list to store section positions
    ArrayList<Integer> mListSectionPos;

    // array list to store listView data
    ArrayList<String> mListItems;

    // custom list view with pinned header
    PinnedHeaderListView mListView;

    // custom adapter
    PinnedHeaderAdapter mAdaptor;

    // search box
    EditText mSearchView;

    // loading view
    ProgressBar mLoadingView;

    // empty view
    TextView mEmptyView;


    //private HashMap<String, Integer> arrContentList;// 存放存在的汉语拼音首字母和与之对应的列表位置
    List<PersonEntity> arrContentList;
    //ArrContentList arrContentList=new ArrContentList();
    private TextWatcher filterTextWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            String str = s.toString();
            if (mAdaptor != null && str != null)
                mAdaptor.getFilter().filter(str);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

        }
    };

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // UI elements
        setupViews();

        // Array to ArrayList

        // mItems = new ArrayList<String>(Arrays.asList(ITEMS));
        // mItems=new ArrayList<String>();
        mItems = new ArrayList<String>();
        Cursor cursor = getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String string = cursor
                        .getString(cursor
                                .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                mItems.add(string);
            } while (cursor.moveToNext());
            cursor.close();
        }

        mListSectionPos = new ArrayList<Integer>();
        mListItems = new ArrayList<String>();

        // for handling configuration change
        if (savedInstanceState != null) {
            mListItems = savedInstanceState.getStringArrayList("mListItems");
            mListSectionPos = savedInstanceState
                    .getIntegerArrayList("mListSectionPos");

            if (mListItems != null && mListItems.size() > 0
                    && mListSectionPos != null && mListSectionPos.size() > 0) {
                setListAdaptor();
                mListView.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view,
                                            int position, long id) {
                        Toast.makeText(getApplicationContext(), "alsdjfl", Toast.LENGTH_LONG).show();
                        int index = position - 1;
                        if (VoipActivity.this.arrContentList == null) return;
                        Intent intent = new Intent(VoipActivity.this, AcContactsDetails.class);
                        intent.putExtra("name", (VoipActivity.this.arrContentList.get(index)).getName());
                        intent.putExtra("phone", (VoipActivity.this.arrContentList.get(index)).getMobile());
                        startActivity(intent);
                    }

                });
            }

            String constraint = savedInstanceState.getString("constraint");
            if (constraint != null && constraint.length() > 0) {
                mSearchView.setText(constraint);
                setIndexBarViewVisibility(constraint);
            }
        } else {
            new Poplulate().execute(mItems);
        }
    }

    private void setupViews() {
        setContentView(R.layout.voip_activity);
        mSearchView = (EditText) findViewById(R.id.search_view);
        mLoadingView = (ProgressBar) findViewById(R.id.loading_view);
        mListView = (PinnedHeaderListView) findViewById(R.id.list_view);
        mEmptyView = (TextView) findViewById(R.id.empty_view);
    }

    public void jianpanbohao(View v) {
        Intent mIntent = new Intent(VoipActivity.this, PhoneActivity.class);
        startActivity(mIntent);
        Toast.makeText(VoipActivity.this, "点击了键盘", 0).show();
    }

    // 点击退出Voip电话界面
    public void onVoipFinish(View v) {
        finish();
    }

    // 点击退出Voip电话界面
    public void All_call(View v) {
        Intent mIntent = new Intent(VoipActivity.this, CallPersonActivity.class);
        startActivity(mIntent);
        Toast.makeText(VoipActivity.this, "点击了通话记录", 0).show();
    }

    // I encountered an interesting problem with a TextWatcher listening for
    // changes in an EditText.
    // The afterTextChanged method was called, each time, the device orientation
    // changed.
    // An answer on Stackoverflow let me understand what was happening: Android
    // recreates the activity, and
    // the automatic restoration of the state of the input fields, is happening
    // after onCreate had finished,
    // where the TextWatcher was added as a TextChangedListener.The solution to
    // the problem consisted in adding
    // the TextWatcher in onPostCreate, which is called after restoration has
    // taken place
    //
    // http://stackoverflow.com/questions/6028218/android-retain-callback-state-after-configuration-change/6029070#6029070
    // http://stackoverflow.com/questions/5151095/textwatcher-called-even-if-text-is-set-before-adding-the-watcher
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        mSearchView.addTextChangedListener(filterTextWatcher);
        super.onPostCreate(savedInstanceState);
    }

    private void setListAdaptor() {
        // create instance of PinnedHeaderAdapter and set adapter to list view
        mAdaptor = new PinnedHeaderAdapter(this, mListItems, mListSectionPos);
        mListView.setAdapter(mAdaptor);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        // set header view
        View pinnedHeaderView = inflater.inflate(R.layout.section_row_view,
                mListView, false);
        mListView.setPinnedHeaderView(pinnedHeaderView);

        // set index bar view
        IndexBarView indexBarView = (IndexBarView) inflater.inflate(
                R.layout.index_bar_view, mListView, false);
        indexBarView.setData(mListView, mListItems, mListSectionPos);
        mListView.setIndexBarView(indexBarView);

        // set preview text view
        View previewTextView = inflater.inflate(R.layout.preview_view,
                mListView, false);
        mListView.setPreviewView(previewTextView);

        // for configure pinned header view on scroll change
        mListView.setOnScrollListener(mAdaptor);
    }

    private void setIndexBarViewVisibility(String constraint) {
        // hide index bar for search results
        if (constraint != null && constraint.length() > 0) {
            mListView.setIndexBarVisibility(false);
        } else {
            mListView.setIndexBarVisibility(true);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mListItems != null && mListItems.size() > 0) {
            outState.putStringArrayList("mListItems", mListItems);
        }
        if (mListSectionPos != null && mListSectionPos.size() > 0) {
            outState.putIntegerArrayList("mListSectionPos", mListSectionPos);
        }
        String searchText = mSearchView.getText().toString();
        if (searchText != null && searchText.length() > 0) {
            outState.putString("constraint", searchText);
        }
        super.onSaveInstanceState(outState);
    }

    public class ListFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            // NOTE: this function is *always* called from a background thread,
            // and
            // not the UI thread.
            String constraintStr = constraint.toString().toLowerCase(
                    Locale.getDefault());
            FilterResults result = new FilterResults();

            if (constraint != null && constraint.toString().length() > 0) {
                ArrayList<String> filterItems = new ArrayList<String>();

                synchronized (this) {
                    for (String item : mItems) {
                        if (item.toLowerCase(Locale.getDefault()).startsWith(
                                constraintStr)) {
                            filterItems.add(item);
                        }
                    }
                    result.count = filterItems.size();
                    result.values = filterItems;
                }
            } else {
                synchronized (this) {
                    result.count = mItems.size();
                    result.values = mItems;
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            ArrayList<String> filtered = (ArrayList<String>) results.values;
            setIndexBarViewVisibility(constraint.toString());
            // sort array and extract sections in background Thread
            new Poplulate().execute(filtered);
        }

    }

    // sort array and extract sections in background Thread here we use
    // AsyncTask
    private class Poplulate extends AsyncTask<ArrayList<String>, Void, Void> {

        private void showLoading(View contentView, View loadingView,
                                 View emptyView) {
            contentView.setVisibility(View.GONE);
            loadingView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }

        private void showContent(View contentView, View loadingView,
                                 View emptyView) {
            contentView.setVisibility(View.VISIBLE);
            loadingView.setVisibility(View.GONE);
            emptyView.setVisibility(View.GONE);
        }

        private void showEmptyText(View contentView, View loadingView,
                                   View emptyView) {
            contentView.setVisibility(View.GONE);
            loadingView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPreExecute() {
            // show loading indicator
            showLoading(mListView, mLoadingView, mEmptyView);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(ArrayList<String>... params) {
            mListItems.clear();
            mListSectionPos.clear();
            ArrayList<String> items = params[0];
            if (mItems.size() > 0) {

                // NOT forget to sort array
                Collections.sort(items, new SortIgnoreCase());

                String prev_section = "";
                for (String current_item : items) {
                    String current_section = current_item.substring(0, 1)
                            .toUpperCase(Locale.getDefault());

                    if (!prev_section.equals(current_section)) {
                        mListItems.add(current_section);
                        mListItems.add(current_item);
                        // array list of section positions
                        mListSectionPos
                                .add(mListItems.indexOf(current_section));
                        prev_section = current_section;
                    } else {
                        mListItems.add(current_item);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (!isCancelled()) {
                if (mListItems.size() <= 0) {
                    showEmptyText(mListView, mLoadingView, mEmptyView);
                } else {
                    setListAdaptor();
                    showContent(mListView, mLoadingView, mEmptyView);
                }
            }
            super.onPostExecute(result);
        }
    }

    public class SortIgnoreCase implements Comparator<String> {
        @Override
        public int compare(String s1, String s2) {
            return s1.compareToIgnoreCase(s2);
        }
    }


}
