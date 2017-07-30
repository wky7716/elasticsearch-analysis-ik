package org.elasticsearch.index.analysis;

import org.wltea.analyzer.db.DBHelper;
import org.wltea.analyzer.dic.Dictionary;

import java.util.Arrays;
import java.util.List;

/**
 * Created by wky77 on 2017/7/30.
 */
public class DBRunnable implements Runnable {

    private String extField;
    private String stopFiled;

    @Override
    public void run() {
        Dictionary dic = Dictionary.getSingleton();

        DBHelper dbHelper = new DBHelper();
        String extWords = dbHelper.getKey(extField);
        List<String> extList = Arrays.asList(extWords.split(","));
        dic.addWords(extList);

        String stopWords = dbHelper.getKey(stopFiled);
        List<String> stopList = Arrays.asList(stopWords.split(","));
        dic.addStopWords(stopList);
    }


    public static void main(String[] args) {

        DBHelper dbHelper = new DBHelper();
        DBHelper.dbUrl = "jdbc:mysql://192.168.3.250:3306/di_v1";
        DBHelper.userName = "admin";
        DBHelper.userPwd = "123456";
        String extWords = dbHelper.getKey("extword");
        System.out.println(extWords);
        String stopWords = dbHelper.getKey("stopword");
        System.out.println(stopWords);

    }

    public String getExtField() {
        return extField;
    }

    public void setExtField(String extField) {
        this.extField = extField;
    }

    public String getStopFiled() {
        return stopFiled;
    }

    public void setStopFiled(String stopFiled) {
        this.stopFiled = stopFiled;
    }
}
