package com.nextiva.nextivaapp.android.managers.interfaces;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.models.AvatarInfo;

public interface AvatarManager {

    @Nullable
    Bitmap getBitmap(@NonNull AvatarInfo avatarInfo);

    @Nullable
    Bitmap getPresenceBitmap(@NonNull AvatarInfo avatarInfo);

    Bitmap byteArrayToBitmap(@Nullable byte[] bytes);

    Bitmap scaleBitmapForMMS(Bitmap bitmap, int fileSize);

    byte[] scaleImageByteArrayUnderTwoMB(byte[] bytes);

    @Nullable
    String byteArrayToString(@Nullable byte[] bytes);

    byte[] bitmapToScaledDownByteArray(@NonNull Bitmap bitmap);

    byte[] bitmapToByteArray(@NonNull Bitmap bitmap);

    @Nullable
    byte[] stringToByteArray(@Nullable String avatarString);

    boolean isByteArrayNotEmpty(@Nullable byte[] bytes);

}
