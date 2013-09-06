package org.prologism.es.plugin;

import org.elasticsearch.index.analysis.AnalysisModule;
import org.elasticsearch.plugins.AbstractPlugin;
import org.elasticsearch.river.RiversModule;
import org.elasticsearch.script.ScriptModule;
import org.prologism.es.plugin.analyser.EllapsedTimeProcessor;
import org.prologism.es.plugin.river.CloneRiver;
import org.prologism.es.plugin.river.CloneRiverModule;
import org.prologism.es.plugin.script.MyScriptFactory;


/**
 * This class is instantiated when Elasticsearch loads the plugin for the
 * first time. If you change the name of this plugin, make sure to update
 * src/main/resources/es-plugin.properties file that points to this class.
 */
public class MyPluginRegister extends AbstractPlugin {

	
	public static final String SCRIPT_NAME ="my_script";
	
    /**
     * The name of the plugin.
     * <p/>
     * This name will be used by elasticsearch in the log file to refer to this plugin.
     *
     * @return plugin name.
     */
    @Override
    public String name() {
        return "native-calculate-elpased-time";
    }

    /**
     * The description of the plugin.
     *
     * @return plugin description
     */
    @Override
    public String description() {
        return "Calcualte Elapsed Time in POC_PRO log line";
    }

    /**
     * Register the Script to Elasticsearch node
     *
     * @param module
     */
    public void onModule(ScriptModule module) {
        // Register each script that we defined in this plugin
        module.registerScript(SCRIPT_NAME, MyScriptFactory.class);
    }
    
    /**
     * Register the Anlyzer to Elasticsearch node
     *
     * @param module
     */
    public void onModule(AnalysisModule module) {
    	//module.addAnalyzer("new-analyzer", NewAnalyzerProvider.class);
    	module.addProcessor(new EllapsedTimeProcessor());
    }
    
    /**
     * Register the River to Elasticsearch node
     *
     * @param module
     */
    public void onModule(RiversModule module) {
        module.registerRiver(CloneRiver.TYPE, CloneRiverModule.class);
    }

}
