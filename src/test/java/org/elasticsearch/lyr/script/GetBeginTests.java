package org.elasticsearch.lyr.script;

import static org.elasticsearch.index.query.FilterBuilders.scriptFilter;
import static org.elasticsearch.index.query.QueryBuilders.filteredQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

import java.io.IOException;
import java.util.Date;

import org.elasticsearch.action.admin.indices.settings.UpdateSettingsRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.lyr.analyser.EllapsedTimeProcessor;
import org.elasticsearch.node.Node;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.tlrx.elasticsearch.test.EsSetup;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchClient;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchSetting;
import com.github.tlrx.elasticsearch.test.request.CreateIndex;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;

@RunWith(ElasticsearchRunner.class)
public class GetBeginTests {

	private final String indexName = "test";

	@ElasticsearchNode(name = "node1", settings = {
			@ElasticsearchSetting(name = "http.enabled", value = "true"),
			@ElasticsearchSetting(name = "log", value = "debug") })
	Node node;

	@ElasticsearchClient(nodeName = "node1")
	Client client1;

	@Before
	public void setUp() {
		EsSetup.deleteAll().execute(client1);
		CreateIndex cIndex = EsSetup.createIndex(indexName)
				.withMapping("line", EsSetup.fromClassPath("typeline.json"))
				.withSettings(EsSetup.fromClassPath("settingsline.json"))
				.withData(EsSetup.fromClassPath("line-simple.bulk.json"));
		cIndex.execute(client1);
	}

	@Test
	public void test() throws IOException {
		// Index a begin and a End
//		client1.prepareIndex(indexName, "line", "1")
//				.setSource(
//						XContentFactory.jsonBuilder().startObject()
//								.field("type_evt", "BEGIN")
//								.field("axe_date", new Date()).endObject())
//				.execute().actionGet();
//
//		client1.prepareIndex(indexName, "line", "2")
//				.setSource(
//						XContentFactory.jsonBuilder().startObject()
//								.field("type_evt", "END")
//								.field("axe_date", new Date()).endObject())
//				.execute().actionGet();
//
//		client1.admin().indices().prepareRefresh(indexName).execute()
//				.actionGet();

		SearchRequestBuilder srb1 = node
				.client()
				.prepareSearch(indexName)
				.setQuery(
						filteredQuery(matchAllQuery(),
								scriptFilter("get_begin").lang("native")
										.addParam("field", "type_evt")));
		SearchResponse sResp = srb1.execute().actionGet();
		System.out.println(srb1.toString());
	}

    @After 
    public void tearDown() throws Exception {
        // This will stop and clean the local node
        EsSetup setup = new EsSetup(client1);
        setup.terminate();
    }
	
}
