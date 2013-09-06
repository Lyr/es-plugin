package org.prologism.es.plugin.river;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.joda.time.DateTime;
import org.elasticsearch.common.util.concurrent.EsExecutors;
import org.elasticsearch.common.xcontent.support.XContentMapValues;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.RangeFilterBuilder;
import org.elasticsearch.river.AbstractRiverComponent;
import org.elasticsearch.river.River;
import org.elasticsearch.river.RiverIndexName;
import org.elasticsearch.river.RiverName;
import org.elasticsearch.river.RiverSettings;
import org.elasticsearch.script.ScriptService;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;

public class CloneRiver extends AbstractRiverComponent implements River {

	public static final String TYPE = "clone";
	private volatile Thread indexerThread;
	private volatile boolean closed;
	private volatile BulkProcessor bulkProcessor;

	private final Client client;
	private final String riverIndexName;
	private static final String TIMESTAMP_NAME = "@timestamp";

	private final String indexName;
	private final String typeName;
	private String typeNameToClone;
	private final int bulkSize;
	private final int updateRate = 10000;// utilise pour le sleep en millisecond
	private final String id_doc = "uid";

	private DateTime lastDateToGet;

	@SuppressWarnings({ "unchecked" })
	@Inject
	public CloneRiver(RiverName riverName, RiverSettings settings,
			@RiverIndexName String riverIndexName, Client client,
			ScriptService scriptService) {
		super(riverName, settings);
		this.riverIndexName = riverIndexName;
		this.client = client;
		if (settings.settings().containsKey("index")) {
			Map<String, Object> indexSettings = (Map<String, Object>) settings
					.settings().get("index");
			indexName = XContentMapValues.nodeStringValue(
					indexSettings.get("name"), TYPE);
			typeName = XContentMapValues.nodeStringValue(
					indexSettings.get("type"), TYPE);
			typeNameToClone = XContentMapValues.nodeStringValue(
					indexSettings.get("typeToClone"), null);
			bulkSize = XContentMapValues.nodeIntegerValue(
					indexSettings.get("bulk_size"), 100);
			if (indexSettings.containsKey("fromDate")) {

			} else {
				// get the last month date
				Calendar aCalendar = Calendar.getInstance();
				aCalendar.add(Calendar.MONTH, -1);
				// set DATE to 1, so first date of previous month
				aCalendar.set(Calendar.DATE, 1);
				Date firstDateOfPreviousMonth = aCalendar.getTime();
				lastDateToGet = new DateTime(firstDateOfPreviousMonth.getTime());
			}
		} else {
			indexName = "elasticsearch";
			typeName = "clone";
			bulkSize = 100;
		}
	}

	@Override
	public void start() {
		// Creating bulk processor
		this.bulkProcessor = BulkProcessor
				.builder(client, new BulkProcessor.Listener() {
					@Override
					public void beforeBulk(long executionId, BulkRequest request) {
						logger.debug(
								"Going to execute new bulk composed of {} actions",
								request.numberOfActions());
					}

					@Override
					public void afterBulk(long executionId,
							BulkRequest request, BulkResponse response) {
						logger.debug("Executed bulk composed of {} actions",
								request.numberOfActions());
						if (response.hasFailures()) {
							logger.warn(
									"There was failures while executing bulk",
									response.buildFailureMessage());
							if (logger.isDebugEnabled()) {
								for (BulkItemResponse item : response.items()) {
									if (item.isFailed()) {
										logger.debug(
												"Error for {}/{}/{} for {} operation: {}",
												item.getIndex(),
												item.getType(), item.getId(),
												item.opType(),
												item.getFailureMessage());
									}
								}
							}
						}
					}

					@Override
					public void afterBulk(long executionId,
							BulkRequest request, Throwable failure) {
						logger.warn("Error executing bulk", failure);
					}
				}).setBulkActions(bulkSize).build();
		indexerThread = EsExecutors.daemonThreadFactory(
				settings.globalSettings(), "clone_river_indexer").newThread(
				new Indexer());
		indexerThread.start();

	}

	@Override
	public void close() {
		if (closed) {
			return;
		}
		logger.info("closing clone river");
		indexerThread.interrupt();
		closed = true;
	}

	private class Indexer implements Runnable {

		@Override
		public void run() {
			while (true) {
				if (closed) {
					return;
				}
				// get the last doc indexed after the last get of the river
				RangeFilterBuilder dateRangeFilter = FilterBuilders
						.rangeFilter(TIMESTAMP_NAME);
				dateRangeFilter.from(lastDateToGet).to(new DateTime());
				SearchResponse response = client
						.prepareSearch(indexName)
						.setTypes(typeNameToClone)
						.setFilter(
								FilterBuilders.andFilter(dateRangeFilter)
								// .add(FilterBuilders.termFilter("type_evt",
								// "BEGIN"))
										.add(FilterBuilders
												.existsFilter(TIMESTAMP_NAME)))
						.setFrom(0)
						.setSize(bulkSize)
						// .setNoFields()
						.addSort(id_doc, SortOrder.DESC)
						.addSort(TIMESTAMP_NAME, SortOrder.DESC).execute()
						.actionGet();
				Iterator<SearchHit> iterator = response.getHits().iterator();
				boolean first = true;
				while (iterator.hasNext()) {
					SearchHit searchHit = (SearchHit) iterator.next();// searchHit.getSortValues();
					DateTime datetime = (DateTime) searchHit.getSource().get(
							TIMESTAMP_NAME);
					if (first) {
						first = false;
						// get the lastdate
						lastDateToGet = datetime;
					}
					Map<String, String> contentSource = new HashMap<String, String>();
					contentSource.put("idSource", searchHit.getId());
					bulkProcessor.add(new IndexRequest(riverIndexName, typeName)
							.source(contentSource));
				}
				client.admin().indices().prepareRefresh(riverIndexName)
						.execute().actionGet();

				try {
					Thread.sleep(updateRate);
					continue;
				} catch (InterruptedException e1) {
					if (closed) {
						return;
					}
				}
			}
		}
	}


}
