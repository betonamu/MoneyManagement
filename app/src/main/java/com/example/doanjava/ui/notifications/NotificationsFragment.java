package com.example.doanjava.ui.notifications;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.doanjava.R;
import com.example.doanjava.data.model.UserModel;
import com.example.doanjava.interfaces.ICallBackFireStore;
import com.example.doanjava.ui.authentication.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    private String[] arrItemAccount;
    private ListItemAccountAdapter adapter;
    List<UserModel> lstUser = new LinkedList<>();
    ListView listView;
    ImageView imgAccount;
    TextView tvFullName;
    FirebaseAuth firebaseAuth;
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
        tvFullName = (TextView) root.findViewById(R.id.tv_fullName_account_fragment);
        imgAccount = (ImageView) root.findViewById(R.id.img_account_fragment);
        arrItemAccount = getActivity().getResources().getStringArray(R.array.list_item_account);
        adapter = new ListItemAccountAdapter(getActivity());
        listView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "Position: " + position, Toast.LENGTH_SHORT).show();
                switch (position) {
                    case 0:
                        startActivity(new Intent(getActivity(), InformationUserActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(getActivity(), HistoryActivity.class));
                        break;
                    case 3:
                        SignOut();
                        break;
                }
            }
        });

        GetUserInformation(new ICallBackFireStore<UserModel>() {
            @Override
            public void onCallBack(List<UserModel> lstObject, Object value) {
                tvFullName.setText(lstObject.get(0).fullName);
                Uri photoUri = Uri.parse(lstObject.get(0).photoUrl);
                Glide.with(root)
                        .load(photoUri)
                        .apply(RequestOptions.circleCropTransform())
                        .into(imgAccount);
            }
        });

        imgAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), InformationUserActivity.class));
            }
        });

        return root;
    }

    public void SignOut() {
        firebaseAuth.signOut();
        startActivity(new Intent(getActivity(), LoginActivity.class));
    }

    public void GetUserInformation(ICallBackFireStore callBack) {
        db.collection("Users").document(firebaseAuth.getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        UserModel userModel = task.getResult().toObject(UserModel.class);
                        lstUser.add(userModel);
                    }
                    callBack.onCallBack(lstUser,null);
            }
        });
    }

}