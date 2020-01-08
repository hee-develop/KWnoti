package kr.hee.kwnoti.u_campus_activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import kr.hee.kwnoti.R;
import kr.hee.kwnoti.databinding.CardCampusBinding;

public class CampusAdapter extends RecyclerView.Adapter<CampusAdapter.CampusViewHolder> {
    private ArrayList<CampusData> campusDataArr = new ArrayList<>();

    public void setCampusDataArr(ArrayList<CampusData> arr) {
        campusDataArr.clear();

        campusDataArr.addAll(arr);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CampusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CampusViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.card_campus, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CampusViewHolder holder, int position) {
        holder.binding.setCardData(campusDataArr.get(position));
    }

    @Override
    public int getItemCount() {
        return campusDataArr.size();
    }

    class CampusViewHolder extends RecyclerView.ViewHolder {
        CardCampusBinding binding;

        public CampusViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
