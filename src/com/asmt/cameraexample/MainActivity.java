package com.asmt.cameraexample;


import java.io.File;
import java.util.Calendar;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	// Activity request codes
	public static MainActivity obj;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQ = 100;
    private static final int REQ_CODE = 200;
    private static final int PICK_GALLERY = 300;
    private static final int CAPTURE_DCIM = 400;
    private static final int PIC_CROP = 500;
    
    Boolean started= false;
    public TextView title;
    
	private Button btnCamera,btnPick,btnActivity,btnService;
	private ImageView img;
	private Uri picUri;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		obj = this;
		title = (TextView)findViewById(R.id.txtBox);
		img = (ImageView)findViewById(R.id.imageView);
		btnCamera = (Button)findViewById(R.id.btnCamera);		
		btnPick = (Button)findViewById(R.id.btnPick);
		btnPick.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pickGallery();
			}
		});
		btnActivity = (Button)findViewById(R.id.btnActivityB);
		btnActivity.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openActivityB();
			}
		});
		btnService = (Button)findViewById(R.id.btnService);
		btnService.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startAndStopService();
			}
		});		
		btnCamera.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				captureImage();
			}
		});
	}
	
	public void captureImage(){		
 
        if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
        	
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, "img");
            // start the image capture Intent
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQ);

        } else {
            Toast.makeText(getApplication(), "Camera not supported", Toast.LENGTH_LONG).show();
        }	
	}
	
	public void captureNSaveInDCIM(View v){
		try {
            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
          
            File dir= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            Calendar c = Calendar.getInstance();
            String date = fromInt(c.get(Calendar.MONTH))
                        + fromInt(c.get(Calendar.DAY_OF_MONTH))
                        + fromInt(c.get(Calendar.YEAR))
                        + fromInt(c.get(Calendar.HOUR_OF_DAY))
                        + fromInt(c.get(Calendar.MINUTE))
                        + fromInt(c.get(Calendar.SECOND));
            File output=new File(dir,"Camera/"+date.toString()+".jpeg");
            picUri = Uri.fromFile(output);
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
            startActivityForResult(captureIntent, CAPTURE_DCIM);
        }
        catch(ActivityNotFoundException anfe){
                    //display an error message
                String errorMessage = "Whoops - your device doesn't support capturing images!";
                Toast.makeText(getApplicationContext(), errorMessage.toString(), Toast.LENGTH_LONG).show();
            }
	}
	public String fromInt(int val)
    {
		return String.valueOf(val);
    }
	
	public void pickGallery(){
		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent, PICK_GALLERY); 
	}
	
	public void openActivityB(){
		Intent intent = new Intent(getApplicationContext(),ActivityB.class);
		startActivityForResult(intent,REQ_CODE);
	}
	public void startAndStopService(){
		
		if(started==false){
			startService(new Intent(this,MyServices.class));			
			started=true;
			btnService.setText("Stop Service");
		}else{
			stopService(new Intent(this,MyServices.class));
			started=false;
			btnService.setText("Start Service");
		}		
	}	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {		
		if(resultCode == RESULT_OK){
			 switch(requestCode) { 
			    case CAPTURE_IMAGE_ACTIVITY_REQ:      		      
		    		Bitmap photo = (Bitmap) data.getExtras().get("data");
		            img.setImageBitmap(photo);  
			        break;
			    case REQ_CODE:
		    		String returnedResult = data.getStringExtra("name");
		    		Toast.makeText(getApplicationContext(), returnedResult,Toast.LENGTH_LONG).show();		        	    	
			    	break;
			    case PICK_GALLERY: 
		            picUri = data.getData();
		            performCrop();
			 		break;
			    case CAPTURE_DCIM:
			    	performCrop();
			    	sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, picUri));
			    	break;
			    case PIC_CROP:
			    	//get the returned data
	                 Bundle extras = data.getExtras();
	                 //get the cropped bitmap
	                 Bitmap thePic = extras.getParcelable("data");
	                 //display the returned cropped image
	                 img.setImageBitmap(thePic);
			    	break;
			    }
		}
		else if (resultCode == RESULT_CANCELED) {			    	
	    	Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
	    } 
	}	
	private void performCrop(){
        //take care of exceptions
        try {
                //call the standard crop action intent (the user device may not support it)
                Intent cropIntent = new Intent("com.android.camera.action.CROP"); 
                //indicate image type and Uri
                cropIntent.setDataAndType(picUri, "image/*");
                //set crop properties
                cropIntent.putExtra("crop", "true");
                //indicate aspect of desired crop
                cropIntent.putExtra("aspectX", 1);
                cropIntent.putExtra("aspectY", 1);
                //indicate output X and Y
                cropIntent.putExtra("outputX", 256);
                cropIntent.putExtra("outputY", 256);
                //retrieve data on return
                cropIntent.putExtra("return-data", true);
                //start the activity - we handle returning in onActivityResult
                startActivityForResult(cropIntent, PIC_CROP);  
        }
        //respond to users whose devices do not support the crop action
        catch(ActivityNotFoundException anfe){
                //display an error message
                String errorMessage = "Whoops - your device doesn't support the crop action!";
                Toast.makeText(getApplicationContext(), errorMessage.toString(), Toast.LENGTH_LONG).show();
        }
    }
}
