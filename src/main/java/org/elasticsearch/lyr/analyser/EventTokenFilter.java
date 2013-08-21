package org.elasticsearch.lyr.analyser;

import java.io.IOException;
import java.util.Iterator;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.util.Attribute;

public class EventTokenFilter extends TokenFilter {

	protected EventTokenFilter(TokenStream input) {
		super(input);
		 Iterator<Class<? extends Attribute>> iterator = input.getAttributeClassesIterator();
		 while(iterator.hasNext()){
			 Class<? extends Attribute> atb = iterator.next();
			 String nameAtb = atb.getName();
			 if(nameAtb.equals("date"))
				 break;
		 }
	}

	@Override
	public boolean incrementToken() throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

}
