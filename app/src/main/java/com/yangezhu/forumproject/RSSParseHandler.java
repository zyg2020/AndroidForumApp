package com.yangezhu.forumproject;

import android.text.Html;
import android.util.Log;

import com.yangezhu.forumproject.model.News;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;


public class RSSParseHandler extends DefaultHandler {

    Boolean inItem = false;
    Boolean inTitle = false;
    Boolean inLink = false;
    Boolean inDescription = false;
    Boolean inMedia = false;
    Boolean inPubDate = false;

    private List<News> newsList;
    private News news;

    StringBuilder sb;

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        Log.d("JN", "Start of the Document");
        newsList = new ArrayList<News>();
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
        Log.d("JN", "End of the Document");
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        Log.d("JN", "Start Element - " + qName);

        if (qName.equals("item")) {
            inItem = true;
            news = new News();
        }else if (qName.equals("title") && inItem){
            inTitle = true;
            sb = new StringBuilder();
        }else if (qName.equals("link") && inItem){
            sb = new StringBuilder();
            inLink = true;
        } else if (qName.equals("description") && inItem) {
            sb = new StringBuilder();
            inDescription = true;
        }else if (qName.equals("media:content")){
            inMedia = true;
            //String url = attributes.getValue(0);
            String url = attributes.getValue("url");
            news.setImage_url(url);
            Log.d("ZHU_JSON_MSG", "Image URL - " + url);
        }else if (qName.equals("pubDate")){
            sb = new StringBuilder();
            inPubDate=true;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);

        if (qName.equals("item")) {
            inItem = false;
            newsList.add(news);
            news=null;
            Log.d("ZHU_JSON_MSG", "Finish item");
        }else if (qName.equals("title") && inItem){
            inTitle = false;
            news.setTitle(sb.toString());
            Log.d("ZHU_JSON_MSG", "Finish title - " + Html.fromHtml(sb.toString()));
        }else if (qName.equals("link") && inItem){
            inLink = false;
            news.setLink(sb.toString());
            Log.d("ZHU_JSON_MSG", "Finish link - " + Html.fromHtml(sb.toString()));
        } else if (qName.equals("description") && inItem) {
            inDescription = false;
            news.setDescription(sb.toString());
            Log.d("ZHU_JSON_MSG", "Finish description - " + Html.fromHtml(sb.toString()));
        }else if (qName.equals("media:content")){
            inMedia = false;
            Log.d("ZHU_JSON_MSG", "Finish media - " + Html.fromHtml(sb.toString()));
        }else if (qName.equals("pubDate")){
            inPubDate=false;
            news.setPublish_date(sb.toString());
            Log.d("ZHU_JSON_MSG", "Finish pubDate - " + Html.fromHtml(sb.toString()));
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);

        String content = new String(ch, start, length);

        if (inItem && inTitle){
            sb.append(ch, start, length);
            // appendStringToSb(content);
        }else if (inItem && inLink){
            sb.append(ch, start, length);
            // appendStringToSb(content);
        }else if (inItem && inDescription){
            sb.append(ch, start, length);
            // appendStringToSb(content);
        }else if (inItem && inMedia){
            sb.append(ch, start, length);
            // appendStringToSb(content);
        }else if (inItem && inPubDate){
            sb.append(ch, start, length);
            // appendStringToSb(content);
        }
    }

    private void appendStringToSb(String content){
        //String trimedContent = content.trim();
        sb.append(content);
    }

    public List<News> getNewsList(){
        return newsList;
    }
}

