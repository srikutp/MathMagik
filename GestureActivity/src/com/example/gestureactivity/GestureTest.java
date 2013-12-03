package com.example.gestureactivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;
import java.lang.Object;

import android.R.string;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.GestureStore;
import android.gesture.GestureStroke;
import android.gesture.Prediction;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.hardware.Sensor;
import  com.example.gestureactivity.ShakeDetector;
import com.example.gestureactivity.ShakeDetector.OnShakeListener;

import expr.Expr;
import expr.Parser;
import expr.SyntaxException;

import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;



public class GestureTest extends Activity implements OnGesturePerformedListener {
 private GestureLibrary gestureLib;
 public EditText display;
 private ShakeDetector mShakeDetector;
 private SensorManager mSensorManager;
 private Sensor mAccelerometer;
 StringBuilder text=new StringBuilder();
 String temp="";
 int cursorPosition,position,flagset=0;
 ImageView imageView;
 boolean exprEval;
 int strl;
 //added the image view!!
 
 //sensorMgr = (SensorManager)getSystemService(SENSOR_SERVICE);
//private CharSequence text="ddd";
 
  
/** Called when the activity is first created. */
  GestureOverlayView mView;
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
 // ShakeDetector initialization
 // ShakeDetector initialization
    mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    mShakeDetector = new ShakeDetector();
    mShakeDetector.setOnShakeListener(new OnShakeListener() {

        @Override
        public void onShake(int count) {
        	text=new StringBuilder(); 
        	display.setText(" "+ text);
            }
        });

    mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    
    
    
   /*
    display.setInputType(InputType.TYPE_NULL);
 // for later than GB only
    if (android.os.Build.VERSION.SDK_INT >= 11) {
     // this fakes the TextView (which actually handles cursor drawing)
     // into drawing the cursor even though you've disabled soft input
     // with TYPE_NULL
     display.setRawInputType(InputType.TYPE_CLASS_TEXT);
    }
*/
    
    display = (EditText) findViewById(R.id.txt);
    display.setInputType(InputType.TYPE_NULL);
 // for later than GB only
 if (android.os.Build.VERSION.SDK_INT >= 11) {
     // this fakes the TextView (which actually handles cursor drawing)
     // into drawing the cursor even though you've disabled soft input
     // with TYPE_NULL
	 display.setRawInputType(InputType.TYPE_CLASS_TEXT);
 }
    
    
    
 
    
    mView = (GestureOverlayView) findViewById(R.id.gestures);
    mView.setGestureColor(Color.BLACK);
    
    // GestureOverlayView gestureOverlayView = new GestureOverlayView(this);
    //View inflate = getLayoutInflater().inflate(R.layout.main, null);
   //  gestureOverlayView.addView(inflate);
    // gestureOverlayView.addOnGesturePerformedListener(this);
    gestureLib = GestureLibraries.fromRawResource(this, R.raw.gestures);
    gestureLib.setOrientationStyle(GestureStore.ORIENTATION_SENSITIVE);
    gestureLib.setSequenceType(GestureStore.SEQUENCE_SENSITIVE);
    
    
    
    if (!gestureLib.load()) {
      finish();
    }
    else{
    	mView.addOnGesturePerformedListener(this);
    }
    
  }

  
