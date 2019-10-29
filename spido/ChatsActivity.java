package com.example.spido;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ChatsActivity extends AppCompatActivity {

    String activeUser = "";
    List<Map<String,String>> messages = new ArrayList<>();
    SimpleAdapter simpleAdapter;
    //ArrayAdapter arrayAdapter;


    public void sendChat (View view) {
        final EditText chatEditText = findViewById(R.id.chatEditText);
        final String typedMessage = chatEditText.getText().toString();
        if (!typedMessage.equals("")) {
            ParseObject message = new ParseObject("Message");
            message.put("sender", ParseUser.getCurrentUser().getUsername());
            message.put("recipient", activeUser);
            message.put("message", typedMessage);
            chatEditText.setText("");
            chatEditText.setHint("Type a message");

            message.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Map<String, String> tweetInfo = new HashMap<>();
                        tweetInfo.put("content", typedMessage);
                        tweetInfo.put("time", "now.");

                        messages.add(tweetInfo);
                        simpleAdapter.notifyDataSetChanged();
                        sendNotification();
                        Toast.makeText(ChatsActivity.this, "Message Sent!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(ChatsActivity.this, "Sending Failed. Check network connection", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {
            Toast.makeText(this, "Empty messages not allowed!", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendNotification()
    {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    try {
                        String jsonResponse;

                        URL url = new URL("https://onesignal.com/api/v1/notifications");
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setUseCaches(false);
                        con.setDoOutput(true);
                        con.setDoInput(true);

                        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        con.setRequestProperty("Authorization", "Basic MjJkMWMwNmUtYWQzZS00MDdmLTg3MWItMGMzNGQ5YmExYzY4");
                        con.setRequestMethod("POST");

                        String strJsonBody = "{"
                                + "\"app_id\": \"982efc88-5cc9-417c-b8d0-b49b0a4c5502\","

                                + "\"filters\": [{\"field\": \"tag\", \"key\": \"User_ID\", \"relation\": \"=\", \"value\": \"" + activeUser + "\"}],"

                                + "\"data\": {\"foo\": \"bar\"},"
                                + "\"contents\": {\"en\": \"from \"" + activeUser +"\"}"
                                + "}";


                        System.out.println("strJsonBody:\n" + strJsonBody);

                        byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                        con.setFixedLengthStreamingMode(sendBytes.length);

                        OutputStream outputStream = con.getOutputStream();
                        outputStream.write(sendBytes);

                        int httpResponse = con.getResponseCode();
                        System.out.println("httpResponse: " + httpResponse);

                        if (httpResponse >= HttpURLConnection.HTTP_OK
                                && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                            Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        } else {
                            Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        }
                        System.out.println("jsonResponse:\n" + jsonResponse);

                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode ==1 ) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri selectedImage = data.getData();

        if (requestCode ==1 && data != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImage);
                //
                if (bitmap.getWidth() > 2048 && bitmap.getHeight() > 2048)
                {
                    bitmap = Bitmap.createScaledBitmap(bitmap, 1024, 1280, true);

                }
                else if (bitmap.getWidth() > 2048 && bitmap.getHeight() < 2048)
                {
                    bitmap = Bitmap.createScaledBitmap(bitmap, 1920, 1200, true);

                }
                else if (bitmap.getWidth() < 2048 && bitmap.getHeight() > 2048)
                {
                    bitmap = Bitmap.createScaledBitmap(bitmap, 1024, 1280, true);

                }
                else if (bitmap.getWidth() < 2048 && bitmap.getHeight() < 2048)
                {
                    bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);

                }
                //
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
                byte[] byteArray = stream.toByteArray();
                ParseFile file = new ParseFile("image.png",byteArray);
                ParseObject object = new ParseObject("Image");
                object.put("image",file);
                object.put("sender",ParseUser.getCurrentUser().getUsername());
                object.put("recipient",activeUser);
                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(ChatsActivity.this, "Image Sent!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ChatsActivity.this, "Problem sending image :(", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sendImage (View view) {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            getPhoto();
        }

    }

    public void getPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,1);
    }

    public void gotoMedia(View view) {
        Intent intent = new Intent(getApplicationContext(),MediaActivity.class);
        intent.putExtra("username",activeUser);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        Intent intent = getIntent();
        activeUser = intent.getStringExtra("username");

        setTitle("Chat with "+activeUser);

        final ListView chatListView = findViewById(R.id.chatListView);

        ParseQuery<ParseObject> query1 = new ParseQuery<ParseObject>("Message");
        query1.whereEqualTo("sender",ParseUser.getCurrentUser().getUsername());
        query1.whereEqualTo("recipient",activeUser);

        ParseQuery<ParseObject> query2 = new ParseQuery<ParseObject>("Message");
        query2.whereEqualTo("sender",activeUser);
        query2.whereEqualTo("recipient",ParseUser.getCurrentUser().getUsername());

        List<ParseQuery<ParseObject>> queries = new ArrayList<>();
        queries.add(query1);
        queries.add(query2);

        ParseQuery<ParseObject> query = ParseQuery.or(queries);
        query.orderByAscending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects.size() > 0) {
                    messages.clear();
                    for (ParseObject message : objects) {
                        Map<String,String> tweetInfo = new HashMap<>();
                        String messageContent = message.getString("message");
                        if (!message.getString("sender").equals(ParseUser.getCurrentUser().getUsername())) {

                            messageContent = activeUser + ":\n"+messageContent;
                        }

                        tweetInfo.put("content",messageContent);
                        Date date = message.getCreatedAt();
                        DateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm");
                        String reportDate = df.format(date);
                        tweetInfo.put("time",reportDate);
                        messages.add(tweetInfo);
                    }
                    simpleAdapter = new SimpleAdapter(ChatsActivity.this,messages,android.R.layout.simple_list_item_2,new String[] {"content","time"},new int[]{android.R.id.text1,android.R.id.text2});
                    chatListView.setAdapter(simpleAdapter);
                    chatListView.post(new Runnable() {
                        @Override
                        public void run() {
                            chatListView.setSelection(chatListView.getCount() - 1);
                        }
                    });
                }
            }
        });

    }
}
