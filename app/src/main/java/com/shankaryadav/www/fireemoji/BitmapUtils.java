       package com.shankaryadav.www.fireemoji;

       import android.content.Context;
      import android.content.Intent;
       import android.graphics.Bitmap;
       import android.graphics.BitmapFactory;
       import android.net.Uri;
       import android.os.Environment;
       import android.support.v4.content.FileProvider;
       import android.util.DisplayMetrics;
       import android.view.WindowManager;
       import android.widget.Toast;

       import java.io.File;
       import java.io.FileOutputStream;
       import java.io.IOException;
       import java.io.OutputStream;
       import java.text.SimpleDateFormat;
       import java.util.Date;
       import java.util.Locale;

         class BitmapUtils {

    private static final String FILE_PROVIDER_AUTHORITY = "com.example.android.fileprovider";

    /*
    * Resample the captured photo to fit the screen for better memory usage.
    *
    * param is context
    * param is imagePath
    * returns bitmap
    * */

    static Bitmap resamplePic(Context context, String imagePath){

        //get device screen size information
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(metrics);

        int targetH = metrics.heightPixels;
        int targetW = metrics.widthPixels;

        // get the dimensions of the original bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath,bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        //scaling down the image
        int scaleFactor = Math.min( photoW/targetW, photoH/targetH);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        return BitmapFactory.decodeFile(imagePath);
     }

     /*
     * Creates the temporary image file in the cache directory
     *
     * @return the temporary image files
     * @throws IOException Thrown if there is an error creating the file
      */

     static File createTempImageFile(Context context) throws IOException {
         String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                 Locale.getDefault()).format(new Date());
         String imageFileName = "JPEG_" + timeStamp + "_";
         File storageDir = context.getExternalCacheDir();

         return File.createTempFile(
                 imageFileName,
                 ".jpg",

                 storageDir
         );
     }

     /*
     * delete image file for a given path
     *
     * @param context  The application context.
     * @param imagePath the path of the photo to be deleted
     * **/

     static  boolean deleteImageFiile(Context context, String imagepath) {
         // Get the file
         File imageFile = new File(imagepath);

         // Delete the image
         boolean deleted = imageFile.delete();

         // checks the error
         if (!deleted){
             Toast.makeText(context, "File is not deleted", Toast.LENGTH_SHORT).show();
         }

         return deleted;
     }

     /*
     * Helper method for adding photo into the device
     * Gallery so that
     * other apps can use that photo
     *
     * **/
     private static void galleryAddPic(Context context, String imagePath){

         Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
         File f = new File(imagePath);
         Uri contentUri = Uri.fromFile(f);

         intent.setData(contentUri);;
         context.sendBroadcast(intent);
     }

     /*
     * Helper method for saving image.
     *
     * @param context the application context
     * @param Bitmap the image to be saved
     * @return The path of the saved image
     * **/

     static String saveImage(Context context,Bitmap image){

         String savedImagePath = null;

         // create the new file in the external storage

         String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                 Locale.getDefault()).format(new Date());
         String imageFileName = "JPEG_" + timeStamp + ".jpg";

         File file = new File(
                 Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                 + "/FireEmoji");

         boolean success = true;

         if (!file.exists()){
             success = file.mkdirs();
         }

         //save the new Bitmap

         if (success) {
             File imageFile = new File(file,imageFileName);
             savedImagePath = imageFile.getAbsolutePath();

             try {
                 OutputStream fOut = new FileOutputStream(imageFile);
                 image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                 fOut.close();
             }catch (Exception e){
                 e.printStackTrace();
             }

             // Add the image to the gallery
             galleryAddPic(context, savedImagePath);


             // Show a Toast with the save location
             Toast.makeText(context, "Saved Location " + savedImagePath, Toast.LENGTH_SHORT).show();
         }
         return savedImagePath;
     }

    /*
    * Helper method for sharing an image
    *
    * @param context the image context
    * @param imagePath The path of the image to be shared
    * **/

      static void shareImage(Context context, String imagePath){
      // creating the share intent and start the share activity
      File imageFile = new File(imagePath);
      Intent shareIntent = new Intent(Intent.ACTION_SEND);
      shareIntent.setType("image/*");
      Uri photoUri = FileProvider.getUriForFile(context, FILE_PROVIDER_AUTHORITY, imageFile);
      shareIntent.putExtra(Intent.EXTRA_STREAM,photoUri);
      context.startActivity(shareIntent);
        }

     }
