package org.elasticsearch.index.analysis;

import org.apache.lucene.analysis.Tokenizer;
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
    private Configuration configuration;

    public UpIkTokenizerFactory(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        super(indexSettings, name, settings);
        configuration = new Configuration(env, settings);

	  /*
        动态从mysql中获取停顿词信息
	    1、从setting中获取数据库信息，并连接数据库查询停顿词
	    2、定时查询，并追加听停词
	   */

        String dbUrl = settings.get("dbUrl");
        String userName = settings.get("userName");
        String userPwd = settings.get("userPwd");
        Integer flushTime = settings.getAsInt("flushTime", 60);
        String extField = settings.get("extField");
        String stopField = settings.get("stopField");

        DBHelper.dbUrl = dbUrl;
        DBHelper.userName = userName;
        DBHelper.userPwd = userPwd;

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        DBRunnable dbRunnable = new DBRunnable();
        dbRunnable.setExtField(extField);
        dbRunnable.setStopFiled(stopField);
        scheduledExecutorService.scheduleAtFixedRate(dbRunnable, 0, flushTime, TimeUnit.SECONDS);
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
