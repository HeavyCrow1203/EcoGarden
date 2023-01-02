package com.example.ta_ecogarden.marker_chart;

import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.ta_ecogarden.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Marker_Chart_Suhu extends MarkerView {
    TextView tanggal_value, waktu_value, suhu;
    DatabaseReference reference;
    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    public Marker_Chart_Suhu(Context context, int layoutResource) {
        super(context, layoutResource);

        suhu = findViewById(R.id.txtInfo);
        tanggal_value= findViewById(R.id.txtTitle);
        waktu_value = findViewById(R.id.txtWaktu);

        reference = FirebaseDatabase.getInstance().getReference("Data");
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        reference.orderByChild("Suhu").equalTo(e.getY()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        String tanggal1 = snapshot1.child("Tanggal").getValue().toString();
                        waktu_value.setText(snapshot1.child("Waktu").getValue().toString());
                        tanggal_value.setText(tanggal1);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

        suhu.setText("Suhu : "+e.getY()+" °C");
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
