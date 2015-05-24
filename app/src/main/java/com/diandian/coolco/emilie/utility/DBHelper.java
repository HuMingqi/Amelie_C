package com.diandian.coolco.emilie.utility;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.diandian.coolco.emilie.model.Image;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;

public class DBHelper extends OrmLiteSqliteOpenHelper{

    private final static String DATABASE_NAME = "image.db";
    private final static int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Image.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {
        //do nothing in version 1.
    }

    private RuntimeExceptionDao<Image, Integer> imageDao;

    private RuntimeExceptionDao<Image, Integer> getImageDao(){
        if (imageDao == null) {
            imageDao = getRuntimeExceptionDao(Image.class);
        }
        return imageDao;
    }

    public boolean insertImage(Image image){
        RuntimeExceptionDao<Image, Integer> imageDao = getImageDao();
        imageDao.createIfNotExists(image);
        return true;
    }

    public boolean insertImage(List<Image> images){
        RuntimeExceptionDao<Image, Integer> imageDao = getImageDao();
        for (int i = 0; i < images.size(); i++) {
            imageDao.create(images.get(i));
        }
        return true;
    }

    public boolean removeImage(Image image){
        RuntimeExceptionDao<Image, Integer> imageDao = getImageDao();
        return imageDao.delete(image) == 1;
    }

    public boolean removeImage(List<Image> images){
        RuntimeExceptionDao<Image, Integer> imageDao = getImageDao();
        return  imageDao.delete(images) == images.size();
    }

    public List<Image> getImage() {
        RuntimeExceptionDao<Image, Integer> imageDao = getImageDao();
        return imageDao.queryForAll();
    }
}
