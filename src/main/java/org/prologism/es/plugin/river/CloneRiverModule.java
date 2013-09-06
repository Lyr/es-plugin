package org.prologism.es.plugin.river;

import org.elasticsearch.common.inject.AbstractModule;
import org.elasticsearch.river.River;

public class CloneRiverModule extends AbstractModule {

	@Override
	protected void configure() {
		 bind(River.class).to(CloneRiver.class).asEagerSingleton();
	}

}
