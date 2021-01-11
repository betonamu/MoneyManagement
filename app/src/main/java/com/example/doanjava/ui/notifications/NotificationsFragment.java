package com.example.doanjava.ui.notifications;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.doanjava.R;
import com.example.doanjava.common.GlobalConst;
import com.example.doanjava.common.GlobalFuc;
import com.example.doanjava.data.model.UserModel;
import com.example.doanjava.interfaces.ICallBackFireStore;
import com.example.doanjava.ui.authentication.LoginActivity;
import com.example.doanjava.ui.authentication.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Currency;
import java.util.LinkedList;
import java.util.List;


public class NotificationsFragment extends Fragment {

    private static final int RESULT_OK = -1;
    private NotificationsViewModel notificationsViewModel;
    private String[] arrItemAccount;
    private ListItemAccountAdapter adapter;
    private List<UserModel> lstUser = new LinkedList<>();
    private ListView listView;
    private ImageView imgAccount;
    private TextView tvFullName, tvBalance;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    FirebaseStorage storage;

    final int REQUEST_CODE = 2;
    static int PReqCode = 1;
    Uri pickedImgUri;

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
                    imageView.setImageResource(R.drawable.information);
                    break;
                case 4:
                    imageView.setImageResource(R.drawable.logout);
                    break;
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
        tvBalance = (TextView) root.findViewById(R.id.tv_balance);
        arrItemAccount = getActivity().getResources().getStringArray(R.array.list_item_account);
        adapter = new ListItemAccountAdapter(getActivity());
        listView.setAdapter(adapter);

        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        startActivity(new Intent(getActivity(), InformationUserActivity.class));
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        startActivity(new Intent(getActivity(), HistoryActivity.class));
                        break;
                    case 4:
                        startActivity(new Intent(getActivity(),AboutUsActivity.class));
                        break;
                    case 5:
                        SignOut();
                        break;
                }
            }
        });

        GetUserInformation(new ICallBackFireStore<UserModel>() {
            @Override
            public void onCallBack(List<UserModel> lstObject, Object value) {
                if (lstObject.size() != 0) {
                    tvFullName.setText(lstObject.get(0).fullName);
                    String balance;
                    if(lstObject.get(0).balance != null)
                        balance  = GlobalFuc.CurrencyFormat(lstObject.get(0).balance);
                    else
                        balance = "0";
                    tvBalance.setText("Số dư: " + balance + " VND");
                    if (lstObject.get(0).photoUrl != null) {
                        Uri photoUri = Uri.parse(lstObject.get(0).photoUrl);
                        //set image from Uri into view
                        Glide.with(root)
                                .load(photoUri)
                                .apply(RequestOptions.circleCropTransform())
                                .into(imgAccount);
                    }
                }
            }
        });

        imgAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 22) {
                    checkAndRequestForPermission();
                } else {
                    openGallery();
                }
            }
        });

        return root;
    }

    public void SignOut() {
        firebaseAuth.signOut();
        startActivity(new Intent(getActivity(), LoginActivity.class));
    }

    public void GetUserInformation(ICallBackFireStore callBack) {
        db.collection(GlobalConst.UsersTable).document(firebaseAuth.getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    UserModel userModel = task.getResult().toObject(UserModel.class);
                    if (userModel != null) {
                        lstUser.add(userModel);
                    }
                }
                callBack.onCallBack(lstUser, null);
            }
        });
    }

    private void openGallery() {
        //TODO: open gallery intent and wait for user to pick an image !
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUEST_CODE);
    }

    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(getActivity(), "Please accept for required permission", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PReqCode);
            }
        } else {
            openGallery();
        }
    }

    public void uploadPhoto(ICallBackFireStore callBack) {
        if (pickedImgUri != null) {
            Calendar calendar = Calendar.getInstance();
            StorageReference mountainsRef = storage.getReferenceFromUrl(GlobalConst.UrlUploadFileStorage).child("image" + calendar.getTimeInMillis());
            mountainsRef.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUri = uri;
                            callBack.onCallBack(null, downloadUri);
                        }
                    });
                }
            });
        } else {
            callBack.onCallBack(null, null);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE && data != null) {
            // the user has successfully picked an image
            // we need to save its reference to a Uri variable
            pickedImgUri = data.getData();
            uploadPhoto(new ICallBackFireStore() {
                @Override
                public void onCallBack(List lstObject, Object value) {
                    //set image from Uri into view
                    Uri photoUri = Uri.parse(value.toString());
                    Glide.with(getActivity())
                            .load(photoUri)
                            .apply(RequestOptions.circleCropTransform())
                            .into(imgAccount);
                    db.collection(GlobalConst.UsersTable).document(firebaseAuth.getCurrentUser().getUid())
                            .update("photoUrl", value.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Update Image Successfully", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        }
    }
}