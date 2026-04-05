package com.example.foldercamgallery;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;

import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {


    Button btnCamera, btnGallery;
    Uri selectedFolderUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_MEDIA_IMAGES
                    },
                    100
            );
        }

        btnCamera = findViewById(R.id.btnCamera);
        btnGallery = findViewById(R.id.btnGallery);

        btnCamera.setOnClickListener(v -> {

            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            startActivityForResult(intent, 201);

        });

        btnGallery.setOnClickListener(v -> {

            String savedUri = getSharedPreferences("FolderPrefs", MODE_PRIVATE)
                    .getString("folderUri", null);

            if (savedUri == null) {

                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                startActivityForResult(intent, 200);

            } else {

                Intent intent = new Intent(MainActivity.this, GalleryActivity.class);
                intent.putExtra("folderUri", savedUri);
                startActivity(intent);

            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 201 && resultCode == RESULT_OK && data != null) {

            selectedFolderUri = data.getData();

            getContentResolver().takePersistableUriPermission(
                    selectedFolderUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            );

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, 101);
        }

        if (requestCode == 200 && resultCode == RESULT_OK && data != null) {

            selectedFolderUri = data.getData();

            getContentResolver().takePersistableUriPermission(
                    selectedFolderUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            );

            getSharedPreferences("FolderPrefs", MODE_PRIVATE)
                    .edit()
                    .putString("folderUri", selectedFolderUri.toString())
                    .apply();

            Intent intent = new Intent(MainActivity.this, GalleryActivity.class);
            intent.putExtra("folderUri", selectedFolderUri.toString());
            startActivity(intent);
        }

        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {

            Bundle extras = data.getExtras();
            if (extras == null) return;

            Bitmap imageBitmap = (Bitmap) extras.get("data");

            try {

                DocumentFile pickedDir = DocumentFile.fromTreeUri(this, selectedFolderUri);

                if (pickedDir != null && pickedDir.canWrite()) {

                    DocumentFile newFile = pickedDir.createFile(
                            "image/jpeg",
                            "photo_" + System.currentTimeMillis()
                    );

                    OutputStream out = getContentResolver().openOutputStream(newFile.getUri());
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

                    if (out != null) {
                        out.close();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}