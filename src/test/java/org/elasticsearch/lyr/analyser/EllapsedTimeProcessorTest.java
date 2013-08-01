package org.elasticsearch.lyr.analyser;

import static org.elasticsearch.index.query.FilterBuilders.scriptFilter;
import static org.elasticsearch.index.query.QueryBuilders.filteredQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

import java.io.IOException;
import java.util.Date;

import org.elasticsearch.ElasticSearchException;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.lyr.AbstractESTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testng.AssertJUnit;

import com.github.tlrx.elasticsearch.test.EsSetup;
import com.github.tlrx.elasticsearch.test.request.CreateIndex;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;

@RunWith(ElasticsearchRunner.class)
public class EllapsedTimeProcessorTest extends AbstractESTest{

	@Before
	public void setUp() {
		super.setUp();
		CreateIndex cIndex = EsSetup.createIndex(indexName)
				.withMapping("line", EsSetup.fromClassPath("typeline.json"))
				.withSettings(EsSetup.fromClassPath("settingsline.json"))
				.withData(EsSetup.fromClassPath("line-simple.bulk.json"));
		cIndex.execute(client1);
	}

	@Test
	public void testCallAnalyzer() throws ElasticSearchException, IOException{
		
		//verification de l'ajout du champ
		SearchRequestBuilder srb1 = client1
				.prepareSearch(indexName)
				.setQuery(
						filteredQuery(matchAllQuery(),
								scriptFilter("get_begin").lang("native")
										.addParam("field", "type_evt")));
		SearchResponse sResp = srb1.execute().actionGet();
		AssertJUnit.assertNotNull(sResp);
	}
}
