package com.example.doanjava.ui.dashboard;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.doanjava.R;
import com.example.doanjava.common.GlobalConst;
import com.example.doanjava.common.GlobalFuc;
import com.example.doanjava.data.model.ExpenseModel;
import com.example.doanjava.interfaces.ICallBackFireStore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class DashboardFragment extends Fragment {

    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    List<ExpenseModel> lstHistory;
    private ArrayList arrItemHistory;
    private List<Double> lstUser = new LinkedList<Double>();
    private List lsttest = new LinkedList<>();
    private DashboardViewModel dashboardViewModel;
    private FirebaseStorage storage;
    ListView listViewHistory;
    TextView tvDataEmpty;
    private ListItemHistoryAdapter adapter;



    class ListItemHistoryAdapter extends ArrayAdapter<String> {
        private List<ExpenseModel> _lstItem;
        public ListItemHistoryAdapter(@NonNull Context context, int resource, int textViewResourceId) {
            super(context, resource, textViewResourceId);
        }

        public ListItemHistoryAdapter(List<ExpenseModel> lstItem, Activity activity) {
            super(activity, android.R.layout.simple_list_item_1);

        }
    }
        public View onCreateView(@NonNull LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {
            dashboardViewModel = new ViewModelProvider(DashboardFragment.this).get(DashboardViewModel.class);
            View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

            storage = FirebaseStorage.getInstance();
            db = FirebaseFirestore.getInstance();
            firebaseAuth = FirebaseAuth.getInstance();

            TextView food, living, car, boy,fashion,healthcare;
            PieChart pieChart;

            food = root.findViewById(R.id.food);
            living = root.findViewById(R.id.living);
            car = root.findViewById(R.id.car);
            boy = root.findViewById(R.id.boy);
            fashion = root.findViewById(R.id.fashion);
            healthcare = root.findViewById(R.id.healthcare);
            pieChart = root.findViewById(R.id.piechart);



            final TextView textView = root.findViewById(R.id.txt_id);
            getListHistory(new ICallBackFireStore<ExpenseModel>(){
                @Override
                public void onCallBack(List<ExpenseModel> lstObject, Object value) {
                    double number_food = 0;
                    double number_living = 0;
                    double number_car = 0;
                    double number_boy = 0;
                    double number_fashion = 0;
                    double number_healthcare = 0;

                    String Food,Living,Car,Boy,Fashion,Healthcare = "0";

                    if (lstObject.size() != 0) {
                        for (ExpenseModel item : lstObject) {
                            switch (item.CategoryId)
                            {
                                case "1":
                                    number_food += item.Value;
                                    break;
                                case "2":
                                    number_living += item.Value;
                                    break;
                                case "3":
                                    number_car += item.Value;

                                    break;
                                case "4":
                                    number_boy += item.Value;

                                    break;
                                case "5":
                                    number_fashion += item.Value;
                                    break;
                                case "6":
                                    number_healthcare += item.Value;
                                    break;
                            }
                            // Set the percentage of language used
                        }
                        double sum = number_food + number_boy + number_car + number_fashion + number_living + number_healthcare;

                        Food = GlobalFuc.CurrencyFormat(number_food);
                        Living = GlobalFuc.CurrencyFormat(number_living);
                        Car = GlobalFuc.CurrencyFormat(number_car);
                        Boy = GlobalFuc.CurrencyFormat(number_boy);
                        Fashion = GlobalFuc.CurrencyFormat(number_fashion);
                        Healthcare = GlobalFuc.CurrencyFormat(number_healthcare);

                        food.setText(Food + " VND");
                        living.setText(Living +" VND");
                        car.setText(Car+" VND");
                        healthcare.setText(Healthcare+" VND");
                        boy.setText(Boy +" VND");
                        fashion.setText(Fashion +" VND");

                                // Set the data and color to the pie chart
                        pieChart.addPieSlice(
                                new PieModel(
                                        "food",
                                        (float) Double.parseDouble(String.valueOf(number_food)),
                                        Color.parseColor("#FFA726")));
                        pieChart.addPieSlice(
                                new PieModel(
                                        "living",
                                        (float) Double.parseDouble(String.valueOf(number_living)),

                                        Color.parseColor("#66BB6A")));
                        pieChart.addPieSlice(
                                new PieModel(
                                        "car",
                                        (float) Double.parseDouble(String.valueOf(number_car)),


                                        Color.parseColor("#EF5350")));
                        pieChart.addPieSlice(
                                new PieModel(
                                        "boy",
                                        (float) Double.parseDouble(String.valueOf(number_boy)),


                                        Color.parseColor("#29B6F6")));
                        pieChart.addPieSlice(
                                new PieModel(
                                        "fashion",
                                        (float) Double.parseDouble(String.valueOf(number_fashion)),


                                        Color.parseColor("#FF00FF")));
                        pieChart.addPieSlice(
                                new PieModel(
                                        "healthcare",
                                        (float) Double.parseDouble(String.valueOf(number_healthcare)),
                                        Color.parseColor("#C0C0C0")));
                        // To animate the pie chart
                        pieChart.startAnimation();
                    }
                }
            });
            return root;
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
    }





//    SimpleDateFormat dateFormat = new SimpleDateFormat(GlobalConst.DateMonthYearFormat, Locale.getDefault());
//
//    //Get data from collection "Expense" in FireStore
//    public void getListHistory(ICallBackFireStore callBack) {
//        db.collection(GlobalConst.ExpensesTable)
//                .whereEqualTo("UserId", firebaseAuth.getCurrentUser().getUid())
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            lstHistory = task.getResult().toObjects(ExpenseModel.class);
//                        }
//                        callBack.onCallBack(lstHistory, null);
//                    }
//                });
//    }
//
//    private DashboardViewModel dashboardViewModel;
//    TextView tvR, tvPython, tvCPP, tvJava;
//    PieChart pieChart;
//
//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             ViewGroup container, Bundle savedInstanceState) {
//        dashboardViewModel =
//                new ViewModelProvider(DashboardFragment.this).get(DashboardViewModel.class);
//        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
//
////        final TextView textView = root.findViewById(R.id.txt_id);
////        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
////            @Override
////            public void onChanged(@Nullable String s) {
////                textView.setText(s);
////            }
////        });
//        PieChart pieChart;
//        tvR = root.findViewById(R.id.tvR);
//        tvPython = root.findViewById(R.id.tvPython);
//        tvCPP = root.findViewById(R.id.tvCPP);
//        tvJava = root.findViewById(R.id.tvJava);
//        pieChart = root.findViewById(R.id.piechart);
//
//        // Set the percentage of language used
//        tvR.setText(Integer.toString(40));
//        tvPython.setText(Integer.toString(30));
//        tvCPP.setText(Integer.toString(5));
//        tvJava.setText(Integer.toString(25));
//
//        // Set the data and color to the pie chart
//        pieChart.addPieSlice(
//                new PieModel(
//                        "R",
//                        Integer.parseInt(tvR.getText().toString()),
//                        Color.parseColor("#FFA726")));
//        pieChart.addPieSlice(
//                new PieModel(
//                        "Python",
//                        Integer.parseInt(tvPython.getText().toString()),
//                        Color.parseColor("#66BB6A")));
//        pieChart.addPieSlice(
//                new PieModel(
//                        "C++",
//                        Integer.parseInt(tvCPP.getText().toString()),
//                        Color.parseColor("#EF5350")));
//        pieChart.addPieSlice(
//                new PieModel(
//                        "Java",
//                        Integer.parseInt(tvJava.getText().toString()),
//                        Color.parseColor("#29B6F6")));
//        // To animate the pie chart
//        pieChart.startAnimation();
//        return root;
//    }
//}