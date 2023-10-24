package com.yitimo.ymage.picker

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import java.util.*

object YmageDBUtils {
    fun queryAlbums(context: Context): ArrayList<Bucket> {
        val uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.MediaColumns.DATA,
            "COUNT(${MediaStore.Images.Media.BUCKET_ID})"
        )
        val cursor: Cursor?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val queryArgs = Bundle()
            queryArgs.putStringArray(ContentResolver.QUERY_ARG_SORT_COLUMNS, arrayOf(MediaStore.Images.Media.DATE_TAKEN))
            queryArgs.putInt(ContentResolver.QUERY_ARG_SORT_DIRECTION, ContentResolver.QUERY_SORT_DIRECTION_DESCENDING)
            // TODO: support api 26~29 :(
            queryArgs.putStringArray(ContentResolver.QUERY_ARG_GROUP_COLUMNS, arrayOf(MediaStore.Images.Media.BUCKET_ID))
            queryArgs.putString(ContentResolver.QUERY_ARG_SQL_HAVING, "COUNT(${MediaStore.Images.Media.BUCKET_ID})>0")
            cursor = context.contentResolver.query(
                uri,
                projection,
                queryArgs,
                null
            )
        } else {
            cursor = context.contentResolver.query(
                uri,
                projection,
                "0=0) GROUP BY ${MediaStore.Images.Media.BUCKET_ID} HAVING (COUNT(${MediaStore.Images.Media.BUCKET_ID})>0",
                null,
                "${MediaStore.Images.Media.DATE_TAKEN} DESC"
            )
        }
        val list = arrayListOf<Bucket>()
        if (cursor == null) {
            return  arrayListOf()
        }
        if (cursor.moveToFirst()) {
            list.add(Bucket(
                    cursor.getLong(0),
                    cursor.getString(2),
                    cursor.getInt(3),
                    cursor.getString(1)
            ))
        }
        while (cursor.moveToNext()) {
            list.add(Bucket(
                    cursor.getLong(0),
                    cursor.getString(2),
                    cursor.getInt(3),
                    cursor.getString(1)
            ))
        }
        cursor.close()
        return list
    }
    fun query(context: Context, albumId: Long = 0): Cursor? {
        val uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.HEIGHT,
            MediaStore.MediaColumns.WIDTH,
            MediaStore.Images.Media.MIME_TYPE
        )
        return context.contentResolver.query(
            uri,
            projection,
            if (albumId == 0.toLong()) null else "${MediaStore.Images.Media.BUCKET_ID}=?",
            if (albumId == 0.toLong()) null else arrayOf(albumId.toString()),
            "${MediaStore.Images.Media.DATE_TAKEN} DESC"
        )
    }
    @SuppressLint("Recycle")
    fun queryChosen(context: Context, chosen: Array<String>): ArrayList<Ymage> {
        val uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.WIDTH,
            MediaStore.MediaColumns.HEIGHT,
            MediaStore.Images.Media.MIME_TYPE
        )
        val rs = arrayListOf<Ymage>()
        val cursor = context.contentResolver.query(
            uri,
            projection,
            "${MediaStore.Images.Media.DATA} in (?)",
            arrayOf(chosen.joinToString(",")),
            "${MediaStore.Images.Media.DATE_TAKEN} DESC"
        ) ?: return rs
        cursor.moveToFirst()
        rs.add(Ymage(
                cursor.getLong(0),
                cursor.getString(1),
                cursor.getInt(2),
                cursor.getInt(3),
                cursor.getString(4) == "image/gif"
        ))
        while (cursor.moveToNext()) {
            rs.add(Ymage(
                    cursor.getLong(0),
                    cursor.getString(1),
                    cursor.getInt(2),
                    cursor.getInt(3),
                    cursor.getString(4) == "image/gif"
            ))
        }
        cursor.close()
        return rs
    }
    @SuppressLint("Recycle")
    fun first(context: Context): Bucket {
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.MediaColumns.DATA,
            "COUNT(${MediaStore.Images.ImageColumns._ID})"
        )
        val cursor: Cursor
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val queryArgs = Bundle()
            queryArgs.putInt(ContentResolver.QUERY_ARG_LIMIT, 1)
            queryArgs.putStringArray(ContentResolver.QUERY_ARG_SORT_COLUMNS, arrayOf(MediaStore.Images.Media.DATE_TAKEN))
            queryArgs.putInt(ContentResolver.QUERY_ARG_SORT_DIRECTION, ContentResolver.QUERY_SORT_DIRECTION_DESCENDING)
            cursor = context.contentResolver.query(
                uri,
                projection,
                queryArgs,
                null
            ) ?: return Bucket()
        } else {
            cursor = context.contentResolver.query(
                uri,
                projection,
                null,
                null,
                "${MediaStore.Images.Media.DATE_TAKEN} DESC"
            ) ?: return Bucket()
        }
        cursor.moveToFirst()
        val rs = Bucket(
            0,
            cursor.getString(0) ?: "",
            cursor.getInt(1),
            "全部"
        )
        cursor.close()
        return rs
    }
}
