package org.elasticsearch.lyr.script;

import static org.elasticsearch.index.query.FilterBuilders.scriptFilter;
import static org.elasticsearch.index.query.QueryBuilders.filteredQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

import java.io.IOException;
import java.util.Date;

import org.elasticsearch.action.admin.indices.close.CloseIndexRequest;
import org.elasticsearch.action.admin.indices.open.OpenIndexRequest;
import org.elasticsearch.action.admin.indices.settings.UpdateSettingsRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.node.Node;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchClient;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchSetting;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;

@RunWith(ElasticsearchRunner.class)
public class GetBeginTests {

	@ElasticsearchNode(name = "node1",settings = {
			@ElasticsearchSetting(name = "http.enabled", value = "true"),
			@ElasticsearchSetting(name = "log", value = "debug")})
	Node node;

	@ElasticsearchClient(nodeName = "node1")
	Client client1;

	@Test
	public void test() throws IOException {
		
		// Create a new index
		String mapping = XContentFactory.jsonBuilder().startObject()
				.startObject("line").startObject("properties")
				.startObject("type_evt").field("type", "string").endObject()
				.startObject("axe_date").field("type", "date").endObject()
				.endObject().endObject()
				.endObject()
				.string();
		client1.admin().indices().prepareCreate("test")
				.addMapping("line", mapping).execute().actionGet();
		client1.admin().indices().close(new CloseIndexRequest("test"));
		
		mapping = XContentFactory.jsonBuilder().startObject().startObject("analysis")
		.startObject("analyzer")
			.startObject("poc_sg").field("type", "custom").field("tokenizer","standard")
			.endObject()
		.endObject().string();
		UpdateSettingsRequestBuilder settingsBuilder = client1.admin().indices().prepareUpdateSettings("test");
		settingsBuilder.setSettings(mapping);
		settingsBuilder.execute();
		client1.admin().indices().open(new OpenIndexRequest("test"));
		
		
		// Index a begin and a End
		client1
				.prepareIndex("test", "line", "1")
				.setSource(
						XContentFactory.jsonBuilder().startObject()
								.field("type_evt", "BEGIN")
								.field("axe_date", new Date()).endObject())
				.execute().actionGet();

		client1
				.prepareIndex("test", "line", "2")
				.setSource(
						XContentFactory.jsonBuilder().startObject()
								.field("type_evt", "END")
								.field("axe_date", new Date()).endObject())
				.execute().actionGet();

		client1.admin().indices().prepareRefresh("test").execute()
				.actionGet();

		SearchRequestBuilder srb1 = node.client().prepareSearch("test")
                .setQuery(filteredQuery(matchAllQuery(),
                        scriptFilter("get_begin").lang("native").addParam("field", "type_evt")));
		SearchResponse sResp = srb1.execute().actionGet();
		System.out.println(srb1.toString());
	}

}
