package com.example.surfa.smanknorrcurrent;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.Display;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.zip.Inflater;

public class MapsActivity extends FragmentActivity {
    private int mActivePointerId;
    private boolean ignoreDrag;
    private boolean ignoreClick;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Button randomiseButton;
    private Button scheduleButton;
    private Boolean isExpanded = false;
    private Boolean isLoaded = false;
    private RelativeLayout detailLayout;
    private RelativeLayout mapLayout;
    private TextView countryName;
    private ImageView countryFlag;
    private RelativeLayout detailLayoutHeader;
    private LinearLayout detailLayoutContent;
    private LinearLayout AgendaContainer;
    private Country currentCountry;
    private ArrayList<Bitmap> images;
    private NotificationCompat.Builder mBuilder;
    private ImageView popupImageView;
    private final static int READ_REQUEST_CODE = 42;

    private AgendaItemsDataSource agendaDatasource;

    private List<AgendaItem> agendaItems;
    private GridView photoGallery;
    private GridViewAdapter photoGalleryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        ignoreDrag = false;
        ignoreClick = false;
        mActivePointerId = 0;
        agendaItems = new ArrayList<>();
        detailLayout = (RelativeLayout) findViewById(R.id.detailLayout);
        detailLayoutHeader = (RelativeLayout) findViewById(R.id.detailLayoutHeader);
        detailLayoutContent = (LinearLayout) findViewById(R.id.detailContentView);
        mapLayout = (RelativeLayout) findViewById(R.id.mapLayout);
        countryName = (TextView) findViewById(R.id.countryName);
        countryFlag = (ImageView) findViewById(R.id.countryFlag);
        photoGallery = (GridView) findViewById(R.id.photoGallery);
        images = new ArrayList<>();
        mBuilder = new NotificationCompat.Builder(this);

        AgendaContainer = (LinearLayout) findViewById(R.id.AgendaContainer);

        randomiseButton = (Button) findViewById(R.id.randomiseButton);
        scheduleButton = (Button) findViewById(R.id.scheduleButton);

        agendaDatasource = new AgendaItemsDataSource(this);

