package org.prologism.es.plugin.analyser.tokenfilter;

import org.apache.lucene.analysis.TokenStream;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;
import org.elasticsearch.index.settings.IndexSettings;

public class ConcatenateTokenFilterFactory extends AbstractTokenFilterFactory {
	
	public static final String DEFAULT_TOKEN_SEPARATOR_NAME = "token_separator";
    private String tokenSeparator = null;
    
    @Inject 
    public ConcatenateTokenFilterFactory(Index index, @IndexSettings Settings indexSettings, @Assisted String name, @Assisted Settings settings) {
        super(index, indexSettings, name, settings);
        // the token_separator is defined in the ES configuration file
        tokenSeparator = settings.get(DEFAULT_TOKEN_SEPARATOR_NAME);
    }

    @Override 
    public TokenStream create(TokenStream tokenStream) {
        return new ConcatTokenFilter(tokenStream, tokenSeparator);
    }
}
