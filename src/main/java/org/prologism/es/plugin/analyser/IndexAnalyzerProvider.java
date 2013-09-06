package org.prologism.es.plugin.analyser;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractIndexAnalyzerProvider;
import org.elasticsearch.index.analysis.AnalysisService;
import org.elasticsearch.index.analysis.TokenFilterFactory;
import org.elasticsearch.index.settings.IndexSettings;

public class IndexAnalyzerProvider extends AbstractIndexAnalyzerProvider<TimestampAnalyzer> {

    private final TimestampAnalyzer analyzer;
    private final Settings analyzerSettings;
    
    @Inject
	public IndexAnalyzerProvider(Index index, @IndexSettings Settings indexSettings, Environment env, @Assisted String name, @Assisted Settings settings) {
        super(index, indexSettings, name, settings);
        String timeFieldName = settings.get("date_field_name", TimestampAnalyzer.DEFAULT_TIME_FIELD_NAME);
        this.analyzerSettings = settings;
        this.analyzer = new TimestampAnalyzer();
        this.analyzer.setTimeFieldName(timeFieldName);
    }

    public void build(AnalysisService analysisService) {
        String[] tokenFilterNames = analyzerSettings.getAsArray("filter");
        List<TokenFilterFactory> tokenFilters = newArrayList();
        for (String tokenFilterName : tokenFilterNames) {
        	
            TokenFilterFactory tokenFilter = analysisService.tokenFilter(tokenFilterName);
            if (tokenFilter == null) {
                throw new IllegalArgumentException("Custom Analyzer [" + name() + "] failed to find filter under name [" + tokenFilterName + "]");
            }
            tokenFilters.add(tokenFilter);
        }
    }
    
	@Override
	public TimestampAnalyzer get() {
		return analyzer;
	}

}
