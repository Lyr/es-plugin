package org.elasticsearch.lyr.analyser.tokenfilter;

import java.io.IOException;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public class ConcatTokenFilter extends TokenFilter {

	private final static String DEFAULT_TOKEN_SEPARATOR = "";

    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private String tokenSeparator = null;
    private StringBuilder builder = new StringBuilder();
	
	protected ConcatTokenFilter(TokenStream input) {
		super(input);
		this.tokenSeparator = DEFAULT_TOKEN_SEPARATOR;
	}
    
	public ConcatTokenFilter(TokenStream input, String tokenSeparator2) {
		super(input);
		this.tokenSeparator = tokenSeparator2;
	}

	@Override
	public boolean incrementToken() throws IOException {
        boolean result = false;
        builder.setLength(0);
        while (input.incrementToken()) {
            if (builder.length()>0) {
                // append the token separator
                builder.append(tokenSeparator);
            }
            // append the term of the current token
            builder.append(termAtt.buffer(), 0, termAtt.length());
        }
        if (builder.length()>0) {
            termAtt.setEmpty().append(builder);
            result = true;
        }
        return result;
	}

}
