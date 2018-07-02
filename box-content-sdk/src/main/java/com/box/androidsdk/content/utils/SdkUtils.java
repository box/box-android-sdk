package com.box.androidsdk.content.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.widget.TextView;
import android.widget.Toast;

import com.box.sdk.android.R;
import com.eclipsesource.json.JsonValue;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SdkUtils {


    protected static final int[] THUMB_COLORS = new int[]{0xffc2185b, 0xffed3757, 0xfffe6b9c
            , 0xfff59e94, 0xfff79600, 0xfff5b31b, 0xffb7c61f, 0xff26c281, 0xff15a2ab, 0xff54c4ef
            , 0xff11a4ff, 0xff6f87ff, 0xff3f51d3, 0xff673ab7, 0xffab47bc};

    public static final int COLLAB_NUMBER_THUMB_COLOR = 0xff1992de;

    /**
     * Per OAuth2 specs, auth code exchange should include a state token for CSRF validation
     *
     * @return a randomly generated String to use as a state token
     */
    public static String generateStateToken() {
        return UUID.randomUUID().toString();
    }

    public static final int BUFFER_SIZE = 8192;

    /**
     * Utility method to write given inputStream to given outputStream.
     *
     * @param inputStream  the inputStream to copy from.
     * @param outputStream the outputStream to write to.
     * @throws IOException          thrown if there was a problem reading from inputStream or writing to outputStream.
     * @throws InterruptedException thrown if the thread is interrupted which indicates cancelling.
     */
    public static void copyStream(final InputStream inputStream, final OutputStream outputStream) throws IOException,
            InterruptedException {
        copyStream(inputStream, outputStream, null);
    }

    /**
     * Utility method to write given inputStream to given outputStream and compute the sha1 while transferring the bytes
     *
     * @param inputStream  the inputStream to copy from.
     * @param outputStream the outputStream to write to.
     * @return
     * @throws IOException          thrown if there was a problem reading from inputStream or writing to outputStream.
     * @throws InterruptedException thrown if the thread is interrupted which indicates cancelling.
     */
    public static String copyStreamAndComputeSha1(final InputStream inputStream, final OutputStream outputStream)
            throws NoSuchAlgorithmException, IOException, InterruptedException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        copyStream(inputStream, outputStream, md);
        return new String(encodeHex(md.digest()));
    }

    /**
     * Utility method to write given inputStream to given outputStream and update the messageDigest while transferring the bytes
     *
     * @param inputStream   the inputStream to copy from.
     * @param outputStream  the outputStream to write to.
     * @param messageDigest messageDigest to update with the outpu
     * @throws IOException          thrown if there was a problem reading from inputStream or writing to outputStream.
     * @throws InterruptedException thrown if the thread is interrupted which indicates cancelling.
     */
    private static void copyStream(final InputStream inputStream, final OutputStream outputStream, MessageDigest messageDigest) throws IOException,
            InterruptedException {
        // Read the rest of the stream and write to the destination OutputStream.
        final byte[] buffer = new byte[BUFFER_SIZE];
        int bufferLength = 0;
        Exception exception = null;
        try {
            while ((bufferLength = inputStream.read(buffer)) > 0) {
                if (Thread.currentThread().isInterrupted()) {
                    InterruptedException e = new InterruptedException();
                    throw e;
                }
                outputStream.write(buffer, 0, bufferLength);
                if (messageDigest != null) {
                    messageDigest.update(buffer, 0, bufferLength);
                }
            }

        } catch (Exception e) {
            exception = e;
            if (exception instanceof IOException) {
                throw (IOException) e;
            }
            if (exception instanceof InterruptedException) {
                throw (InterruptedException) e;
            }
        } finally {
            // Try to flush the OutputStream
            if (exception == null) {
                outputStream.flush();
            }
        }
    }

    /**
     * Helper method that wraps given arrays inside of a single outputstream.
     *
     * @param outputStreams an array of multiple outputstreams.
     * @return a single outputstream that will write to provided outputstreams.
     */
    public static OutputStream createArrayOutputStream(final OutputStream[] outputStreams) {
        return new OutputStream() {


            @Override
            public void close() throws IOException {
                for (OutputStream o : outputStreams) {
                    o.close();
                }
                super.close();
            }

            @Override
            public void flush() throws IOException {
                for (OutputStream o : outputStreams) {
                    o.flush();
                }
                super.flush();
            }


            @Override
            public void write(int oneByte) throws IOException {
                for (OutputStream o : outputStreams) {
                    o.write(oneByte);
                }
            }


            @Override
            public void write(byte[] buffer) throws IOException {
                for (OutputStream o : outputStreams) {
                    o.write(buffer);
                }
            }

            @Override
            public void write(byte[] buffer, int offset, int count) throws IOException {
                for (OutputStream o : outputStreams) {
                    o.write(buffer, offset, count);
                }
            }
        };
    }

    /**
     * Helper method to return String form of an object that null checks.
     *
     * @param object an object to get string from.
     * @return String representation of object or null if object is null.
     */
    public static String getAsStringSafely(Object object) {
        return object == null ? null : object.toString();
    }

    /**
     * Utility method to check if given string is empty.
     *
     * @param str string to check.
     * @return true if string provided is null or is of length 0.
     */
    public static boolean isEmptyString(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * Utility method to check if given string is blank.
     *
     * @param str string to check.
     * @return true if string provided is null, is length 0, or consists of only spaces.
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().length() == 0;
    }

    /**
     * Utilitiy method to calculate sha1 based on given inputStream.
     *
     * @param inputStream InputStream of file to calculate sha1 for.
     * @return the calculated sha1 for given stream.
     * @throws IOException              thrown if there was issue getting stream content.
     * @throws NoSuchAlgorithmException thrown if Sha-1 algorithm implementation is not supported by OS.
     */
    public static String sha1(final InputStream inputStream) throws IOException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] bytes = new byte[8192];
        int byteCount;
        while ((byteCount = inputStream.read(bytes)) > 0) {
            md.update(bytes, 0, byteCount);
        }

        inputStream.close();
        return new String(encodeHex(md.digest()));
    }

    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

    private static char[] encodeHex(byte[] data) {
        int l = data.length;
        char[] out = new char[l << 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = HEX_CHARS[(0xF0 & data[i]) >>> 4];
            out[j++] = HEX_CHARS[0x0F & data[i]];
        }
        return out;
    }


    /**
     * Parse a given JsonValue to a long regardless of whether that value is a String or a long.
     *
     * @param value a JsonValue to parse to a long.
     * @return a long representation of the given value. Can throw a runtime exception (ParseException, UnsupportedOperationException, or NumberFormatException).
     */
    public static long parseJsonValueToLong(JsonValue value) {
        try {
            return value.asLong();
        } catch (UnsupportedOperationException e) {
            String s = value.asString().replace("\"", "");
            return Long.parseLong(s);
        }
    }

    /**
     * Parse a given JsonValue to an int regardless of whether that value is a String or an int.
     *
     * @param value a JsonValue to parse to an int.
     * @return an int representation of the given value. Can throw a runtime exception (ParseException, UnsupportedOperationException, or NumberFormatException).
     */
    public static long parseJsonValueToInteger(JsonValue value) {
        try {
            return value.asInt();
        } catch (UnsupportedOperationException e) {
            String s = value.asString().replace("\"", "");
            return Integer.parseInt(s);
        }
    }

    /**
     * Utility method to create a large String with the given delimiter.
     *
     * @param strings   Strings to concatenate.
     * @param delimiter The delimiter to use to put between each string item.
     * @return a large string with all items separated by given delimiter.
     */
    public static String concatStringWithDelimiter(String[] strings, String delimiter) {
        StringBuilder sbr = new StringBuilder();
        int size = strings.length;
        for (int i = 0; i < size - 1; i++) {
            sbr.append(strings[i]).append(delimiter);
        }
        sbr.append(strings[size - 1]);
        return sbr.toString();
    }

    /**
     * Utility method to create a new StringMappedThreadPoolExecutor (which can be used to inspect runnables).
     *
     * @param corePoolSize    the number of threads to keep in the pool, even if they are idle, unless allowCoreThreadTimeOut is set
     * @param maximumPoolSize the maximum number of threads to allow in the pool
     * @param keepAliveTime   when the number of threads is greater than the core, this is the maximum time that excess idle threads will wait for new tasks before terminating.
     * @param unit            the time unit for the keepAliveTime argument
     * @return a StringMappedThreadPoolExecutor created from given arguments.
     */
    public static ThreadPoolExecutor createDefaultThreadPoolExecutor(int corePoolSize,
                                                                     int maximumPoolSize,
                                                                     long keepAliveTime,
                                                                     TimeUnit unit) {
        return new StringMappedThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, new LinkedBlockingQueue<Runnable>(),
                new ThreadFactory() {

                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r);
                    }
                });
    }

    /**
     * Helper method to clone a serializable object.
     *
     * @param source the serializable object to clone.
     * @param <T>    The class of the serializable object.
     * @return a clone of the given source object.
     */
    public static <T extends Object> T cloneSerializable(T source) {
        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;
        ByteArrayInputStream bais = null;
        ObjectInputStream ois = null;
        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(source);

            bais = new ByteArrayInputStream(baos.toByteArray());
            ois = new ObjectInputStream(bais);
            return (T) ois.readObject();
        } catch (IOException e) {
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        } finally {
            closeQuietly(baos, oos, bais, ois);
        }
    }

    /**
     * Helper method to write a serializable object into a String.
     *
     * @param obj the Serializable object
     * @return a String representation of obj.
     */
    public static String convertSerializableToString(Serializable obj) {
        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;
        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);

            return new String(baos.toByteArray());
        } catch (IOException e) {
            return null;
        } finally {
            closeQuietly(baos, oos);
            closeQuietly(oos);
        }
    }

    /**
     * Helper method to close one or more closeables while ignoring exceptions.
     *
     * @param closeables one or more closeables to close while swallowing exceptions.
     */
    public static void closeQuietly(Closeable... closeables) {
        for (Closeable c : closeables) {
            try {
                c.close();
            } catch (Exception e) {

            }
        }
    }

    /**
     * Recursively delete a folder and all its subfolders and files.
     *
     * @param f directory to be deleted.
     * @return True if the folder was deleted.
     */
    public static boolean deleteFolderRecursive(final File f) {
        if (f.isDirectory()) {
            File[] files = f.listFiles();
            if (files == null) {
                return false;
            }
            for (File c : files) {
                deleteFolderRecursive(c);
            }
        }
        return f.delete();
    }

    /**
     * Check for an internet connection.
     *
     * @param context current context.
     * @return whether or not there is a valid internet connection
     */
    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return isInternetAvailable(connectivityManager);
        } else {
            return isInternetAvailablePreLollipop(connectivityManager);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static boolean isInternetAvailable(ConnectivityManager connectivityManager) {
        Network[] allNetworks = connectivityManager.getAllNetworks();
        if (allNetworks != null) {
            for (Network network : allNetworks) {
                NetworkInfo networkInfo = connectivityManager.getNetworkInfo(network);
                if (networkInfo != null && networkInfo.isConnected()) {
                    if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean isInternetAvailablePreLollipop(ConnectivityManager connectivityManager) {
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return (wifi != null && wifi.isConnected()) || (mobile != null && mobile.isConnected());
    }

    /**
     * Helper method for reading an asset file into a string.
     *
     * @param context   current context.
     * @param assetName the asset name
     * @return a string representation of a file in assets.
     */
    public static String getAssetFile(final Context context, final String assetName) {
        // if the file is not found create it and return that.
        // if we do not have a file we copy our asset out to create one.
        AssetManager assetManager = context.getAssets();
        BufferedReader in = null;
        try {
            StringBuilder buf = new StringBuilder();
            InputStream is = assetManager.open(assetName);
            in = new BufferedReader(new InputStreamReader(is));

            String str;
            boolean isFirst = true;
            while ((str = in.readLine()) != null) {
                if (isFirst)
                    isFirst = false;
                else
                    buf.append('\n');
                buf.append(str);
            }
            return buf.toString();
        } catch (IOException e) {
            BoxLogUtils.e("getAssetFile", assetName, e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                BoxLogUtils.e("getAssetFile", assetName, e);
            }
        }
        // should never get here unless the asset file is inaccessible or cannot be copied out.
        return null;
    }

    /**
     * Helper class to manage showing toasts.
     */
    private static HashMap<Integer, Long> LAST_TOAST_TIME = new HashMap<Integer, Long>(10) {

        @Override
        public Long put(Integer key, Long value) {
            Long oldItem = super.put(key, value);
            if (this.size() > 9) {
                clean();
            }
            return oldItem;
        }

        private void clean() {
            long maxDelayedTime = System.currentTimeMillis() - TOAST_MIN_REPEAT_DELAY;
            for (Entry<Integer, Long> entry : this.entrySet()) {
                if (entry.getValue() < maxDelayedTime) {
                    LAST_TOAST_TIME.remove(entry);
                }
            }

        }
    };

    public static long TOAST_MIN_REPEAT_DELAY = 3000;

    /**
     * Helper method for showing a toast message checking to see if user is on ui thread, and not showing the
     * same toast if it has already been shown within TOAST_MIN_REPEAT_DELAY time.
     *
     * @param context  current context.
     * @param resId    string resource id to display.
     * @param duration Toast.LENGTH_LONG or Toast.LENGTH_SHORT.
     */
    public static void toastSafely(final Context context, final int resId, final int duration) {
        Long lastToastTime = LAST_TOAST_TIME.get(resId);
        if (lastToastTime != null && (lastToastTime + TOAST_MIN_REPEAT_DELAY) < System.currentTimeMillis()) {
            return;
        }
        Looper mainLooper = Looper.getMainLooper();
        if (Thread.currentThread().equals(mainLooper.getThread())) {
            LAST_TOAST_TIME.put(resId, System.currentTimeMillis());
            Toast.makeText(context, resId, duration).show();
        } else {
            Handler handler = new Handler(mainLooper);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    LAST_TOAST_TIME.put(resId, System.currentTimeMillis());
                    Toast.makeText(context, resId, duration).show();
                }
            });
        }
    }

    /**
     * Helper method used to display initials into a given textview.
     *
     * @param context      current context
     * @param initialsView TextView used to set initials into.
     * @param fullName     The user's full name to create initials for.
     */
    public static void setInitialsThumb(Context context, TextView initialsView, String fullName) {
        char initial1 = '\u0000';
        char initial2 = '\u0000';
        if (fullName != null) {
            String[] nameParts = fullName.split(" ");
            if (nameParts[0].length() > 0) {
                initial1 = nameParts[0].charAt(0);
            }
            if (nameParts.length > 1) {
                initial2 = nameParts[nameParts.length - 1].charAt(0);
            }
        }
        setColorForInitialsThumb(initialsView, initial1 + initial2);
        initialsView.setText(initial1 + "" + initial2);
        initialsView.setTextColor(Color.WHITE);
    }

    /**
     * Helper method to display number of collaborators. If there are more than 99 collaborators it
     * would show "99+" due to the width constraint in the view.
     *
     * @param context      current context
     * @param initialsView TextView used to display number of collaborators
     * @param collabNumber Number of collaborators
     */
    public static void setCollabNumberThumb(Context context, TextView initialsView, int collabNumber) {
        String collabNumberDisplay = (collabNumber >= 100) ? "+99" : "+" + Integer.toString(collabNumber);
        setColorForCollabNumberThumb(initialsView);
        initialsView.setTextColor(COLLAB_NUMBER_THUMB_COLOR);
        initialsView.setText(collabNumberDisplay);
    }

    /**
     * @param initialsView view where the thumbs will be shown
     * @param position     Used to pick a material color from an array
     * @deprecated Use setColorsThumb(TextView initialsView, int backgroundColor, int strokeColor) instead.
     * Sets the the background thumb color for the account view to one of the material colors
     */
    @Deprecated
    public static void setColorsThumb(TextView initialsView, int position) {
        setColorForInitialsThumb(initialsView, position);
    }

    /**
     * Sets the the background thumb color for the account view to one of the material colors
     *
     * @param initialsView view where the thumbs will be shown
     */
    public static void setColorsThumb(TextView initialsView, int backgroundColor, int strokeColor) {
        GradientDrawable drawable = (GradientDrawable) initialsView.getResources().getDrawable(R.drawable.boxsdk_thumb_background);
        drawable.setColorFilter(backgroundColor, PorterDuff.Mode.MULTIPLY);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            drawable.setStroke(3, strokeColor);
            initialsView.setBackground(drawable);
        } else {
            drawable.setStroke(3, strokeColor);
            initialsView.setBackgroundDrawable(drawable);
        }
    }

    /**
     * Sets the thumb color that displays users initials
     *
     * @param initialsView TextView used to display number of collaborators
     * @param position     Used to pick a material color from an array
     */
    public static void setColorForInitialsThumb(TextView initialsView, int position) {
        int backgroundColor = THUMB_COLORS[(position) % THUMB_COLORS.length];
        setColorsThumb(initialsView, backgroundColor, Color.WHITE);
    }

    /**
     * Sets the thumb color that displays number of additional collaborators
     *
     * @param initialsView TextView used to display number of collaborators
     */
    public static void setColorForCollabNumberThumb(TextView initialsView) {
        setColorsThumb(initialsView, Color.WHITE, COLLAB_NUMBER_THUMB_COLOR);
    }

    /**
     * Utility method to create a downsampled bitmap from a given image file preserving aspect ratio.
     *
     * @param res       Resource to get asset from.
     * @param resId     the id of the image asset.
     * @param reqWidth  the required width for the bitmap
     * @param reqHeight the required height for the bitmap.
     * @return Bitmap from file that has been downsampled to fit width and height.
     */
    public static Bitmap decodeSampledBitmapFromFile(Resources res, int resId,
                                                     int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * Utility method to create a downsampled bitmap from a given image file preserving aspect ratio.
     *
     * @param file      image file
     * @param reqWidth  the required width for the bitmap
     * @param reqHeight the required height for the bitmap.
     * @return Bitmap from file that has been downsampled to fit width and height.
     */
    public static Bitmap decodeSampledBitmapFromFile(File file,
                                                     int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(file.getAbsolutePath(), options);

    }

    /**
     * Utility method to calculate the best sample size for downscaling Bitmap preserving aspect ratio.
     *
     * @param options   BitmapFactory options of a Bitmap used to check dimensions.
     * @param reqWidth  the required width for displaying bitmap.
     * @param reqHeight the required height for displaying the bitmap.
     * @return an integer indicating best sample size to display Bitmap for the given dimensions.
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


    private static String SIZE_BYTES = "%4.0f B";
    private static String SIZE_KILOBYTES = "%4.1f KB";
    private static String SIZE_MEGABYTES = "%4.1f MB";
    private static String SIZE_GIGABYTES = "%4.1f GB";
    private static String SIZE_TERABYTES = "%4.1f TB";

    private static String SIZE_LANGUAGE = "";


    private static final int constKB = 1024;
    private static final int constMB = constKB * constKB;
    private static final double constGB = constMB * constKB;
    private static final double constTB = constGB * constKB;

    private static final double floatKB = 1024.0f;
    private static final double floatMB = floatKB * floatKB;
    private static final double floatGB = floatMB * floatKB;
    private static final double floatTB = floatGB * floatKB;

    /**
     * Java version of routine to turn a long into a short user readable string.
     * 
     * This routine is used if the JNI native C version is not available.
     *
     * @param numSize the number of bytes in the file.
     * @return String Short human readable String e.g. 2.5 MB
     */

    public static String getLocalizedFileSize(final Context context, double numSize) {

        String localeLanguage = Locale.getDefault().getLanguage();
        if (!SIZE_LANGUAGE.equals(localeLanguage) && context != null && context.getResources() != null) {
            Resources resources = context.getResources();
            SIZE_BYTES = resources.getString(R.string.boxsdk_bytes);
            SIZE_KILOBYTES = resources.getString(R.string.boxsdk_kilobytes);
            SIZE_MEGABYTES = resources.getString(R.string.boxsdk_megabytes);
            SIZE_GIGABYTES = resources.getString(R.string.boxsdk_gigabytes);
            SIZE_TERABYTES = resources.getString(R.string.boxsdk_terabytes);
            SIZE_LANGUAGE = localeLanguage;
        }

        String textSize = null;
        double size;

        if (numSize < constKB) {
            textSize = String.format(Locale.getDefault(), SIZE_BYTES, numSize);
        } else if ((numSize >= constKB) && (numSize < constMB)) {
            size = numSize / floatKB;
            textSize = String.format(Locale.getDefault(), SIZE_KILOBYTES, size);
        } else if ((numSize >= constMB) && (numSize < constGB)) {
            size = numSize / floatMB;
            textSize = String.format(Locale.getDefault(), SIZE_MEGABYTES, size);
        } else if (numSize >= constGB && (numSize < constTB)) {
            size = numSize / floatGB;
            textSize = String.format(Locale.getDefault(), SIZE_GIGABYTES, size);
        } else if (numSize >= constTB) {
            size = numSize / floatTB;
            textSize = String.format(Locale.getDefault(), SIZE_TERABYTES, size);
        }
        return textSize;
    }

}
