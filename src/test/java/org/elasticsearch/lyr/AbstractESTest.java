package org.elasticsearch.lyr;

import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;
import org.junit.After;
import org.junit.Before;

import com.github.tlrx.elasticsearch.test.EsSetup;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchClient;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchSetting;

public abstract class AbstractESTest {

	protected final String indexName = "test";

	@ElasticsearchNode(name = "node1", settings = {
			@ElasticsearchSetting(name = "http.enabled", value = "true"),
			@ElasticsearchSetting(name = "log", value = "debug") })
	private Node node;

	@ElasticsearchClient(nodeName = "node1")
	protected Client client1;
	
	@Before
	public void setUp() {
		EsSetup.deleteAll().execute(client1);
	}
	

    @After 
    public void tearDown() throws Exception {
        // This will stop and clean the local node
        EsSetup setup = new EsSetup(client1);
        setup.terminate();
    }
	
}
