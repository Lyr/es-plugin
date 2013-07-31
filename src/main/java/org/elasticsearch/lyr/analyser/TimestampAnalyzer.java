package org.elasticsearch.lyr.analyser;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;

public class TimestampAnalyzer extends Analyzer {

	@Override
	public TokenStream tokenStream(String arg0, Reader arg1) {
		// TODO Auto-generated method stub
		return new TokenStream() {
			
			@Override
			public boolean incrementToken() throws IOException {
				// TODO Auto-generated method stub
				return false;
			}
		};
	}


}
