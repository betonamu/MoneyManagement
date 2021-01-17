package com.example.doanjava.ui.notifications;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.doanjava.R;
import com.example.doanjava.common.GlobalConst;
import com.example.doanjava.common.GlobalFuc;
import com.example.doanjava.data.model.ExpenseCategoryModel;
import com.example.doanjava.data.model.ExpenseModel;
import com.example.doanjava.helpers.CurrencyEditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UpdateHistoryActivity extends AppCompatActivity {

    String categoryId, createAt, id, description;
    Double value;
    private Spinner spinnerMoney;
    private Button btnUpdate;
    private EditText txtValueMoney, txtDescription, txtCreateAt;
    private DatePickerDialog picker;
    private List<ExpenseCategoryModel> lstExpenseCategory = new ArrayList<>();
    SimpleDateFormat dateFormat = new SimpleDateFormat(GlobalConst.DateMonthYearFormat, Locale.ENGLISH);

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_history);
        GetDataIntent(getIntent());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spinnerMoney = findViewById(R.id.spinner_category_update);
        txtDescription = (EditText) findViewById(R.id.description_update);
        txtValueMoney = (CurrencyEditText) findViewById(R.id.value_money_update);
        btnUpdate = (Button) findViewById(R.id.btn_update);
        txtCreateAt = (EditText) findViewById(R.id.create_at_update);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        db.collection(GlobalConst.ExpenseCategoriesTable).orderBy("Id").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            lstExpenseCategory = task.getResult().toObjects(ExpenseCategoryModel.class);
                            try {
                                ArrayAdapter<ExpenseCategoryModel> adapter = new ArrayAdapter<ExpenseCategoryModel>(UpdateHistoryActivity.this,
                                        android.R.layout.simple_spinner_item, lstExpenseCategory);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinnerMoney.setAdapter(adapter);
                                spinnerMoney.setSelection(Integer.parseInt(categoryId) - 1);
                            } catch (Exception e) {
                                //Toast.makeText(getActivity(), "Error when load data", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection(GlobalConst.ExpensesTable).whereEqualTo("Id", id)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<DocumentSnapshot> lstDocumentId = task.getResult().getDocuments();
                        String currentDocumentId = lstDocumentId.get(0).getId();

                        Date dateParse = null;
                        try {
                            dateParse = dateFormat.parse(txtCreateAt.getText().toString());
                        } catch (ParseException e) {
                            Toast.makeText(UpdateHistoryActivity.this, "The date is not in the correct format", Toast.LENGTH_LONG).show();
                        }
                        //Make timeStamp from date
                        Timestamp createDate = new Timestamp(dateParse);
                        String[] splitBalance = txtValueMoney.getText().toString().split(" ");

                        /*input value have "," character
                        need remove "," character to parse double*/
                        final Double valueMoney = Double.parseDouble(splitBalance[1].replace(",", ""));
                        description = txtDescription.getText().toString();
                        categoryId = ((ExpenseCategoryModel)spinnerMoney.getSelectedItem()).Id;

                        ExpenseModel expenseModel = new ExpenseModel(id, valueMoney, categoryId, description, createDate, "");

                        //update data to FireStore
                        UpdateHistoryExpense(currentDocumentId, expenseModel);
                    }
                });
            }
        });

//        spinnerMoney.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                categoryId = lstExpenseCategory.get(position).Id;
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
        spinnerMoney.setSelection(Integer.parseInt(categoryId));
        //Set current values of item has clicked in HistoryActivity
        SetTextForControls();
    }

    public void GetDataIntent(Intent data) {
        id = data.getStringExtra("Id");
        categoryId = data.getStringExtra("CategoryId");
        createAt = data.getStringExtra("CreateAt");
        description = data.getStringExtra("Description");
        value = data.getDoubleExtra("Value", 0.0);
    }

    public void SetTextForControls() {
        txtValueMoney.setText("VND " + GlobalFuc.CurrencyFormat(value));
        txtDescription.setText(description);
        txtCreateAt.setText(createAt);
    }

    public void UpdateHistoryExpense(String documentId, ExpenseModel expense) {
        db.collection(GlobalConst.ExpensesTable).document(documentId).update("CategoryId", expense.CategoryId,
                "CreateAt", expense.CreateAt, "Description",
                expense.Description, "Value", expense.Value).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    GlobalFuc.DialogShowMessage(UpdateHistoryActivity.this,
                            getResources().getString(R.string.app_name), "aaaa");
                }
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