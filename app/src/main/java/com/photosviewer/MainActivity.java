package com.photosviewer;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.photosviewer.adapter.CustomAdapter;
import com.photosviewer.model.Photo;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class MainActivity extends AppCompatActivity {

    private List<Photo> allPhotos = new ArrayList<>();
    private List<Photo> photos = new ArrayList<>();
    private String flickrPhotosEndpoint = "https://www.flickr.com/services/feeds/photos_public.gne";
    private ListView list;
    private ArrayAdapter<Photo> adapter;
    private boolean orderByDateTakenDesc = true;
    private boolean orderByPublishedDesc = true;
    private EditText searchByTagEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_main);

        list = findViewById(R.id.listView);
        adapter = new CustomAdapter(this,0, photos);
        list.setAdapter(adapter);

        searchByTagEditText = findViewById(R.id.searchByTagEditText);
        searchByTagEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                photos = allPhotos.stream()
                        .filter(photo -> photo.getTags().toString().contains(s))
                        .collect(Collectors.toList());

                adapter.notifyDataSetChanged();
            }
        });

        loadNewPhotos();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private class PhotosDownloader extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                downloadData();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        private void downloadData() {
            try {
                URL url = new URL(flickrPhotosEndpoint);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                InputSource inputSource = new InputSource(url.openStream());
                Document doc = db.parse(inputSource);
                doc.getDocumentElement().normalize();

                NodeList nodeList = doc.getElementsByTagName("entry");

                int length = nodeList.getLength();
                for (int i = 0; i < length; i++) {
                    if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) nodeList.item(i);

                        String title = element.getElementsByTagName("title").item(0).getTextContent();
                        String photoUrl = element.getElementsByTagName("link").item(1).getAttributes().getNamedItem("href").getNodeValue();
                        String dateTakenString = element.getElementsByTagName("flickr:date_taken").item(0).getTextContent();
                        ZonedDateTime dateTaken = ZonedDateTime.parse(dateTakenString, DateTimeFormatter.ISO_DATE_TIME);
                        String publishedString = element.getElementsByTagName("published").item(0).getTextContent();
                        ZonedDateTime published = ZonedDateTime.parse(publishedString, DateTimeFormatter.ISO_DATE_TIME);

                        NodeList categoriesNodeList = element.getElementsByTagName("category");
                        int categoriesNodeListLength = categoriesNodeList.getLength();
                        List<String> tags = new LinkedList<>();
                        for (int j = 0; j < categoriesNodeListLength; j++) {
                            Element categoryElement = (Element) categoriesNodeList.item(j);
                            String tag = categoryElement.getAttribute("term");
                            tags.add(tag);
                        }
                        Photo photo = new Photo(title, photoUrl, tags, dateTaken, published);
                        if (!allPhotos.contains(photo))
                            allPhotos.add(photo);
                    }
                }

                photos.addAll(allPhotos);

            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
            } catch (ParserConfigurationException | SAXException e) {
                e.printStackTrace();
            }

        }
    }

    public void orderByDateTaken(View view) {
        orderByDateTakenDesc = !orderByDateTakenDesc;

        if (orderByDateTakenDesc)
            photos.sort((photo1, photo2) -> {
                if (photo1.getDateTaken().isAfter(photo2.getDateTaken()))
                    return -1;
                else if (photo1.getDateTaken().isBefore(photo2.getDateTaken()))
                    return 1;
                else
                    return 0;
            });
        else
            photos.sort((photo1, photo2) -> {
                if (photo1.getDateTaken().isAfter(photo2.getDateTaken()))
                    return 1;
                else if (photo1.getDateTaken().isBefore(photo2.getDateTaken()))
                    return -1;
                else
                    return 0;
            });
        adapter.notifyDataSetChanged();
    }

    public void loadNewPhotos(View view) {
        loadNewPhotos();
    }

    private void loadNewPhotos() {
        if (!isNetworkAvailable())
            Toast.makeText(MainActivity.this, R.string.no_internet, Toast.LENGTH_LONG).show();
        else {
            try {
                new PhotosDownloader().execute().get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            adapter.notifyDataSetChanged();
        }
    }

    public void orderByPublished(View view) {
        orderByPublishedDesc = !orderByPublishedDesc;

        if (orderByPublishedDesc)
            photos.sort((photo1, photo2) -> {
                if (photo1.getPublished().isAfter(photo2.getPublished()))
                    return -1;
                else if (photo1.getPublished().isBefore(photo2.getPublished()))
                    return 1;
                else
                    return 0;
            });
        else
            photos.sort((photo1, photo2) -> {
                if (photo1.getPublished().isAfter(photo2.getPublished()))
                    return 1;
                else if (photo1.getPublished().isBefore(photo2.getPublished()))
                    return -1;
                else
                    return 0;
            });
        adapter.notifyDataSetChanged();
    }
}
