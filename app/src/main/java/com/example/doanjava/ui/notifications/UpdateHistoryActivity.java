package com.example.doanjava.ui.notifications;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.doanjava.R;
import com.example.doanjava.common.GlobalConst;
import com.example.doanjava.common.GlobalFuc;
import com.example.doanjava.data.model.ExpenseCategoryModel;
import com.example.doanjava.helpers.CurrencyEditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

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

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_history);
        GetDataIntent(getIntent());

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
        txtValueMoney.setText(GlobalFuc.CurrencyFormat(value));
        txtDescription.setText(description);
        txtCreateAt.setText(createAt);
    }

}