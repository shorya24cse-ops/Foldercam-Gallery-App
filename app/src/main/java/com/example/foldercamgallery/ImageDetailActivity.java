package com.example.foldercamgallery;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import java.text.DateFormat;
import java.util.Date;

public class ImageDetailActivity extends AppCompatActivity {

    ImageView imageView;
    TextView tvName, tvPath, tvSize, tvDate;
    Button btnDelete;

    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        imageView = findViewById(R.id.imageView);
        tvName = findViewById(R.id.tvName);
        tvPath = findViewById(R.id.tvPath);
        tvSize = findViewById(R.id.tvSize);
        tvDate = findViewById(R.id.tvDate);
        btnDelete = findViewById(R.id.btnDelete);

        String uriString = getIntent().getStringExtra("path");
        imageUri = Uri.parse(uriString);

        imageView.setImageURI(imageUri);

        DocumentFile file = DocumentFile.fromSingleUri(this, imageUri);

        if (file != null) {

            tvName.setText("Name: " + file.getName());
            tvPath.setText("Path: " + imageUri.toString());
            tvSize.setText("Size: " + file.length() + " bytes");

            String date = DateFormat.getDateTimeInstance()
                    .format(new Date(file.lastModified()));

            tvDate.setText("Date Taken: " + date);
        }

        btnDelete.setOnClickListener(v -> {

            new AlertDialog.Builder(this)
                    .setTitle("Delete Image")
                    .setMessage("Are you sure you want to delete this image?")
                    .setPositiveButton("Delete", (dialog, which) -> {

                        if (file != null) {
                            file.delete();
                        }

                        Toast.makeText(ImageDetailActivity.this,
                                "Image deleted successfully",
                                Toast.LENGTH_SHORT).show();

                        setResult(RESULT_OK);
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();

        });
    }

}