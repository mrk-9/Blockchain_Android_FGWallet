package com.ivan.fgwallet.Fragments;

import android.graphics.Color;
import android.os.Bundle;

//import android.app.Fragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ivan.fgwallet.R;
import com.ivan.fgwallet.interfaces.NetworkCallBack;
import com.ivan.fgwallet.listener.ChangeTitleListener;
import com.ivan.fgwallet.model.MapChart;
import com.ivan.fgwallet.networking.Networking;
import com.ivan.fgwallet.utils.Constant;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import lecho.lib.hellocharts.formatter.SimpleAxisValueFormatter;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;


public class MapChartFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    ArrayList<Chart> charts = new ArrayList<>();
    ArrayList<MapChart> mapCharts = new ArrayList<>();
    ArrayList<Chart> subcharts = new ArrayList<>();

    @Override
    public void onRefresh() {
        getChart();
    }

    static class Chart {
        String curency, weight, latest_trade, high, low;

        public String getCurency() {
            return curency;
        }

        public void setCurency(String curency) {
            this.curency = curency;
        }

        public String getWeight() {
            return weight;
        }

        public void setWeight(String weight) {
            this.weight = weight;
        }

        public String getLatest_trade() {
            return latest_trade;
        }

        public void setLatest_trade(String latest_trade) {
            this.latest_trade = latest_trade;
        }

        public String getHigh() {
            return high;
        }

        public void setHigh(String high) {
            this.high = high;
        }

        public String getLow() {
            return low;
        }

        public void setLow(String low) {
            this.low = low;
        }
    }

    TextView highy;
    TextView greenh;
    TextView red;
    TextView date;


    private LineChartView chart;
    private LineChartData data;
    SwipeRefreshLayout swipeRefreshLayout;

    private void init(View rootView) {
        highy = rootView.findViewById(R.id.highy);
        greenh = rootView.findViewById(R.id.greenh);
        red = rootView.findViewById(R.id.red);
        date = rootView.findViewById(R.id.date);
    }

    @Override
    public void onResume() {
        super.onResume();
        ChangeTitleListener.getIntance().setTitle(getResources().getString(R.string.market_chart));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_map_chart, container, false);
//        ButterKnife.inject(this, rootView);
        init(rootView);
        chart = (LineChartView) rootView.findViewById(R.id.chart);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c.getTime());

        getChart();
