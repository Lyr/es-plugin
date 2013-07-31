package org.elasticsearch.lyr.analyser;

import org.elasticsearch.index.analysis.AnalysisModule.AnalysisBinderProcessor;

public class EllapsedTimeProcessor extends AnalysisBinderProcessor {

	@Override
	public void processAnalyzers(AnalyzersBindings analyzersBindings) {
		analyzersBindings.processAnalyzer("poc_sg", IndexAnalyzerProvider.class);
		super.processAnalyzers(analyzersBindings);
	}

}
