package ExtendedPackage;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import lml.androidlivemylife.R;

/**
 * Created by Gimlibéta on 14/05/2017.
 */

/**
 *
 * The classes uses that needs to use the following functions : showFileChooser
 * TODO : Use multipart upload in  order to get lighter cache file on the server
 */
public class UploadPictureActivity extends AppCompatActivity {

    //Declaring views
    protected ImageView imageViewPicturePreview;
   // protected EditText editTextPictureName;

    //Image request code
    private int PICK_IMAGE_REQUEST = 1;

    //storage permission code
    private static final int STORAGE_PERMISSION_CODE = 123;

    //Bitmap to get image from gallery
    protected Bitmap bitmap;

    //Uri to store the image uri
    private Uri filePath;

    //Used to start the capture picture intent
    static final int REQUEST_IMAGE_CAPTURE = 2;

    private String mCurrentPhotoPath;

    protected final int PERMISSION_ALL = 10;
    private ImageButton takePictureButton;
    private ImageButton choosePictureFromGalleryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setImageViewForUploadClass(int idFromViewById){
        imageViewPicturePreview = (ImageView) findViewById(idFromViewById);
    }

    public void setTakePictureButton(int idFromViewById){
        this.takePictureButton = (ImageButton) findViewById(idFromViewById);
    }

    public void setChoosePictureFromGalleryButton(int idFromViewById){
        this.choosePictureFromGalleryButton = (ImageButton) findViewById(idFromViewById);
    }

    /**
     * Method to show file chooser when we click on the button
     */
    public void showFileChooser(View v) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    /**
     * Method to take a picture when we click on the button
     */
    public void takeNewPicture(View v) {
        this.dispatchTakePictureIntent();
    }

    /**
     * Handling the image chooser activity result
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                mCurrentPhotoPath = getPath(filePath);
                setPictureWithRotation();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            setPictureWithRotation();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    protected Boolean requestEveryPermission(){

        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            return false;
        }

        return true;
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                //For every permissions, check if it is granted
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    //If it is not, we will ask them
                    return false;
                }
            }
        }
        //Otherwise do nothing
        return true;
    }

    /**
     * Requesting permission
     */
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    /**
     * Requesting permission
     */
    private void requestCapturePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
    }

    /**
     * This method will be called when the user will tap on allow or deny
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can use the camera", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }

        if( requestCode == PERMISSION_ALL){
            if(verificatePermissions(permissions, grantResults, true) == 0){

                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle(R.string.alert_confirm);
                alert.setMessage(R.string.alert_message_permission_confirm);
                alert.setPositiveButton(R.string.choice_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alert.setNegativeButton(R.string.choice_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestEveryPermission();
                        dialog.dismiss();
                    }
                });

                alert.show();
            }
        }
    }

    /**
     *
     * @param permissions
     * @param grantResults
     * @param acceptRevokedPermission If we can go futher with revoked permissions
     * @return -1 : there are some denied permissions and the user clicked on "remember my choice"
     *          0 : there are some denied permissions and the user didn't click on "remember my choice"
     *          1 : Ok
     */
    protected int verificatePermissions(@NonNull String[] permissions, @NonNull int[] grantResults, Boolean acceptRevokedPermission ){

        Boolean permissionOk = false;
        Boolean permissionNotOkButRemember = false;
        Boolean permissionNotOk = false;
        Boolean permissionTemp = false;

        for(int i = 0;i<grantResults.length;i++){
            switch(permissions[i]){
                case Manifest.permission.WRITE_EXTERNAL_STORAGE :
                    permissionTemp = setVisibleInvisibleButton(grantResults[i], this.choosePictureFromGalleryButton);
                    break;
                case Manifest.permission.CAMERA :
                    permissionTemp = setVisibleInvisibleButton(grantResults[i], this.takePictureButton);
                    break;
            }

            if (! permissionTemp){
                if(! ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])){
                    permissionNotOkButRemember = true;
                }else{
                    permissionNotOk = true;
                }
            }else if(permissionTemp){
                permissionOk = true;
            }
        }

        if(permissionNotOk){
            return 0;
        }else if(permissionNotOkButRemember){
            if(acceptRevokedPermission){
                Toast.makeText(this, R.string.toast_remember_permission, Toast.LENGTH_LONG).show();
                return 1;
            }else{
                return -1;
            }
        }else{
            return 1;
        }
    }

    /**
     * Sets to Visible or Invisible the buttons depending on the permissions
     * @param resultPermission
     * @param imageButtonToSet
     */
    private Boolean setVisibleInvisibleButton(int resultPermission, ImageButton imageButtonToSet){
        if(resultPermission  == PackageManager.PERMISSION_GRANTED){
            if(imageButtonToSet != null){
                imageButtonToSet.setVisibility(View.VISIBLE);
            }
            return true;
        }else{
            if(imageButtonToSet != null){
                imageButtonToSet.setVisibility(View.INVISIBLE);
            }
            return false;
        }
    }


    /**
     * Gets the file path from uri
     * @param uri
     * @return
     */
    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    /**
     * Encodes the picture to Base64 string
     * @param bmp
     * @return the encoded picture
     */
    private String getStringImage(Bitmap bmp){

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        return encodedImage;
    }

    /**
     * Avoids to pass the current bitmap to use getStringImage.
     * Will convert the bitmap to Base64 for uploading
     * @return
     */
    public String getImageToPassForRequest(){
        return this.getStringImage(this.bitmap);
    }

    /**
     * Displays a new intent to take a picture
     */
    private void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFileExternal();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e("UploadPictureActivity", ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }

    }

    /**
     * Create a temp image
     * @return
     * @throws IOException
     */
    private File createImageFileExternal() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        //If you change one of the 2 storageDir, be sure to update the xml/file_paths.xml (with the corresponding path)
        //If you want to put the files on the following folder : SDCart/Android/data/lml.androidlivemylife/files/Pictures/LiveMyLife/
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/LiveMyLife/") ;

        //If you want to put the files on the common folder : Pictures/LiveMyLife/
        File storageDir =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/LiveMyLife/");

        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        //create a collision-resistant file name
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * Resize the picture - rotate the picture correctly - Add it immediatly to the gallery
     * From : https://stackoverflow.com/questions/39287131/how-reduce-image-size-when-upload-to-server
     */
    private void setPictureWithRotation(){
        String filePath = mCurrentPhotoPath;
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612
        float maxHeight = 1280.0f;
        float maxWidth = 800.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            int rotate = exifOrientationToDegrees(orientation);
            Log.d("EXIF", "Rotate: " + rotate);
            matrix.postRotate(rotate);

            //Display it properly
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }


        this.bitmap = scaledBitmap;
        imageViewPicturePreview.setImageBitmap(scaledBitmap);

        galleryAddPic();
    }

    /**
     * The following example method demonstrates how to invoke the system's media scanner
     * to add your photo to the Media Provider's database, making it available in the Android
     * Gallery application and to other apps.
     */
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    /**
     * Setting inSampleSize value allows to load a scaled down version of the original image
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

}
