package org.prologism.es.plugin.script;

import static org.elasticsearch.index.query.FilterBuilders.scriptFilter;
import static org.elasticsearch.index.query.QueryBuilders.filteredQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

import java.io.IOException;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.prologism.es.plugin.AbstractESTest;
import org.prologism.es.plugin.MyPluginRegister;
import org.testng.AssertJUnit;

import com.github.tlrx.elasticsearch.test.EsSetup;
import com.github.tlrx.elasticsearch.test.request.CreateIndex;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;

@RunWith(ElasticsearchRunner.class)
public class MyScriptTests extends AbstractESTest{

	@Before
	public void setUp() {
		super.setUp();
		CreateIndex cIndex = EsSetup.createIndex(indexName)
				.withMapping("line", EsSetup.fromClassPath("typeline.json"))
				.withSettings(EsSetup.fromClassPath("settingsline.json"))
				.withData(EsSetup.fromClassPath(fileBulkName));
		cIndex.execute(client1);
	}

	@Test
	public void testScriptOnSearch() throws IOException {
		//the line after make this request :
		//{
		//		  "query" : {
		//	    "filtered" : {
		//	      "query" : {
		//	        "match_all" : { }
		//	      },
		//	      "filter" : {
		//	        "script" : {
		//	          "script" : "my_script",
		//	          "params" : {
		//	            "field" : "type_evt"
		//	          },
		//	          "lang" : "native"
		//	        }
		//	      }
		//	    }
		//	  }
		//	}
		SearchRequestBuilder srb1 = client1
				.prepareSearch(indexName)
				.setQuery(
						filteredQuery(matchAllQuery(),
								scriptFilter(MyPluginRegister.SCRIPT_NAME).lang("native")
										.addParam("field", "type_evt")));
		SearchResponse sResp = srb1.execute().actionGet();
		AssertJUnit.assertNotNull(sResp);
		AssertJUnit.assertNotNull(sResp.getHits());
		AssertJUnit.assertEquals(2, sResp.getHits().totalHits());
	}

	
}
