package com.example.abhishek.flashnews.ThreadingParsingClasses;

import org.xmlpull.v1.XmlPullParser;
import com.example.abhishek.flashnews.DataClasses.NewsData;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by abhishek on 1/2/16.
 */
public class XMLPullParserHandler {
    List<NewsData> newsList;
    private NewsData newsData;
    private String text;

    public XMLPullParserHandler() {
        newsList = new ArrayList<NewsData>();
    }

    public List<NewsData> parse(InputStream is) {
        String parent = "";
        XmlPullParserFactory factory = null;
        XmlPullParser parser = null;
        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            parser = factory.newPullParser();

            parser.setInput(is, null);

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = parser.getName();

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagname.equalsIgnoreCase("item")) {
                            // create a new instance of employee
                            newsData = new NewsData();
                            parent = "item";
                            //laptop.setModel(parser.getAttributeValue(null, "model"));
                        }
                        break;

                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (tagname.equalsIgnoreCase("item")) {
                            // add employee object to list
                            newsList.add(newsData);
                        } else if (tagname.equalsIgnoreCase("title") && parent.equals("item")) {
                            newsData.setTitle(text);
                        } else if (tagname.equalsIgnoreCase("author")) {
                            newsData.setAuthor(text);
                        } else if (tagname.equalsIgnoreCase("category")) {
                            newsData.setCategory(text);
                        } else if (tagname.equalsIgnoreCase("link") && parent.equals("item")) {
                            newsData.setUrl(text);
                        }
                        break;

                    default:
                        break;
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return newsList;
    }
}