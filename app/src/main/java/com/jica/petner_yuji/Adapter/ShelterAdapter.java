package com.jica.petner_yuji.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jica.petner_yuji.R;
import com.jica.petner_yuji.model.Shelter;

import java.util.ArrayList;
import java.util.List;

public class ShelterAdapter extends RecyclerView.Adapter<ShelterAdapter.ViewHolder> {

    private ArrayList<Shelter> arrayList;
    private LayoutInflater layoutInflater;
    private Context context;

    public ShelterAdapter(ArrayList<Shelter> arrayList) {
        this.arrayList = arrayList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.shelter_item, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.careNm.setText(arrayList.get(position).getCareNm());
        holder.orgNm.setText(arrayList.get(position).getOrgNm());
        holder.careAddr.setText(arrayList.get(position).getCareAddr());
        holder.careTel.setText(arrayList.get(position).getCareTel());
        holder.divisionNm.setText(arrayList.get(position).getDivisionNm());

    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView careNm;
        public TextView orgNm;
        public TextView careAddr;
        public TextView careTel;
        public TextView divisionNm;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            careNm = itemView.findViewById(R.id.careNm);
            orgNm = itemView.findViewById(R.id.orgNm);
            careAddr = itemView.findViewById(R.id.careAddr);
            careTel = itemView.findViewById(R.id.careTel);
            divisionNm = itemView.findViewById(R.id.divisionNm);
        }

    }


}
