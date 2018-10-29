package com.shankaryadav.www.fireemoji;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_STORAGE_PERMISSION = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private static final String FILE_PROVIDER_AUTHORITY = "com.example.android.fileprovider";
    private static String mTempPhotoPath;

    FloatingActionButton clearFab, saveFab, shareFab;
    TextView mtextView;
    ImageView imageView;
    Button mButton;

    private static  Bitmap mResultsBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        clearFab = (FloatingActionButton) findViewById(R.id.clearMe);
        saveFab = (FloatingActionButton) findViewById(R.id.saveMe);
        shareFab = (FloatingActionButton) findViewById(R.id.shareMe);

        mtextView = findViewById(R.id.textView);
        imageView = findViewById(R.id.imageView);
        mButton = findViewById(R.id.clickMeButton);
    }






    /**
     * OnClick method for "Emojify Me!" Button. Launches the camera app.
     */

    public void checkPermission(View view) {
        // Check for the external storage permission
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // If you do not have permission, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION);
        } else {
            // Launch the camera if the permission exists
            launchCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // Called when you request permission to read and write to external storage
        switch (requestCode) {
            case REQUEST_STORAGE_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // If you get permission, launch the camera
                    launchCamera();
                } else {
                    // If you do not get permission, show a Toast
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    /**
     * Creates a temporary image file and captures a picture to store in it.
     */
    private void launchCamera() {

        // Create the capture image intent
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the temporary File where the photo should go
            File photoFile = null;
            try {
                photoFile = BitmapUtils.createTempImageFile(this);
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {

                // Get the path of the temporary file
                mTempPhotoPath = photoFile.getAbsolutePath();

                // Get the content URI for the image file
                Uri photoURI = FileProvider.getUriForFile(this,
                        FILE_PROVIDER_AUTHORITY,
                        photoFile);

                // Add the URI so the camera can store the image
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                // Launch the camera activity
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // If the image capture activity was called and was successful
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Process the image and set it to the TextView
            processAndSetImage();
        } else {

            // Otherwise, delete the temporary image file
            BitmapUtils.deleteImageFiile(this, mTempPhotoPath);
        }
    }

    /**
     * Method for processing the captured image and setting it to the TextView.
     */
    private void processAndSetImage() {

        // Toggle Visibility of the views
        mButton.setVisibility(View.GONE);
        mtextView.setVisibility(View.GONE);

        // Resample the saved image to fit the ImageView
        mResultsBitmap = BitmapUtils.resamplePic(this, mTempPhotoPath);


        // Detect the faces and overlay the appropriate emoji
//        mResultsBitmap = Emojifier.detectFacesandOverlayEmoji(this, mResultsBitmap);

        // Set the new bitmap to the ImageView
        imageView.setImageBitmap(mResultsBitmap);
    }


    /**
     * OnClick method for the save button.
     */

    public void saveMe(View view) {
        // Delete the temporary image file
        BitmapUtils.deleteImageFiile(this, mTempPhotoPath);

        // Save the image
        BitmapUtils.saveImage(this, mResultsBitmap);
    }

    /**
     * OnClick method for the share button, saves and shares the new bitmap.
     */

    public void shareMe(View view) {
        // Delete the temporary image file
        BitmapUtils.deleteImageFiile(this, mTempPhotoPath);

        // Save the image
        BitmapUtils.saveImage(this, mResultsBitmap);

        // Share the image
        BitmapUtils.shareImage(this, mTempPhotoPath);
    }

    /**
     * OnClick for the clear button, resets the app to original state.
     */

    public void clearMe(View view) {
        // Clear the image and toggle the view visibility
        imageView.setImageResource(0);
        mButton.setVisibility(View.VISIBLE);
        mtextView.setVisibility(View.VISIBLE);

        // Delete the temporary image file
        BitmapUtils.deleteImageFiile(this, mTempPhotoPath);
    }
}