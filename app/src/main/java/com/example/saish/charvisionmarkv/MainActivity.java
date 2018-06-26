package com.example.saish.charvisionmarkv;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.graphics.Bitmap.createScaledBitmap;

public class MainActivity extends AppCompatActivity {

    //reference website https://code.tutsplus.com/tutorials/how-to-use-google-cloud-machine-learning-services-for-android--cms-28630

    ImageView imgview;
    Button analyze;
    Button capture;
    TextView textview;
    Bitmap picture;

    String data;
    String density= "";

    int CAMERA_REQUEST_CODE = 111;
    int gotImage = 0;

    String CurrentPhotoPath;
    Uri imageUri;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK)
        {
            picture = BitmapFactory.decodeFile(imageUri.getPath());
            //picture = (Bitmap) data.getExtras().get("data");
            Log.e("CAPTURE:", "onActivityResult: picture size :" + picture.getByteCount() );
            gotImage = 1;
            setImage();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgview = (ImageView) findViewById(R.id.imageView);
        analyze = (Button) findViewById(R.id.button);
        capture = (Button) findViewById(R.id.button2);
        textview = (TextView) findViewById(R.id.textView);


        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Filename = Environment.getExternalStorageDirectory().getPath() + "/Test/test.jpg";
               // imageUri = Uri.fromFile(new File(Filename));

                Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                try {
                    File photoFile = createImageFile();
                    imageUri = Uri.fromFile(photoFile);
                    captureImage.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);

                    startActivityForResult(captureImage,CAMERA_REQUEST_CODE);

                } catch (IOException e) {
                    e.printStackTrace();
                    textview.setText("ERROR could not create PHOTO file");
                }



                //Drawable mydraw = getResources().getDrawable(R.drawable.pay3);
               // picture = ((BitmapDrawable) mydraw).getBitmap();
                //density = density + picture.getByteCount();
               // Log.e("Density::::::", "onClick:  " + density );
                //This function checks the size and adjusts it to requried size of < 4 million bytes.
                //check_size();
                //imgview.setImageBitmap(picture);

            }
        });


        analyze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                data = "";
                //checking internet permission
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED)
                {

                    data = data + GoogleVision.Run_OCR(getApplicationContext(),picture);
                    finaliseData();
                }
                else
                {
                    data = "SORRY PLEASE CHECK YOUR INTERNET CONNECTION";
                }

            }
        });
    }


    //FUNCTION TO finalise data
    public void finaliseData() {
        data = data + " \n ABOVE ARE THE RESULTS";
        textview.setText(data);
    }

    //FUNCTION TO GET APPROPRIATE SIZE OF IMAGE
    public void check_size()
    {
        if (picture.getByteCount() > 4000000)
        {
            //try changing the height and width in createScaledBitmap function.
            picture = createScaledBitmap(picture, ((int) (picture.getWidth() * 0.9)), ((int) (picture.getHeight() * 0.9)),true);
            density = "";
            density = density + picture.getByteCount();
            Log.e("NEEDS REDUCTION:", "check_size:" + density );
            check_size();
        }
    }



    //FUNCTION TO CAPTURE IMAGE
    public void setImage()
    {
        if(gotImage == 1)
        {
            check_size();
            imgview.setImageBitmap(picture);

        }
        else
        {
            textview.setText("UNABLE TO CAPTURE IMAGE" + gotImage);
        }
    }


    //creates a image file with a unique name.
    public File createImageFile() throws IOException{

        String TimeStamp = new SimpleDateFormat("yyyymmdd_hhmmss").format(new Date());
        String ImageFileName = "JEPG_"+TimeStamp + "_";
        File StorageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(ImageFileName,".jpg",StorageDir);

        CurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

}






