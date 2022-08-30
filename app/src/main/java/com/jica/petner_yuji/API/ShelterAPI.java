package com.jica.petner_yuji.API;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.jica.petner_yuji.model.Animal;
import com.jica.petner_yuji.model.Shelter;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ShelterAPI extends AsyncTask<String, Void, ArrayList<Shelter>> {

    private String requestUrl;


    Shelter shelter;

    ArrayList<Shelter> arrayList;
    private Context context;


    @Override
    protected ArrayList<Shelter> doInBackground(String... strings) {
        Log.d("파싱이 시작됩니다", "파싱 시작");


        // 보호소 20개 불러오기
        requestUrl = "http://apis.data.go.kr/1543061/animalShelterSrvc/shelterInfo?numOfRows=20&pageNo=1&serviceKey=6UfUSLy6OQOXLTfgrYGJUxyCO9fb7p52qPhVXGdK8wYk4UuPKpzJV%2FumRtYg5JoGN5KsQA%2Bli4iU9Sc%2Fs8eB%2Bg%3D%3D";
        try {
            boolean b_careNm = false;
            boolean b_orgNm = false;
            boolean b_careAddr = false;
            boolean b_careTel = false;
            boolean b_divisionNm = false;

            URL url = new URL(requestUrl);
            InputStream is = url.openStream();
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new InputStreamReader(is, "UTF-8"));

            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        arrayList = new ArrayList<Shelter>();
                        System.out.println("문서 시작");
                        break;
                    case XmlPullParser.END_DOCUMENT:
                        break;
                    case XmlPullParser.END_TAG: //마지막 태크가 item이면 shelter안의 내용 리스트에 저장
                        if (parser.getName().equals("item") && shelter != null) {
                            arrayList.add(shelter);
                            System.out.println("중간 리스트 점검" + arrayList);
                        }
                        break;
                    case XmlPullParser.START_TAG:// item 시작 태그시 안에 내용 shelter에 넣기 위한 작업
                        if (parser.getName().equals("item")) {
                            shelter = new Shelter();
                        }
                        //각각의 api속 변수명이다.
                        if (parser.getName().equals("careNm")) b_careNm = true;
                        if (parser.getName().equals("orgNm")) b_orgNm = true;
                        if (parser.getName().equals("careAddr")) b_careAddr = true;
                        if (parser.getName().equals("careTel")) b_careTel = true;
                        if (parser.getName().equals("divisionNm")) b_divisionNm = true;
                        break;
                    case XmlPullParser.TEXT:
                        if (b_careNm) {
                            shelter.setCareNm(parser.getText());
                            b_careNm = false;
                        } else if (b_orgNm) {
                            shelter.setOrgNm(parser.getText());
                            b_orgNm = false;
                        } else if (b_careAddr) {
                            shelter.setCareAddr(parser.getText());
                            b_careAddr = false;
                        } else if (b_careTel) {
                            shelter.setCareTel(parser.getText());
                            b_careTel = false;
                        } else if (b_divisionNm) {
                            shelter.setDivisionNm(parser.getText());
                            b_divisionNm = false;
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
    protected void onPostExecute(ArrayList<Shelter> shelters) {
        super.onPostExecute(shelters);

        int n = shelters.size();
        Log.d("파싱 결과 배열의 size", Integer.toString(n));

    }


}
