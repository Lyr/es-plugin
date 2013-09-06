package org.prologism.es.plugin.analyser;

import org.elasticsearch.index.analysis.AnalysisModule.AnalysisBinderProcessor;
import org.prologism.es.plugin.analyser.tokenfilter.ConcatenateTokenFilterFactory;

public class EllapsedTimeProcessor extends AnalysisBinderProcessor {

	//This string is used to register the analyzer plugin name which be used 
	//in the config file like ("type":"poc_pro")
	public static final String KEY_ANALYZER_PLUGIN =  "poc_pro_analyzer";
	public static final String KEY_TOKENFILTER_PLUGIN =  "poc_pro_tkfilter";
	
	@Override
	public void processAnalyzers(AnalyzersBindings analyzersBindings) {
		analyzersBindings.processAnalyzer(KEY_ANALYZER_PLUGIN, IndexAnalyzerProvider.class);
	}

	@Override
	public void processTokenFilters(TokenFiltersBindings tokenFiltersBindings) {
		tokenFiltersBindings.processTokenFilter(KEY_TOKENFILTER_PLUGIN, ConcatenateTokenFilterFactory.class);
	}

}
