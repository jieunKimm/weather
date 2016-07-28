package com.example.alab.weather;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        text = (TextView) findViewById(R.id.tv);

        final weather weatherActivity = this;

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                // Do your task
                Thread x= new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            setXML();
                            weatherActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    text.setText(weatherStatus.get(0));
                                }
                            });
                        } catch (SAXException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ParserConfigurationException e) {
                            e.printStackTrace();
                        }
                    }
                });
                x.start();
            }

        }, 0, 360000);


    }

    public void setXML() throws SAXException, IOException, ParserConfigurationException{
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse("http://www.kma.go.kr/wid/queryDFS.jsp?gridx=89&gridy=115");
        if(document != null){
            NodeList list = document.getElementsByTagName("data");
            System.out.println("차일드 노드의 엘리먼트 수"+list.item(0).getChildNodes().getLength());
            for(int i = 0; i < list.getLength(); i++){
                System.out.println("==="+list.item(i).getAttributes().getNamedItem("seq").getTextContent()+"===");
                //childNode 출력
                for(int k = 0; k < list.item(i).getChildNodes().getLength(); k++){
                    if(list.item(i).getChildNodes().item(k).getNodeType() == Node.ELEMENT_NODE){
                        if (list.item(i).getChildNodes().item(k).getNodeName().compareTo("wfEn") == 0) {
                            weatherStatus.add(list.item(i).getChildNodes().item(k).getTextContent());
                        }
                    }
                }
            }
        }

        System.out.println(weatherStatus);



    }//end setXML

}
