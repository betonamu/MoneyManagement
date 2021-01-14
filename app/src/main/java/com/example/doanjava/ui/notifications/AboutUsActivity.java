package com.example.doanjava.ui.notifications;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.doanjava.R;
import com.example.doanjava.common.GlobalConst;

import org.w3c.dom.Text;

import java.util.LinkedList;
import java.util.List;

public class AboutUsActivity extends AppCompatActivity {
    ImageView imgApp;
    TextView tvVersion,tvCreateBy;
    String[] arrItemContact;
    ListView lsvContact;
    ListItemContactAdapter adapter;

    class ListItemContactAdapter extends ArrayAdapter<String> {
        public ListItemContactAdapter(@NonNull Context context, int resource, int textViewResourceId) {
            super(context, resource, textViewResourceId);
        }

        public ListItemContactAdapter(Activity activity) {
            super(activity, android.R.layout.simple_list_item_1, arrItemContact);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View row = convertView;
            if (row == null) {
                LayoutInflater inflater = getLayoutInflater();
                row = inflater.inflate(R.layout.layout_list_item_contact, null);
            }
            String title = arrItemContact[position];
            ((TextView) row.findViewById(R.id.title_contact)).setText(title);

            //set image for list item
            ImageView imageView = (ImageView) row.findViewById(R.id.img_contact);

            switch (position) {
                case 0:
                    imageView.setImageResource(R.drawable.facebook);
                    break;
                case 1:
                    imageView.setImageResource(R.drawable.gmail);
                    break;
                case 2:
                    imageView.setImageResource(R.drawable.facebook);
                    break;

            }
            return row;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imgApp = (ImageView) findViewById(R.id.img_app);
        tvVersion = (TextView) findViewById(R.id.tv_version);
        tvCreateBy = (TextView) findViewById(R.id.tv_create_by);
        lsvContact = (ListView) findViewById(R.id.lsv_contact);

        Glide.with(this)
                .load(R.drawable.coins)
                .apply(RequestOptions.circleCropTransform())
                .into(imgApp);
        tvVersion.setText(GlobalConst.AppVersion);
        tvCreateBy.setText(GlobalConst.CreateBy);
        arrItemContact = getResources().getStringArray(R.array.list_item_contact);

        adapter = new ListItemContactAdapter(this);
        lsvContact.setAdapter(adapter);

        lsvContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        LaunchFacebook();
                        break;
                }
            }
        });

    }

    public final void LaunchFacebook() {
        final String urlFb = GlobalConst.UrlFacebook + GlobalConst.PageIdFacebook;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(urlFb));

        // If a Facebook app is installed, use it. Otherwise, launch
        // a browser
        final PackageManager packageManager = getPackageManager();
        List<ResolveInfo> list =
                packageManager.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() == 0) {
            final String urlBrowser = GlobalConst.UrlBrowserFacebook + GlobalConst.PageIdFacebook;
            intent.setData(Uri.parse(urlBrowser));
        }
        startActivity(intent);
    }

    //Back to previous fragment when press back button in Actionbar
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}