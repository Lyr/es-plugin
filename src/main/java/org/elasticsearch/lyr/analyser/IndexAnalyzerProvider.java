package org.elasticsearch.lyr.analyser;

import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractIndexAnalyzerProvider;
import org.elasticsearch.index.settings.IndexSettings;

public class IndexAnalyzerProvider extends AbstractIndexAnalyzerProvider<TimestampAnalyzer> {

    private final TimestampAnalyzer analyzer;
    @Inject
	public IndexAnalyzerProvider(Index index, @IndexSettings Settings indexSettings, Environment env, @Assisted String name, @Assisted Settings settings) {
        super(index, indexSettings, name, settings);
        analyzer = new TimestampAnalyzer();
	}

	@Override
	public TimestampAnalyzer get() {
		return analyzer;
	}

}
