package com.example.alab.weather;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class weather extends AppCompatActivity {
    List<String> weatherStatus = new ArrayList<>();
    List<String> tempStatus = new ArrayList<>();
    ImageView image;
    TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        //각각의 이미지와 텍스트를 스크린으로 보여주기 위해 만듬
        image = (ImageView) findViewById(R.id.weather);
        text = (TextView) findViewById(R.id.temp);

        final weather weatherActivity = this;
//타이머를 이용하여 360000마이크로초 후에 다시 정보를 다운받게 설정
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
//새로운 thread를 만들어 그곳에서 정보를 받음
            @Override
            public void run() {
                // Do your task
                Thread x= new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            setXML();//자세한 코드는 아래에 정의
                            weatherActivity.runOnUiThread(new Runnable() {//날씨 정보를 메인에서 사용
                                @Override
                                public void run() {
                                    if(weatherStatus.get(0).equals("Mostly Cloudy")){//각각의경우에 따라 다른 이미지가 나오게 함
                                        image.setImageResource(R.drawable.cloud4);
                                    } else if(weatherStatus.get(0).equals("Rain")){
                                        image.setImageResource(R.drawable.rain);
                                    }else if(weatherStatus.get(0).equals("Cloudy")){
                                        image.setImageResource(R.drawable.cloud3);
                                    }else if(weatherStatus.get(0).equals("Clear")){
                                        image.setImageResource(R.drawable.sunny);
                                    }else if(weatherStatus.get(0).equals("Partly cloudy")){
                                        image.setImageResource(R.drawable.littlecloud);
                                    }else if(weatherStatus.get(0).equals("Snow/Rain")){
                                        image.setImageResource(R.drawable.rainsnow);
                                    }else
                                        image.setImageResource(R.drawable.snow);
                                    //온도를 보여줌
                                    text.setText("Temperature" + " : " + tempStatus.get(0));//스트리 어레이의 영번째 스트링을 보여줌
                                }
                            });
                            //에러 낫을시 다름 설정한 것을 실행하도록 함
                        } catch (SAXException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ParserConfigurationException e) {
                            e.printStackTrace();
                        }
                    }
                });
                //thread x를 실행하라고 명령
                x.start();
            }

        }, 0, 360000);


    }
//setxml정의
    public void setXML() throws SAXException, IOException, ParserConfigurationException{
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse("http://www.kma.go.kr/wid/queryDFS.jsp?gridx=89&gridy=115");// 이 링크에서 정보를 받아옴
        if(document != null){
            NodeList list = document.getElementsByTagName("data");
            System.out.println("차일드 노드의 엘리먼트 수"+list.item(0).getChildNodes().getLength());//길이를 48시간으로 정하고 3시간 단위로 예측정보 가져옴
            for(int i = 0; i < list.getLength(); i++){//총 16개(0-15)
                System.out.println("==="+list.item(i).getAttributes().getNamedItem("seq").getTextContent()+"===");
                //childNode 출력
                for(int k = 0; k < list.item(i).getChildNodes().getLength(); k++){//각각의 섹션에 대한 정보들을 가져옴
                    if(list.item(i).getChildNodes().item(k).getNodeType() == Node.ELEMENT_NODE){
                        if (list.item(i).getChildNodes().item(k).getNodeName().compareTo("wfEn") == 0) {//그중 wfEN과 같은 것만 따서 스트링 어레이로 정리
                            weatherStatus.add(list.item(i).getChildNodes().item(k).getTextContent());
                        }
                        if(list.item(i).getChildNodes().item(k).getNodeName().compareTo("temp") == 0){//그중 temp와 같은 것만 따서 스트링 어레이로 저장
                            tempStatus.add(list.item(i).getChildNodes().item(k).getTextContent());
                        }
                    }
                }
            }
        }

        System.out.println(weatherStatus);



    }//end setXML

}
