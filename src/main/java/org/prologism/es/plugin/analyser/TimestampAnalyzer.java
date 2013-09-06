package org.prologism.es.plugin.analyser;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseTokenizer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.util.Version;

public class TimestampAnalyzer extends Analyzer {

	protected static final String DEFAULT_TIME_FIELD_NAME = "_timestamp";
	private String timeFieldName = DEFAULT_TIME_FIELD_NAME;

	
	public TokenStream tokenStream(String fieldName, Reader reader) {
		TokenStream out = null;
		if(fieldName.equals(timeFieldName)){
			//CUSTOM TOKENIZER POSSIBLE
			out = new EventTokenFilter(new LowerCaseTokenizer(Version.LUCENE_31,reader));
		}
		else{
			out = new  LowerCaseTokenizer(Version.LUCENE_31,reader);
		}
		return out;
	}

	protected void setTimeFieldName(String timeFieldName) {
		this.timeFieldName = timeFieldName;
	}

//	@Override
//	protected TokenStreamComponents createComponents(String fieldName,
//			Reader reader) {
//		// TODO Auto-generated method stub
//		return null;
//	}

}
