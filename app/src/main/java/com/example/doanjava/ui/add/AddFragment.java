package com.example.doanjava.ui.add;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.InputType;
import android.text.TextUtils;
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
import com.example.doanjava.common.GlobalConst;
import com.example.doanjava.common.GlobalFuc;
import com.example.doanjava.data.model.ExpenseCategoryModel;
import com.example.doanjava.data.model.ExpenseModel;
import com.example.doanjava.data.model.UserModel;
import com.example.doanjava.interfaces.ICallBackFireStore;
import com.example.doanjava.ui.home.HomeViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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

    //initialize controls to binding with view
    private Spinner spinnerMoney;
    private Button btnSave;
    private EditText txtValueMoney, txtDescription, txtCreateAt;
    private DatePickerDialog picker;

    //initialize shared variables
    private List<ExpenseCategoryModel> lstExpenseCategory = new ArrayList<>();
    private ExpenseCategoryModel expenseCategoryModel = new ExpenseCategoryModel();
    private UserModel user;
    SimpleDateFormat dateFormat = new SimpleDateFormat(GlobalConst.DateMonthYearFormat, Locale.ENGLISH);
    private HomeViewModel homeViewModel;

    //initialize shared variables
    private String categoryId;
    private int maxId;
    private String currentUserId;

    //initialize firebase object
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add, container, false);
        setHasOptionsMenu(true);
        FirebaseApp.initializeApp(getActivity());

        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        //Binding Java variables with controls in XML
        spinnerMoney = root.findViewById(R.id.spinner_category);
        txtDescription = (EditText) root.findViewById(R.id.description);
        txtValueMoney = (EditText) root.findViewById(R.id.value_money);
        btnSave = (Button) root.findViewById(R.id.btn_save);
        txtCreateAt = (EditText) root.findViewById(R.id.create_at);
        btnSave.setOnClickListener(onSave);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();

        //Load data from FireStore to fill in Spinner category
        db.collection(GlobalConst.ExpenseCategoriesTable).orderBy("Id").get()
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

                                homeViewModel.getPositionSpinner().observe(getViewLifecycleOwner(), new Observer<String>() {
                                    @Override
                                    public void onChanged(String s) {
                                        spinnerMoney.setSelection(Integer.parseInt(s));
                                    }
                                });
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
                    //validate user input
                    if (TextUtils.isEmpty(txtValueMoney.getText().toString().trim())) {
                        txtValueMoney.setError("Input money is required!");
                        return;
                    }
                    if (TextUtils.isEmpty(txtCreateAt.getText().toString().trim())) {
                        txtValueMoney.setError("Date is required!");
                        return;
                    }
                    String[] splitBalance = txtValueMoney.getText().toString().split(" ");

                    /*input value have "," character
                    need remove "," character to parse double*/
                    final Double valueMoney = Double.parseDouble(splitBalance[1].replace(",", ""));
                    Date dateParse = null;
                    try {
                        dateParse = dateFormat.parse(txtCreateAt.getText().toString());
                    } catch (ParseException e) {
                        Toast.makeText(getActivity(), "The date is not in the correct format", Toast.LENGTH_LONG).show();
                    }
                    //Make timeStamp from date
                    Timestamp createDate = new Timestamp(dateParse);
                    String description = txtDescription.getText().toString();
                    int maxId = Integer.parseInt(value.toString());
                    String id = (maxId + 1) + "";

                    //Add data to model Expense
                    ExpenseModel expense = new ExpenseModel(id, valueMoney, categoryId, description, createDate, currentUserId);

                    //set empty for EditTexts after save
                    txtValueMoney.setText("");
                    txtCreateAt.setText("");
                    txtDescription.setText("");

                    //update data to firebase
                    UpdateBalanceOfCurrentUser(valueMoney, expense);
                }
            });
        }
    };


    //Get max current id in FireStore
    public void getMaxIdExpense(ICallBackFireStore callBack) {
        db.collection(GlobalConst.ExpensesTable).orderBy("Id").get()
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

    //check current user has entered balance or not
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

    public void UpdateBalanceOfCurrentUser(Double valueMoney, ExpenseModel expense) {
        GetBalanceOfCurrentUserToUpdate(new ICallBackFireStore() {
            @Override
            public void onCallBack(List lstObject, Object value) {
                if (((UserModel) value).balance == null) {
                    ((UserModel) value).balance = 0.0;
                }
                Double balanceOfCurrentUser = ((UserModel) value).balance - valueMoney;
                if (balanceOfCurrentUser < valueMoney) {
                    GlobalFuc.DialogShowMessage(getActivity(), GlobalConst.AppTitle, "Your balance not enough!");
                } else
                    db.collection(GlobalConst.UsersTable).document(currentUserId)
                            .update("balance", balanceOfCurrentUser)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        //Push data to FireStore
                                        db.collection(GlobalConst.ExpensesTable).document().set(expense);
                                        Toast.makeText(getActivity(), "Save data successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
            }
        });
    }

}
