package com.example.data;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Pure Java Backend Service for Permanent Crop Photo Storage & Validation.
 * Ensures that photos uploaded by Crop Sellers (Farmers) are copied into internal storage
 * (`filesDir/crop_photos/`) so they never disappear on app refresh or reboot.
 */
public final class ImageStorageHelper {

    private ImageStorageHelper() {
        // Utility class
    }

    /**
     * Copies a picked photo URI into the app's permanent internal files directory
     * (`filesDir/crop_photos/`) so that the photo NEVER disappears after app refresh,
     * activity recreation, or device reboot.
     */
    @Nullable
    public static String copyUriToPermanentStorage(@NonNull Context context, @NonNull Uri sourceUri) {
        try {
            try {
                context.getContentResolver().takePersistableUriPermission(
                        sourceUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                );
            } catch (Exception ignored) {
                // Ignore if Photo Picker URI doesn't support persistable permission;
                // we copy the bytes directly into internal storage below.
            }

            File photosDir = new File(context.getFilesDir(), "crop_photos");
            if (!photosDir.exists()) {
                //noinspection ResultOfMethodCallIgnored
                photosDir.mkdirs();
            }

            File destFile = new File(photosDir, "crop_" + System.currentTimeMillis() + ".jpg");
            try (InputStream input = context.getContentResolver().openInputStream(sourceUri)) {
                if (input != null) {
                    try (FileOutputStream output = new FileOutputStream(destFile)) {
                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        while ((bytesRead = input.read(buffer)) != -1) {
                            output.write(buffer, 0, bytesRead);
                        }
                        output.flush();
                    }
                }
            }

            if (destFile.exists() && destFile.length() > 0L) {
                return Uri.fromFile(destFile).toString();
            } else {
                return sourceUri.toString();
            }
        } catch (Exception e) {
            return sourceUri.toString();
        }
    }

    /**
     * Verifies that the stored imageUri is non-blank and actually readable,
     * preventing blank empty boxes when an old temporary URI has expired.
     */
    public static boolean isImageUriReadable(@NonNull Context context, @Nullable String uriString) {
        if (uriString == null || uriString.trim().isEmpty()) {
            return false;
        }
        try {
            if (uriString.startsWith("file://")) {
                String path = Uri.parse(uriString).getPath();
                if (path == null) {
                    return false;
                }
                File file = new File(path);
                return file.exists() && file.length() > 0L;
            } else if (uriString.startsWith("/")) {
                File file = new File(uriString);
                return file.exists() && file.length() > 0L;
            } else {
                Uri uri = Uri.parse(uriString);
                try (InputStream stream = context.getContentResolver().openInputStream(uri)) {
                    return stream != null && stream.available() >= 0;
                }
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns the optimal model object (File or Uri) for Coil's AsyncImage.
     */
    @NonNull
    public static Object resolveImageModel(@NonNull String uriString) {
        if (uriString.startsWith("file://")) {
            String path = Uri.parse(uriString).getPath();
            if (path != null) {
                return new File(path);
            }
            return Uri.parse(uriString);
        } else if (uriString.startsWith("/")) {
            return new File(uriString);
        } else {
            return Uri.parse(uriString);
        }
    }

    /**
     * Deletes the internal image file when the seller explicitly removes the crop listing.
     */
    public static void deleteStoredImageIfInternal(@Nullable String uriString) {
        if (uriString == null || uriString.trim().isEmpty()) {
            return;
        }
        try {
            File file = null;
            if (uriString.startsWith("file://")) {
                String path = Uri.parse(uriString).getPath();
                if (path != null) {
                    file = new File(path);
                }
            } else if (uriString.startsWith("/")) {
                file = new File(uriString);
            }
            if (file != null && file.exists() && file.getAbsolutePath().contains("crop_photos")) {
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            }
        } catch (Exception ignored) {
        }
    }
}
