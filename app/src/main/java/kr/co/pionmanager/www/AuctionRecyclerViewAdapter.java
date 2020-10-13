package kr.co.pionmanager.www;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

public class AuctionRecyclerViewAdapter extends RecyclerView.Adapter<AuctionRecyclerViewAdapter.ViewHolder> {
    private ArrayList<AuctionListData> data;

    public AuctionRecyclerViewAdapter(ArrayList<AuctionListData> data) {
        this.data = data;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView company_name;
        TextView loanPrice;
        TextView loanRate;
        TextView repayMethod;
        TextView repayFeeRate;
        TextView company_comment;
        ImageView isChecked;
        ImageView company_logo;

        ViewHolder(View view) {
            super(view);
            this.company_name = view.findViewById(R.id.company_name);
            this.loanPrice = view.findViewById(R.id.maxPrice);
            this.loanRate = view.findViewById(R.id.loanRate);
            this.repayMethod = view.findViewById(R.id.repayMethod);
            this.repayFeeRate = view.findViewById(R.id.repayFeeRate);
            this.company_comment = view.findViewById(R.id.company_comment);
            this.isChecked = view.findViewById(R.id.isChecked);
            this.company_logo = view.findViewById(R.id.company_logo);

        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.auction_listview_contents, parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.company_name.setText(data.get(position).getComName());
        int loanPriceToInteger = Integer.parseInt(data.get(position).getLoanPrice());
        holder.loanPrice.setText(Util.GetNumFormat(loanPriceToInteger));
        holder.loanRate.setText(data.get(position).getLoanRate() + "%");
        holder.repayMethod.setText(data.get(position).getRepayMethod());
        holder.repayFeeRate.setText(data.get(position).getRepayFeeRate() + "%");

        String decodeResult = "";
        if (data.get(position).getCompanyComment().length() > 0) {
            try {
                decodeResult = URLDecoder.decode(data.get(position).getCompanyComment(), "utf-8");
                holder.company_comment.setText(Util.changeTagToText(decodeResult));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        String imageUrl = data.get(position).getCompanyLogoUrl();
        if (imageUrl.length() > 0) {
            holder.company_logo.setVisibility(View.VISIBLE);
            Glide.with(Util.CURRENT_CONTEXT)
                    .load(Util.thepion_URL + imageUrl)
                    .into(holder.company_logo);
        } else {
            holder.company_logo.setVisibility(View.GONE);
        }

        //셀렉트 매니저가 있을 때
        if (data.get(position).getSelectManagerNum().length() > 0) {
//            holder.company_name.setBackgroundResource(R.color.pion_basic_deep_blue);
//            holder.company_name.setTextColor(Util.CURRENT_CONTEXT.getResources().getColor(R.color.white));
            holder.loanPrice.setTextColor(Util.CURRENT_CONTEXT.getResources().getColor(R.color.pion_basic_deep_blue));
            holder.loanRate.setTextColor(Util.CURRENT_CONTEXT.getResources().getColor(R.color.pion_basic_deep_blue));
            holder.repayMethod.setTextColor(Util.CURRENT_CONTEXT.getResources().getColor(R.color.pion_basic_deep_blue));
            holder.repayFeeRate.setTextColor(Util.CURRENT_CONTEXT.getResources().getColor(R.color.pion_basic_deep_blue));
            holder.isChecked.setVisibility(View.VISIBLE);
            holder.isChecked.setAlpha(0.65f);

            //코드에서 size 수정할 때 dp값으로 인식하고, 숫자만 입력해주면 됨
            holder.company_name.setTextSize(16);
            holder.loanPrice.setTextSize(14.5f);
            holder.loanRate.setTextSize(14.5f);
            holder.repayFeeRate.setTextSize(14.5f);
            holder.repayMethod.setTextSize(14.5f);

        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


}
