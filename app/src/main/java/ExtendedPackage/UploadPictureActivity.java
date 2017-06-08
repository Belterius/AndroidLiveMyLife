package ExtendedPackage;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import ClassPackage.ToastClass;
import lml.androidlivemylife.R;

import static android.R.attr.data;
import static android.R.attr.permission;

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
                imageViewPicturePreview.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            setPic();
        }

        super.onActivityResult(requestCode, resultCode, data);
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
     * Encodes the picture to Base64 string
     * @param bmp
     * @return the encoded picture
     */
    private String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 10, baos);
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

//        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);

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
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoFile);7
//                Uri photoURI = Uri.fromFile(photoFile);
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
     * @throws UnsupportedOperationException
     * TODO
     * If you want to resize the picture before uploading (and reduce its weight)
     */
    private void setPicAndResize() {
        throw new UnsupportedOperationException("UploadPictureActivity - setPicAndResize : Not yet implemented !");
//        // Get the dimensions of the View
//        int targetW = imageViewPicturePreview.getWidth();
//        int targetH = imageViewPicturePreview.getHeight();
//
//        // Get the dimensions of the bitmap
//        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//        bmOptions.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
//        int photoW = bmOptions.outWidth;
//        int photoH = bmOptions.outHeight;
//
//        // Determine how much to scale down the image
//        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
//
//        // Decode the image file into a Bitmap sized to fill the View
//        bmOptions.inJustDecodeBounds = false;
//        bmOptions.inSampleSize = scaleFactor;
//        bmOptions.inPurgeable = true;
//
//        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
//        imageViewPicturePreview.setImageBitmap(bitmap);
    }

    /**
     * Set the image view after having taken a new picture from the camera
     * And make it available.
     */
    private void setPic() {
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);

        //Use Picasso to handle rotated picture
        Picasso.with(this.getApplicationContext()).load(contentUri).into(imageViewPicturePreview);

        /*
            Make the new picture instantly vailable in the Android
            Gallery application and to other apps.
         */
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);

        //Set the bitmap which will be used for the upload
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), contentUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
