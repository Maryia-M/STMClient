package com.example.stmclient;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity<ToogleButton> extends AppCompatActivity {

    ExtendedImageView smallMapImageView;
    ImageView mapPieceImageView;
    Button getImageButton;
    ToggleButton selectToggleButton;
    EditText leftTopXEditText;
    EditText leftTopYEditText;
    EditText rightBottomXEditText;
    EditText rightBottomYEditText;

    final static String SERVER_ADDRESS = "http://192.168.100.16:8080/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        smallMapImageView = findViewById(R.id.smallMapImageView);
        mapPieceImageView = findViewById(R.id.mapPieceImageView);
        getImageButton = findViewById(R.id.getImageButton);
        selectToggleButton = findViewById(R.id.selectToggleButton);
        leftTopXEditText = findViewById(R.id.leftTopXEditText);
        leftTopYEditText = findViewById(R.id.leftTopYEditText);
        rightBottomXEditText = findViewById(R.id.rightBottomXEditText);
        rightBottomYEditText = findViewById(R.id.rightBottomYEditText);
        selectToggleButton.setChecked(true);
        try {
            Connection connector = new Connection(SERVER_ADDRESS + "img",this, true);
            connector.execute();
        } catch (MalformedURLException e) {
            System.out.println("Exeception while trying to connect:\n" + e.getMessage());
        }

        getImageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println(selectToggleButton.getText());
               if(selectToggleButton.getText().equals("Image")){
                   if(smallMapImageView.point_count != 2){
                       Toast.makeText(getApplicationContext(),
                               "You didn't select the rectange on image yet", Toast.LENGTH_SHORT).show();
                   }
                   else{
                       getImagePartGivenRectangle();
                   }
               }
               else{
                   try {
                       float leftTopX = Float.parseFloat(leftTopXEditText.getText().toString());
                       float leftTopY = Float.parseFloat(leftTopYEditText.getText().toString());
                       float rightBottomX = Float.parseFloat(rightBottomXEditText.getText().toString());
                       float rightBottomY = Float.parseFloat(rightBottomYEditText.getText().toString());
                       if(leftTopX >= 0 && leftTopX <= smallMapImageView.getWidth() && leftTopY >= 0 &&
                               leftTopY <= smallMapImageView.getHeight() && rightBottomX >= 0 &&
                               rightBottomX <= smallMapImageView.getWidth() && rightBottomY >= 0 &&
                               rightBottomY <= smallMapImageView.getHeight()){
                           getImagePartGivenCoords(leftTopX, leftTopY, rightBottomX, rightBottomY);
                       }
                       else{
                           Toast.makeText(getApplicationContext(),
                                   "Wrong coordinates input", Toast.LENGTH_SHORT).show();
                       }
                   }
                   catch(Exception e){
                       Toast.makeText(getApplicationContext(),
                               "Wrong coordinates input", Toast.LENGTH_SHORT).show();
                   }

               }
            }
        });

    }

    void getImagePartGivenRectangle(){
        try {
            String request = SERVER_ADDRESS + "imgc?xLU=" + Math.min(smallMapImageView.a.x, smallMapImageView.b.x)/smallMapImageView.getWidth() +
                    "&yLU=" + Math.min(smallMapImageView.a.y, smallMapImageView.b.y)/smallMapImageView.getHeight() + "&xRL=" +
                    Math.max(smallMapImageView.a.x, smallMapImageView.b.x)/smallMapImageView.getWidth() + "&yRL=" +
                    Math.max(smallMapImageView.a.y, smallMapImageView.b.y)/smallMapImageView.getHeight();
            /*String request = SERVER_ADDRESS + "imgc?xLU=" + String.valueOf(0) +
                    "&yLU=" + String.valueOf(0.5) + "&xRL=" +
                    String.valueOf(0.5) + "&yRL=" +
                    String.valueOf(0);*/
            System.out.println(request);
            Connection connector = new Connection(request,this, false);
            connector.execute();
        } catch (MalformedURLException e) {
            System.out.println("Exeception while trying to connect:\n" + e.getMessage());
        }
    }

    void getImagePartGivenCoords(float leftTopX, float leftTopY, float rightBottomX, float rightBottomY){
        try {
            String request = SERVER_ADDRESS + "imgc?xLU=" + leftTopX/smallMapImageView.getWidth() +
                    "&yLU=" + leftTopY/smallMapImageView.getHeight() + "&xRL=" +
                    rightBottomX/smallMapImageView.getWidth() + "&yRL=" +
                    rightBottomY/smallMapImageView.getHeight();
            /*String request = SERVER_ADDRESS + "imgc?xLU=" + String.valueOf(0) +
                    "&yLU=" + String.valueOf(0.5) + "&xRL=" +
                    String.valueOf(0.5) + "&yRL=" +
                    String.valueOf(0);*/
            System.out.println(request);
            Connection connector = new Connection(request,this, false);
            connector.execute();
        } catch (MalformedURLException e) {
            System.out.println("Exception while trying to connect:\n" + e.getMessage());
        }
    }




    public class Connection extends AsyncTask<Void, Void, Void> {

        private final URL requestURL;
        private final MainActivity activity;
        Boolean isWholeImage;

        public Connection(String request, MainActivity activity, Boolean isGettingWholeImage) throws MalformedURLException {
            this.requestURL = new URL(request);
            this.activity = activity;
            this.isWholeImage = isGettingWholeImage;
        }

        @Override
        protected Void doInBackground(Void... params) {
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) requestURL.openConnection();
                connection.setRequestProperty("User-Agent", "map-svc");
                connection.setRequestMethod("GET");
                System.out.println("Connection opened");
                if (connection.getResponseCode() == 200) {
                    try (InputStream in = connection.getInputStream()) {

                        final Drawable d = Drawable.createFromStream(in, null);
                        System.out.println("Received data");

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(isWholeImage){
                                    activity.smallMapImageView.setImageDrawable(d);
                                }
                                else{
                                    activity.mapPieceImageView.setImageDrawable(d);
                                }
                            }
                        });

                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        return null;
                    }
                } else {
                    System.out.println("Didn't get the result, response code is " + connection.getResponseCode());
                }

            } catch (IOException e) {

                System.out.println(e.getMessage());
                return null;

            } finally {

                if (connection != null) {
                    connection.disconnect();
                }

            }
            return null;
        }
    }

}