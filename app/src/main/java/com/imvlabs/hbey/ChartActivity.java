package com.imvlabs.hbey;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;

public class ChartActivity extends AppCompatActivity {

    PieChart genderChart, ageChart, statusChart;
    //private float[] yDataGender;
    private float[] yDataAge;
    String TAG="ChartActivity";
    private String[] xDataGender={"Male","Female"};
    private String[] xDataAge = {"Young","Middle","Old"};
    int allCounterUmur, allCounterKelamin, counterMiddle, counterYoung, counterOld,
        counterMale,counterFemale, counterNormal, counterAnemia, allCounterStatus;
    private float[] yDataStatus;
    private String[] xDataStatus={"Normal", "Anemia"};

    Bundle b;
    int mediumPurple= Color.parseColor("#bf55ec");
    int caribbeanGreen=Color.parseColor("#03c9a9");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        methodKelaminChart();
        methodStatusChart();
        methodUmurChart();
        //Log.d(TAG,"starting the status Chart");


    }

    private void methodUmurChart() {
        ageChart = (PieChart) findViewById(R.id.umurChart);
        ageChart.setRotationEnabled(true);
        ageChart.setHoleRadius(30f);
        ageChart.setTransparentCircleAlpha(0);
        ageChart.setCenterText("Age");
        ageChart.setDrawEntryLabels(true);

        addDataUmurChart();
        b = getIntent().getExtras();
        counterYoung=b.getInt("parseCounterYoung");
        counterMiddle=b.getInt("parseCounterMiddle");
        counterOld=b.getInt("parseCounterOld");
        allCounterUmur=b.getInt("parseAllCounterUmur");
        //Log.i(TAG, "addDataKelaminChart: parsing chart kelamin "+counterFemale+counterMale+allCounterKelamin);
        ageChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int pos1 = e.toString().indexOf("y: ");
                float[] yDataAge={((float) counterYoung / (float) allCounterUmur) * 100, ((float) counterMiddle/ (float) allCounterUmur) * 100,((float) counterOld / (float) allCounterUmur) * 100};
                String jenisGender=e.toString().substring(pos1+3);
                for(int i = 0; i<yDataAge.length;i++){
                    if (yDataAge[i]==Float.parseFloat(jenisGender)){
                        pos1=i; break;
                    }
                }
                String jenisKelamin = xDataAge[pos1+1];
                // Toast.makeText(ChartActivity.this,jenisKelamin + "\n"+jenisGender, Toast.LENGTH_LONG).show();
            }



            @Override
            public void onNothingSelected() {

            }
        });
    }

    private void addDataUmurChart() {
        ArrayList<PieEntry> yEntrys=new ArrayList<>();
        ArrayList<String> xEntrys= new ArrayList<>();
        b = getIntent().getExtras();
        counterYoung=b.getInt("parseCounterYoung");
        counterMiddle=b.getInt("parseCounterMiddle");
        counterOld=b.getInt("parseCounterOld");
        allCounterUmur=b.getInt("parseAllCounterUmur");
        //Log.i(TAG, "addDataKelaminChart: parsing method kelamin "+counterFemale+counterMale+allCounterKelamin);
        float[] yDataAge={((float) counterYoung / (float) allCounterUmur) * 100, ((float) counterMiddle / (float) allCounterUmur * 100),((float) counterOld / (float) allCounterUmur) * 100};

        for(int i = 0; i< xDataAge.length;i++){
            yEntrys.add(new PieEntry(yDataAge[i],i));
        }

        for(int i = 1; i< xDataAge.length;i++){
            xEntrys.add(xDataAge[i]);
        }
        //create Data set
        PieDataSet pieDataSet = new PieDataSet(yEntrys,"");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);

        //add Colors to Dataset
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(mediumPurple);
        colors.add(caribbeanGreen);
        colors.add(Color.RED);


        pieDataSet.setColors(colors);

        //add legend to chart (legenda)

