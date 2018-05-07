package com.ajol.ajolpaper;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.widget.Toast;

public class ImageBitmapFromCursor extends AsyncTask<ImageBitmapArgs,Integer,Bitmap> {
    @Override
    public Bitmap doInBackground(ImageBitmapArgs... params) {
        Bitmap bitmap = getImageBitmap(params[0].cursor, params[0].context, params[0].width, params[0].height);

        return bitmap;
    }

    @Nullable
    private Bitmap getImageBitmap(Cursor imageCursor, Context context, int width, int height) { //to maintain image, set width & height to -1
        try {
            imageCursor.moveToFirst();

            String imagePath = imageCursor.getString(imageCursor.getColumnIndex(DatabaseConstants.COLUMN_IMG));

            if (!imageCursor.isClosed()) {
                imageCursor.close();
            }

            Bitmap output;
            if (width > 0 && height > 0) {
                //Owen: lower resolution to speed rendering
                BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
                bmpFactoryOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(imagePath, bmpFactoryOptions);

                int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight / (float) height);
                int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth / (float) width);

                if (heightRatio > 1 || widthRatio > 1) {
                    if (heightRatio > widthRatio) {
                        bmpFactoryOptions.inSampleSize = heightRatio;
                    } else {
                        bmpFactoryOptions.inSampleSize = widthRatio;
                    }
                }

                bmpFactoryOptions.inJustDecodeBounds = false;
                output = BitmapFactory.decodeFile(imagePath, bmpFactoryOptions);
            }
            else {
                output = BitmapFactory.decodeFile(imagePath);
            }

            return output;
        }
        catch (Exception e) {
            Toast.makeText(context,"Failed to get bitmap from cursor.",Toast.LENGTH_SHORT).show();

            return null;
        }
    }
}
