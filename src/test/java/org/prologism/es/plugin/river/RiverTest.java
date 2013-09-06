package org.prologism.es.plugin.river;

import static org.elasticsearch.index.query.FilterBuilders.scriptFilter;
import static org.elasticsearch.index.query.QueryBuilders.filteredQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.TypeFilterBuilder;
import org.junit.Assert;
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
public class RiverTest extends AbstractESTest{
	
	private final String indexNameClone = "testclone";
	private final String indexNameSource = "test";
	protected final String typeNameClone = "clone";
	
	@Before
	public void setUp() {
		super.setUp();
		//creation de data
		CreateIndex cIndex = EsSetup.createIndex(indexNameSource)
				.withMapping("line", EsSetup.fromClassPath("typeline.json"))
				.withSettings(EsSetup.fromClassPath("settingsline.json"))
				.withData(EsSetup.fromClassPath(fileBulkName));
		cIndex.execute(client1);
		//creation du river
		client1.prepareIndex("_river", indexNameClone, "_meta").setSource("{ \"type\":\"clone\",\"index\":{\"name\":\""+indexNameSource+"\",\"bulk_timeout\":100000,\"type\": \""+typeNameClone+"\", \"typeToClone\": \""+typeName+"\"} }").execute().actionGet();
	}

	@Test
	public void testInsertWithRiver() {
		//client1.prepareBulk().add(new IndexRequest(indexNameSource, typeName).source(EsSetup.fromClassPath(AbstractESTest.fileBulkName).toString())).execute();
		try {
			Thread.sleep(50000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Assert.assertNotNull(client1);
		//get doc into the new index
		SearchRequestBuilder srb1 = client1
				.prepareSearch()
				.setQuery(
						filteredQuery(matchAllQuery(),
								FilterBuilders.typeFilter(typeNameClone)));
		SearchResponse sResp = srb1.execute().actionGet();
		AssertJUnit.assertNotNull(sResp);
		AssertJUnit.assertNotNull(sResp.getHits());
		AssertJUnit.assertEquals(2, sResp.getHits().totalHits());
	}

}
