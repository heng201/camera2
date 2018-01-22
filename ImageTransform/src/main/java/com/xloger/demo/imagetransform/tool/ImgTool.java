package com.xloger.demo.imagetransform.tool;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

/**
 * Created by xloger on 9月8日.
 * Author:xloger
 * Email:phoenix@xloger.com
 */
public class ImgTool {

    public static String getPath(final Context context,final Uri uri){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT&& DocumentsContract.isDocumentUri(context, uri)){

            if (isExternalStorageDocument(uri)){
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type=split[0];
                if ("primary".equalsIgnoreCase(type)){
                    return Environment.getExternalStorageDirectory()+"/"+split[1];
                }
            }

            else if (isDownloadsDocument(uri)){
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type=split[0];
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                return getDataColumn(context,contentUri,null,null);
            }

            else if (isMediaDocument(uri)){
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type=split[0];
                Uri contentUri=null;
                if ("image".equals(type)){
                    contentUri= MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                }else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection="_id=?";
                String[] selectionArgs=new String[]{
                        split[1]
                };
                return getDataColumn(context,contentUri,selection,selectionArgs);
            }

        }

        else if ("content".equalsIgnoreCase(uri.getScheme())){
            if (isGooglePhotosUri(uri)){
                return uri.getLastPathSegment();
            }else {
                return getDataColumn(context,uri,null,null);
            }
        }

        else if ("file".equalsIgnoreCase(uri.getScheme())){
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context,Uri uri,String selection,String[] selectionArgs){
        Cursor cursor=null;
        String column="_data";
        String[] projection={
                column
        };
        try {
            cursor=context.getContentResolver().query(uri,projection,selection,selectionArgs,null);
            if (cursor!=null&&cursor.moveToFirst()){
                int index=cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }else {
                Log.e("img","为Null");
            }
        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }


    public static boolean isExternalStorageDocument(Uri uri){
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri){
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri){
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri){
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static String selectImage(Context context,Uri uri){
        if(uri!=null){
            String uriStr=uri.toString();
            String path=uriStr.substring(10,uriStr.length());
            if(path.startsWith("com.sec.android.gallery3d")){
                Log.e("xlogerBug", "It's auto backup pic path:"+uri.toString());
                return null;
            }
        }
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(uri,filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return picturePath;
    }
}
