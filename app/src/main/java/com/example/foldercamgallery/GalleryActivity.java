package com.example.foldercamgallery;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {

    GridView gridView;
    ArrayList<String> imageList = new ArrayList<>();
    Uri folderUri;
    Button btnChangeFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        gridView = findViewById(R.id.gridView);
        btnChangeFolder = findViewById(R.id.btnChangeFolder);

        String uriString = getIntent().getStringExtra("folderUri");

        if (uriString != null) {
            folderUri = Uri.parse(uriString);
        } else {

            String savedUri = getSharedPreferences("FolderPrefs", MODE_PRIVATE)
                    .getString("folderUri", null);

            if (savedUri != null) {
                folderUri = Uri.parse(savedUri);
            }
        }

        if (folderUri != null) {
            loadImages();
        }

        btnChangeFolder.setOnClickListener(v -> {

            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            startActivityForResult(intent, 300);

        });

        gridView.setOnItemClickListener((AdapterView<?> parent, android.view.View view, int position, long id) -> {

            Intent intent = new Intent(GalleryActivity.this, ImageDetailActivity.class);
            intent.putExtra("path", imageList.get(position));
            startActivityForResult(intent, 400);

        });
    }

    private void loadImages() {

        imageList.clear();

        DocumentFile folder = DocumentFile.fromTreeUri(this, folderUri);

        if (folder != null && folder.isDirectory()) {

            for (DocumentFile file : folder.listFiles()) {

                if (file.isFile() && file.getType() != null &&
                        file.getType().startsWith("image/")) {

                    imageList.add(file.getUri().toString());

                }
            }
        }

        ImageAdapter adapter = new ImageAdapter(this, imageList);
        gridView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 400 && resultCode == RESULT_OK) {
            loadImages();
        }

        if (requestCode == 300 && resultCode == RESULT_OK && data != null) {

            folderUri = data.getData();

            getSharedPreferences("FolderPrefs", MODE_PRIVATE)
                    .edit()
                    .putString("folderUri", folderUri.toString())
                    .apply();

            loadImages();
        }
    }


}