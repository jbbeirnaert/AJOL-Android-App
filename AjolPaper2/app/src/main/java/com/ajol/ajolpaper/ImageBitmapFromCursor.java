package com.ajol.ajolpaper;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

public class ImageBitmapFromCursor extends AsyncTask<ImageBitmapArgs,Integer,Bitmap> {
    @Override
    public Bitmap doInBackground(ImageBitmapArgs... params) {
        Bitmap bitmap = getImageBitmap(params[0].cursor,params[0].index);

        return bitmap;
    }

    @Nullable
    private Bitmap getImageBitmap(Cursor imageCursor, int index) {
        if (imageCursor.moveToPosition(index)){
            byte[] imgByte = imageCursor.getBlob(imageCursor.getColumnIndex(DatabaseConstants.COLUMN_IMG));
//            imageCursor.close();

            return BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
        }

//        if (!imageCursor.isClosed()) {
//            imageCursor.close();
//        }

        return null;
    }
}
