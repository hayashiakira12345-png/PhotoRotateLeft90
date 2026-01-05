package com.example.photorotateleft90;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CREATE_FILE_REQUEST = 2;

    private ImageView imageView;
    private Button btnRotate;
    private Button btnSave;
    private Bitmap currentBitmap;

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    loadImage(uri);
                }
            }
    );

    private final ActivityResultLauncher<Intent> saveImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    saveImage(uri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        btnRotate = findViewById(R.id.btnRotate);
        btnSave = findViewById(R.id.btnSave);
        Button btnSelect = findViewById(R.id.btnSelect);

        btnSelect.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            pickImageLauncher.launch(intent);
        });

        btnRotate.setOnClickListener(v -> {
            if (currentBitmap != null) {
                rotateBitmap();
            }
        });

        btnSave.setOnClickListener(v -> {
            if (currentBitmap != null) {
                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_TITLE, "rotated_image.jpg");
                saveImageLauncher.launch(intent);
            }
        });
    }

    private void loadImage(Uri uri) {
        try (InputStream inputStream = getContentResolver().openInputStream(uri)) {
            currentBitmap = BitmapFactory.decodeStream(inputStream);
            imageView.setImageBitmap(currentBitmap);
            btnRotate.setEnabled(true);
            btnSave.setEnabled(true);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "加载图片失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void rotateBitmap() {
        Matrix matrix = new Matrix();
        matrix.postRotate(-90);
        Bitmap rotated = Bitmap.createBitmap(currentBitmap, 0, 0, 
                currentBitmap.getWidth(), currentBitmap.getHeight(), matrix, true);
        currentBitmap = rotated;
        imageView.setImageBitmap(currentBitmap);
    }

    private void saveImage(Uri uri) {
        try (OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
            if (currentBitmap != null) {
                currentBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
        }
    }
}
