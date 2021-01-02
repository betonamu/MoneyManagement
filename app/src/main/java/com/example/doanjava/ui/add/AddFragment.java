package com.example.doanjava.ui.add;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.doanjava.R;
import com.example.doanjava.data.model.ExpenseCategoryModel;
import com.example.doanjava.data.model.ExpenseModel;
import com.example.doanjava.interfaces.ICallBackFireStore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class AddFragment extends Fragment {

    private AddViewModel addViewModel;
    private FirebaseFirestore db;
    Spinner spinnerMoney;
    Button btnSave;
    EditText txtValueMoney, txtDescription;
    FirebaseAuth firebaseAuth;
    List<ExpenseCategoryModel> lstExpenseCategory = new ArrayList<>();
    ExpenseCategoryModel expenseCategoryModel = new ExpenseCategoryModel();
    String categoryId;
    int maxId;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        addViewModel = new ViewModelProvider(this).get(AddViewModel.class);
        View root = inflater.inflate(R.layout.fragment_add, container, false);
        setHasOptionsMenu(true);

        spinnerMoney = root.findViewById(R.id.spinner_category);
        txtDescription = (EditText) root.findViewById(R.id.description);
        txtValueMoney = (EditText) root.findViewById(R.id.value_money);
        btnSave = (Button) root.findViewById(R.id.btn_save);
        btnSave.setOnClickListener(onSave);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        db.collection("ExpenseCategories").orderBy("Id").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            lstExpenseCategory = task.getResult().toObjects(ExpenseCategoryModel.class);

                            ArrayAdapter<ExpenseCategoryModel> adapter = new ArrayAdapter<ExpenseCategoryModel>(getActivity(),
                                    android.R.layout.simple_spinner_item, lstExpenseCategory);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerMoney.setAdapter(adapter);

                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
        spinnerMoney.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoryId = lstExpenseCategory.get(position).Id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        db.collection("Expense").whereGreaterThan("Id", "1")
                .orderBy("Id").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("A", document.getId() + " => " + document.getData());
                            }
                        }
                    }
                });

        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_add, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public View.OnClickListener onSave = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getMaxIdExpense(new ICallBackFireStore<Object>() {
                @Override
                public void onCallBack(List<Object> lstObject, Object value) {
                    String userId = firebaseAuth.getCurrentUser().getUid();
                    Double valueMoney = Double.parseDouble(txtValueMoney.getText().toString());
                    Date createDate = new Date(System.currentTimeMillis());
                    String description = txtDescription.getText().toString();
                    int maxId = Integer.parseInt(value.toString());
                    String id = (maxId + 1) + "";
                    ExpenseModel expense = new ExpenseModel(id, valueMoney, categoryId, description, createDate, userId);

                    //Push data to FireStore
                    db.collection("Expense").document().set(expense);
                }
            });
        }
    };

    public void getMaxIdExpense(ICallBackFireStore callBack) {
        db.collection("Expense").orderBy("Id").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isComplete()) {
                            maxId = task.getResult().size();
                        }
                        callBack.onCallBack(null, maxId);
                    }
                });
    }
}
