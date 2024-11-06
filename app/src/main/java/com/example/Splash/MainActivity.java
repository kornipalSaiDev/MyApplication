package com.example.Splash;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "Announcements";
    private BroadcastReceiver customReceiver;
    private static final String CUSTOM_INTENT_ACTION = "com.example.CUSTOM_DEVICE_ACTION";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView i1 = findViewById(R.id.pdf_preview);
        i1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPdf();
            }
        });

        customReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (CUSTOM_INTENT_ACTION.equals(intent.getAction())) {
                    Log.d("MainActivity", "Custom intent received!");
                    showNotification(getApplicationContext());
                }
            }
        };
        IntentFilter filter = new IntentFilter(CUSTOM_INTENT_ACTION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(customReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        }
//        Intent customIntent = new Intent(CUSTOM_INTENT_ACTION);
//        getApplicationContext().sendBroadcast(customIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(customReceiver);
    }

    private void openPdf() {
        File pdfFile = new File(getExternalFilesDir(null), "/storage/emulated/O/Download/sample_doc.pdf");

        if (pdfFile.exists()) {
            Uri pdfUri = Uri.fromFile(pdfFile);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(pdfUri, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, "No PDF viewer installed", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "PDF file not found", Toast.LENGTH_SHORT).show();
        }
    }

    public static void showNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Meeting Announcements", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Meeting Reminder")
                .setContentText("Hey, you have a meeting scheduled!!")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(1, builder.build());
    }
}