        try {
            agendaDatasource.open();
            agendaItems = agendaDatasource.getAllAgendaItems();

            for(AgendaItem a : agendaItems)
            {
                mMap.addMarker(new MarkerOptions().position(a.getLatLng()).title(a.getCountryName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.foodmarker)));
            }
        }
        catch (Exception ex)
        {
            Log.e("Database",ex.toString());
        }


        randomiseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String[] countriesArray = v.getResources().getStringArray(R.array.countries);

                String randomStr = countriesArray[new Random().nextInt(countriesArray.length)];

                String[] splitStr = randomStr.split(",");

                currentCountry = new Country(splitStr[0], splitStr[1]);

                new DownloadCountryData().execute();
            }
        });

        scheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {


                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                final View inflatedView = layoutInflater.inflate(R.layout.schedule_popup_layout, null, false);


                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();

                display.getSize(size);


                final PopupWindow popWindow = new PopupWindow(inflatedView, detailLayoutContent.getWidth(), detailLayoutContent.getHeight(), true);

                Button dismissButton = (Button) inflatedView.findViewById(R.id.dismissButton);
                Button scheduleConfirmButton = (Button) inflatedView.findViewById(R.id.scheduleConfirmButton);

                scheduleConfirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent resultIntent = new Intent(MapsActivity.this, MapsActivity.class);

                        resultIntent.putExtra("CountryName", currentCountry.getCountryName());
                        resultIntent.putExtra("CountryIso", currentCountry.getCountryISO());

                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(MapsActivity.this);
                        // Adds the back stack for the Intent (but not the Intent itself)
                        stackBuilder.addParentStack(MapsActivity.class);
                        // Adds the Intent that starts the Activity to the top of the stack
                        stackBuilder.addNextIntent(resultIntent);
                        PendingIntent resultPendingIntent =
                                stackBuilder.getPendingIntent(
                                        0,
                                        PendingIntent.FLAG_UPDATE_CURRENT
                                );
                        mBuilder.setContentIntent(resultPendingIntent);
                        mBuilder.setContentTitle("Wat eten we vandaag?");
                        mBuilder.setContentText(currentCountry.getCountryName());
                        mBuilder.setSmallIcon(R.drawable.notificationicon);
                        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                        mBuilder.setLargeIcon(currentCountry.getCountryFlag());

                        Notification mNotification = mBuilder.build();

                        DatePicker mDatePicker = (DatePicker) inflatedView.findViewById(R.id.datePicker);

                        Calendar calendar = Calendar.getInstance();

                        calendar.set(mDatePicker.getYear(), mDatePicker.getMonth(), mDatePicker.getDayOfMonth(), 12, 00);

                        scheduleNotification(mNotification, calendar.getTimeInMillis(), new Date(mDatePicker.getYear(), mDatePicker.getMonth(), mDatePicker.getDayOfMonth()));

                        popWindow.dismiss();

                        refreshCurrentAgenda();

                        mMap.addMarker(new MarkerOptions().position(currentCountry.getCountryLatLng()).title(currentCountry.getCountryName()));

                    }
                });

                dismissButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popWindow.dismiss();
                    }
                });


                popWindow.setFocusable(true);
                popWindow.setOutsideTouchable(true);

                popWindow.setAnimationStyle(R.style.AnimationPopup);
                popWindow.setBackgroundDrawable(new ColorDrawable());

                final ObjectAnimator animatorEnd = ObjectAnimator.ofFloat(detailLayoutContent, "translationY", 0);
                animatorEnd.setDuration(250);

                popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        animatorEnd.start();
                    }
                });

                ObjectAnimator animatorStart = ObjectAnimator.ofFloat(detailLayoutContent, "translationY", detailLayoutContent.getHeight());

                animatorStart.setDuration(250);

                animatorStart.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        popWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });


                animatorStart.start();


            }
        });




        detailLayoutHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isExpanded) {
                    new ResizeViews();
                } else if (!isExpanded && isLoaded) {
                    new ResizeViews();
                }
            }
        });

        detailLayoutHeader.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final int action = MotionEventCompat.getActionMasked(event);


                switch (action) {
                    case MotionEvent.ACTION_DOWN: {
                        ignoreDrag = false;
                        ignoreClick = false;
                        mActivePointerId = MotionEventCompat.getPointerId(event, 0);
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {

                        ignoreClick = true;

                        final int pointerIndex = MotionEventCompat.findPointerIndex(event, mActivePointerId);

                        final float y = MotionEventCompat.getY(event, pointerIndex);

                        if(y > (detailLayoutHeader.getHeight() * 2) && isLoaded && isExpanded && !ignoreDrag)
                        {
                            new ResizeViews();

                            ignoreDrag = true;

                            Log.d("Drag","down");

                            return false;
                        }

                        else if(y < ( - detailLayoutHeader.getHeight() * 2) && !isExpanded && isLoaded && !ignoreDrag)
                        {
                            new ResizeViews();

                            ignoreDrag = true;

                            Log.d("Drag","up");

                            return false;

                        }

                        Log.e("Gesture", String.valueOf(y));
                        break;
                    }
                    case MotionEvent.ACTION_UP :
                    {
                        if(!ignoreClick) {
                            detailLayoutHeader.performClick();
                        }
                    }
                }

                return true;
            }
        });

        Intent intent = getIntent();

        if(intent.getStringExtra("CountryName") != null && intent.getStringExtra("CountryIso") != null)
        {
            currentCountry = new Country(intent.getStringExtra("CountryName"),intent.getStringExtra("CountryIso"));
            new DownloadCountryData().execute();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void scheduleNotification(Notification notification, long time, Date date) {

        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Log.e("Notif", String.valueOf(time - System.currentTimeMillis()));
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);

        agendaDatasource.createAgendaItem(currentCountry, date);
        //agendaItems = agendaDatasource.getAllAgendaItems();

        agendaItems = agendaDatasource.getAgendaItemsForCountry(currentCountry);
        Log.e("Databasetest", String.valueOf(agendaItems.size()));
    }



    public String makeCountryLookupURL (String countryName){

        countryName = countryName.replace(" ","%20");
        String url = "http://maps.googleapis.com/maps/api/geocode/json?address=" + countryName;

        return url;
    }

    public String makeImageLookupURL (String query)
    {
        query = query.replace(" ","%20");
        String url = "https://www.googleapis.com/customsearch/v1?key=AIzaSyD4W_zcMvhGRrlnhcQOYXseZJvUrECK_Mo&cx=008168024561828621954:5teot76oyuw&q=" + query + "&searchType=image&num=9";



        return url;
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (isExpanded) {
                    new ResizeViews();
                }


            }
        });

        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));

        //mMap.setMyLocationEnabled(true);
    }

    private class getCountryMapBounds extends AsyncTask<String, Void, ArrayList<LatLng>> {

        @Override
        protected void onPreExecute()
        {

        }

        @Override
        protected ArrayList<LatLng> doInBackground(String... params) {
            ArrayList<LatLng> latlist = new ArrayList<>();

            try {
                StringBuilder response = new StringBuilder();
                JSONObject jsonObject;
                URL url = new URL(makeCountryLookupURL(params[0]));

                Log.e("url", url.toString());

                HttpURLConnection httpconn = (HttpURLConnection)url.openConnection();

                if (httpconn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader input = new BufferedReader(new InputStreamReader(httpconn.getInputStream()));
                    String strLine = null;
                    while ((strLine = input.readLine()) != null)
                    {
                        response.append(strLine);
                    }
                    input.close();
                }

                if(response != null)
                {
                    Log.e("Nils",url.toString());
                    jsonObject = new JSONObject(response.toString());

                    JSONArray resultsArray = jsonObject.getJSONArray("results");

                    if(resultsArray != null)
                    {
                        currentCountry.setCountryLatLng(new LatLng(resultsArray.getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat"),resultsArray.getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng")));
                        latlist.add(new LatLng(Double.parseDouble(resultsArray.getJSONObject(0).getJSONObject("geometry").getJSONObject("viewport").getJSONObject("southwest").getString("lat")), Double.parseDouble(resultsArray.getJSONObject(0).getJSONObject("geometry").getJSONObject("viewport").getJSONObject("southwest").getString("lng"))));
                        latlist.add(new LatLng(Double.parseDouble(resultsArray.getJSONObject(0).getJSONObject("geometry").getJSONObject("viewport").getJSONObject("northeast").getString("lat")),Double.parseDouble(resultsArray.getJSONObject(0).getJSONObject("geometry").getJSONObject("viewport").getJSONObject("northeast").getString("lng"))));
                    }

                    else
                    {
                        Log.e("Error","Results array is null");
                    }
                }

                else
                {
                    Log.e("Error","Response is null");
                }

            }
            catch (Exception ex)
            {

                Log.e("Error ", ex.toString());
            }

            return latlist;
        }

        @Override
        protected void onPostExecute(ArrayList<LatLng> latlist)
            {
                if(latlist.size() == 2)
                {
                    LatLngBounds llb = new LatLngBounds(latlist.get(0),latlist.get(1));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(llb, 20), 4000, new GoogleMap.CancelableCallback() {
                        @Override
                        public void onFinish() {
                            /*if(!isExpanded) {
                                new ResizeViews();
                            }*/
                        }

                        @Override
                        public void onCancel() {
                            Log.e("Camerzoom","cancelled");
                        }
                    });
                } else {
                    Log.e("Succes ", "nee");
                }
            }
    }

    private class ResizeViews
    {
        final Animation detailAnimation;
        final Animation mapAnimation;

        public ResizeViews()
        {
            if(!isExpanded)
            {
                //Set button animation
                randomiseButton.setVisibility(View.INVISIBLE);
                randomiseButton.setEnabled(false);
                randomiseButton.setClickable(false);

                scheduleButton.setEnabled(true);
                scheduleButton.setClickable(true);

                ObjectAnimator objaDC = ObjectAnimator.ofFloat((LinearLayout) findViewById(R.id.detailContentView), "alpha", 100);
                objaDC.setDuration(2500);
                objaDC.start();

                detailLayoutContent.setVisibility(View.VISIBLE);





                //Set detail and mapview animation
                detailAnimation  = new ExpandAnimation(2, 15, detailLayout);
                mapAnimation = new ExpandAnimation(18, 5, mapLayout);
                isExpanded = true;
                isLoaded = true;
            }

            else
            {
                randomiseButton.setVisibility(View.VISIBLE);
                randomiseButton.setEnabled(true);
                randomiseButton.setClickable(true);

                scheduleButton.setEnabled(false);
                scheduleButton.setClickable(false);

                detailLayoutContent.setVisibility(View.INVISIBLE);

                //Set detail and mapview animation
                detailAnimation  = new ExpandAnimation(15, 2, detailLayout);
                mapAnimation = new ExpandAnimation(5, 18, mapLayout);
                isExpanded = false;
            }

            detailAnimation.setDuration(500);
            mapAnimation.setDuration(500);

            detailLayout.startAnimation(detailAnimation);
            mapLayout.startAnimation(mapAnimation);
        }
    }

    private class ExpandAnimation extends Animation {

        private final float mStartWeight;
        private final float mDeltaWeight;
        private final RelativeLayout mContent;

        public ExpandAnimation(float startWeight, float endWeight, RelativeLayout mcontent) {
            mStartWeight = startWeight;
            mDeltaWeight = endWeight - startWeight;
            mContent = mcontent;

        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContent.getLayoutParams();
            lp.weight = (mStartWeight + (mDeltaWeight * interpolatedTime));
            mContent.setLayoutParams(lp);
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }

    private class DownloadCountryData extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            //Country selectedCountry = country[0];

            String imageURL = "http://www.geonames.org/flags/x/" + currentCountry.getCountryISO() + ".gif";

            try {
                // Download Image from URL
                InputStream input = new java.net.URL(imageURL).openStream();
                // Decode Bitmap
                currentCountry.setCountryFlag(BitmapFactory.decodeStream(input));
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Download", e.toString());
                return false;
            }

            finally {
                try
                {
                    StringBuilder response = new StringBuilder();
                    JSONArray jsonArray;
                    URL url = new URL("https://restcountries.eu/rest/v1/alpha?codes=" + currentCountry.getCountryISO());

                    HttpURLConnection httpconn = (HttpURLConnection)url.openConnection();

                    if (httpconn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader input = new BufferedReader(new InputStreamReader(httpconn.getInputStream()));
                        String strLine = null;
                        while ((strLine = input.readLine()) != null)
                        {
                            response.append(strLine);
                        }
                        input.close();
                    }

                    if(response != null)
                    {
                        jsonArray = new JSONArray(response.toString());

                        if(jsonArray != null)
                        {
                            currentCountry.setCountryCapital(jsonArray.getJSONObject(0).getString("capital"));
                            currentCountry.setCountryPopulation(jsonArray.getJSONObject(0).getDouble("population"));
                            currentCountry.setCountryContintent(jsonArray.getJSONObject(0).getString("region"));

                            try {
                                response = new StringBuilder();



                                url = new URL(makeImageLookupURL(currentCountry.getCountryName() + " cuisine food dish"));
                                Log.e("Imageserror",makeImageLookupURL(currentCountry.getCountryName() + " cuisine food dish"));

                                HttpURLConnection imagescon = (HttpURLConnection)url.openConnection();


                                if (imagescon.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                    BufferedReader input = new BufferedReader(new InputStreamReader(imagescon.getInputStream()));
                                    String strLine = null;
                                    while ((strLine = input.readLine()) != null)
                                    {
                                        response.append(strLine);
                                    }
                                    input.close();
                                }

                                else if(imagescon.getResponseCode() == HttpURLConnection.HTTP_FORBIDDEN)
                                {
                                    return true;
                                }
                                if(response != null) {
                                    Log.e("json", "threshold");
                                    JSONObject jobject = new JSONObject(response.toString());

                                    if (jobject != null) {

                                       JSONArray jarray = jobject.getJSONArray("items");

                                        images.clear();

                                        for(int n = 0; n < jarray.length(); n++)
                                        {
                                            try {
                                                InputStream input = new java.net.URL(jarray.getJSONObject(n).getJSONObject("image").getString("thumbnailLink")).openStream();

                                                images.add(BitmapFactory.decodeStream(input));
                                            }
                                            catch (Exception ex)
                                            {
                                                Log.e("json", ex.toString());
                                                return false;
                                            }
                                        }

                                        return true;
                                    }


                                    else
                                    {
                                        Log.e("json", "Array is null");
                                        return false;
                                    }
                                }
                                else
                                {
                                    Log.e("Imageserror", "Geen response");
                                    return false;

                                }
                            }

                            catch (Exception ex)
                            {
                                Log.e("Imageserror", ex.toString());
                                return false;
                            }
                        }

                        else
                        {
                            Log.e("Error","Results array is null");
                            return false;
                        }
                    }

                    else
                    {
                        Log.e("Error", "Response is null");
                        return false;
                    }
                }
                catch(Exception ex)
                {
                    Log.e("Error", ex.toString());
                    return false;
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if(success) {
                countryFlag.setImageBitmap(currentCountry.getCountryFlag());
                countryName.setText(currentCountry.getCountryName());

                TextView capitalText = (TextView) findViewById(R.id.capitalText);
                TextView populationText = (TextView) findViewById(R.id.populationText);
                TextView contintentText = (TextView) findViewById(R.id.continentText);

                capitalText.setText(currentCountry.getCountryCapital());
                populationText.setText(currentCountry.getCountryPopulation().toString());
                contintentText.setText(currentCountry.getCountryContintent());

                ObjectAnimator objaDH = ObjectAnimator.ofFloat(detailLayoutHeader, "alpha", 100);
                objaDH.setDuration(2500);
                objaDH.start();

                photoGalleryAdapter = new GridViewAdapter(MapsActivity.this, R.layout.grid_item_layout, images);

                photoGallery.setAdapter(photoGalleryAdapter);

                getCountryMapBounds gcmb = new getCountryMapBounds();

                gcmb.execute(currentCountry.getCountryName());

                refreshCurrentAgenda();

                if (!isExpanded) {
                    new ResizeViews();
                }
            }
        }
    }

    public void refreshCurrentAgenda()
    {
        List<AgendaItem> items = agendaDatasource.getAgendaItemsForCountry(currentCountry);

        AgendaContainer.removeAllViews();

        if(items.size() > 0)
        {
            for(AgendaItem aItem : items)
            {
                AgendaItemView itemView = new AgendaItemView(MapsActivity.this, null);

                itemView.setDateText(aItem.getDate().toString());

                itemView.setButtonOnclick(new Button.OnClickListener() {
                    @Override
                    public void onClick(final View v) {

                        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                        final View inflatedView = layoutInflater.inflate(R.layout.submit_popup_layout, null, false);

                        Button choosePhotoButton = (Button)inflatedView.findViewById(R.id.choosePictureButton);

                        choosePhotoButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                performFileSearch();
                            }
                        });

                        final PopupWindow popWindow = new PopupWindow(inflatedView, detailLayoutContent.getWidth(), detailLayoutContent.getHeight(), true);

                        popupImageView = (ImageView) inflatedView.findViewById(R.id.popupImageView);



                        popWindow.setFocusable(true);
                        popWindow.setOutsideTouchable(true);

                        popWindow.setAnimationStyle(R.style.AnimationPopup);
                        popWindow.setBackgroundDrawable(new ColorDrawable());

                        final ObjectAnimator animatorEnd = ObjectAnimator.ofFloat(detailLayoutContent, "translationY", 0);
                        animatorEnd.setDuration(250);

                        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                animatorEnd.start();
                            }
                        });

                        ObjectAnimator animatorStart = ObjectAnimator.ofFloat(detailLayoutContent, "translationY", detailLayoutContent.getHeight());

                        animatorStart.setDuration(250);

                        animatorStart.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                popWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });


                        animatorStart.start();
                    }
                });

                AgendaContainer.addView(itemView,0);
            }
        }

    }

    @Override
    public void onBackPressed() {
        if(isExpanded)
        {
            new ResizeViews();
        }
    }

    public void performFileSearch() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, READ_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (resultData != null) {

                try {

                    Bundle extras = resultData.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    popupImageView.setImageBitmap(imageBitmap);
                    popupImageView.postInvalidate();


                    /*Bitmap photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                    popupImageView.setImageBitmap(photo);

                    popupImageView.post(new Runnable() {

                        @Override
                        public void run() {
                            LinearLayout.LayoutParams mParams;
                            mParams = (LinearLayout.LayoutParams) popupImageView.getLayoutParams();
                            mParams.height = popupImageView.getWidth();
                            popupImageView.setLayoutParams(mParams);
                            popupImageView.postInvalidate();
                        }
                    });*/
                }
                catch (Exception ex)
                {
                    Log.e("imagerror",ex.toString());
                }

                /*uri = resultData.getData();
                Log.i("ImageTag", "Uri: " + uri.toString());

                popupImageView.setImageBitmap();*/
                //showImage(uri);
            }
        }
    }

}
