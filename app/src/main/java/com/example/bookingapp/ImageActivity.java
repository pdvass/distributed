package com.example.bookingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import android.util.Log;
import android.view.View;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import com.example.bookingapp.Client.*;
import com.example.bookingapp.Share.*;

public class ImageActivity extends AppCompatActivity {
    private static final String TAG = "ImageActivity";
    ImageView imageDieth;
    TextView titleDieth;
    TextView textDieth;
    ImageView imageFS;
    TextView titleFS;
    TextView textFS;
    ImageView imageHil;
    TextView titleHil;
    TextView textHil;
    ImageView imageHC;
    TextView titleHC;
    TextView textHC;
    ImageView imagePerg;
    TextView titlePerg;
    TextView textPerg;
    Button btnFilter;
    Button btnBook;
    Button btnReview;
    EditText editFrom;
    EditText editTo;
    EditText editRoomId;
    EditText editPersons;
    EditText editStars;
    EditText editRegion;
    EditText editReview;
    EditText editHotel;
    private Request req;
    private Socket clientSocket = null;
    private ObjectInputStream ois = null;
    private ObjectOutputStream oos = null;
    private HashMap<String, String> hotels = null;
    private HashMap<String, ArrayList<String>> rooms = null;
    private HashMap<String, String> filteredHotels = null;
    private HashMap<String, ArrayList<String>> filteredRooms = null;
    private List<byte[]> bytesOfImages = null;
    private List<Integer> bytesLength = null;

    public Handler titleHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String resTitleD = message.getData().getString("Diet");
                    titleDieth.setText(resTitleD);
                    titleDieth.setVisibility(View.VISIBLE);

                    String resTitleFS = message.getData().getString("FourS");
                    titleFS.setText(resTitleFS);
                    titleFS.setVisibility(View.VISIBLE);

                    String resTitleH = message.getData().getString("Hilton");
                    titleHil.setText(resTitleH);
                    titleHil.setVisibility(View.VISIBLE);

                    String resTitleHC = message.getData().getString("HotelCal");
                    titleHC.setText(resTitleHC);
                    titleHC.setVisibility(View.VISIBLE);

