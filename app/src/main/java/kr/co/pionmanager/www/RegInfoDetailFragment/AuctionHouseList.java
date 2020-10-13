package kr.co.pionmanager.www.RegInfoDetailFragment;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import kr.co.pionmanager.www.ExplainDialog.ExplainAuctionPick;
import kr.co.pionmanager.www.OnSingleClickListener;
import kr.co.pionmanager.www.R;
import kr.co.pionmanager.www.RegInfoDetail;
import kr.co.pionmanager.www.databinding.AuctionHouseListBinding;

public class AuctionHouseList extends Fragment {
    private ExplainAuctionPick explainAuctionPick;
    private static final String TAG = "AuctionHouseList";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        AuctionHouseListBinding binding = DataBindingUtil.inflate(inflater, R.layout.auction_house_list, container, false);
        RegInfoDetail.regInfoDetail.recyclerView = binding.recyclerViewAuctionHouse;
        Log.e(TAG, "onCreateView: AuctionHouseList recyclerview : " + RegInfoDetail.regInfoDetail.recyclerView );
        Log.e(TAG, "onCreateView: " );
        binding.ibtnexplain.setOnClickListener(v -> showExplain() );

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        return binding.getRoot();
    }

    private void showExplain(){
        explainAuctionPick = new ExplainAuctionPick(getActivity(), positiveListener);
        Objects.requireNonNull(explainAuctionPick.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        explainAuctionPick.show();

        //디스플레이 해상도 가져오기
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        Window window = explainAuctionPick.getWindow();
        int x = (int) (size.x * 0.95f);
        int y = (int) (size.y * 0.8f);
        window.setLayout(x, y);

    }

    private OnSingleClickListener positiveListener = new OnSingleClickListener() {
        @Override
        public void onSingleClick(View v) {
            explainAuctionPick.dismiss();
        }
    };


}
