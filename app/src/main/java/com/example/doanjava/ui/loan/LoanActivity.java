package com.example.doanjava.ui.loan;

import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doanjava.R;
import com.example.doanjava.common.GlobalConst;
import com.example.doanjava.common.GlobalFuc;
import com.example.doanjava.data.model.ExpenseCategoryModel;
import com.example.doanjava.data.model.ExpenseModel;
import com.example.doanjava.data.model.LoanCategoryModel;
import com.example.doanjava.data.model.LoanModel;
import com.example.doanjava.data.model.UserModel;
import com.example.doanjava.interfaces.ICallBackFireStore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LoanActivity extends AppCompatActivity{
    private Spinner spinnerMoney;
    private EditText txtBorrowing;
    private EditText txtPay_day;
    private DatePickerDialog picker;
    private EditText txtValueMoney;
    private EditText txtdebtor;
    private Button btnSave;
    //initialize shared variables
    private List<LoanCategoryModel> lstLoanCategory;
    private LoanCategoryModel loanCategoryModel;
    private SimpleDateFormat dateFormat;
    private UserModel user;
    Uri pickedImgUri;
    //initialize shared variables
    private int maxId;
    private String currentUserId;
    //initialize firebase object
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loanCategoryModel = new LoanCategoryModel();
        lstLoanCategory = new ArrayList<>();

        dateFormat = new SimpleDateFormat(GlobalConst.DateMonthYearFormat, Locale.ENGLISH);
        spinnerMoney = (Spinner)findViewById(R.id.spinner_category);
        txtBorrowing = (EditText)findViewById(R.id.borrowing);
        txtValueMoney = (EditText) findViewById(R.id.value_money);
        txtPay_day = (EditText)findViewById(R.id.pay_day);
        txtdebtor = (EditText)findViewById(R.id.debtor);
        btnSave = (Button)findViewById(R.id.btnsave);


        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();



        //Load data from FireStore to fill in Spinner category
        db.collection("LoanCategories").orderBy("Id").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            lstLoanCategory = task.getResult().toObjects(LoanCategoryModel.class);
                            try {
                                ArrayAdapter<LoanCategoryModel> adapter = new ArrayAdapter<LoanCategoryModel>(LoanActivity.this,
                                        android.R.layout.simple_spinner_item, lstLoanCategory);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinnerMoney.setAdapter(adapter);
                                int category = getIntent().getIntExtra("Cate",0);
                                spinnerMoney.setSelection(category);

                                //Get data passed from HomeFragment using ViewModel
//                                homeViewModel.getPositionSpinner().observe(getViewLifecycleOwner(), new Observer<String>() {
//                                    @Override
//                                    public void onChanged(String s) {
//                                        spinnerMoney.setSelection(Integer.parseInt(s));
//                                });
                            } catch (Exception e) {
                                //Toast.makeText(getActivity(), "Error when load data", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSave();
            }
        });
        //show DatePicker to select date when focus Edit Text "Enter date"
        txtBorrowing.setInputType(InputType.TYPE_NULL);
        txtBorrowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                picker = new DatePickerDialog(LoanActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        txtBorrowing.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                picker.show();
            }
        });
        txtPay_day.setInputType(InputType.TYPE_NULL);
        txtPay_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                picker = new DatePickerDialog(LoanActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        txtPay_day.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                picker.show();
            }
        });
    }
    public void getMaxIdExpense(ICallBackFireStore callBack) {
        db.collection(GlobalConst.LoanTable).orderBy("Id").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isComplete()) {
                            maxId = task.getResult().size();
                            if (maxId == 0)
                                maxId = 1;
                        }
                        callBack.onCallBack(null, maxId);
                    }
                });
    }
    public void GetBalanceOfCurrentUserToUpdate(ICallBackFireStore callBack) {
        db.collection(GlobalConst.UsersTable).document(currentUserId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    //Converts response data to UserModel
                    user = task.getResult().toObject(UserModel.class);
                }
                //callback value when load data successfully from firebase
                callBack.onCallBack(null, user);
            }
        });
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
    public void UpdateBalanceOfCurrentUser(Double valueMoney, LoanModel loan) {
        GetBalanceOfCurrentUserToUpdate(new ICallBackFireStore() {
            @Override
            public void onCallBack(List lstObject, Object value) {
                Double balanceOfCurrentUser;
                if (((UserModel) value).balance == null) {
                    ((UserModel) value).balance = 0.0;
                }
                if(loan.CategoryId.equals("1"))
                {
                    balanceOfCurrentUser = ((UserModel) value).balance - valueMoney;
                    if (balanceOfCurrentUser < valueMoney) {
                        GlobalFuc.DialogShowMessage(LoanActivity.this, GlobalConst.AppTitle, "Your balance not enough!");
                        return;
                    }
                }
                else {
                    balanceOfCurrentUser = ((UserModel) value).balance + valueMoney;
                    if (balanceOfCurrentUser < valueMoney) {
                        GlobalFuc.DialogShowMessage(LoanActivity.this, GlobalConst.AppTitle, "Your balance not enough!");
                        return;
                    }
                }
                db.collection(GlobalConst.UsersTable).document(currentUserId)
                        .update("balance", balanceOfCurrentUser)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    //Push data to FireStore
                                    db.collection(GlobalConst.LoanTable).document().set(loan);
                                    Toast.makeText(LoanActivity.this, "Save data successfully", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
    public void onSave() {
        getMaxIdExpense(new ICallBackFireStore<Object>() {
            @Override
            public void onCallBack(List<Object> lstObject, Object value) {
                //validate user input
                if (TextUtils.isEmpty(txtValueMoney.getText().toString().trim())) {
                    txtValueMoney.setError("Input money is required!");
                    return;
                }
                if (TextUtils.isEmpty(txtBorrowing.getText().toString().trim())) {
                    txtBorrowing.setError("Date is required!");
                    return;
                }
                if (TextUtils.isEmpty(txtPay_day.getText().toString().trim())) {
                    txtPay_day.setError("Date is required!");
                    return;
                }
                String[] splitBalance = txtValueMoney.getText().toString().split(" ");

                    /*input value have "," character
                    need remove "," character to parse double*/
                final Double valueMoney = Double.parseDouble(splitBalance[1].replace(",", ""));
                Date payday = null;
                try {
                    String a = txtPay_day.getText().toString();
                    payday = dateFormat.parse(txtPay_day.getText().toString());
                } catch (ParseException e) {
                    Toast.makeText(LoanActivity.this, "The date is not in the correct format", Toast.LENGTH_LONG).show();
                }
                Date borrowing = null;
                try {
                    borrowing = dateFormat.parse(txtBorrowing.getText().toString());
                } catch (ParseException e) {
                    Toast.makeText(LoanActivity.this, "The date is not in the correct format", Toast.LENGTH_LONG).show();
                }
                //Make timeStamp from date
                Timestamp createPayday = new Timestamp(payday);
                Timestamp createBorrowing = new Timestamp(borrowing);
                String debtor = txtdebtor.getText().toString();
                int maxId = Integer.parseInt(value.toString());
                String id = (maxId + 1) + "";
                String categoryId = ((LoanCategoryModel) spinnerMoney.getSelectedItem()).Id;

                //set empty for EditTexts after save
                txtValueMoney.setText("");
                txtBorrowing.setText("");
                txtPay_day.setText("");
                txtdebtor.setText("");

                //upload photo and save data expense to fireStore
                uploadPhoto(new ICallBackFireStore() {
                    @Override
                    public void onCallBack(List lstObject, Object value) {
                        String photoUri = "";
                        if (value != null && value != "") {
                            photoUri = value.toString();
                        }
                        //Add data to model Loan
                        LoanModel loan = new LoanModel(id, valueMoney, categoryId,
                                debtor, createPayday,createBorrowing, currentUserId, photoUri);
                        //update data to firebase
                        UpdateBalanceOfCurrentUser(valueMoney, loan);
                    }
                });

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
