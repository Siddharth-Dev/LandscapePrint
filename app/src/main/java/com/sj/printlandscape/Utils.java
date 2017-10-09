package com.sj.printlandscape;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.support.v7.app.AppCompatActivity;

import com.hp.mss.hpprint.activity.PrintPluginManagerActivity;
import com.hp.mss.hpprint.util.PrintPluginStatusHelper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by SiddharthJain on 7/7/2017.
 */

public class Utils {

    public static void executePrint(AppCompatActivity appActivity, String filePath) {
        if (filePath != null) {
            if (needHelpToInstallPlugin(appActivity)) {
                Intent intent = new Intent(appActivity, PrintPluginManagerActivity.class);
                appActivity.startActivity(intent);
            } else {
                androidDefaultPrint(appActivity, filePath);
            }
        }
    }

    public static void printSalesSummary(final AppCompatActivity appActivity) {
        new PrintSummaryTask(){
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                executePrint(appActivity, s);
            }
        }.execute();
    }

    private static boolean needHelpToInstallPlugin(Activity activity) {
        boolean needHelp = true;

        PrintPluginStatusHelper pluginStatusHelper = PrintPluginStatusHelper.getInstance(activity);
        if (pluginStatusHelper.readyToPrint())
            needHelp = false;

        return needHelp;
    }

    private static void androidDefaultPrint(AppCompatActivity appActivity, final String filePath) {

        PrintDocumentAdapter pda = new PrintDocumentAdapter(){

            @Override
            public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback){
                InputStream input = null;
                OutputStream output = null;

                try {

                    input = new FileInputStream(filePath);
                    output = new FileOutputStream(destination.getFileDescriptor());

                    byte[] buf = new byte[1024];
                    int bytesRead;

                    while ((bytesRead = input.read(buf)) > 0) {
                        output.write(buf, 0, bytesRead);
                    }

                    callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});

                } catch (FileNotFoundException ee){
                    //Catch exception
                } catch (Exception e) {
                    //Catch exception
                } finally {
                    try {
                        input.close();
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras){

                if (cancellationSignal.isCanceled()) {
                    callback.onLayoutCancelled();
                    return;
                }


                PrintDocumentInfo pdi = new PrintDocumentInfo.Builder("Test").setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).build();

                callback.onLayoutFinished(pdi, true);
            }
        };

        PrintManager printManager = (PrintManager) appActivity.getSystemService(Context.PRINT_SERVICE);
        String jobName = "Document";
        printManager.print(jobName, pda, null);
    }

}
