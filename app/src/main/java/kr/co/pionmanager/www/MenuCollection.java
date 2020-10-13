package kr.co.pionmanager.www;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kr.co.pionmanager.www.Menu.Analystic;
import kr.co.pionmanager.www.Menu.BoardPager;
import kr.co.pionmanager.www.Menu.CustomerInvite;
import kr.co.pionmanager.www.Menu.CustomerList;
import kr.co.pionmanager.www.Menu.InviteKaKao;
import kr.co.pionmanager.www.Menu.MyInfo.MyInfo;
import kr.co.pionmanager.www.Menu.Payment.MenuCommissionExplanation;
import kr.co.pionmanager.www.Menu.TeamList;
import kr.co.pionmanager.www.PionIntroduce.IntroduceParent;
import kr.co.pionmanager.www.databinding.ActivityMenuCollectionBinding;

public class MenuCollection extends Fragment implements View.OnClickListener{
    private ActivityMenuCollectionBinding b;
    public static MenuCollection menuCollection;
    public static MenuCollection singlton;
    private long backBtnTime = 0;
    private static final String TAG = "MenuCollection";

    public static MenuCollection newInstance() {
        if (singlton == null) {
            singlton = new MenuCollection();
        }
        return singlton;
    }

    public void clearSingleTon(){
        this.singlton = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        b = DataBindingUtil.inflate(inflater, R.layout.activity_menu_collection, container, false);
        menuCollection = this;

        if (UserInfo.getUserGrade() == 1 && UserInfo.getRefManagerGrade().equals("3")) {
            b.tvGradeName.setText("매니저님");
            Log.e(TAG, "onCreateView: 비즈파트너 직원" );
            b.teamListWrap.setVisibility(View.GONE);
            b.analysticWrap.setVisibility(View.GONE);
            b.commissionExplainWrap.setVisibility(View.GONE);
        }else if (UserInfo.getUserGrade() == 1) {
            b.tvGradeName.setText("매니저님");
            b.textTeamList.setText("추천인");
        } else if (UserInfo.getUserGrade() == 2) {
            b.tvGradeName.setText("비즈매니저님");
            b.textTeamList.setText("팀원리스트");
        } else if (UserInfo.getUserGrade() == 3) {
            b.tvGradeName.setText("비즈파트너님");
            b.textTeamList.setText("팀원리스트");
        }

        b.tvName.setText(UserInfo.getUserName());


        b.btnPrivacy.setOnClickListener(this);
        b.managerInviteWrap.setOnClickListener(this);
        b.customerInviteWrap.setOnClickListener(this);
        b.teamListWrap.setOnClickListener(this);
        b.customerWrap.setOnClickListener(this);
        b.boardWrap.setOnClickListener(this);
        b.faqWrap.setOnClickListener(this);
        b.analysticWrap.setOnClickListener(this);
        b.WhatIsManagerWrap.setOnClickListener(this);
        b.commissionExplainWrap.setOnClickListener(this);

        return b.getRoot();
    }

    @Override
    public void onResume() {
        Util.CURRENT_CONTEXT = getActivity();
        super.onResume();
    }

    public void moveWebBoard(String title) {
        Intent intent = new Intent(getActivity(), BoardPager.class);
        intent.putExtra("page", title);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPrivacy:
                Util.intentNext(MyInfo.class);
                break;
            case R.id.managerInviteWrap:
                Util.intentNext(InviteKaKao.class);
                break;
            case R.id.customerInviteWrap:
                Util.intentNext(CustomerInvite.class);
                break;
            case R.id.teamListWrap:
                Util.intentNext(TeamList.class);
                break;
            case R.id.customerWrap:
                Util.intentNext(CustomerList.class);
                break;
            case R.id.boardWrap:
                MenuCollection.menuCollection.moveWebBoard("board");
                break;
            case R.id.faqWrap:
                MenuCollection.menuCollection.moveWebBoard("faq");
                break;
            case R.id.analysticWrap:
                Util.intentNext(Analystic.class);
                break;
            case R.id.WhatIsManagerWrap:
                Util.intentNext(IntroduceParent.class);
                break;
            case R.id.commissionExplainWrap:
                Util.intentNext(MenuCommissionExplanation.class);
                break;
        }

    }

}