@Override
  public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
	//set the flag so that it does not enter the gesture matching parentesis below
	boolean faulty_gesture_flag = false;
	if (flagset==1){
		imageView.setVisibility(View.INVISIBLE);
		flagset=0;
		}
    ArrayList<Prediction> predictions = gestureLib.recognize(gesture);
    ArrayList<Prediction> twoStrokePredictions=new ArrayList<Prediction>();
    ArrayList<Prediction> singleStrokePredictions=new ArrayList<Prediction>();
    ArrayList<Prediction> threeStrokePredictions=new ArrayList<Prediction>();
    display.requestFocus();
    int No_of_strokes = gesture.getStrokesCount();
    if(No_of_strokes == 2){
    	for (Prediction prediction : predictions) {
    		 if(!(prediction.name.equals("one") || prediction.name.equals("two") || prediction.name.equals("three")
    				 || prediction.name.equals("four") || prediction.name.equals("five") || prediction.name.equals("six") || prediction.name.equals("seven") || prediction.name.equals("eight") || prediction.name.equals("nine") 
    				   || prediction.name.equals("zero") || prediction.name.equals("openbracket") || prediction.name.equals("closebracket") ||  prediction.name.equals("multiply") || prediction.name.equals("back") || prediction.name.equals("divide") ||  prediction.name.equals("help") ||  prediction.name.equals("minus"))){
    			 twoStrokePredictions.add(prediction);
    		 }
    	}
    	
    }
    else if(No_of_strokes>2 && No_of_strokes <5){
    	for (Prediction prediction : predictions) {
    		if((prediction.name.equals("multiply"))){
    			threeStrokePredictions.add(prediction);
    		}
    	}
    	
    }
    else{
    	for (Prediction prediction : predictions) {
    		if(!(prediction.name.equals("share") || prediction.name.equals("plus") || prediction.name.equals("multiply") || prediction.name.equals("equals")  )){
    			singleStrokePredictions.add(prediction);
    		}
    	}
    	
    }
    if(No_of_strokes == 2 ){
    	predictions.clear();
    	predictions.addAll(twoStrokePredictions);
    	
    }
    else if(No_of_strokes <2 ){
    	predictions.clear();
    	predictions.addAll(singleStrokePredictions);	
    }
    else if(No_of_strokes ==3 || No_of_strokes ==4){
    	predictions.clear();
    	predictions.addAll(threeStrokePredictions);	
    
    }
    //Mutlistroke gesture condition!! 
    else {
    	final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
	    alertDialog.setTitle("Math Expreesion");
	    alertDialog.setMessage("Your Gesture cannot be recognized please try an appropriate gesture");
	    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int which) {

			  alertDialog.cancel();

		} });
	alertDialog.show();
	faulty_gesture_flag = true;
	//predictions.clear();
	
    	
    }
    Prediction highestPrediction=predictions.get(0);
    for (Prediction prediction : predictions) {
    	if(prediction.score > highestPrediction.score){
    		highestPrediction=prediction;
    	}
    }
        
         
        cursorPosition = display.getSelectionStart();
        {
        	if(cursorPosition == 0){
        		position = cursorPosition; 
        		temp =text.substring(position,(text.length()));
        		text.delete(position, (text.length()));
        	}
        	else{
        		position = cursorPosition; 
        		temp =text.substring(position,(text.length()));
        		text.delete(position, (text.length()));
        	}
        	
        }
        
        
    
   //StringBuilder appnd=new StringBuilder();
    //if faulty gesture flag is set the control does not enter this loop!!
   boolean recognizedGesture=false;
   boolean isBack=false;
   if (faulty_gesture_flag != true) {
      if (highestPrediction.score > 1.0) {
    	  //int i=1;
    	  
    	        String symbol = highestPrediction.name;
    	        if ("one".equals(symbol)) {
    	        	text=text.append("1");
    	        	recognizedGesture=true;
    	        }
    	        else if("two".equals(symbol)){
    	        	text=text.append("2");
    	        	recognizedGesture=true;
    	        }
    	        else if("three".equals(symbol)){
    	        	text=text.append("3");
    	        	recognizedGesture=true;
    	        }
    	        else if("four".equals(symbol)){
    	        	text=text.append("4");
    	        	recognizedGesture=true;
    	        }
    	        else if("five".equals(symbol)){
    	        	text=text.append("5");
    	        	recognizedGesture=true;
    	        }
    	        else if("six".equals(symbol)){
    	        	text=text.append("6");
    	        	recognizedGesture=true;
    	        }
    	        else if("seven".equals(symbol)){
    	        	text=text.append("7");
    	        	recognizedGesture=true;
    	        }
    	        else if("eight".equals(symbol)){
    	        	text=text.append("8");
    	        	recognizedGesture=true;
    	        }
    	        else if("nine".equals(symbol)){
    	        	text=text.append("9");
    	        	recognizedGesture=true;
    	        }
    	        else if("zero".equals(symbol)){
    	        	text=text.append("0");
    	        	recognizedGesture=true;
    	        }
    	        else if("divide".equals(symbol)){
    	        	text=text.append("/");
    	        	recognizedGesture=true;
    	        }
    	        
    	        else if("plus".equals(symbol)){
    	        	text=text.append("+");
    	        	recognizedGesture=true;
    	        }
    	        else if("openbracket".equals(symbol)){
    	        	text=text.append("(");
    	        	recognizedGesture=true;
    	        }
    	        else if("closebracket".equals(symbol)){
    	        	text=text.append(")");
    	        	recognizedGesture=true;
    	        }
    	        else if("minus".equals(symbol) ){
    	        	text=text.append("-");
    	        	recognizedGesture=true;
    	        }
    	        else if("multiply".equals(symbol)){
    	        	text=text.append("*");
    	        	recognizedGesture=true;
    	        }
    	        else if("equals".equals(symbol)){
    	        
    	            exprEval=true;
    	        	recognizedGesture=true;
    	           	Expr expr;
    	           	//String s="1+2";
    	           	try {
    	           		String txt= String.valueOf(text);
    	               expr = Parser.parse(txt);
    	               double r=expr.value();
    	               strl=String.valueOf(r).length();
    	               cursorPosition=strl;
    	           	   String exp=String.valueOf(r);
    	           	   //System.out.println(expr.value());
    	           	   text=new StringBuilder(exp);
    	           	   ///text.append(exp);
    	           	   
    	           	} catch (SyntaxException e) {
    	           	   System.err.println(e.explain());
    	           	   return;
    	           	}
    	        
    	        
    	        
    	        
    	        
    	        
    	        }
    	        // help gesture to show the gesture sheet!!
    	        else if("help".equals(symbol)){
    	        	imageView = (ImageView) findViewById(R.id.imageView1);
    	        	imageView.setImageResource(R.drawable.help);
    	        	imageView.setVisibility(View.VISIBLE);
    	        	flagset=1;
    	        	
    	            			     	        			   	        		        
    	        		   
    	        		    //imageView.setVisibility(View.GONE);
    	        	/*ImageView imageView = new ImageView(getApplicationContext());
    	        	LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    	        	String path = Environment.getExternalStorageDirectory() + "/storage/emulated/0/help.bmp";
    	        	Bitmap image = BitmapFactory.decodeFile(path);
    	        	imageView.setImageBitmap(image);
    	        	LinearLayout rl = (LinearLayout) findViewById(R.id.LL1);
    	        	rl.addView(imageView, lp);*/
    	        }    	         	        	
    	        else if("back".equals(symbol)){
    	        	if(cursorPosition ==0){
    	        		final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        	        	alertDialog.setTitle("Delete");
        	        	alertDialog.setMessage("You are at the start of expression. Cannot delete");
        	        	alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
        	        		  public void onClick(DialogInterface dialog, int which) {

        	        			  alertDialog.cancel();

        	        		} });
        	        	alertDialog.show();

    	        		//int index=text.length();
        	        	//text.deleteCharAt(index-1);
    	        	}
    	        	else{
    	        		int index=position;
        	        	text.deleteCharAt(index-1);
    	        	   	}
    	        	isBack=true;
    	           }
    	        else if ("save".equals(symbol)){
    	        	
    	        	Bitmap image=takeScreenshot(this);
    	        	SaveImage(image); 
    	        	final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    	        	alertDialog.setTitle("Math Expreesion");
    	        	alertDialog.setMessage("Your expresson is saved in Gallery");
    	        	alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
    	        		  public void onClick(DialogInterface dialog, int which) {

    	        			  alertDialog.cancel();

    	        		} });
    	        	alertDialog.show();

    	        	//Toast.makeText(GestureTest.this, "Your image is saved in Gallery", Toast.LENGTH_LONG) .show();
    	        	
    	        }
    	          	        	
    	        	else if("share".equals(symbol)){
    	        		
    	               	Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
    	               	sharingIntent.setType("text/plain");
    	               	String shareBody = "Expression is:" + text.toString();
    	               	sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Math Expression");
    	               	sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
    	               	startActivity(Intent.createChooser(sharingIntent, "Share via"));
    	            
    	      	        }
    	        else if ("quit".equals(symbol)){
    	        	//Toast.makeText(GestureTest.this, "Bye Bye .. See U later ", Toast.LENGTH_LONG).show();
    	        	finish();
    	            System.exit(0);
    	        	
    	        }
    	        
    	        
    	        
    	  if (temp!=null && exprEval==false){
    	     text.append(temp);
    	     temp="";
    	     //position =0;
    	  }
    	  else{
    		  
    	  }
    	    display.setText("" + text);
			  if(isBack==true && cursorPosition != 0){
    	    	display.setSelection(cursorPosition-1);
    	    	isBack=false;
    	    }
    	    else if(recognizedGesture==true ){
    	    	if(exprEval == false){
    	    		display.setSelection(cursorPosition+1);
    	    	}
    	    	else{
    	    		display.setSelection(cursorPosition);
    	    		exprEval=false;
    	    	}
    	    	recognizedGesture=false;
    	    }
    	    else{
    	    	display.setSelection(cursorPosition);
    	    	
    	    }
    	    
    	   // Toast.makeText(this, prediction.name, Toast.LENGTH_SHORT).show();
      }
   }
    
  }

