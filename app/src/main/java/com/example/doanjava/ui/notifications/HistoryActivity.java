package com.example.doanjava.ui.notifications;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import androidx.appcompat.app.AppCompatActivity;

import com.example.doanjava.R;
import com.example.doanjava.common.GlobalConst;
import com.example.doanjava.common.GlobalFuc;
import com.example.doanjava.data.model.ExpenseCategoryModel;
import com.example.doanjava.data.model.ExpenseModel;
import com.example.doanjava.interfaces.ICallBackFireStore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    List<ExpenseModel> lstHistory;
    ListView listViewHistory;
    TextView tvDataEmpty;
    private ListItemHistoryAdapter adapter;
    SimpleDateFormat dateFormat = new SimpleDateFormat(GlobalConst.DateMonthYearFormat, Locale.getDefault());

    class ListItemHistoryAdapter extends ArrayAdapter<ExpenseModel> {
        private List<ExpenseModel> _lstItem;

        public ListItemHistoryAdapter(@NonNull Context context, int resource, int textViewResourceId) {
            super(context, resource, textViewResourceId);
        }

        public ListItemHistoryAdapter(List<ExpenseModel> lstItem) {
            super(HistoryActivity.this, android.R.layout.simple_list_item_1, lstItem);
            _lstItem = lstItem;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.layout_list_item_history, null);

            ExpenseModel item = _lstItem.get(position);
            String valueMoney = GlobalFuc.CurrencyFormat(item.Value);
            String description = item.Description;
            String createAt = dateFormat.format(item.CreateAt.toDate());
            String categoryId = item.CategoryId;

            getExpenseCategoryName(new ICallBackFireStore<ExpenseCategoryModel>() {
                @Override
                public void onCallBack(List<ExpenseCategoryModel> lstObject, Object value) {
                    ((TextView) row.findViewById(R.id.txt_expense_category_history)).setText(lstObject.get(0).Name);
                }
            }, categoryId);

            //((TextView) row.findViewById(R.id.txt_id)).setText(categoryId);
            ((TextView) row.findViewById(R.id.txt_create_at_history)).setText(createAt);
            ((TextView) row.findViewById(R.id.txt_value_history)).setText(valueMoney + " VND");

            switch (item.CategoryId) {
                case "1":
                    ((ImageView) row.findViewById(R.id.image_list_view_history))
                            .setImageResource(R.drawable.food);
                    break;
                case "2":
                    ((ImageView) row.findViewById(R.id.image_list_view_history))
                            .setImageResource(R.drawable.living);
                    break;
                case "3":
                    ((ImageView) row.findViewById(R.id.image_list_view_history))
                            .setImageResource(R.drawable.car);
                    break;
                case "4":
                    ((ImageView) row.findViewById(R.id.image_list_view_history))
                            .setImageResource(R.drawable.boy);
                    break;
                case "5":
                    ((ImageView) row.findViewById(R.id.image_list_view_history))
                            .setImageResource(R.drawable.fashion);
                    break;
                case "6":
                    ((ImageView) row.findViewById(R.id.image_list_view_history))
                            .setImageResource(R.drawable.healthcare);
                    break;
            }
            return row;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listViewHistory = (ListView) findViewById(R.id.list_item_history);
        tvDataEmpty = (TextView) findViewById(R.id.tv_data_empty);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        getListHistory(new ICallBackFireStore<ExpenseModel>() {
            @Override
            public void onCallBack(List<ExpenseModel> lstObject, Object value) {
                if (lstObject.size() != 0) {
                    Collections.sort(lstObject, new Comparator<ExpenseModel>() {
                        @Override
                        public int compare(ExpenseModel o1, ExpenseModel o2) {
                            return o2.CreateAt.compareTo(o1.CreateAt);
                        }
                    });
                    tvDataEmpty.setVisibility(View.GONE);
                    adapter = new ListItemHistoryAdapter(lstObject);
                    listViewHistory.setAdapter(adapter);
                } else {
                    tvDataEmpty.setText("Chưa có dữ liệu");
                    tvDataEmpty.setVisibility(View.VISIBLE);
                }
            }
        });

        listViewHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getListHistory(new ICallBackFireStore<ExpenseModel>() {
                    @Override
                    public void onCallBack(List<ExpenseModel> lstObject, Object value) {
                        //Sort lstObject by day
                        Collections.sort(lstObject, new Comparator<ExpenseModel>() {
                            @Override
                            public int compare(ExpenseModel o1, ExpenseModel o2) {
                                return o2.CreateAt.compareTo(o1.CreateAt);
                            }
                        });

                        ExpenseModel currentItem = lstObject.get(position);

                        Intent intent = new Intent(HistoryActivity.this, UpdateHistoryActivity.class);
                        Bundle bundle = new Bundle();

                        String createDate = dateFormat.format(currentItem.CreateAt.toDate());

                        bundle.putString("Id", currentItem.Id);
                        bundle.putString("CategoryId", currentItem.CategoryId);
                        bundle.putString("CreateAt", createDate);
                        bundle.putString("Description", currentItem.Description);
                        bundle.putDouble("Value", currentItem.Value);
                        intent.putExtras(bundle);

                        startActivity(intent);
                    }
                });
            }
        });
    }

    //Get data from collection "Expense" in FireStore
    public void getListHistory(ICallBackFireStore callBack) {
        db.collection(GlobalConst.ExpensesTable)
                .whereEqualTo("UserId", firebaseAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            lstHistory = task.getResult().toObjects(ExpenseModel.class);
                        }
                        callBack.onCallBack(lstHistory, null);
                    }
                });
    }

    public void getExpenseCategoryName(ICallBackFireStore callBack, String id) {
        db.collection(GlobalConst.ExpenseCategoriesTable).whereEqualTo("Id", id)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<ExpenseCategoryModel> lstItem = null;
                if (task.isSuccessful()) {
                    lstItem = task.getResult().toObjects(ExpenseCategoryModel.class);
                }
                callBack.onCallBack(lstItem, null);
            }
        });
    }


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
