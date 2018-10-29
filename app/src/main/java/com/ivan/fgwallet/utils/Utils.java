package com.ivan.fgwallet.utils;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by arvind on 30/10/17.
 */

public class Utils {

//    Map<Currency, Locale> map = getCurrencyLocaleMap();
//    String [] countries = { "US", "CA", "MX", "GB", "DE", "PL", "RU", "JP", "CN" };
//
//        for (String countryCode : countries) {
//        Locale locale = new Locale("EN",countryCode);
//        Currency currency = Currency.getInstance(locale);
//        String symbol = currency.getSymbol(map.get(currency));
//        System.out.println("For country " + countryCode + ", currency symbol is " + symbol);
//    }
//}

    public static String upCaseFirst(String name) {
        StringBuilder sb = new StringBuilder(name);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }

    public static ArrayList<String> currency(){
        ArrayList<String> strings = new ArrayList<>();
            Map<Currency, Locale> map = getCurrencyLocaleMap();
//    String [] countries = { "JP","US", "IN", "MX", "GB", "DE", "PL", "RU" };
    String [] countries = { "JP"};

        for (String countryCode : countries) {
        Locale locale = new Locale("EN",countryCode);
        Currency currency = Currency.getInstance(locale);
        String symbol = currency.getSymbol(map.get(currency));
        System.out.println("For country " + countryCode + ", currency symbol is " + symbol);
            strings.add(symbol);
    }
    return strings;
    }

    public static double parseForgiveness(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException nfe) {
            return 0.0;
        }
    }

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

    public static void sendEmail(final Context p_context, final String p_subject, final String p_body, final ArrayList<Uri> p_attachments)
    {
        try
        {
            PackageManager pm = p_context.getPackageManager();
            ResolveInfo selectedEmailActivity = null;

            Intent emailDummyIntent = new Intent(Intent.ACTION_SENDTO);
            emailDummyIntent.setData(Uri.parse("mailto:exc@srsfc.com"));

            List<ResolveInfo> emailActivities = pm.queryIntentActivities(emailDummyIntent, 0);

            if (null == emailActivities || emailActivities.size() == 0)
            {
                Intent emailDummyIntentRFC822 = new Intent(Intent.ACTION_SEND_MULTIPLE);
                emailDummyIntentRFC822.setType("message/rfc822");

                emailActivities = pm.queryIntentActivities(emailDummyIntentRFC822, 0);
            }

            if (null != emailActivities)
            {
                if (emailActivities.size() == 1)
                {
                    selectedEmailActivity = emailActivities.get(0);
                }
                else
                {
                    for (ResolveInfo currAvailableEmailActivity : emailActivities)
                    {
                        if (true == currAvailableEmailActivity.isDefault)
                        {
                            selectedEmailActivity = currAvailableEmailActivity;
                        }
                    }
                }

                if (null != selectedEmailActivity)
                {
                    // Send email using the only/default email activity
                    sendEmailUsingSelectedEmailApp(p_context, p_subject, p_body, p_attachments, selectedEmailActivity);
                }
                else
                {
                    final List<ResolveInfo> emailActivitiesForDialog = emailActivities;

                    String[] availableEmailAppsName = new String[emailActivitiesForDialog.size()];
                    for (int i = 0; i < emailActivitiesForDialog.size(); i++)
                    {
                        availableEmailAppsName[i] = emailActivitiesForDialog.get(i).activityInfo.applicationInfo.loadLabel(pm).toString();
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(p_context);
                    builder.setTitle("Send email using...");
                    builder.setItems(availableEmailAppsName, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            sendEmailUsingSelectedEmailApp(p_context, p_subject, p_body, p_attachments, emailActivitiesForDialog.get(which));
                        }
                    });

                    builder.create().show();
                }
            }
            else
            {
                sendEmailUsingSelectedEmailApp(p_context, p_subject, p_body, p_attachments, null);
            }
        }
        catch (Exception ex)
        {
//            Log.e(TAG, "Can't send email", ex);
        }
    }

    protected static void sendEmailUsingSelectedEmailApp(Context p_context, String p_subject, String p_body, ArrayList<Uri> p_attachments, ResolveInfo p_selectedEmailApp)
    {
        try
        {
            Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);

            String aEmailList[] = { "exc@srsfc.com"};

            emailIntent.putExtra(Intent.EXTRA_EMAIL, aEmailList);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, null != p_subject ? p_subject : "");
            emailIntent.putExtra(Intent.EXTRA_TEXT, null != p_body ? p_body : "");

            if (null != p_attachments && p_attachments.size() > 0)
            {
//                ArrayList<Uri> attachmentsUris = new ArrayList<Uri>();
//
//                // Convert from paths to Android friendly Parcelable Uri's
//                for (String currAttachemntPath : p_attachments)
//                {
//                    File fileIn = new File(currAttachemntPath);
//                    Uri currAttachemntUri = Uri.fromFile(fileIn);
//                    attachmentsUris.add(currAttachemntUri);
//                }
//                emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, attachmentsUris);
                emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, p_attachments);
            }

            if (null != p_selectedEmailApp)
            {
//                Log.d(TAG, "Sending email using " + p_selectedEmailApp);
                emailIntent.setComponent(new ComponentName(p_selectedEmailApp.activityInfo.packageName, p_selectedEmailApp.activityInfo.name));

                p_context.startActivity(emailIntent);
            }
            else
            {
                Intent emailAppChooser = Intent.createChooser(emailIntent, "Select Email app");

                p_context.startActivity(emailAppChooser);
            }
        }
        catch (Exception ex)
        {
//            Log.e(TAG, "Error sending email", ex);
        }
    }
}
