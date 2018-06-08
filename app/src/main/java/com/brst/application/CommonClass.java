package com.brst.application;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <h>CommonClass</h>
 * <p>
 *     In this class we used to write the common class. Which is being used in one than one place
 *     like isNetworkAvailable(). is being called many places where ever there is a network call.
 * </p>
 * @since 3/31/2017
 */
public class CommonClass
{

    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager = (ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * <h>GetDeviceWidth</h>
     * <p>
     *     In this method we used to find the width of the current android device.
     * </p>
     * @param activity the current context
     * @return it returns the width of the device in pixel
     */
    public static int getDeviceWidth(Activity activity)
    {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        //int height = metrics.heightPixels;
        //int width = metrics.widthPixels;
        return metrics.widthPixels;
    }

    /**
     * <h>GetDeviceHeight</h>
     * <p>
     *     In this method we used to find the height of the current android device.
     * </p>
     * @param activity the current context
     * @return it returns the height of the device in pixel
     */
    public static int getDeviceHeight(Activity activity)
    {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        //int height = metrics.heightPixels;
        //int width = metrics.widthPixels;
        return metrics.heightPixels;
    }

    /**
     * <p>
     * This method is used to validate email Id
     * </p>
     *
     * @param email the email-address which is to be checked
     * @return boolean value true is the id format will be correct
     */
    public static boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * <h>GenerateHashKey</h>
     * <p>
     *     In this method we used to generate the debug facebook has-key.
     * </p>
     * @param context the current context
     * @return it returns the haskey of the app.
     */
    public static String generateHashKey(Context context) {
        String hashKey = "";
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    "com.yelo.com",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                hashKey = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.d("KeyHash:", hashKey);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hashKey;
    }

    /**
     * <h>GetTimeDifference</h>
     * <p>
     * In this method we calculate the difference of two time.
     * </p>
     *
     * @param date it accept the given time from where it is being called.
     * @return String
     */
    public static String getTimeDifference(String date) {
        int days = 0;
        int hours = 0;
        int min = 0;
        int sec = 0;
        Date date1, date2;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("put the date format here");
        try {
            //date1 = simpleDateFormat.parse(givenTime);
            date2 = simpleDateFormat.parse(getCurrentDateTime());

            long difference = date2.getTime() - Long.parseLong(date);

            System.out.println("time difference="+(int)difference);

            days = (int) (difference / (1000 * 60 * 60 * 24));
            hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
            min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
            sec = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours) - (1000 * 60 * min)) / (1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("get time diff="+"days="+days+" "+" "+"hours="+hours+" "+"min="+min+" "+"sec="+sec);
        if (days != 0) {
            String day;
            Log.d("days..", days + "");
            if (days > 7 && days < 29) {
                day = days / 7 + "w";
                return day;
            } else if (days > 29 && days < 365) {
                day = days / 29 + "m";
                return day;
            } else if (days > 364) {
                day = days / 364 +  "y";
                return day;
            } else {
                return days + "d";
            }
        }
        if (hours != 0) {
            return hours + "h";
        }
        if (min != 0) {
            return min +"min";
        }
        if (sec != 0) {
            return sec+"sec";
        }
        else return "just now";
    }

    /**
     * <h>GetMonth</h>
     * <p>
     * In this method we used to extract the given date format(yyyy-MM-dd HH:mm:ss)
     * into required day,month and year.
     * </p>
     *
     * @param date given date
     * @return String
     */
    public static String getDate(String date) {
        int month = 0;
        int dayOfMonth = 0;
        int year = 0;
        String all_date;

        try {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(Long.parseLong(date));
            Date temp =c.getTime();
            SimpleDateFormat format = new SimpleDateFormat("put the date format here");
            String time = format.format(temp);//this variable time contains the time in the format of "day/month/year".
            Date d = format.parse(time);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            month = cal.get(Calendar.MONTH);
            dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
            year = cal.get(Calendar.YEAR);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        all_date =  dayOfMonth + " " + getMonthName(month) + " " + year;
        return all_date;
    }


    /**
     * <h>getMonthName</h>
     * <p>
     * In this method we used to convert given month in integer format(eg. 1,2)
     * into String format eg. (Jan, Feb)
     * </p>
     *
     * @param month given month
     * @return String
     */
    private static String getMonthName(int month) {
        switch (month + 1) {
            case 1:
                return "Jan";

            case 2:
                return "Feb";

            case 3:
                return "Mar";

            case 4:
                return "Apr";

            case 5:
                return "May";

            case 6:
                return "Jun";

            case 7:
                return "Jul";

            case 8:
                return "Aug";

            case 9:
                return "Sep";

            case 10:
                return "Oct";

            case 11:
                return "Nov";

            case 12:
                return "Dec";
        }
        return "";
    }

    /**
     * <h>GetDayName</h>
     * <p>
     * In this method we used to convert day in integer format(1,2) into
     * String format(Sunday,Monday etc)
     * </p>
     *
     * @param day in integer
     * @return String
     */
    private static String getDayName(int day) {
        switch (day) {
            case 1:
                return "Sun";
            case 2:
                return "Mon";
            case 3:
                return "Tue";
            case 4:
                return "Wed";
            case 5:
                return "Thr";
            case 6:
                return "Fri";
            case 7:
                return "Sat";
        }
        return "";
    }

    /**
     *<h>GetCurrentTime</h>
     * <p>
     *     In This method we get the current date and time using
     *     SimpleDateFormat. which accept our own required date
     *     format. The Date() This constructor initializes the
     *     object with the current date and time. format() method
     *     Formats the specified date using the rules of this date
     *     format.
     * </p>
     * @return String
     */
    private static String getCurrentDateTime()
    {
        DateFormat dateFormat = new SimpleDateFormat("put the date format here");
        Date date = new Date();
        return dateFormat.format(date); //2014/08/06 15:59:48
    }

    /**
     * <h>statusBarColor</h>
     * <p>
     *     This method is used to change device default status default color to
     *     our required status bar color. Changing the color of status bar also
     *     requires setting two additional flags on the Window; we need to add
     *     the FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag and clear the
     *     FLAG_TRANSLUCENT_STATUS flag.
     * </p>
     * @param activity the current context
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void statusBarColor(Activity activity) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            Window window = activity.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
            window.setStatusBarColor(ContextCompat.getColor(activity,R.color.colorPrimary));
        }
    }

    /**
     * <h>sShowSnackbarMessage</h>
     * <p>
     *     In this method we used to show the snackbar which comes at a bottom of
     *     the screen. It usually shows when any error comes like if there is not
     *     internet connection or any error from the server.
     * </p>
     * @param message The content to show
     */
    public static void showSnackbarMessage(View rootViw, String message)
    {
        if (rootViw!=null && message!=null) {
            Snackbar snackbar = Snackbar
                    .make(rootViw, message, Snackbar.LENGTH_LONG);
            snackbar.setActionTextColor(Color.WHITE);
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(Color.parseColor("#ff3a3a"));
            TextView textView = (TextView) snackbarView.findViewById(R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
        }
    }

    /**
     * <h>sShowSnackbarMessage</h>
     * <p>
     *     In this method we used to show the snackbar which comes at a bottom of
     *     the screen. It usually shows when any error comes like if there is not
     *     internet connection or any error from the server.
     * </p>
     * @param message The content to show
     */
    public static void showSuccessSnackbarMsg(View rootViw, String message)
    {
        if (rootViw!=null && message!=null) {
            Snackbar snackbar = Snackbar
                    .make(rootViw, message, Snackbar.LENGTH_LONG);
            snackbar.setActionTextColor(Color.WHITE);
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(Color.parseColor("#2ecc71"));
            TextView textView = (TextView) snackbarView.findViewById(R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
        }
    }

    /**
     * <h>showShortSuccessMsg</h>
     * <p>
     *     In this method we used to show snack bar from top for short time of period.
     * </p>
     * @param rootViw The activity or frag root view
     * @param message The content to show
     */
    public static void showShortSuccessMsg(View rootViw, String message)
    {
        if (rootViw!=null && message!=null) {
            Snackbar snackbar = Snackbar
                    .make(rootViw, message, Snackbar.LENGTH_SHORT);
            snackbar.setActionTextColor(Color.WHITE);
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(Color.parseColor("#2ecc71"));
            TextView textView = (TextView) snackbarView.findViewById(R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
        }
    }

    /**
     * <h>SetMargins</h>
     * <p>
     *     In this method we used to put margin to the given view from all sides.
     * </p>
     * @param view The given view
     * @param left margin from left
     * @param top margin from top
     * @param right margin from right
     * @param bottom margin from bottom
     */
    public static void setMargins (View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    /**
     * code get just two digit after the point in decimal value
     */
    public static String twoDigitAfterDewcimal(String value) {
        String getValue="";

        if (value!=null && !value.isEmpty())
        {
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            getValue=decimalFormat.format(Double.parseDouble(value));
        }
        return getValue;
    }

    public static void hideStatusBar(Activity mActivity)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            View decorView = mActivity.getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        } else
            mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * <h>SetViewOpacity</h>
     * <p>
     *     In this method we used to set the transparency to the given view
     *     according to the given alpha value.
     * </p>
     * @param mActivity The current context
     * @param view The given view
     * @param alphaValue The interger value ranges from 0 to 255.
     */
    public static void setViewOpacity(Activity mActivity, View view, int alphaValue, int id)
    {
        Drawable d = ContextCompat.getDrawable(mActivity,id);
        d.setAlpha(alphaValue);
        view.setBackground(d);
    }

    /**
     * <h>GetCurrencyLocaleMap</h>
     * <p>
     *     In this method we used to find the country iso code and currency.
     *     Store that two in Map as key-value pair.
     * </p>
     * @return The map with currency and code pair
     */
    public static Map<Currency, Locale> getCurrencyLocaleMap() {
        Map<Currency, Locale> map = new HashMap<>();
        for (Locale locale : Locale.getAvailableLocales()) {
            try {
                Currency currency = Currency.getInstance(locale);
                map.put(currency, locale);
            }
            catch (Exception e){
                // skip strange locale
            }
        }
        return map;
    }

    /**
     * <h>GetCompleteAddressString</h>
     * <p>
     *     In this method we used to retrieve complete address from
     *     given latitude and longitude.
     * </p>
     * @param activity The current context
     * @param latitude The given latitude
     * @param longitude The given longitude
     * @return it returns string with complete address
     */
    public static String getCompleteAddressString(Activity activity, double latitude, double longitude)
    {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                if(returnedAddress.getMaxAddressLineIndex()>0){
                    for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                        strReturnedAddress.append(", ").append(returnedAddress.getAddressLine(i));
                        System.out.println("My Current loction address inside for loop"+ "" +returnedAddress.getAddressLine(i));
                    }
                }else{
                    strReturnedAddress.append(", ").append(returnedAddress.getAddressLine(0));
                    System.out.println("My Current loction address at 0"+ "" +returnedAddress.getAddressLine(0));
                }

                strAdd = strReturnedAddress.toString();
                if (strAdd.startsWith(","))
                    strAdd=strAdd.replaceFirst(",","");

                System.out.println("My Current loction address"+ " " + strReturnedAddress.toString());
            } else {
                System.out.println("My Current loction address"+ "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("My Current loction address"+ "Canont get Address!");
        }
        return strAdd.trim();
    }

    // Can be triggered by a view event such as a button press
    public static void onShareItem(Activity activity, ImageView iV_logo, String title) {
        // Get access to bitmap image from view
        // Get access to the URI for the bitmap
        Uri bmpUri = getLocalBitmapUri(activity,iV_logo);
        if (bmpUri != null) {
            // Construct a ShareIntent with link to image
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, title);
            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
            shareIntent.setType("image/*");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            activity.startActivity(Intent.createChooser(shareIntent, "Share on..."));
        }
    }

    public static void shareItem(Activity activity, String productName, String postId)
    {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey check out my app at: https://yelo-app.xyz/item/1502203409899");
        sendIntent.setType("text/plain");
        activity.startActivity(sendIntent);
    }

    // Returns the URI path to the Bitmap displayed in specified ImageView
    private static Uri getLocalBitmapUri(Activity activity, ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable){
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            // Use methods on Context to access package-specific directories on external storage.
            // This way, you don't need to request external read/write permission.
            // See https://youtu.be/5xVh-7ywKpE?t=25m25s
            File file =  new File(activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            // **Warning:** This will fail for API >= 24, use a FileProvider as shown below instead.
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    /**
     * <h>GetGmtTimeDiff</h>
     * <p>
     * In this method we calculate the difference of two time.
     * </p>
     *
     * @param date it accept the given time in gmt
     * @return String
     */
    public static String getGmtTimeDiff(String date)
    {
        int days = 0;
        int hours = 0;
        int min = 0;
        int sec = 0;

        Date date1, date2;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("put the date format here");
        try {
            date1 = simpleDateFormat.parse(date);
            date2 = getGMTDate("put the date format here");

            long difference = date2.getTime() - date1.getTime();

            days = (int) (difference / (1000 * 60 * 60 * 24));
            hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
            min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
            sec = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours) - (1000 * 60 * min)) / (1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (days != 0) {
            String day;
            Log.d("days..", days + "");
            if (days > 7 && days < 29) {
                day = days / 7 + " " + "Weeks";
                return day;
            } else if (days > 29 && days < 365) {
                day = days / 29 + " " + "Months";
                return day;
            } else if (days > 364) {
                day = days / 364 + " " + "Years";
                return day;
            } else {
                return days + " " + "Days";
            }
        }
        if (hours != 0) {
            return hours + " " + "Hours";
        }
        if (min != 0) {
            return min + " " + "Minutes";
        }
        if (sec != 0) {
            return sec + " " + "Seconds";
        }
        return "";
    }

    /**
     * <h>getGMTDate</h>
     * <p>
     *     In this method we used to generate time in GMT.
     * </p>
     * @param dateFormat The date format i.e yyyy-MM-dd HH:mm:ss
     * @return the current date
     */
    private static Date getGMTDate(String dateFormat) throws ParseException
    {
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat(dateFormat);
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));

        // Local time zone
        SimpleDateFormat dateFormatLocal = new SimpleDateFormat(dateFormat);

        // Time in GMT
        return dateFormatLocal.parse(dateFormatGmt.format(new Date()));
    }

    /**
     * <h>showTopSnackBar</h>
     * <p>
     *     In this method we used to show the custom snackbar from Top of the screen.
     * </p>
     * @param view The root element of the given layout
     * @param content The Message to show
     */
    public static void showTopSnackBar(View view, String content)
    {
        if (view!=null && content!=null) {
            Snackbar snackbar = Snackbar
                    .make(view, content, Snackbar.LENGTH_LONG);
            snackbar.setActionTextColor(Color.WHITE);
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(Color.parseColor("#ff3a3a"));
            TextView textView = (TextView) snackbarView.findViewById(R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
        }
    }

    /**
     * <h>GetCityName</h>
     * <p>
     *     In this method we used to retrive the city name from the given lat and lng.
     * </p>
     * @param mActivity The current context
     * @param latitude The given latitude
     * @param longitude The given longitude
     * @return The city name
     */
    public static String getCityName(Activity mActivity, double latitude, double longitude)
    {
        String cityName="";
        try {
            Geocoder geo = new Geocoder(mActivity.getApplicationContext(), Locale.getDefault());
            List< Address > addresses = geo.getFromLocation(latitude, longitude, 1);

            if (addresses.size() > 0) {
                cityName=addresses.get(0).getLocality();
                System.out.println("locations="+addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());
                if (!cityName.isEmpty())
                    cityName=cityName.toLowerCase();
            }

        } catch (Exception e) {
            e.printStackTrace(); // getFromLocation() may sometimes fail
        }
        return cityName;
    }

    /**
     * <h>GetCountryName</h>
     * <p>
     *     In this method we used to retrive the Country name from the given lat and lng.
     * </p>
     * @param mActivity The current context
     * @param latitude The given latitude
     * @param longitude The given longitude
     * @return The country name
     */
    public static String getCountryName(Activity mActivity, double latitude, double longitude)
    {
        String countryName="";
        try {
            Geocoder geo = new Geocoder(mActivity.getApplicationContext(), Locale.getDefault());
            List< Address > addresses = geo.getFromLocation(latitude, longitude, 1);

            if (addresses.size() > 0) {
                countryName=addresses.get(0).getCountryName();
                System.out.println("locations="+addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());
                //Toast.makeText(getApplicationContext(), "Address:- " + addresses.get(0).getFeatureName() + addresses.get(0).getAdminArea() + addresses.get(0).getLocality(), Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            e.printStackTrace(); // getFromLocation() may sometimes fail
        }
        return countryName;
    }

    /**
     * <h>GetCountryName</h>
     * <p>
     *     In this method we used to retrive the Country iso code from the given lat and lng.
     * </p>
     * @param mActivity The current context
     * @param latitude The given latitude
     * @param longitude The given longitude
     * @return The country name
     */
    public static String getCountryCode(Activity mActivity, double latitude, double longitude)
    {
        String countryName="";
        try {
            Geocoder geo = new Geocoder(mActivity.getApplicationContext(), Locale.getDefault());
            List< Address > addresses = geo.getFromLocation(latitude, longitude, 1);

            if (addresses.size() > 0) {
                countryName=addresses.get(0).getCountryCode();
                System.out.println("locations="+addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());

                if (!countryName.isEmpty())
                    countryName = countryName.toLowerCase();
            }

        } catch (Exception e) {
            e.printStackTrace(); // getFromLocation() may sometimes fail
        }
        return countryName;
    }

    public static void playBackgroungVideo(Activity mActivity, VideoView mVideoView)
    {
        mActivity.getWindow().setFormat(PixelFormat.UNKNOWN);
        //Displays a video file.

        //String uriPath="android.resource://"+mActivity.getPackageName()+"/"+R.raw.landing_screen;
        String uriPath="android.resource://"+mActivity.getPackageName()+"/"+"";
        Uri uri = Uri.parse(uriPath);
        mVideoView.setVideoURI(uri);
        mVideoView.requestFocus();
        mVideoView.start();

        mVideoView.setOnPreparedListener (new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
    }

    /**
     * <h>ExtractNumberFromString</h>
     * <p>
     *     In this method we used to extract the numeric value from given string
     * </p>
     * @param string The given string value
     * @return The numeric value.
     */
    public static String extractNumberFromString(String string)
    {
        StringBuilder stringBuilder=new StringBuilder(100);
        if (string!=null && !string.isEmpty())
        {
            for (char ch : string.toCharArray())
            {
                if (ch>='0' && ch<='9')
                {
                    stringBuilder.append(ch);
                }
            }
        }
        return stringBuilder.toString();
    }

    /**
     * simple function for sending mail
     */
    public static void sendEmail(Context context, String emailId, String userName, String emailSubject) {
        Log.i("Send email", "");
        String[] TO = {emailId};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, emailSubject + " " + userName);
        emailIntent.putExtra(Intent.EXTRA_TEXT, "");

        try {
            context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex)
        {
            ex.printStackTrace();
        }
    }

    public static SortedMap<Currency, Locale> currencyLocaleMap;
    static {
        currencyLocaleMap = new TreeMap<>(new Comparator<Currency>()
        {
            public int compare(Currency c1, Currency c2) {
                return c1.getCurrencyCode().compareTo(c2.getCurrencyCode());
            }
        });
        for (Locale locale : Locale.getAvailableLocales()) {
            try {
                Currency currency = Currency.getInstance(locale);
                currencyLocaleMap.put(currency, locale);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public static String getCurrencySymbol(String currencyCode)
    {
        Currency currency = Currency.getInstance(currencyCode);
        return currency.getSymbol(currencyLocaleMap.get(currency));
    }
}
