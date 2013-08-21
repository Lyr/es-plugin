import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import java.io.StringReader;

// Cache analyzer for further use
cachedAnalyzer=(isdef cachedAnalyzer)?cachedAnalyzer:doc.mapperService().documentMapper(doc._type.value).mappers().indexAnalyzer();

terms=[];
// Get value from Fields Lookup
//val=_fields[field].values;

// Get value from Source Lookup
val=_source[field];

if(val != null) {
  tokenStream=cachedAnalyzer.tokenStream(field, new StringReader(val)); 
  CharTermAttribute termAttribute = tokenStream.addAttribute(CharTermAttribute); 
  while(tokenStream.incrementToken()) { 
    terms.add(termAttribute.toString())
  }; 
  tokenStream.close(); 
} 
terms