package com.example.doanjava.ui.notifications;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.doanjava.R;
import com.example.doanjava.data.model.UserModel;
import com.example.doanjava.ui.authentication.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;


public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    private String[] arrItemAccount;
    private ListItemAccountAdapter adapter;
    ListView listView;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db;



    class ListItemAccountAdapter extends ArrayAdapter<String> {
        public ListItemAccountAdapter(@NonNull Context context, int resource, int textViewResourceId) {
            super(context, resource, textViewResourceId);
        }

        public ListItemAccountAdapter(Activity activity) {
            super(activity, android.R.layout.simple_list_item_1, arrItemAccount);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View row = convertView;
            if (row == null) {
                LayoutInflater inflater = getLayoutInflater();
                row = inflater.inflate(R.layout.layout_list_item_account_fragment, null);
            }
            String title = arrItemAccount[position];
            ((TextView) row.findViewById(R.id.title)).setText(title);

            //set image for list item
            ImageView imageView = (ImageView) row.findViewById(R.id.image);

            switch (position) {
                case 0:
                    imageView.setImageResource(R.drawable.user);
                    break;
                case 1:
                    imageView.setImageResource(R.drawable.fashion);
                    break;
                case 2:
                    imageView.setImageResource(R.drawable.history);
                    break;
                case 3:
                    imageView.setImageResource(R.drawable.logout);
             }
            return row;
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        listView = (ListView) root.findViewById(R.id.lsv_notification);
        arrItemAccount = getActivity().getResources().getStringArray(R.array.list_item_account);
        adapter = new ListItemAccountAdapter(getActivity());
        listView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "Position: " + position, Toast.LENGTH_SHORT).show();
                switch (position) {
                    case 0:
                        startActivity(new Intent(getActivity(),InformationUserActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(getActivity(),HistoryActivity.class));
                        break;
                    case 3:
                        SignOut();
                        break;
                }
            }
        });

        return root;
    }

    public void SignOut() {
        firebaseAuth.signOut();
        startActivity(new Intent(getActivity(), LoginActivity.class));
    }

}