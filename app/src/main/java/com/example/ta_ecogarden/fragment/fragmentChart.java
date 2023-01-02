package com.example.ta_ecogarden.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ta_ecogarden.R;
import com.example.ta_ecogarden.database.getData;
import com.example.ta_ecogarden.marker_chart.Marker_Chart_Kelembaban;
import com.example.ta_ecogarden.marker_chart.Marker_Chart_Lama_Siram;
import com.example.ta_ecogarden.marker_chart.Marker_Chart_Suhu;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class fragmentChart extends Fragment {
    LineChart line_chart_suhu, line_chart_kelembaban, line_chart_siram;
    LineDataSet lineDataSet1, lineDataSet2, lineDataSet3;
    ArrayList<ILineDataSet> iLineDataSets1, iLineDataSets2, iLineDataSets3;
    LineData lineData;
    List<String> label = new ArrayList<>();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Data");
    DatePickerDialog date;
    SimpleDateFormat dateFormat;
    Button filter_chart;
    EditText tanggal;
    Marker_Chart_Suhu mark_chart;
    Marker_Chart_Kelembaban mark_chart1;
    Marker_Chart_Lama_Siram mark_chart2;
    getData dataku;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart, container, false);
        setHasOptionsMenu(true);

        line_chart_suhu = view.findViewById(R.id.grafik_suhu);
        line_chart_kelembaban = view.findViewById(R.id.grafik_kelembaban_tanah);
        line_chart_siram = view.findViewById(R.id.grafik_lama_siram);
        filter_chart = view.findViewById(R.id.aksi_filter);
        dataku = new getData();
        filter_chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tanggal.getText().toString().isEmpty()) {
                    tanggal.setError("isi dulu");
                    tanggal.requestFocus();
                } else {
                    filter_grafik();
                }
            }
        });

        tampilkan_grafik();

        tanggal = view.findViewById(R.id.filter_grafik);
        tanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog();
            }
        });

        dateFormat = new SimpleDateFormat("d MMMM yyyy", Locale.US);
        mark_chart = new Marker_Chart_Suhu(getActivity().getApplicationContext(), R.layout.marker_chart_suhu);
        mark_chart1 = new Marker_Chart_Kelembaban(getActivity().getApplicationContext(), R.layout.marker_chart_kelembaban);
        mark_chart2 = new Marker_Chart_Lama_Siram(getActivity().getApplicationContext(), R.layout.custom_mark_chart);

        return view;
    }

    private void filter_grafik() {
        lineDataSet1 = new LineDataSet(null, null);
        iLineDataSets1 = new ArrayList<>();
        lineDataSet2 = new LineDataSet(null, null);
        iLineDataSets2 = new ArrayList<>();
        lineDataSet3 = new LineDataSet(null, null);
        iLineDataSets3 = new ArrayList<>();
        reference.orderByChild("Tanggal").equalTo(tanggal.getText().toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Entry> entries1 = new ArrayList<>();
                ArrayList<Entry> entries2 = new ArrayList<>();
                ArrayList<Entry> entries3 = new ArrayList<>();

                if (snapshot.hasChildren()) {
                    int i = -1;
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        i = i+1;
                        String suhu = snapshot1.child("Suhu").getValue().toString();
                        String kelembaban_tanah = snapshot1.child("Kelembaban_Tanah").getValue().toString();
                        String durasi_siram = snapshot1.child("Lama_Siram").getValue().toString();
                        String timestamp = snapshot1.child("Waktu").getValue().toString();
                        entries1.add(new Entry(i, Float.parseFloat(suhu)));
                        entries2.add(new Entry(i, Float.parseFloat(kelembaban_tanah)));
                        entries3.add(new Entry(i, Float.parseFloat(durasi_siram)));
                        label.add(timestamp);
                    }
                    lineDataSet1.setValues(entries1);
                    display_chart_suhu();
                    lineDataSet2.setValues(entries2);
                    display_chart_kelembaban();
                    lineDataSet3.setValues(entries3);
                    display_chart_durasi();
                } else {
                    AlertDialog dialog = new AlertDialog.Builder(getContext())
                            .setTitle("Data pada grafik tidak ditemukan")
                            .setNegativeButton("Kembali", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).create();
                    dialog.show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void tampilkan_grafik() {
        lineDataSet1 = new LineDataSet(null, null);
        iLineDataSets1 = new ArrayList<>();
        lineDataSet2 = new LineDataSet(null, null);
        iLineDataSets2 = new ArrayList<>();
        lineDataSet3 = new LineDataSet(null, null);
        iLineDataSets3 = new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Entry> entries1 = new ArrayList<>();
                ArrayList<Entry> entries2 = new ArrayList<>();
                ArrayList<Entry> entries3 = new ArrayList<>();

                if (snapshot.hasChildren()) {
                    int i = -1;
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        i = i+1;
                        String suhu = snapshot1.child("Suhu").getValue().toString();
                        String kelembaban_tanah = snapshot1.child("Kelembaban_Tanah").getValue().toString();
                        String durasi_siram = snapshot1.child("Lama_Siram").getValue().toString();
                        String timestamp = snapshot1.child("Waktu").getValue().toString();
                        entries1.add(new Entry(i, Float.parseFloat(suhu)));
                        entries2.add(new Entry(i, Float.parseFloat(kelembaban_tanah)));
                        entries3.add(new Entry(i, Float.parseFloat(durasi_siram)));
                        label.add(timestamp);
                    }
                    lineDataSet1.setValues(entries1);
                    display_chart_suhu();
                    lineDataSet2.setValues(entries2);
                    display_chart_kelembaban();
                    lineDataSet3.setValues(entries3);
                    display_chart_durasi();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void display_chart_suhu() {
        line_chart_suhu.setMarker(mark_chart);
        lineDataSet1.setLabel("Suhu (°C)");
        lineDataSet1.setColor(Color.BLUE);
        lineDataSet1.setCircleRadius(2f);
        lineDataSet1.setCircleColor(Color.BLUE);
        iLineDataSets1.clear();
        iLineDataSets1.add(lineDataSet1);
        lineData = new LineData(iLineDataSets1);
        line_chart_suhu.clear();
        line_chart_suhu.setData(lineData);
        line_chart_suhu.invalidate();
        line_chart_suhu.setDoubleTapToZoomEnabled(true);
        line_chart_suhu.getXAxis().setValueFormatter(new IndexAxisValueFormatter(label));
        line_chart_suhu.animateX(2000);
        lineDataSet1.setLineWidth(2);
        line_chart_suhu.setScaleEnabled(true);
        line_chart_suhu.getLegend().setDrawInside(false);
        line_chart_suhu.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        line_chart_suhu.getDescription().setEnabled(false);
    }

    private void display_chart_kelembaban() {
        line_chart_kelembaban.setMarker(mark_chart1);
        lineDataSet2.setLabel("Kelembaban Tanah (%)");
        lineDataSet2.setColor(Color.GREEN);
        lineDataSet2.setCircleRadius(2f);
        lineDataSet2.setCircleColor(Color.GREEN);
        iLineDataSets2.clear();
        iLineDataSets2.add(lineDataSet2);
        lineData = new LineData(iLineDataSets2);
        line_chart_kelembaban.clear();
        line_chart_kelembaban.setData(lineData);
        line_chart_kelembaban.invalidate();
        line_chart_kelembaban.setDoubleTapToZoomEnabled(true);
        line_chart_kelembaban.getXAxis().setValueFormatter(new IndexAxisValueFormatter(label));
        line_chart_kelembaban.animateX(2000);
        lineDataSet2.setLineWidth(2);
        line_chart_kelembaban.setScaleEnabled(true);
        line_chart_kelembaban.getLegend().setDrawInside(false);
        line_chart_kelembaban.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        line_chart_kelembaban.getDescription().setEnabled(false);
    }

    private void display_chart_durasi() {
        line_chart_siram.setMarker(mark_chart2);
        lineDataSet3.setLabel("Lama Siram (detik)");
        lineDataSet3.setColor(Color.RED);
        lineDataSet3.setCircleRadius(2f);
        lineDataSet3.setCircleColor(Color.RED);
        iLineDataSets3.clear();
        iLineDataSets3.add(lineDataSet3);
        lineData = new LineData(iLineDataSets3);
        line_chart_siram.clear();
        line_chart_siram.setData(lineData);
        line_chart_siram.invalidate();
        line_chart_siram.setDoubleTapToZoomEnabled(true);
        line_chart_siram.getXAxis().setValueFormatter(new IndexAxisValueFormatter(label));
        line_chart_siram.animateX(2000);
        lineDataSet2.setLineWidth(2);
        line_chart_siram.setScaleEnabled(true);
        line_chart_siram.getLegend().setDrawInside(false);
        line_chart_siram.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        line_chart_siram.getDescription().setEnabled(false);
    }


    private void showDateDialog() {
        Calendar newCalendar = Calendar.getInstance();
        date = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                tanggal.setText(dateFormat.format(newDate.getTime()));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        date.show();
    }
}
