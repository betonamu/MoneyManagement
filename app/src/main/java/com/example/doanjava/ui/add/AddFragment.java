package com.example.doanjava.ui.add;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.doanjava.R;
import com.example.doanjava.data.model.ExpenseCategoryModel;
import com.example.doanjava.data.model.ExpenseModel;
import com.example.doanjava.interfaces.ICallBackFireStore;
import com.example.doanjava.ui.authentication.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class AddFragment extends Fragment {

    private AddViewModel addViewModel;
    private FirebaseFirestore db;
    private Spinner spinnerMoney;
    private Button btnSave;
    private EditText txtValueMoney, txtDescription, txtCreateAt;
    private FirebaseAuth firebaseAuth;
    private DatePickerDialog picker;
    private List<ExpenseCategoryModel> lstExpenseCategory = new ArrayList<>();
    private ExpenseCategoryModel expenseCategoryModel = new ExpenseCategoryModel();
    private String categoryId;
    private int maxId;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        addViewModel = new ViewModelProvider(this).get(AddViewModel.class);
        View root = inflater.inflate(R.layout.fragment_add, container, false);
        setHasOptionsMenu(true);
        FirebaseApp.initializeApp(getActivity());

        //Binding Java variables with controls in XML
        spinnerMoney = root.findViewById(R.id.spinner_category);
        txtDescription = (EditText) root.findViewById(R.id.description);
        txtValueMoney = (EditText) root.findViewById(R.id.value_money);
        btnSave = (Button) root.findViewById(R.id.btn_save);
        txtCreateAt = (EditText) root.findViewById(R.id.create_at);
        btnSave.setOnClickListener(onSave);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //Load data from FireStore to fill in Spinner category
        db.collection("ExpenseCategories").orderBy("Id").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            lstExpenseCategory = task.getResult().toObjects(ExpenseCategoryModel.class);
                            try {
                                ArrayAdapter<ExpenseCategoryModel> adapter = new ArrayAdapter<ExpenseCategoryModel>(getActivity(),
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

        //Get position of spinner expense category
        spinnerMoney.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoryId = lstExpenseCategory.get(position).Id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
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
                picker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        txtCreateAt.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                picker.show();
            }
        });

        //Test query in Firebase FireStore
//        db.collection("Expense").whereGreaterThan("Id", "1")
//                .orderBy("Id").get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d("A", document.getId() + " => " + document.getData());
//                            }
//                        }
//                    }
//                });

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
                    Date dateParse = null;
                    try {
                        dateParse = dateFormat.parse(txtCreateAt.getText().toString());
                    } catch (ParseException e) {
                        Toast.makeText(getActivity(), "The date is not in the correct format", Toast.LENGTH_LONG).show();
                    }
                    Timestamp createDate = new Timestamp(dateParse);
                    String description = txtDescription.getText().toString();
                    int maxId = Integer.parseInt(value.toString());
                    String id = (maxId + 1) + "";

                    //Add data to model Expense
                    ExpenseModel expense = new ExpenseModel(id, valueMoney, categoryId, description, createDate, userId);

                    //Push data to FireStore
                    db.collection("Expense").document().set(expense);
                }
            });
        }
    };

    //Get max current id in FireStore
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