                    String resTitleP = message.getData().getString("Per");
                    titlePerg.setText(resTitleP);
                    titlePerg.setVisibility(View.VISIBLE);

                }
            });
            return true;
        }
    });

    public Handler infoHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String reTextD = message.getData().getString("roomsD");
                    textDieth.setText(reTextD);
                    textDieth.setVisibility(View.VISIBLE);

                    String reTextFS = message.getData().getString("roomsFS");
                    textFS.setText(reTextFS);
                    textFS.setVisibility(View.VISIBLE);

                    String reTextH = message.getData().getString("roomsH");
                    textHil.setText(reTextH);
                    textHil.setVisibility(View.VISIBLE);

                    String reTextHC = message.getData().getString("roomsHC");
                    textHC.setText(reTextHC);
                    textHC.setVisibility(View.VISIBLE);

                    String reTextP = message.getData().getString("roomsP");
                    textPerg.setText(reTextP);
                    textPerg.setVisibility(View.VISIBLE);

                }
            });
            return true;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        btnFilter = findViewById(R.id.btnFilter);
        btnBook = findViewById(R.id.btnBook);
        btnReview = findViewById(R.id.btnReview);

        imageDieth = findViewById(R.id.imageDieth);
        titleDieth = findViewById(R.id.titleDieth);
        textDieth = findViewById(R.id.textDieth);

        imageFS = findViewById(R.id.imageFS);
        titleFS = findViewById(R.id.titleFS);
        textFS = findViewById(R.id.textFS);

        imageHil = findViewById(R.id.imageHil);
        titleHil = findViewById(R.id.titleHil);
        textHil = findViewById(R.id.textHil);

        imageHC = findViewById(R.id.imageHC);
        titleHC = findViewById(R.id.titleHC);
        textHC = findViewById(R.id.textHC);

        imagePerg = findViewById(R.id.imagePerg);
        titlePerg = findViewById(R.id.titlePerg);
        textPerg = findViewById(R.id.textPerg);

        editFrom = findViewById(R.id.editFrom);
        editTo = findViewById(R.id.editTo);
        editRoomId = findViewById(R.id.editRoomName);
        editPersons = findViewById(R.id.editPerson);
        editStars = findViewById(R.id.editStars);
        editRegion = findViewById(R.id.editRegion);
        editReview = findViewById(R.id.editReview);
        editHotel = findViewById(R.id.editHotel);

        // Start connection in a background thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TCPClient client = new TCPClient();
                    client.startConnection("192.168.2.6", 4555);

                    req = new Request(client, "user connection");
                    req.sendMessage();

                    if (!req.receiveMessage().equals("client connected")) {
                        runOnUiThread(() -> Toast.makeText(ImageActivity.this, "Could not connect. Please try again later.", Toast.LENGTH_SHORT).show());
                        return;
                    }
                } catch (Exception e) {
                    Log.d(TAG, Objects.requireNonNull(e.getMessage()));
                }

                try {
                    req.changeContents("hotels");
                    req.sendMessage();

                    // We already know from server side, that we need to cast
                    hotels = new HashMap<>();
                    rooms = new HashMap<>();
                    bytesOfImages = new ArrayList<>();
                    bytesLength = new ArrayList<>();

                    rooms = (HashMap<String, ArrayList<String>>) req.receiveRequestObject();
                    hotels = (HashMap<String, String>) req.receiveRequestObject();
                    bytesOfImages = (List<byte[]>) req.receiveRequestObject();
                    bytesLength = (List<Integer>) req.receiveRequestObject();

                } catch (Exception e) {
                    Toast.makeText(ImageActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }

                Message mesHotel = new Message();
                Bundle bunHotel = new Bundle();
                Message m = new Message();
                Bundle b = new Bundle();

                StringBuilder hotelInfo;
                StringBuilder roomsInfo;
                for (String hotelName : hotels.keySet()) {
                    hotelInfo = new StringBuilder();
                    roomsInfo = new StringBuilder();
                    for (String room : (Objects.requireNonNull(rooms.get(hotelName)))) {
                        roomsInfo.append(room);
                    }

                    hotelInfo.append(hotels.get(hotelName));
                    switch (hotelName) {
                        case "Diethnes":
                            bunHotel.putString("Diet", hotelInfo.toString());
                            b.putString("roomsD", roomsInfo.toString());
                            break;
                        case "Four Seasons":
                            bunHotel.putString("FourS", hotelInfo.toString());
                            b.putString("roomsFS", roomsInfo.toString());
                            break;
                        case "Hilton":
                            bunHotel.putString("Hilton", hotelInfo.toString());
                            b.putString("roomsH", roomsInfo.toString());
                            break;
                        case "Hotel California":
                            bunHotel.putString("HotelCal", hotelInfo.toString());
                            b.putString("roomsHC", roomsInfo.toString());
                            break;
                        case "Pergamos":
                            bunHotel.putString("Per", hotelInfo.toString());
                            b.putString("roomsP", roomsInfo.toString());
                            break;
                    }
                }

                mesHotel.setData(bunHotel);
                titleHandler.sendMessage(mesHotel);
                m.setData(b);
                infoHandler.sendMessage(m);

                // Ensure UI update is performed on the main thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytesOfImages.get(0), 0, bytesLength.get(0));
                        imageDieth.setImageBitmap(bitmap);

                        bitmap = BitmapFactory.decodeByteArray(bytesOfImages.get(1), 0, bytesLength.get(1));
                        imageFS.setImageBitmap(bitmap);

                        bitmap = BitmapFactory.decodeByteArray(bytesOfImages.get(2), 0, bytesLength.get(2));
                        imageHil.setImageBitmap(bitmap);

                        bitmap = BitmapFactory.decodeByteArray(bytesOfImages.get(3), 0, bytesLength.get(3));
                        imageHC.setImageBitmap(bitmap);

                        bitmap = BitmapFactory.decodeByteArray(bytesOfImages.get(4), 0, bytesLength.get(4));
                        imagePerg.setImageBitmap(bitmap);


                    }
                });

                btnBook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(ImageActivity.this, "Clicked", Toast.LENGTH_SHORT).show();

                        // Network operations must be done on a separate thread
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {

                                    String from = editFrom.getText().toString();
                                    String to = editTo.getText().toString();
                                    String roomId = editRoomId.getText().toString();

                                    String msg = "book " + roomId + " dates:[" + from + "-" + to + "]";
                                    Log.d(TAG, "Message: "+msg);

                                    req.changeContents(msg);
                                    req.sendMessage();

                                    String answer = req.receiveMessage();
                                    Log.d(TAG, "Message received: " + Boolean.toString(answer == null));

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (answer.equals("Booked successfully")) {
                                                Toast.makeText(ImageActivity.this, "This room is now booked", Toast.LENGTH_LONG).show();
                                                Log.d("TAG", "This room is now booked");
                                            } else {
                                                Toast.makeText(ImageActivity.this, "This room wasn't available the dates you wanted.", Toast.LENGTH_LONG).show();
                                                Log.d("TAG", "This room wasn't available the dates you wanted.");
                                            }
                                        }
                                    });

                                } catch (Exception e) {
                                    Log.d(TAG, Objects.requireNonNull(e.getMessage()));
                                }
                            }
                        }).start();
                    }
                });

                btnFilter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(ImageActivity.this, "Clicked", Toast.LENGTH_SHORT).show();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    req.changeContents("filter");
                                    req.sendMessage();

                                    String msg = "get hotels filter ";

                                    String person = editPersons.getText().toString();
                                    if (!person.equals("persons")){
                                        msg = msg + " nOfPersons:"+person;
                                    }

                                    String region = editRegion.getText().toString();
                                    if (!region.equals("region")){
                                        msg = msg + " region:"+region;
                                    }

                                    String stars = editStars.getText().toString();
                                    if (!stars.equals("stars")){
                                        msg = msg + " stars:"+stars;
                                    }

                                    String from = editFrom.getText().toString();
                                    String to = editTo.getText().toString();
                                    if (!(from.equals("from"))||(to.equals("to"))){
                                        msg = msg + " dates:["+from+"-"+to+"]";
                                    }

                                    Log.d(TAG, "Filter: "+msg);
                                    req.changeContents(msg);
                                    req.sendRequestObject();

                                    // We already know from server side, that we need to cast
                                    filteredHotels = new HashMap<>();
                                    filteredRooms = new HashMap<>();

                                    filteredRooms = (HashMap<String, ArrayList<String>>) req.receiveRequestObject();
                                    filteredHotels = (HashMap<String, String>) req.receiveRequestObject();
                                    bytesOfImages = (List<byte[]>) req.receiveRequestObject();
                                    bytesLength = (List<Integer>) req.receiveRequestObject();

                                    Log.d(TAG, String.valueOf((filteredRooms == null)));

                                } catch (Exception e) {
                                    Log.d(TAG, Objects.requireNonNull(e.getMessage()));
                                }

                                    Message m = new Message();
                                    Bundle b = new Bundle();

                                    StringBuilder roomsInfo;
                                    for (String hotelName : filteredHotels.keySet()) {
                                        roomsInfo = new StringBuilder();
                                        for (String room : (Objects.requireNonNull(filteredRooms.get(hotelName)))) {
                                            roomsInfo.append(room);
                                        }

                                        switch (hotelName) {
                                            case "Diethnes":
                                                b.putString("roomsD", roomsInfo.toString());
                                                break;
                                            case "Four Seasons":
                                                b.putString("roomsFS", roomsInfo.toString());
                                                break;
                                            case "Hilton":
                                                b.putString("roomsH", roomsInfo.toString());
                                                break;
                                            case "Hotel California":
                                                b.putString("roomsHC", roomsInfo.toString());
                                                break;
                                            case "Pergamos":
                                                b.putString("roomsP", roomsInfo.toString());
                                                break;
                                        }
                                    }
                                    m.setData(b);
                                    infoHandler.sendMessage(m);


                            }
                        }).start();
                    }
                });

                btnReview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(ImageActivity.this, "Clicked", Toast.LENGTH_SHORT).show();

                        // Network operations must be done on a separate thread
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    req.changeContents("review");
                                    req.sendMessage();

                                    String stars = editTo.getText().toString();
                                    String hotelName = editRoomId.getText().toString();

                                    String msg = "Hotel:" +hotelName+ " Stars:" +stars;
                                    req.changeContents(msg);
                                    req.sendRequestObject();

                                } catch (Exception e) {
                                    Log.d(TAG, Objects.requireNonNull(e.getMessage()));
                                }
                            }
                        }).start();
                    }
                });

            }
        }).start();

    }
}

