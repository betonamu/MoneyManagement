package com.example.doanjava.ui.notifications;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.doanjava.R;
import com.example.doanjava.common.GlobalConst;
import com.example.doanjava.common.GlobalFuc;
import com.example.doanjava.data.model.ExpenseCategoryModel;
import com.example.doanjava.data.model.ExpenseModel;
import com.example.doanjava.helpers.CurrencyEditText;
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

public class UpdateHistoryActivity extends AppCompatActivity {
    private Spinner spinnerMoney;
    private Button btnUpdate;
    private EditText txtValueMoney, txtDescription, txtCreateAt;
    private DatePickerDialog picker;
    private ImageView imgExpense;

    //Shared variables
    private List<ExpenseCategoryModel> lstExpenseCategory;
    private SimpleDateFormat dateFormat;
    private String categoryId, createAt, id, description, photoUri;
    private Double value;
    private Uri pickedImgUri;
    private final int REQUEST_CODE = 2;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_history);
        GetDataIntent(getIntent());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lstExpenseCategory = new ArrayList<>();
        dateFormat = new SimpleDateFormat(GlobalConst.DateMonthYearFormat, Locale.ENGLISH);

        //Binding Java variables with controls in XML
        spinnerMoney = findViewById(R.id.spinner_category_update);
        txtDescription = (EditText) findViewById(R.id.description_update);
        txtValueMoney = (CurrencyEditText) findViewById(R.id.value_money_update);
        btnUpdate = (Button) findViewById(R.id.btn_update);
        txtCreateAt = (EditText) findViewById(R.id.create_at_update);
        imgExpense = (ImageView) findViewById(R.id.img_expense_update);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

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

                        if (TextUtils.isEmpty(txtValueMoney.getText().toString().trim())) {
                            txtValueMoney.setError(getResources().getString(R.string.required_money));
                            return;
                        }
                        if (TextUtils.isEmpty(txtCreateAt.getText().toString().trim())) {
                            txtValueMoney.setError(getResources().getString(R.string.required_date));
                            return;
                        }

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
                        categoryId = ((ExpenseCategoryModel) spinnerMoney.getSelectedItem()).Id;

                        uploadPhoto(new ICallBackFireStore() {
                            @Override
                            public void onCallBack(List lstObject, Object value) {
                                if (value != null && value != "") {
                                    photoUri = value.toString();
                                }
                                /*
                                Add data to model Expense
                                UserId does not need to be updated so it can be null
                                 */
                                ExpenseModel expenseModel = new ExpenseModel(id, valueMoney, categoryId,
                                        description, createDate, "", photoUri);

                                //update data to FireStore
                                UpdateHistoryExpense(currentDocumentId, expenseModel);
                            }
                        });
                    }
                });
            }
        });

        //show DatePicker to select date when focus Edit Text "Enter date"
        txtCreateAt.setInputType(InputType.TYPE_NULL);
        txtCreateAt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                picker = new DatePickerDialog(UpdateHistoryActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        txtCreateAt.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                picker.show();
            }
        });

        spinnerMoney.setSelection(Integer.parseInt(categoryId));
        //Set current values of item has clicked in HistoryActivity
        SetTextForControls();

        imgExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }

    public void GetDataIntent(Intent data) {
        id = data.getStringExtra("Id");
        categoryId = data.getStringExtra("CategoryId");
        createAt = data.getStringExtra("CreateAt");
        description = data.getStringExtra("Description");
        value = data.getDoubleExtra("Value", 0.0);
        photoUri = data.getStringExtra("PhotoUri");
    }

    public void SetTextForControls() {
        txtValueMoney.setText("VND " + GlobalFuc.CurrencyFormat(value));
        txtDescription.setText(description);
        txtCreateAt.setText(createAt);
        if (photoUri != null && photoUri != "") {
            Glide.with(this)
                    .load(photoUri)
                    .into(imgExpense);
        } else {
            imgExpense.setImageResource(R.drawable.image_no_available);
        }
    }

    public void UpdateHistoryExpense(String documentId, ExpenseModel expense) {
        db.collection(GlobalConst.ExpensesTable).document(documentId).update("CategoryId", expense.CategoryId,
                "CreateAt", expense.CreateAt, "Description", expense.Description,
                "Value", expense.Value, "PhotoUri", expense.PhotoUri).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    GlobalFuc.DialogShowMessage(UpdateHistoryActivity.this,
                            getResources().getString(R.string.app_name),
                            getResources().getString(R.string.update_expense_successfully));
                }
            }
        });
    }

    private void openGallery() {
        //TODO: open gallery intent and wait for user to pick an image !
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUEST_CODE);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE && data != null) {
            // the user has successfully picked an image
            // we need to save its reference to a Uri variable
            pickedImgUri = data.getData();
            Glide.with(this)
                    .load(pickedImgUri)
                    .into(imgExpense);
        }
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