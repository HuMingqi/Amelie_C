package com.diandian.coolco.emilie.utility;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.diandian.coolco.emilie.activity.WebActivity;

public class IntentUtil {

    public static void startWebActivity(Activity activity, String url)
    {
        Intent intent = new Intent(activity, WebActivity.class);
        intent.putExtra(ExtraDataName.WEB_ACTIVITY_URL, url);
        activity.startActivity(intent);
//        try
//        {
//            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
//            activity.startActivity(intent);
//        }
//        catch(android.content.ActivityNotFoundException e)
//        {
//            // can't start activity
//        }
    }


    public static void startMarketActivity(Activity activity)
    {
        try
        {
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("market://details?id="+activity.getPackageName()));
            activity.startActivity(intent);
        }
        catch(android.content.ActivityNotFoundException e)
        {
            // can't start activity
        }
    }


    public static void startMapCoordinatesActivity(Activity activity, double lat, double lon, String label)
    {
        try
        {
            StringBuilder builder = new StringBuilder();
            builder.append("geo:");
            builder.append(lat);
            builder.append(",");
            builder.append(lon);
            builder.append("?z=16"); // zoom value: 2..23
            builder.append("&q="); // query allows to show pin
            builder.append(Uri.encode(lat + "," + lon + "(" + label + ")"));

            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(builder.toString()));
            activity.startActivity(intent);
        }
        catch(android.content.ActivityNotFoundException e)
        {
            // can't start activity
        }
    }


    public static void startMapSearchActivity(Activity activity, String query)
    {
        try
        {
            StringBuilder builder = new StringBuilder();
            builder.append("geo:0,0?q=");
            builder.append(Uri.encode(query));

            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(builder.toString()));
            activity.startActivity(intent);
        }
        catch(android.content.ActivityNotFoundException e)
        {
            // can't start activity
        }
    }


    public static void startSmsActivity(Activity activity, String phoneNumber, String text)
    {
        try
        {
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
            intent.setType("vnd.android-dir/mms-sms");
            intent.putExtra("address", phoneNumber);
            intent.putExtra("sms_body", text);
            activity.startActivity(intent);
        }
        catch(android.content.ActivityNotFoundException e)
        {
            // can't start activity
        }
    }


    public static void startShareActivity(Activity activity, String chooserTitle, String subject, String text)
    {
        try
        {
            Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(android.content.Intent.EXTRA_TEXT, text);
            activity.startActivity(Intent.createChooser(intent, chooserTitle));
        }
        catch(android.content.ActivityNotFoundException e)
        {
            // can't start activity
        }
    }


    public static void startEmailActivity(Activity activity, String email, String subject, String text)
    {
        try
        {
            StringBuilder builder = new StringBuilder();
            builder.append("mailto:");
            builder.append(email);

            Intent intent = new Intent(android.content.Intent.ACTION_SENDTO, Uri.parse(builder.toString()));
            intent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(android.content.Intent.EXTRA_TEXT, text);
            activity.startActivity(intent);
        }
        catch(android.content.ActivityNotFoundException e)
        {
            // can't start activity
        }
    }


    public static void startCalendarActivity(Activity activity, String title, String description, long beginTime, long endTime)
    {
        try
        {
            Intent intent = new Intent(android.content.Intent.ACTION_EDIT);
            intent.setType("vnd.android.cursor.item/event");
            intent.putExtra("title", title);
            intent.putExtra("description", description);
            intent.putExtra("beginTime", beginTime); // time in milliseconds
            intent.putExtra("endTime", endTime);
            activity.startActivity(intent);
        }
        catch(android.content.ActivityNotFoundException e)
        {
            // can't start activity
        }
    }


    public static void startCallActivity(Activity activity, String phoneNumber)
    {
        try
        {
            StringBuilder builder = new StringBuilder();
            builder.append("tel:");
            builder.append(phoneNumber);

            Intent intent = new Intent(android.content.Intent.ACTION_DIAL, Uri.parse(builder.toString()));
            activity.startActivity(intent);
        }
        catch(android.content.ActivityNotFoundException e)
        {
            // can't start activity
        }
    }


    public static void startSettingsActivity(Activity activity)
    {
        try
        {
            activity.startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
        }
        catch(android.content.ActivityNotFoundException e)
        {
            // can't start activity
        }
    }
}
