package org.elasticsearch.index.analysis;

import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Tokenizer;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.wltea.analyzer.cfg.Configuration;
import org.wltea.analyzer.db.DBHelper;
import org.wltea.analyzer.lucene.IKTokenizer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class UpIkTokenizerFactory extends AbstractTokenizerFactory {

    public static final Logger logger = Loggers.getLogger(UpIkTokenizerFactory.class);

    private Configuration configuration;

    public UpIkTokenizerFactory(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        super(indexSettings, name, settings);
        configuration = new Configuration(env, settings);
      /*
        动态从mysql中获取停顿词信息
	    1、从setting中获取数据库信息，并连接数据库查询停顿词
	    2、定时查询，并追加听停词
	   */
        Settings iSettings = indexSettings.getSettings();
        String dbUrl = iSettings.get("dbUrl");
        String userName = iSettings.get("userName");
        String userPwd = iSettings.get("userPwd");
        Integer flushTime = iSettings.getAsInt("flushTime", 60);
        String extField = iSettings.get("extField");
        String stopField = iSettings.get("stopField");
        if ((dbUrl != null && !"".equals(dbUrl)) && (DBHelper.dbUrl == null || "".equals(DBHelper.dbUrl))) {


            logger.warn("dbUrl :" + dbUrl);
            DBHelper.dbUrl = dbUrl;
            DBHelper.userName = userName;
            DBHelper.userPwd = userPwd;

            ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            DBRunnable dbRunnable = new DBRunnable();
            dbRunnable.setExtField(extField);
            dbRunnable.setStopField(stopField);
            scheduledExecutorService.scheduleAtFixedRate(dbRunnable, 0, flushTime, TimeUnit.SECONDS);
        }

    }

    public static UpIkTokenizerFactory getIkTokenizerFactory(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        return new UpIkTokenizerFactory(indexSettings, env, name, settings).setSmart(false);
    }

    public static UpIkTokenizerFactory getIkSmartTokenizerFactory(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        return new UpIkTokenizerFactory(indexSettings, env, name, settings).setSmart(true);
    }

    public UpIkTokenizerFactory setSmart(boolean smart) {
        this.configuration.setUseSmart(smart);
        return this;
    }

    @Override
    public Tokenizer create() {
        return new IKTokenizer(configuration);
    }
}
