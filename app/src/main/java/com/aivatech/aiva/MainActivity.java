package com.aivatech.aiva;

import android.app.*;
import android.os.*;
import android.speech.tts.*;
import android.speech.*;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.content.pm.*;
import java.io.File;
import java.io.FileWriter;
import android.location.*;
import java.util.*;
import java.time.*;
import android.media.AudioManager;
import android.app.NotificationManager;
import java.lang.Integer;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.ACCESS_NOTIFICATION_POLICY;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.content.*;
import java.util.logging.*;
import android.media.*;
import android.net.wifi.*;
import android.bluetooth.*;
import android.provider.*;
import java.time.format.*;


public class MainActivity extends Activity 
{

	private TextToSpeech textToSpeech;
	private TextView textView, textView2, textView1;
	private Intent intent;
	private BluetoothAdapter bAdapter;
	private DateTimeFormatter dt;
	private CameraManager mCameraManager;
	private String mCameraId;
	private NotificationManager mNotificationManager;
	private SpeechRecognizer speechRecognizer;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		requestPermissions(new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE, CAMERA, READ_EXTERNAL_STORAGE, ACCESS_FINE_LOCATION, ACCESS_NOTIFICATION_POLICY}, PackageManager.PERMISSION_GRANTED);
		textView = findViewById(R.id.TextView1);
		textView1 = findViewById(R.id.TextView3);
		textView2 = findViewById(R.id.TextView2);
		
		

		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		Set<String> a=new HashSet<>();
		a.add("female");
		boolean isFlashAvailable = getApplicationContext().getPackageManager()
			.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);

		if (!isFlashAvailable) {
			showNoFlashError();
		}
		
		
		
		mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
		try {
			mCameraId = mCameraManager.getCameraIdList()[0];
		} catch (CameraAccessException e) {
			e.printStackTrace();
		}
		
		textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
				@Override
				public void onInit(int status)
				{
				}
			});
		intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
		speechRecognizer.setRecognitionListener(new RecognitionListener() {
				@Override
				public void onReadyForSpeech(Bundle params)
				{
					
					textView1.setText("Ready on speech");
					//mAudioManager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
				}
				@Override
				public void onBeginningOfSpeech()
				{
					textView1.setText("Beginning of speech");
				}
				@Override
				public void onRmsChanged(float rmsdB)
				{

				}
				@Override
				public void onBufferReceived(byte[] buffer)
				{
				}
				@Override
				public void onEndOfSpeech()
				{
					textView1.setText("End of speech");
				}
				@Override
				public void onError(int error)
				{
				}
				@Override
				public void onResults(Bundle results)
				{
					
					ArrayList<String> matches = results.getStringArrayList(speechRecognizer.RESULTS_RECOGNITION);
					String string = "";
					textView.setText("");
					if (matches != null)
					{
						string = matches.get(0);
						textView.setText(string);
						commands(string.toLowerCase());
					}
				}
				@Override
				public void onPartialResults(Bundle partialResults)
				{
				}
				@Override
				public void onEvent(int eventType, Bundle params)
				{
				}
			});
    }
	private void commands(String cmd)
	{
		if (cmd.contains("hi") || cmd.contains("hello"))
		{
			String greet ="";
			if (cmd.contains("hello"))
			{
				greet = "Hi";
			}
			else
			{
				greet = "Hello";
			}
			speaking(greet + ", I am your personal voice assistant, how may i help you?");
		}
		else if (cmd.contains("what") && cmd.contains("name"))
		{
			speaking("My name is aiva, stands for AI voice assistant, so what is your name?");
			//textView.setText("My name is Aiva, stands for AI Voice Assistant, so what is your name?");
		}
		else if (cmd.contains("you have") || cmd.contains("you got") && cmd.contains("name"))
		{
			speaking("Yes, I do have, my name is aiva, stands for AI voice assistant. so what's yours?");
			//textView.setText("Yes, I do have. My name is Aiva, stands for AI Voice Assistant, so what's yours?");
		}
		else if (cmd.contains("how") && cmd.contains("your") && cmd.contains("day"))
		{
			speaking("I'm actually fine, how about you?");
		}
		else if (cmd.contains("open") || cmd.contains("launch"))
		{
			String[] cmmd = cmd.split("open ");
			if (cmd.contains("launch"))
			{
				cmmd = cmd.split("launch ");
			}
			cmmd[1] = cmmd[1].toLowerCase();
			//textView1.setText(cmmd[1]);
			packageList(cmmd[1].trim());
		}
		else if (cmd.contains("location") && (cmd.contains("find") || cmd.contains("my") || cmd.contains("me")))
		{
			//String location = ("https://www.google.com/maps/search/?api=1&query=");
			speaking("finding your location");
		}
		else if (cmd.contains("turn on") || cmd.contains("turn off"))
		{
			if (cmd.contains("flashlight"))
			{
				if (cmd.contains("turn off"))
				{
					speaking("Turning off flashlight");
					switchFlashLight(false);
				}
				else if (cmd.contains("turn on"))
				{
					speaking("Turning on Flashlight");
					switchFlashLight(true);
				}
			}
			else if (cmd.contains("bluetooth"))
			{
				bAdapter = BluetoothAdapter.getDefaultAdapter();
				if (bAdapter.isEnabled() && cmd.contains("turn off"))
				{
					speaking("Turning off Bluetooth");
					bAdapter.disable();
				}
				else
				{
					speaking("Turning on Bluetooth");
					bAdapter.enable();
				}
			}
		}
		else if ((cmd.contains("time") || cmd.contains("date")) && (cmd.contains("what") || cmd.contains("get") || cmd.contains("current")))
		{
			LocalDateTime current = LocalDateTime.now();
			LocalDate currentDate = LocalDate.now();
			if (cmd.contains("date") && cmd.contains("time"))
			{
				DateTimeFormatter formatterDate = dt.ofLocalizedDate(FormatStyle.MEDIUM);
				DateTimeFormatter formatterTime = dt.ofLocalizedTime(FormatStyle.SHORT);
				DayOfWeek week = currentDate.getDayOfWeek();
				String formattedDate = formatterDate.format(current);
				String formattedTime = formatterTime.format(current);
				speaking("The current date and time is " + week + ", " + formattedDate + ", " + formattedTime);
			}
			else if (cmd.contains("date"))
			{
				DayOfWeek week = currentDate.getDayOfWeek();
				DateTimeFormatter formatter = dt.ofLocalizedDate(FormatStyle.MEDIUM);
				String formatted = formatter.format(current);
				speaking("The current date is " + week + ", " + formatted);
			}
			else if (cmd.contains("time"))
			{
				DateTimeFormatter formatter = dt.ofLocalizedTime(FormatStyle.SHORT);
				String formatted = formatter.format(current);
				speaking("The current time is " + formatted);
			}
			else
			{
				speaking("Sorry, I don't understand that");
			}
		}
		else
		{
			speaking("Sorry, I don't understand that");
		}
	}
	private void speakAI()
	{
		
		
		muteAudio();
		//audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0);
		speechRecognizer.startListening(intent);
	}

	public void speaking(String phrase)
	{
		try
		 {
		 Thread.sleep(500);

		 }
		 catch (InterruptedException e)
		 {
		 e.printStackTrace();
		 }
		//original_volume_level = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		//audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, original_volume_level, 0);
		Voice voiceobj = new Voice("en-us-x-tpf-local", 
								   Locale.getDefault(), 400, 200, false, null);
		textToSpeech.setVoice(voiceobj);
		textView2.setText(phrase);
		
		textToSpeech.speak(phrase, TextToSpeech.QUEUE_FLUSH, null);
		unMuteAudio();
		try
		{
			Thread.sleep(500);

		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		//textView1.setText(status);
	}
	public void startButton(View view)
	{
		//speaking("Hi, this is Aiva, how can i help you?");
		speakAI();
	}
	public void packageList(String pkgName)
	{
		String pkgSpace = pkgName.replace(" ", "");
		final PackageManager pm = getPackageManager();
		List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
		for (ApplicationInfo packageInfo : packages)
		{

			//textView1.setText("Installed package :" + packageInfo.loadLabel(getPackageManage);
			//Log.d(TAG, "Source dir : " + packageInfo.sourceDir);
			//Log.d(TAG, "Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName)); 
			if (packageInfo.loadLabel(getPackageManager()).toString().toLowerCase().replace(" ", "").contains(pkgSpace) || packageInfo.packageName.contains(pkgName))
			{
				textView1.setText("Installed package :" + packageInfo.packageName);
				Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageInfo.packageName);
				if (launchIntent != null)
				{ 
					speaking("Sure!, opening " + pkgName);
					startActivity(launchIntent);
					return;
				}
			}
		}
	}
	private void muteAudio(){
		AudioManager amanager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
		amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
		amanager.setStreamMute(AudioManager.STREAM_ALARM, true);
		amanager.setStreamMute(AudioManager.STREAM_MUSIC, true);
		amanager.setStreamMute(AudioManager.STREAM_RING, true);
		amanager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
	}
	private void unMuteAudio(){
		AudioManager amanager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);

		amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
		amanager.setStreamMute(AudioManager.STREAM_ALARM, false);
		amanager.setStreamMute(AudioManager.STREAM_MUSIC, false);
		amanager.setStreamMute(AudioManager.STREAM_RING, false);
		amanager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
	}
	public void showNoFlashError() {
		AlertDialog alert = new AlertDialog.Builder(this)
			.create();
		alert.setTitle("Oops!");
		alert.setMessage("Flash not available in this device...");
		alert.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});
		alert.show();
	}
	public void switchFlashLight(boolean status) {
		try {
			mCameraManager.setTorchMode(mCameraId, status);
		} catch (CameraAccessException e) {
			e.printStackTrace();
		}
	}
	public void dnd(Boolean stats){
		if(!stats){
			NotificationManager interruptionFilter = 
			mNotificationManager.setInterruptionFilter(interruptionFilter);
		}
	}
	/*private class LocationListener implements android.location.LocationListener {
	 Location mLastLocation;

	 public LocationListener(String provider) {
	 textView1.setText("LocationListener " + provider);
	 mLastLocation = new Location(provider);
	 }

	 @Override
	 public void onLocationChanged(Location location) {
	 textView1.setText("onLocationChanged: " + location);
	 mLastLocation.set(location);
	 }
	 @Override
	 public void onProviderDisabled(String provider) {
	 textView1.setText("onProviderDisabled: " + provider);
	 }

	 @Override
	 public void onProviderEnabled(String provider) {
	 Log.e(TAG, "onProviderEnabled: " + provider);
	 }
	 @Override
	 public void onStatusChanged(String provider, int status, Bundle extras) {
	 textView1.setText("onStatusChanged: " + provider);
	 }
	 }*/
}

