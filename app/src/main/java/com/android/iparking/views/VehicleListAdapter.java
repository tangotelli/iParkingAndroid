package com.android.iparking.views;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.iparking.R;
import com.android.iparking.models.Vehicle;

import java.util.List;

public class VehicleListAdapter extends BaseAdapter {

    private List<Vehicle> vehicles;
    protected Resources resources;
    protected Context context;
    private final LayoutInflater inflater;
    private int clickedCard;

    public VehicleListAdapter(List<Vehicle> vehicles, Resources resources, Context context) {
        this.vehicles = vehicles;
        this.resources = resources;
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return this.vehicles.size();
    }

    @Override
    public Object getItem(int position) {
        return this.vehicles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VehicleCardHolder holder = new VehicleCardHolder();
        View view = this.inflater.inflate(R.layout.vehicle_list_item, null);
        this.setTexts(view, holder, position);
        this.setButton(view, holder, position);
        view.setOnClickListener(v -> {
            clickedCard = position;
            notifyDataSetChanged();
        });
        return view;
    }

    private void setTexts(View view, VehicleCardHolder holder, int position) {
        holder.tvItemNickname = view.findViewById(R.id.tvItemNickname);
        holder.tvItemNickname.setText(this.vehicles.get(position).getNickname());
        holder.tvItemLicensePlate = view.findViewById(R.id.tvItemLicensePlate);
        holder.tvItemLicensePlate.setText(this.vehicles.get(position).getLicensePlate());
    }

    private void setButton(View view, VehicleCardHolder holder, int position) {
        holder.buttonRemove = view.findViewById(R.id.buttonRemove);
        holder.buttonRemove.setOnClickListener(this::removeVehicle);
        if (this.clickedCard == position) {
            holder.buttonRemove.setEnabled(true);
            holder.buttonRemove.setAlpha(1f);
        } else {
            holder.buttonRemove.setEnabled(false);
            holder.buttonRemove.setAlpha(0.25f);
        }
    }

    private void removeVehicle(View view) {

    }

    public static class VehicleCardHolder {

        TextView tvItemNickname;
        TextView tvItemLicensePlate;
        Button buttonRemove;

    }
}
