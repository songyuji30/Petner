package com.jica.petner_yuji.API;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import androidx.core.app.ComponentActivity;

import com.jica.petner_yuji.model.Animal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class AnimalAPI extends AsyncTask<String, Void, ArrayList<Animal>> {

        private String requestUrl;


    Animal animal;

    ArrayList<Animal> arrayList;
    private Context context;


    @Override
    protected ArrayList<Animal> doInBackground(String... strings) {
        Log.d("파싱이 시작됩니다", "파싱 시작");

        //현재 날짜, 10일 전 날짜
        Calendar calendar = new GregorianCalendar();
        SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMdd");
        String now = SDF.format(calendar.getTime());
        System.out.println("Today : " + now);

        Calendar cal = new GregorianCalendar();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        cal.add(Calendar.DATE, -7);
        String weekAgo = sdf.format(cal.getTime());
        System.out.println("week ago : " + weekAgo);


        // 현재 날짜부터 10일 전까지의 유기동물 구하기
        requestUrl = "http://apis.data.go.kr/1543061/abandonmentPublicSrvc/abandonmentPublic?bgnde="+ weekAgo +"&endde="+ now +"&upr_cd=6450000&org_cd=4640000&pageNo=1&numOfRows=20&serviceKey=6UfUSLy6OQOXLTfgrYGJUxyCO9fb7p52qPhVXGdK8wYk4UuPKpzJV%2FumRtYg5JoGN5KsQA%2Bli4iU9Sc%2Fs8eB%2Bg%3D%3D";
        try {
            boolean b_happenPlace = false;
            boolean b_kindCd = false;
            boolean b_specialMark = false;
            boolean b_careNm = false;
            boolean b_careTel = false;
            boolean b_careAddr = false;
            boolean b_popfile = false;
            boolean b_desertionNo = false;

            URL url = new URL(requestUrl);
            InputStream is = url.openStream();
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new InputStreamReader(is, "UTF-8"));

            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        arrayList = new ArrayList<Animal>();
                        System.out.println("문서 시작");
                        break;
                    case XmlPullParser.END_DOCUMENT:
                        break;
                    case XmlPullParser.END_TAG: //마지막 태크가 item이면 animal안의 내용 리스트에 저장
                        if (parser.getName().equals("item") && animal != null) {
                            arrayList.add(animal);
                            System.out.println("중간 리스트 점검" + arrayList);
                        }
                        break;
                    case XmlPullParser.START_TAG:// item 시작 태그시 안에 내용 animal에 넣기 위한 작업
                        if (parser.getName().equals("item")) {
                            animal = new Animal();
                        }
                        //각각의 api속 변수명이다.
                        if (parser.getName().equals("happenPlace")) b_happenPlace = true;
                        if (parser.getName().equals("kindCd")) b_kindCd = true;
                        if (parser.getName().equals("specialMark")) b_specialMark = true;
                        if (parser.getName().equals("careNm")) b_careNm = true;
                        if (parser.getName().equals("careTel")) b_careTel = true;
                        if (parser.getName().equals("careAddr")) b_careAddr = true;
                        if (parser.getName().equals("popfile")) b_popfile = true;
                        if (parser.getName().equals("desertionNo")) b_desertionNo = true;
                        break;
                    case XmlPullParser.TEXT:
                        if (b_happenPlace) {
                            animal.setHappenPlace(parser.getText());
                            b_happenPlace = false;
                        } else if (b_kindCd) {
                            animal.setKindCd(parser.getText());
                            b_kindCd = false;
                        } else if (b_specialMark) {
                            animal.setSpecialMark(parser.getText());
                            b_specialMark = false;
                        } else if (b_careNm) {
                            animal.setCareNm(parser.getText());
                            b_careNm = false;
                        } else if (b_careTel) {
                            animal.setCareTel(parser.getText());
                            b_careTel = false;
                        } else if (b_careAddr) {
                            animal.setCareAddr(parser.getText());
                            b_careAddr = false;
                        } else if (b_popfile) {
                            animal.setPopfile(parser.getText());
                            b_popfile = false;
                        } else if (b_desertionNo) {
                            animal.setDesertionNo(parser.getText());
                            b_desertionNo = false;
                        }
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("리스트 결과!!" + arrayList);
        return arrayList;
    }

    @Override
    protected void onPostExecute(ArrayList<Animal> animals) {
        super.onPostExecute(animals);

        int s = animals.size();
        Log.d("파싱 결과 배열의 size", Integer.toString(s));

    }


}
