package com.treaknite.autoespiallite;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Capture extends AppCompatActivity {

    // Variable declaration for capture process
    static final int GALLERY = 1, CAMERA = 2;
    Uri photoURI = null;
    private ImageView image;  // image view
    private String currentPhotoPath; // Captured photo path
    private Bitmap imageBitmap;  // Image bitmap

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Removing action bar
        getSupportActionBar().hide();

        setContentView(R.layout.activity_capture);

        showPictureDialogBox(); // Picture dialog for choice to users
        Button proceed = (Button) findViewById(R.id.capture_btn_proceed);
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceedForResult();
            }
        });

    }

    private void showPictureDialogBox() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {"Select photo from gallery", "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallery();
                                break;
                            case 1:
                                try {
                                    takePhotoFromCamera();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                    }
                });
        pictureDialog.setNegativeButton("Go Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Capture.this, MainActivity.class);
                finish();
                startActivity(intent);
            }
        });
        pictureDialog.setCancelable(false);
        pictureDialog.show();
    }

    public void choosePhotoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() throws IOException {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        image = (ImageView) findViewById(R.id.capture_imgView_capturedImage);
        if (requestCode == GALLERY && resultCode == RESULT_OK) {
            if (data != null) {
                photoURI = data.getData();
                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoURI);
                    // saveImage(imageBitmap);
                    image.setImageBitmap(imageBitmap);
                    Toast.makeText(getApplicationContext(), "Image Ready for analysing!", Toast.LENGTH_SHORT).show();


                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Image Saving Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        }
        if (requestCode == CAMERA && resultCode == RESULT_OK) {
            imageBitmap = (Bitmap) data.getExtras().get("data");
            image.setImageBitmap(imageBitmap);
            // saveImage(imageBitmap);
            Toast.makeText(getApplicationContext(), "Image Saved & Ready for analysing!", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to convert Bitmap into Base64 string
    // Bitmap -> Compress bitmap to ByteArrayOutputStream -> ByteArrayOutputStream to ByteArray -> ByteArray to Base64
    private String convertBitmapToBase64() {
        Bitmap bitmap = imageBitmap;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageByteArray = byteArrayOutputStream.toByteArray();
        String imageBase64 = Base64.encodeToString(imageByteArray, Base64.DEFAULT);

        // Removing new line character from imageBase64 string
        imageBase64 = imageBase64.replaceAll("\n","");

        return imageBase64;
    }


    // Method to be executed on clicking of Proceed Button
    private void proceedForResult() {
        String imageToTextString = convertBitmapToBase64();
        Log.d("base64", imageToTextString);
        Intent intent = new Intent(this, Result.class);
        intent.putExtra("imageTextString", imageToTextString);
        finish();
        startActivity(intent);
    }
}