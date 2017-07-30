package org.elasticsearch.index.analysis;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.common.logging.Loggers;
import org.wltea.analyzer.db.DBHelper;
import org.wltea.analyzer.dic.Dictionary;

import java.util.Arrays;
import java.util.List;

/**
 * Created by wky77 on 2017/7/30.
 */
public class DBRunnable implements Runnable {
    public static final Logger logger = Loggers.getLogger(DBRunnable.class);

    private String extField;
    private String stopField;

    @Override
    public void run() {
        Dictionary dic = Dictionary.getSingleton();
        DBHelper dbHelper = new DBHelper();

        if (extField != null && !"".equals(extField)) {
            logger.warn("开始从mysql中加载扩展词");
            String extWords = dbHelper.getKey(extField);
            if (extWords != null && !"".equals(extWords)) {
                List<String> extList = Arrays.asList(extWords.split(","));
                dic.addWords(extList);
            }
            logger.warn("完成从mysql中加载扩展词");
        }

        if (stopField != null && !"".equals(stopField)) {
            logger.warn("开始从mysql中加载停止词");
            String stopWords = dbHelper.getKey(stopField);
            if (stopWords != null && !"".equals(stopWords)) {
                List<String> stopList = Arrays.asList(stopWords.split(","));
                dic.addStopWords(stopList);
            }
            logger.warn("完成从mysql中加载停止词");
        }

    }


    public String getExtField() {
        return extField;
    }

    public void setExtField(String extField) {
        this.extField = extField;
    }

    public String getStopField() {
        return stopField;
    }

    public void setStopField(String stopField) {
        this.stopField = stopField;
    }
}
