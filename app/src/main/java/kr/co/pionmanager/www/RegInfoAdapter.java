package kr.co.pionmanager.www;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class RegInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "RegInfoAdapter";
    private ArrayList<RegistrationData> data;
    private final int TYPE_HEADER = 0;
    private final int TYPE_ITEM = 1;

    public RegInfoAdapter(ArrayList<RegistrationData> data) {
        this.data = data;
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {

        public View header_blank;

        HeaderViewHolder(View headerView) {
            super(headerView);
            header_blank = headerView.findViewById(R.id.header_blank);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_listView_reg_name;
        TextView tv_hope_loan_price;
        TextView tv_hope;
        TextView tv_status;
        TextView tv_auctionCompleteDate_date;
        ImageView iv_status_color;

        ViewHolder(View view) {
            super(view);
            this.tv_listView_reg_name = view.findViewById(R.id.tv_listView_reg_name);
            this.tv_hope_loan_price = view.findViewById(R.id.tv_hope_loan_price);
            this.tv_hope = view.findViewById(R.id.tv_hope);
            this.tv_status = view.findViewById(R.id.tv_status);
            this.tv_auctionCompleteDate_date = view.findViewById(R.id.tv_auctionCompleteDate_date);
            this.iv_status_color = view.findViewById(R.id.iv_status_color);

            view.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    if (data.size() > 0) {

                        RegistrationListPage.registrationListPage.registrationInfo_all_taskKill();
                        int position = getAdapterPosition() - 1;
                        String str = String.valueOf(data.get(position).getLoanNum());
                        UserInfo.setLoanNum(str);
                        Util.intentNext(RegInfoDetail.class);
                    }
                }

            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        RecyclerView.ViewHolder vh;

        if (viewType == TYPE_HEADER) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_header, parent, false);
            vh = new HeaderViewHolder(v);
            return vh;
        } else if (viewType == TYPE_ITEM) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.reg_info_list, parent, false);
            vh = new ViewHolder(v);
            return vh;
        } else {
            return null;
        }
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int itemPosition = position - 1;
//        position = position -1;
        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.header_blank.setVisibility(View.VISIBLE);
        } else if (holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;
            if (data.size() > 0) {

                viewHolder.tv_listView_reg_name.setText(data.get(itemPosition).getName());

                int auctionHouseCount = data.get(itemPosition).getAuctionHouseCount();
                if (auctionHouseCount > 0 && data.get(itemPosition).getStatus().equals("입찰")) {
                    viewHolder.tv_status.setText(data.get(itemPosition).getStatus() + "(" + auctionHouseCount + ")");
                } else {
                    viewHolder.tv_status.setText(data.get(itemPosition).getStatus());
                }

                switch (data.get(itemPosition).getStatus()) {
                    case "등록":
                    case "입찰":
                        viewHolder.tv_hope_loan_price.setText(Util.GetNumFormat(Integer.parseInt(data.get(itemPosition).getHopeLoanPrice())));
                        viewHolder.tv_hope.setText("희망대출금");
                        viewHolder.tv_auctionCompleteDate_date.setText(data.get(itemPosition).getAuctionCompleteDate());
                        viewHolder.tv_auctionCompleteDate_date.setVisibility(View.VISIBLE);
                        break;
                    case "취소됨":
                    case "유찰":
                        viewHolder.tv_hope_loan_price.setText(Util.GetNumFormat(Integer.parseInt(data.get(itemPosition).getHopeLoanPrice())));
                        viewHolder.tv_hope.setText("희망대출금");
                        break;
                    default:
                        viewHolder.tv_hope_loan_price.setText(Util.GetNumFormat(Integer.parseInt(data.get(itemPosition).getMaxPrice())));
                        viewHolder.tv_hope.setText("대출금");
                        viewHolder.tv_auctionCompleteDate_date.setVisibility(View.GONE);
                        break;
                }

                switch (data.get(itemPosition).getColor()) {
                    case "b":
                        viewHolder.iv_status_color.setBackgroundResource(R.drawable.status_circle_ing);
                        break;
                    case "g":
                        viewHolder.iv_status_color.setBackgroundResource(R.drawable.status_circle_finish);
                        viewHolder.tv_auctionCompleteDate_date.setVisibility(View.GONE);
                        break;
                    case "r":
                        viewHolder.iv_status_color.setBackgroundResource(R.drawable.status_circle_cancel);
                        viewHolder.tv_auctionCompleteDate_date.setVisibility(View.GONE);
                        break;
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return data.size() + 1;
    }

}