//        Legend legend = genderChart.getLegend();
//        legend.setForm(Legend.LegendForm.CIRCLE);
//        legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);

        //create pie data object
        PieData pieData= new PieData(pieDataSet);
        ageChart.setData(pieData);
        ageChart.invalidate();

    }

    private void methodStatusChart() {
        statusChart = (PieChart) findViewById(R.id.statusChart2);
        statusChart.setRotationEnabled(true);
        statusChart.setHoleRadius(30f);
        statusChart.setTransparentCircleAlpha(0);
        statusChart.setCenterText("Status");
        statusChart.setDrawEntryLabels(true);

        addDataStatusChart();
        b = getIntent().getExtras();
        counterNormal=b.getInt("parseCounterNormal");
        counterAnemia=b.getInt("parseCounterAnemia");
        allCounterStatus=b.getInt("parseAllCounterStatus");
        //Log.i(TAG, "addDataKelaminChart: parsing chart kelamin "+counterFemale+counterMale+allCounterKelamin);
        statusChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int pos1 = e.toString().indexOf("y: ");
                float[] yDataStatus={((float) counterAnemia / (float) allCounterStatus) * 100, (((float) counterNormal/ (float) allCounterStatus) * 100)};
                String jenisGender=e.toString().substring(pos1+3);
                for(int i = 0; i<yDataStatus.length;i++){
                    if (yDataStatus[i]==Float.parseFloat(jenisGender)){
                        pos1=i; break;
                    }
                }
                String jenisKelamin = xDataStatus[pos1+1];
               // Toast.makeText(ChartActivity.this,jenisKelamin + "\n"+jenisGender, Toast.LENGTH_LONG).show();
            }



            @Override
            public void onNothingSelected() {

            }
        });


    }

    private void addDataStatusChart() {
        ArrayList<PieEntry> yEntrys=new ArrayList<>();
        ArrayList<String> xEntrys= new ArrayList<>();
        b = getIntent().getExtras();
        counterNormal=b.getInt("parseCounterNormal");
        counterAnemia=b.getInt("parseCounterAnemia");
        allCounterStatus=b.getInt("parseAllCounterStatus");
        //Log.i(TAG, "addDataKelaminChart: parsing method kelamin "+counterFemale+counterMale+allCounterKelamin);
        float[] yDataStatus={((float) counterAnemia / (float) allCounterStatus) * 100, (((float) counterNormal / (float) allCounterStatus) * 100)};

        for(int i = 0; i< xDataStatus.length;i++){
            yEntrys.add(new PieEntry(yDataStatus[i],i));
        }

        for(int i = 1; i< xDataStatus.length;i++){
            xEntrys.add(xDataStatus[i]);
        }
        //create Data set
        PieDataSet pieDataSet = new PieDataSet(yEntrys,"Status");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);

        //add Colors to Dataset
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(mediumPurple);
        colors.add(caribbeanGreen);


        pieDataSet.setColors(colors);

        //add legend to chart (legenda)

//        Legend legend = genderChart.getLegend();
//        legend.setForm(Legend.LegendForm.CIRCLE);
//        legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);

        //create pie data object
        PieData pieData= new PieData(pieDataSet);
        statusChart.setData(pieData);
        statusChart.invalidate();

    }


    private void methodKelaminChart() {
        genderChart = (PieChart) findViewById(R.id.kelaminChart);
        genderChart.setRotationEnabled(true);
        genderChart.setHoleRadius(25f);
        genderChart.setTransparentCircleAlpha(0);
        genderChart.setCenterText("Gender");
        genderChart.setDrawEntryLabels(true);

        addDataKelaminChart();
        b = getIntent().getExtras();
        counterFemale=b.getInt("parseCounterFemale");
        counterMale=b.getInt("parseCounterMale");
        allCounterKelamin=b.getInt("parseAllCounterKelamin");
        Log.i(TAG, "addDataKelaminChart: parsing chart kelamin "+counterFemale+counterMale+allCounterKelamin);
        genderChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int pos1 = e.toString().indexOf("y: ");
                float[] yDataGender={((float) counterMale / (float) allCounterKelamin) * 100, (((float) counterFemale / (float) allCounterKelamin) * 100)};
                String jenisGender=e.toString().substring(pos1+3);
                for(int i = 0; i<yDataGender.length;i++){
                    if (yDataGender[i]==Float.parseFloat(jenisGender)){
                        pos1=i; break;
                    }
                }
                String jenisKelamin = xDataGender[pos1+1];
                Toast.makeText(ChartActivity.this,jenisKelamin + "\n"+jenisGender, Toast.LENGTH_LONG).show();
            }



            @Override
            public void onNothingSelected() {

            }
        });

    }

    private void addDataKelaminChart() {
        //Log.d(TAG, "addDataSet started ");
        ArrayList<PieEntry> yEntrys=new ArrayList<>();
        ArrayList<String> xEntrys= new ArrayList<>();
        b = getIntent().getExtras();
        counterFemale=b.getInt("parseCounterFemale");
        counterMale=b.getInt("parseCounterMale");
        allCounterKelamin=b.getInt("parseAllCounterKelamin");
        Log.i(TAG, "addDataKelaminChart: parsing method kelamin "+counterFemale+counterMale+allCounterKelamin);
        float[] yDataGender={((float) counterMale / (float) allCounterKelamin) * 100, (((float) counterFemale / (float) allCounterKelamin) * 100)};

        for(int i = 0; i< xDataGender.length;i++){
            yEntrys.add(new PieEntry(yDataGender[i],i));
        }

        for(int i = 1; i< xDataGender.length;i++){
            xEntrys.add(xDataGender[i]);
        }
        //create Data set
        PieDataSet pieDataSet = new PieDataSet(yEntrys,"Gender");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);

        //add Colors to Dataset
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.BLUE);
        colors.add(Color.RED);


        pieDataSet.setColors(colors);

        //add legend to chart (legenda)

        Legend legend = genderChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);

        //create pie data object
        PieData pieData= new PieData(pieDataSet);
        genderChart.setData(pieData);
        genderChart.invalidate();

    }
}