public static Bitmap takeScreenshot(Activity activity) { 
	View view = activity.getWindow().getDecorView();
	view.setDrawingCacheEnabled(true); 
	view.buildDrawingCache();
	Bitmap bitmap = view.getDrawingCache();
	Rect rect = new Rect(); 
	activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect); 
	int statusBarHeight = rect.top;
	int width = activity.getWindowManager().getDefaultDisplay().getWidth(); 
	int height = activity.getWindowManager().getDefaultDisplay().getHeight(); 
	Bitmap bitmap2 = Bitmap.createBitmap(bitmap, 0, statusBarHeight, width, height - statusBarHeight);
	view.destroyDrawingCache(); return bitmap2;
	}

private void SaveImage(Bitmap finalBitmap) {

String root = Environment.getExternalStorageDirectory().toString(); 
File myDir = new File(root + "/saved_images");
myDir.mkdirs(); 
Random generator = new Random();
int n = 10000; n = generator.nextInt(n);
String fname = "Image-"+ n +".jpg"; File file = new File (myDir, fname); 
if (file.exists ()) file.delete (); try { FileOutputStream out = new FileOutputStream(file);
finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out); 
out.flush();
out.close(); 
sendBroadcast(new Intent( Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));

} catch (Exception e) { e.printStackTrace(); } }



 
} 