//        generateTempoData();
        return rootView;
    }


    public void getChart() {
        swipeRefreshLayout.setRefreshing(true);
        mapCharts = new ArrayList<>();
//        new Networking(getActivity(),networkCallBack).doexecuteGETWITHOUTHEADER("http://api.bitcoincharts.com/v1/markets.json");
        new Networking(getActivity(), networkCallBack).doexecuteGETWITHOUTHEADER("https://api.coindesk.com/v1/bpi/currentprice/JPY.json");
    }

    NetworkCallBack networkCallBack = new NetworkCallBack() {
        @Override
        public void callBack(String response) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date dateTime = new Date();
            try {
                dateTime = dateFormat.parse(dateTime.toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String strDate = dateFormat.format(dateTime).toString();

            try {
                JSONObject jsonResponse = new JSONObject(response);
                JSONObject jsonObject = jsonResponse.getJSONObject("bpi");
                JSONObject jsoJPY = jsonObject.getJSONObject("JPY");
                Constant.CURRENCY_JPY = Double.valueOf(jsoJPY.getString("rate_float"));
                DecimalFormat df = new DecimalFormat("#.000000");
                greenh.setText("￥ "+Constant.CURRENCY_JPY);
                red.setText("￥ "+Constant.CURRENCY_JPY);
                highy.setText("￥ "+Constant.CURRENCY_JPY);
                date.setText(strDate);

                MapChart mapChart = new MapChart();
                mapChart.setX(strDate);
                mapChart.setY(Constant.CURRENCY_JPY.toString());
                mapCharts.add(mapChart);
//                JSONArray jsonArray = new JSONArray(response);
//                for (int i = 0; i <jsonArray.length() ; i++) {
//                    JSONObject  c = jsonArray.getJSONObject(i);
//                    String weighted_price = c.getString("weighted_price");
//                    String currency = c.getString("currency");
//                    String latest_trade = c.getString("low");
//                    Chart chart = new Chart();
//                    chart.setCurency(currency);
//                    chart.setWeight(weighted_price);
//                    chart.setLatest_trade(latest_trade);
//                    chart.setHigh(c.getString("high"));
//                    chart.setLow(latest_trade);
//                    charts.add(chart);
//                }
//                for (int i = 0; i <charts.size() ; i++) {
//                    if(charts.get(i).curency.equals("JPY")){
//                        subcharts.add(charts.get(i));
//                        greenh.setText("￥ "+charts.get(i).getHigh());
//                        red.setText("￥ "+charts.get(i).getLow());
//                        highy.setText("￥ "+charts.get(i).getHigh());
//                        date.setText(strDate);
//                    }
//
//                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            getMapChart();
        }
    };

    public void getMapChart() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        Date dateNow = new Date();
        date.setDate(dateNow.getDate() - 30);
        try {
            date = dateFormat.parse(date.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String strDate = dateFormat.format(date).toString();
        new Networking(getActivity(), networkCallBackMapChart).doexecuteGETWITHOUTHEADER("https://www.quandl.com/api/v3/datasets/BCHARTS/ZAIFJPY.json?api_key=SHty8AwZ5qSiJPBC4E8j&start_date=" + strDate);
    }

    NetworkCallBack networkCallBackMapChart = new NetworkCallBack() {
        @Override
        public void callBack(String response) {
            //                JSONObject ojb = response.getJSONObject("data");
            JSONObject object = null, objectData = null;
            JSONArray jsonArr = null, jsonArr1 = null;
            try {
                object = new JSONObject(response);
                objectData = object.getJSONObject("dataset");
                jsonArr = objectData.getJSONArray("data");
                for (int i = 0; i < jsonArr.length(); i++) {
                    jsonArr1 = jsonArr.getJSONArray(i);
                    MapChart mapChart = new MapChart();
                    mapChart.setX(jsonArr1.getString(0));
                    mapChart.setY(jsonArr1.get(1).toString());
                    mapCharts.add(mapChart);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            generateTempoData();
            swipeRefreshLayout.setRefreshing(false);
        }
    };

    private void generateTempoData() {
        Collections.reverse(mapCharts);
        // I got speed in range (0-50) and height in meters in range(200 - 300). I want this chart to display both
        // information. Differences between speed and height values are large and chart doesn't look good so I need
        // to modify height values to be in range of speed values.

        // The same for displaying Tempo/Height chart.

        float temp = Float.valueOf(mapCharts.get(0).getY());
        for (int i = 1; i < mapCharts.size(); i++)
            if (temp < Float.valueOf(mapCharts.get(i).getY()))
                temp = Float.valueOf(mapCharts.get(i).getY());

        float minHeight = 0;
        float maxHeight = temp + temp/10;
        float tempoRange = 15; // from 0min/km to 15min/km

        float scale = tempoRange / maxHeight;
        float sub = (minHeight * scale) / 2;

        int numValues = 52;

        Line line;
        List<PointValue> values;
        List<Line> lines = new ArrayList<Line>();

        // Height line, add it as first line to be drawn in the background.
        values = new ArrayList<PointValue>();
//        for (int i = 0; i < numValues; ++i) {
        // Some random height values, add +200 to make line a little more natural
//            float rawHeight = (float) (Math.random() * 100 + 200);
//            float normalizedHeight = rawHeight * scale - sub;
//            values.add(new PointValue(i, normalizedHeight));
//        }

        for (int i = 0; i < mapCharts.size(); i++) {
            MapChart chart = mapCharts.get(i);
            float rawHeight = Float.valueOf(chart.getY());
            float normalizedHeight = rawHeight * scale - sub;
            values.add(new PointValue(i, normalizedHeight));
        }

        line = new Line(values);
        line.setColor(Color.BLUE);
        line.setHasPoints(false);
        line.setFilled(true);
        line.setStrokeWidth(1);
        lines.add(line);

        // Tempo line is a little tricky because worse tempo means bigger value for example 11min per km is worse
        // than 2min per km but the second should be higher on the chart. So you need to know max tempo and
        // tempoRange and set
        // chart values to minTempo - realTempo.
        values = new ArrayList<PointValue>();
//        for (int i = 0; i < numValues; ++i) {
//            // Some random raw tempo values.
//            float realTempo = (float) Math.random() * 6 + 2;
//            float revertedTempo = tempoRange - realTempo;
//            values.add(new PointValue(i, revertedTempo));
//        }
        for (int i = 0; i < mapCharts.size(); i++) {
            // Some random raw tempo values.
            MapChart chart = mapCharts.get(i);
            float realTempo = Float.valueOf(chart.getY());
            float revertedTempo = tempoRange - realTempo;
            values.add(new PointValue(i, revertedTempo));
        }

        line = new Line(values);
        line.setColor(ChartUtils.COLOR_RED);
        line.setHasPoints(false);
        line.setStrokeWidth(3);
        lines.add(line);

        // Data and axes
        data = new LineChartData(lines);

        // Distance axis(bottom X) with formatter that will ad [km] to values, remember to modify max label charts
        // value.
        List<String> strings = new ArrayList<String>();
        for (int i = 0; i < 30; i++) {
            Date dateNow = new Date();
            Date dateChart = new Date();
            dateChart.setDate(dateNow.getDate() - i);
            strings.add(String.valueOf(dateChart.getDate()) + " " + theMonth(dateChart.getMonth()));
        }
        Collections.reverse(strings);

        Axis distanceAxis = new Axis();
        distanceAxis.setTextColor(ChartUtils.COLOR_ORANGE);
        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        for (int i = 0; i < 30; i += 7) {
            // I'am translating float to minutes because I don't have data in minutes, if You store some time data
            // you may skip translation.
            axisValues.add(new AxisValue(i).setLabel(strings.get(i)));
        }
        distanceAxis.setValues(axisValues);
        distanceAxis.setHasLines(true);
//        distanceAxis.setHasTiltedLabels(true);
        data.setAxisXBottom(distanceAxis);


        // Tempo uses minutes so I can't use auto-generated axis because auto-generation works only for decimal
        // system. So generate custom axis values for example every 15 seconds and set custom labels in format
        // minutes:seconds(00:00), you could do it in formatter but here will be faster.
//        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        for (float i = 0; i < tempoRange; i += 0.25f) {
            // I'am translating float to minutes because I don't have data in minutes, if You store some time data
            // you may skip translation.
//            axisValues.add(new AxisValue(i).setLabel(formatMinutes(tempoRange - i)));
        }

//        Axis tempoAxis = new Axis(axisValues).setName(" ").setHasLines(true).setMaxLabelChars(4)
//                .setTextColor(ChartUtils.COLOR_RED);
//        data.setAxisYLeft(tempoAxis);

        // *** Same as in Speed/Height chart.
        // Height axis, this axis need custom formatter that will translate values back to real height values.
        data.setAxisYRight(new Axis().setName(" ").setMaxLabelChars(5)
                .setFormatter(new HeightValueFormatter(scale, sub, 0)).setHasLines(true));

        // Set data
        chart.setLineChartData(data);

        // Important: adjust viewport, you could skip this step but in this case it will looks better with custom
        // viewport. Set
        // viewport with Y range 0-12;
        Viewport v = chart.getMaximumViewport();
        v.set(v.left, tempoRange, v.right, 0);
        chart.setMaximumViewport(v);
        chart.setCurrentViewport(v);

    }

    private String coolFormat(double n, int iteration) {
        char[] c = new char[]{'k', 'm', 'b', 't'};
        double d = ((long) n / 100) / 10.0;
        boolean isRound = (d * 10) %10 == 0;//true if the decimal part is equal to 0 (then it's trimmed anyway)
        return (d < 1000? //this determines the class, i.e. 'k', 'm' etc
                ((d > 99.9 || isRound || (!isRound && d > 9.99)? //this decides whether to trim the decimals
                        (int) d * 10 / 10 : d + "" // (int) d * 10 / 10 drops the decimal
                ) + "" + c[iteration])
                : coolFormat(d, iteration+1));

    }

    public static String theMonth(int month) {
        String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        return monthNames[month];
    }

    private String formatMinutes(float value) {
        StringBuilder sb = new StringBuilder();

        // translate value to seconds, for example
        int valueInSeconds = (int) (value * 60);
        int minutes = (int) Math.floor(valueInSeconds / 60);
        int seconds = (int) valueInSeconds % 60;

        sb.append(String.valueOf(minutes)).append(':');
        if (seconds < 10) {
            sb.append('0');
        }
        sb.append(String.valueOf(seconds));
        return sb.toString();
    }

    /**
     * Recalculated height values to display on axis. For this example I use auto-generated height axis so I
     * override only formatAutoValue method.
     */
    private static class HeightValueFormatter extends SimpleAxisValueFormatter {

        private float scale;
        private float sub;
        private int decimalDigits;

        public HeightValueFormatter(float scale, float sub, int decimalDigits) {
            this.scale = scale;
            this.sub = sub;
            this.decimalDigits = decimalDigits;
        }

        @Override
        public int formatValueForAutoGeneratedAxis(char[] formattedValue, float value, int autoDecimalDigits) {
            float scaledValue = (value + sub) / scale;
            return super.formatValueForAutoGeneratedAxis(formattedValue, scaledValue, this.decimalDigits);
        }
    }


}
