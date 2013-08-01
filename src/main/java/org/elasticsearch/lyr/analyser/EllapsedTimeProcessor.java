package org.elasticsearch.lyr.analyser;

import org.elasticsearch.index.analysis.AnalysisModule.AnalysisBinderProcessor;

public class EllapsedTimeProcessor extends AnalysisBinderProcessor {


	public static final String KEY_PLUGIN =  "poc_lyr";
	
	@Override
	public void processAnalyzers(AnalyzersBindings analyzersBindings) {
		analyzersBindings.processAnalyzer(KEY_PLUGIN, IndexAnalyzerProvider.class);
		super.processAnalyzers(analyzersBindings);
	}

}
