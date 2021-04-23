package com.temple.onit.GeofencedReminder;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.temple.onit.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GeofencedRecyclerAdaptor extends RecyclerView.Adapter<GeofencedRecyclerAdaptor.ViewHolder> {
    private ItemClickListener clicked;
    private LayoutInflater mInflator;
    private JSONArray payload;
    private Context context;
    JSONObject extract;

    // constructor
    public GeofencedRecyclerAdaptor(Context con, JSONArray jArray){
        context = con;
        payload = jArray;
        mInflator = LayoutInflater.from(con);
    }


    @NonNull
    @Override // inflate row layout
    public GeofencedRecyclerAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View geoReminderDisplay = mInflator.inflate(R.layout.row_item_geo_reminder,parent,false);
        return new ViewHolder(geoReminderDisplay);
    }

    @Override
    public void onBindViewHolder(@NonNull GeofencedRecyclerAdaptor.ViewHolder holder, int position) {

        try{
            extract = payload.getJSONObject(position); // extract data at row position
            holder.title.setText(extract.getString("title"));
            holder.distance.setText(extract.getString("distance"));

        } catch (JSONException e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return payload.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title, distance;

        // constructor
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.geoRowTitle);
            distance = itemView.findViewById(R.id.geoRowDistance);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            if (clicked != null) {
                try {
                    extract = payload.getJSONObject(getAdapterPosition());
                    double llat = extract.getDouble("latitude");
                    double llong = extract.getDouble("longitude");
                    clicked.onItemClick(view, getAdapterPosition(),llat,llong);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("Touch Event", "recycler entry touched");
            }

        }
    }
    void setClickListener(ItemClickListener itemClickListener) {
        clicked = itemClickListener;
    }
    public interface ItemClickListener {
        void onItemClick(View view, int position, double latitude, double longitude) throws JSONException;
    }
}
