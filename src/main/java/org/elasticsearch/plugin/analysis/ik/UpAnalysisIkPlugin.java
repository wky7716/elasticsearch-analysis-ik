package org.elasticsearch.plugin.analysis.ik;

import org.apache.lucene.analysis.Analyzer;
import org.elasticsearch.common.settings.Setting;
import org.elasticsearch.index.analysis.AnalyzerProvider;
import org.elasticsearch.index.analysis.IkAnalyzerProvider;
import org.elasticsearch.index.analysis.TokenizerFactory;
import org.elasticsearch.index.analysis.UpIkTokenizerFactory;
import org.elasticsearch.indices.analysis.AnalysisModule;
import org.elasticsearch.plugins.AnalysisPlugin;
import org.elasticsearch.plugins.Plugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;


public class UpAnalysisIkPlugin extends Plugin implements AnalysisPlugin {

    public static String PLUGIN_NAME = "analysis-ik";

    @Override
    public Map<String, AnalysisModule.AnalysisProvider<TokenizerFactory>> getTokenizers() {
        Map<String, AnalysisModule.AnalysisProvider<TokenizerFactory>> extra = new HashMap<>();


        extra.put("ik_smart", UpIkTokenizerFactory::getIkSmartTokenizerFactory);
        extra.put("ik_max_word", UpIkTokenizerFactory::getIkTokenizerFactory);

        return extra;
    }

    @Override
    public Map<String, AnalysisModule.AnalysisProvider<AnalyzerProvider<? extends Analyzer>>> getAnalyzers() {
        Map<String, AnalysisModule.AnalysisProvider<AnalyzerProvider<? extends Analyzer>>> extra = new HashMap<>();

        extra.put("ik_smart", IkAnalyzerProvider::getIkSmartAnalyzerProvider);
        extra.put("ik_max_word", IkAnalyzerProvider::getIkAnalyzerProvider);

        return extra;
    }

    @Override
    public List<Setting<?>> getSettings() {
        Setting<String> dbUrl = new Setting<>("dbUrl", "", Function.identity(), Setting.Property.NodeScope);
        Setting<String> userName = new Setting<>("userName", "", Function.identity(), Setting.Property.NodeScope);
        Setting<String> userPwd = new Setting<>("userPwd", "", Function.identity(), Setting.Property.NodeScope);
        Setting<String> extField = new Setting<>("extField", "", Function.identity(), Setting.Property.NodeScope);
        Setting<String> stopField = new Setting<>("stopField", "", Function.identity(), Setting.Property.NodeScope);
        Setting<Integer> flushTime = Setting.intSetting("flushTime", 30, Setting.Property.NodeScope);
        return Arrays.asList(dbUrl, userName, userPwd, extField, stopField, flushTime);
    }

}
