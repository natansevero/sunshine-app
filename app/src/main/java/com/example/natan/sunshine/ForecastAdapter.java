package com.example.natan.sunshine;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {
    private String[] mWeatherData;

    private ForecastAdapterOnClickHandler mOnClickHandler;

    public interface ForecastAdapterOnClickHandler {
        void onClick(String weatherDayData);
    }

    public ForecastAdapter(ForecastAdapterOnClickHandler onClickHandler) {
        mOnClickHandler = onClickHandler;
    }

    @NonNull
    @Override
    public ForecastAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int idItemLayout = R.layout.data_list_item;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        boolean shouldAttachToParent = false;

        View view = layoutInflater.inflate(idItemLayout, parent, shouldAttachToParent);
        ForecastAdapterViewHolder viewHolder = new ForecastAdapterViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastAdapterViewHolder holder, int position) {
        holder.mWeatherDataTextView.setText(mWeatherData[position]);
    }

    @Override
    public int getItemCount() {
        if(mWeatherData == null) return 0;

        return mWeatherData.length;
    }

    public void setmWeatherData(String[] weatherData) {
        this.mWeatherData = weatherData;
        notifyDataSetChanged();
    }

    public class ForecastAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mWeatherDataTextView;

        public ForecastAdapterViewHolder(View view) {
            super(view);
            mWeatherDataTextView = (TextView) view.findViewById(R.id.tv_weather_data);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnClickHandler.onClick(mWeatherData[getAdapterPosition()]);
        }
    }

}
