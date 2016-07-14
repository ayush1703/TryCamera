package in.co.ayushjain.trycamera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity {
    protected String _path;
    protected boolean _taken;
    protected Button img;
    protected ImageView image;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStorageDirectory().toString());
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".png",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        _path = "file:" + image.getAbsolutePath();
        Log.i(_path,_path);
        return image;

    }
    protected static final String PHOTO_TAKEN = "photo_taken";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = (Button) findViewById(R.id.imgbutton);
        img.setOnClickListener(new ButtonClickHandler());

    }


    public class ButtonClickHandler implements View.OnClickListener
    {
        public void onClick( View view ){
            try {
                startCameraActivity();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    protected void startCameraActivity() throws IOException {
        File file = createImageFile();
        Uri outputFileUri = Uri.fromFile( file );

        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE );
       // intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
        intent.putExtra( MediaStore.EXTRA_OUTPUT, outputFileUri );

        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.i("MakeMachine", "resultCode: " + resultCode);
        switch( resultCode )
        {
            case 0:
                Log.i( "MakeMachine", "User cancelled" );
                break;

            case -1:
                onPhotoTaken();
                break;
        }
    }
    protected void onPhotoTaken()
    {
        Intent intent_email = new Intent(Intent.ACTION_SEND);
        intent_email.setData(Uri.parse("mailto:"));

        intent_email.putExtra(Intent.EXTRA_SUBJECT,"Image Attached: ");
        intent_email.putExtra(Intent.EXTRA_TEXT,"Recent Camera Image Attached");
        intent_email.putExtra(Intent.EXTRA_STREAM, Uri.parse(_path));
        if (intent_email.resolveActivity(getPackageManager())!=null)
        {
            Log.i("intent called","inside if");
            startActivity(intent_email);
        }

        _taken = true;
        image = (ImageView)findViewById(R.id.imageView);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;


        Bitmap bitmap = BitmapFactory.decodeFile( _path, options );
        if(bitmap ==null) {
            Log.i(_path, "inside on photo");
        }
        image.setImageBitmap(bitmap);

    }

    @Override
    protected void onRestoreInstanceState( Bundle savedInstanceState){
        Log.i( "MakeMachine", "onRestoreInstanceState()");
        if( savedInstanceState.getBoolean( MainActivity.PHOTO_TAKEN ) ) {
            onPhotoTaken();
        }
    }

    @Override
    protected void onSaveInstanceState( Bundle outState ) {
        outState.putBoolean(MainActivity.PHOTO_TAKEN, _taken);
    }

}
