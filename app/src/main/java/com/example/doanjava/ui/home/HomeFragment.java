package com.example.doanjava.ui.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.doanjava.MainActivity;
import com.example.doanjava.R;
import com.example.doanjava.common.GlobalConst;
import com.example.doanjava.common.GlobalFuc;
import com.example.doanjava.data.model.UserModel;
import com.example.doanjava.interfaces.ICallBackFireStore;
import com.example.doanjava.ui.loan.LoanActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.LinkedList;
import java.util.List;

public class HomeFragment extends Fragment {
    private FirebaseAuth firebaseAuth;
    private HomeViewModel homeViewModel;
    private List<UserModel> lstUser = new LinkedList<>();
    private TextView tvSurplus;
    ImageButton btnLoand,btnLend,btnFood,btnLiving,btnCar,btnBoy,btnFashion,btnHealthCare,btnWallet;


    private FirebaseFirestore db;
    FirebaseStorage storage;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        tvSurplus = (TextView) root.findViewById(R.id.tvSurplus);
        btnLoand = root.findViewById(R.id.btnLoand);
        btnLend = root.findViewById(R.id.btnLend);
        btnFood = root.findViewById(R.id.btnFood);
        btnLiving = root.findViewById(R.id.btnLiving);
        btnCar = root.findViewById(R.id.btnCar);
        btnBoy = root.findViewById(R.id.btnBoy);
        btnFashion = root.findViewById(R.id.btnFashion);
        btnHealthCare = root.findViewById(R.id.btnHealthCare);
        btnWallet = root.findViewById(R.id.btnWallet);

        btnLoand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), LoanActivity.class));
            }
        });

        btnFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeViewModel.setPositionSpinner("0");
                MainActivity.SwitchFragment(R.id.navigation_add);
            }
        });

        btnLiving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeViewModel.setPositionSpinner("1");
                MainActivity.SwitchFragment(R.id.navigation_add);
            }
        });

        btnCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeViewModel.setPositionSpinner("2");
                MainActivity.SwitchFragment(R.id.navigation_add);
            }
        });

        btnBoy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeViewModel.setPositionSpinner("3");
                MainActivity.SwitchFragment(R.id.navigation_add);
            }
        });

        btnFashion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeViewModel.setPositionSpinner("4");
                MainActivity.SwitchFragment(R.id.navigation_add);
            }
        });

        btnHealthCare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeViewModel.setPositionSpinner("5");
                MainActivity.SwitchFragment(R.id.navigation_add);
            }
        });

        btnWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeViewModel.setPositionSpinner("6");
                MainActivity.SwitchFragment(R.id.navigation_add);
            }
        });

        btnLend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), LoanActivity.class));
            }
        });


        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        GetUserInformation(new ICallBackFireStore<UserModel>() {
            @Override
            public void onCallBack(List<UserModel> lstObject, Object value) {
                if (lstObject.size() != 0) {
                    String balance;
                    if(lstObject.get(0).balance != null)
                        balance  = GlobalFuc.CurrencyFormat(lstObject.get(0).balance);
                    else
                        balance = "0";
                    tvSurplus.setText(balance + " VND");
                }
            }
        });
        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_home,menu);
        super.onCreateOptionsMenu(menu, inflater);
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



}