package com.androidex.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.androidex.bean.FaceBean;
import com.arcsoft.dysmart.ArcsoftManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/6/4.
 */

public class FaceHelper extends SQLiteOpenHelper {
    private static final String NAME = "face.db";
    private static final int VERSION = 1;
    private static final String TABLE = "face_access";

    public FaceHelper(Context context){
        super(context,NAME,null,VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE "+TABLE+" (id INTEGER , byUserid INTEGER, roomid INTEGER, lockid INTEGER, imageUrl TEXT, dataUrl TEXT,faceName TEXT,phone TEXT,communityId INTEGER,creDate TEXT,loadName TEXT,loading INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void updateLoading(int id,String loadName,int loading){
        ContentValues cv = new ContentValues();
        cv.put("loadName",loadName);
        cv.put("loading",loading);
        String whereClause = "id=?";
        String[] whereArgs={""+id};
        getWritableDatabase().update(TABLE,cv,whereClause,whereArgs);
    }

    public FaceBean getFaceByLoadName(String loadName){
        String columns[] = {"id","byUserid","roomid","lockid","imageUrl","dataUrl","faceName","phone","communityId","creDate","loadName","loading"};
        String whereClause = "loadName=?";
        String[] whereArgs={loadName};
        Cursor c = getReadableDatabase().query(TABLE,columns,whereClause,whereArgs,null,null,null);
        if(c.moveToFirst()){
            c.move(0);
            FaceBean faceBean = new FaceBean();
            faceBean.id = c.getInt(c.getColumnIndex("id"));
            faceBean.byUserid = c.getInt(c.getColumnIndex("byUserid"));
            faceBean.roomid = c.getInt(c.getColumnIndex("roomid"));
            faceBean.lockid = c.getInt(c.getColumnIndex("lockid"));
            faceBean.imageUrl = c.getString(c.getColumnIndex("imageUrl"));
            faceBean.dataUrl = c.getString(c.getColumnIndex("dataUrl"));
            faceBean.faceName = c.getString(c.getColumnIndex("faceName"));
            faceBean.phone = c.getString(c.getColumnIndex("phone"));
            faceBean.communityId = c.getInt(c.getColumnIndex("communityId"));
            faceBean.creDate = c.getString(c.getColumnIndex("creDate"));
            faceBean.loadName = c.getString(c.getColumnIndex("loadName"));
            faceBean.loading = c.getInt(c.getColumnIndex("loading"));
            return faceBean;
        }else{
            return null;
        }
    }

    public FaceBean getFaceByid(int id){
        String columns[] = {"id","byUserid","roomid","lockid","imageUrl","dataUrl","faceName","phone","communityId","creDate","loadName","loading"};
        String whereClause = "id=?";
        String[] whereArgs={""+id};
        Cursor c = getReadableDatabase().query(TABLE,columns,whereClause,whereArgs,null,null,null);
        if(c.moveToFirst()){
            c.move(0);
            FaceBean faceBean = new FaceBean();
            faceBean.id = c.getInt(c.getColumnIndex("id"));
            faceBean.byUserid = c.getInt(c.getColumnIndex("byUserid"));
            faceBean.roomid = c.getInt(c.getColumnIndex("roomid"));
            faceBean.lockid = c.getInt(c.getColumnIndex("lockid"));
            faceBean.imageUrl = c.getString(c.getColumnIndex("imageUrl"));
            faceBean.dataUrl = c.getString(c.getColumnIndex("dataUrl"));
            faceBean.faceName = c.getString(c.getColumnIndex("faceName"));
            faceBean.phone = c.getString(c.getColumnIndex("phone"));
            faceBean.communityId = c.getInt(c.getColumnIndex("communityId"));
            faceBean.creDate = c.getString(c.getColumnIndex("creDate"));
            faceBean.loadName = c.getString(c.getColumnIndex("loadName"));
            faceBean.loading = c.getInt(c.getColumnIndex("loading"));
            return faceBean;
        }else{
            return null;
        }
    }

    public List<FaceBean> getAllFace(){
        String columns[] = {"id","byUserid","roomid","lockid","imageUrl","dataUrl","faceName","phone","communityId","creDate","loadName","loading"};
        Cursor c = getReadableDatabase().query(TABLE,columns,null,null,null,null,null,null);
        if(c.getCount()>0){
            List<FaceBean> data = new ArrayList<>();
            while (c.moveToNext()){
                FaceBean faceBean = new FaceBean();
                faceBean.id = c.getInt(c.getColumnIndex("id"));
                faceBean.byUserid = c.getInt(c.getColumnIndex("byUserid"));
                faceBean.roomid = c.getInt(c.getColumnIndex("roomid"));
                faceBean.lockid = c.getInt(c.getColumnIndex("lockid"));
                faceBean.imageUrl = c.getString(c.getColumnIndex("imageUrl"));
                faceBean.dataUrl = c.getString(c.getColumnIndex("dataUrl"));
                faceBean.faceName = c.getString(c.getColumnIndex("faceName"));
                faceBean.phone = c.getString(c.getColumnIndex("phone"));
                faceBean.communityId = c.getInt(c.getColumnIndex("communityId"));
                faceBean.creDate = c.getString(c.getColumnIndex("creDate"));
                faceBean.loadName = c.getString(c.getColumnIndex("loadName"));
                faceBean.loading = c.getInt(c.getColumnIndex("loading"));
                data.add(faceBean);
            }
            return data;
        }else{
            return null;
        }
    }


    private boolean checkDataByID(int id){
        Cursor c = getReadableDatabase().query(TABLE,new String[]{"id"},"id = ?",new String[]{id+""},null,null,null);
        return c.getCount()>0;
    }

    private void insertData(FaceBean bean){
        ContentValues cv = new ContentValues();
        cv.put("id",bean.id);
        cv.put("byUserid",bean.byUserid);
        cv.put("roomid",bean.roomid);
        cv.put("lockid",bean.lockid);
        cv.put("imageUrl",bean.imageUrl);
        cv.put("dataUrl",bean.dataUrl);
        cv.put("faceName",bean.faceName);
        cv.put("phone",bean.phone);
        cv.put("communityId",bean.communityId);
        cv.put("creDate",bean.creDate);
        cv.put("loading",0);
        getWritableDatabase().insert(TABLE,null,cv);
    }

    public void registerFace(String result){
        try{
            JSONObject j = new JSONObject(result);
            int code = j.has("code")?j.getInt("code"):1;
            if(code == 0){
                List<FaceBean> resultArray = j.has("data")?handResult(j.getJSONArray("data")):null;
                if(resultArray!=null && resultArray.size()>0){
                    for(int i=0;i<resultArray.size();i++){
                        //HttpApi.i("xiao_","服务器数据："+resultArray.get(i).faceName);
                    }
                }else{
                    //HttpApi.i("xiao_","服务器数据 = null");
                }
                List<FaceBean> dbArray = getAllFace();
                if(dbArray!=null && dbArray.size()>0){
                    for(int i=0;i<dbArray.size();i++){
                       //HttpApi.i("xiao_","本地数据库数据："+dbArray.get(i).faceName);
                    }
                }else{
                    //HttpApi.i("xiao_","本地数据库数据 = null");
                }
                List<FaceBean> deleteArray = checkDelete(dbArray,resultArray);
                if(deleteArray!=null && deleteArray.size()>0){
                    for(int i=0;i<deleteArray.size();i++){
                        //HttpApi.i("xiao_","需要删除的数据："+deleteArray.get(i).faceName);
                    }
                }else{
                    //HttpApi.i("xiao_","需要删除的数据= null");
                }
                List<FaceBean> addArray = checkAdd(dbArray,resultArray);
                if(addArray!=null && addArray.size()>0){
                    for(int i=0;i<addArray.size();i++){
                        //HttpApi.i("xiao_","需要增加的数据："+addArray.get(i).faceName);
                    }
                }else{
                    //HttpApi.i("xiao_","需要增加的数据= null");
                }

                //删除
                if(deleteArray!=null){
                    for(int i=0;i<deleteArray.size();i++){
                        ArcsoftManager.getInstance().mFaceDB.delete(deleteArray.get(i).loadName);
                        deleteByID(deleteArray.get(i).id);
                    }
                    //HttpApi.i("删除条目数："+deleteArray.size());
                }
                //增加
                if(addArray!=null){
                    for(int i=0;i<addArray.size();i++){
                        insertData(addArray.get(i));
                    }
                    //HttpApi.i("增加条目数："+addArray.size());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void deleteByID(int id){
        String whereClause = "id=?";
        String[] whereArgs = {String.valueOf(id)};
        getWritableDatabase().delete(TABLE,whereClause,whereArgs);
    }

    private List<FaceBean> handResult(JSONArray jsonArray){
        if(jsonArray == null || jsonArray.length()<=0){
            return null;
        }
        try {
            List<FaceBean> array = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                FaceBean bean = new FaceBean();
                bean.id = jsonArray.getJSONObject(i).getInt("id");
                bean.byUserid = jsonArray.getJSONObject(i).getInt("byUserid");
                bean.roomid = jsonArray.getJSONObject(i).getInt("roomid");
                bean.lockid = jsonArray.getJSONObject(i).getInt("lockid");
                bean.communityId = jsonArray.getJSONObject(i).getInt("communityId");
                bean.imageUrl = jsonArray.getJSONObject(i).getString("imageUrl");
                bean.dataUrl = jsonArray.getJSONObject(i).getString("dataUrl");
                bean.faceName = jsonArray.getJSONObject(i).getString("faceName");
                bean.phone = jsonArray.getJSONObject(i).getString("phone");
                bean.creDate = jsonArray.getJSONObject(i).getString("creDate");
                array.add(bean);
            }
            return array;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    private List<FaceBean> checkDelete(List<FaceBean> dbArray,List<FaceBean> resultArry){
        if(dbArray == null || dbArray.size()<=0){
            return null;
        }
        if(resultArry == null || resultArry.size()<=0){
            return dbArray;
        }
        if(dbArray!=null && dbArray.size()>0
                && resultArry!=null && resultArry.size()>0){
            List<FaceBean> deleteArray = new ArrayList<>();
            for(int a = 0;a<dbArray.size();a++){
                boolean isDelete = true;
                for(int b = 0;b<resultArry.size();b++){
                    if(dbArray.get(a).id == resultArry.get(b).id){
                        isDelete = false;
                        break;
                    }
                }
                if(isDelete){
                    deleteArray.add(dbArray.get(a));
                }
            }
            return deleteArray;
        }
        return null;
    }

    private static List<FaceBean> checkAdd(List<FaceBean> dbArray,List<FaceBean> resultArry){
        if(dbArray == null || dbArray.size()<=0) {
            return resultArry;
        }
        if(resultArry == null || resultArry.size()<=0){
            return null;
        }
        List<FaceBean> data = new ArrayList<>();
        for(int a = 0;a<resultArry.size();a++) {
            boolean isAdd = true;
            for(int b=0;b<dbArray.size();b++) {
                if(resultArry.get(a).id == dbArray.get(b).id) {
                    isAdd = false;
                    break;
                }
            }
            if(isAdd) {
                data.add(resultArry.get(a));
            }
        }
        return data;
    }

}
