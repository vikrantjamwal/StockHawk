package com.udacity.stockhawk.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.udacity.stockhawk.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

public class LineChartActivity extends Activity {

    private static final int YEARS_OF_HISTORY = 2;

    List<HistoricalQuote> history;

    LineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_chart);

        chart = (LineChart) findViewById(R.id.chart);

        final Calendar from = Calendar.getInstance();
        final Calendar to = Calendar.getInstance();
        from.add(Calendar.YEAR, -YEARS_OF_HISTORY);

        final String symbol = getIntent().getStringExtra("symbol_key");

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://example.com") // using example.com because url can't be null
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(LineChartActivity.this, "Connection Error!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Stock stock = YahooFinance.get(symbol);
                if(stock!=null){
                    history = stock.getHistory(from, to, Interval.MONTHLY);
                }

                if (history!=null && history.size()!=0) {
                    LineChartActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            List<Entry> entries = new ArrayList<>();

                            for (int i = 0; i < history.size(); i++) {
                                entries.add(new Entry(i, history.get(i).getClose().floatValue()));
                            }
                            chart.setVisibility(View.VISIBLE);
                            LineDataSet dataSet = new LineDataSet(entries, "Label");
                            dataSet.setDrawValues(false);
                            dataSet.setCircleColor(Color.GREEN);
                            LineData lineData = new LineData(dataSet);
                            chart.setData(lineData);
                            chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                            chart.getXAxis().setDrawGridLines(false);
                            chart.getAxisLeft().setDrawGridLines(false);
                            chart.getAxisRight().setDrawGridLines(false);
                            chart.getAxisRight().setEnabled(false);
                            chart.invalidate();
                        }
                    });
                }

            }
        });

    }

}
