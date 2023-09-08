package com.example.qrtarea;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CAMERA = 111;
    private Bitmap mSelectedImage;
    private ImageView mImageView;
    private TextView txtResultado;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
    }
    private void initializeViews() {
        txtResultado = findViewById(R.id.txtresultado);
        mImageView = findViewById(R.id.image_view);
    }

    public void abrircam(View view) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE);
        startActivityForResult(cameraIntent, REQUEST_CAMERA);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            handleImageCapture(requestCode, data);
        }
    }

    private void handleImageCapture(int requestCode, Intent data) {
        try {
            mSelectedImage = requestCode == REQUEST_CAMERA ? (Bitmap) data.getExtras().get("data")
                    : MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
            mImageView.setImageBitmap(mSelectedImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //hacer el scaner
    public void scanqr(View v) {
        if (mSelectedImage != null) {
            InputImage image = InputImage.fromBitmap(mSelectedImage, 0);
            BarcodeScanner scanner = BarcodeScanning.getClient();
            processImage(scanner, image);
        } else {
            txtResultado.setText("No image to scan");
        }
    }
//procesar la imagen
    private void processImage(BarcodeScanner scanner, InputImage image) {
        scanner.process(image)
                .addOnSuccessListener(this::displayBarcodes)
                .addOnFailureListener(e -> txtResultado.setText("Error imagen"));
    }
    private void displayBarcodes(List<Barcode> barcodes) {
        for (Barcode barcode : barcodes) {
            String value = barcode.getDisplayValue();
            txtResultado.setText(value);
        }
    }
}