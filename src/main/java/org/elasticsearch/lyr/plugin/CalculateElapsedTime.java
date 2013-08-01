package org.elasticsearch.lyr.plugin;

import org.elasticsearch.index.analysis.AnalysisModule;
import org.elasticsearch.lyr.analyser.EllapsedTimeProcessor;
import org.elasticsearch.lyr.script.GetBeginScript;
import org.elasticsearch.plugins.AbstractPlugin;
import org.elasticsearch.script.ScriptModule;


/**
 * This class is instantiated when Elasticsearch loads the plugin for the
 * first time. If you change the name of this plugin, make sure to update
 * src/main/resources/es-plugin.properties file that points to this class.
 */
public class CalculateElapsedTime extends AbstractPlugin {

	
	public static final String SCRIPT_NAME ="get_begin";
	
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
        return "Calcualte Elapsed Time in POC_LY log line";
    }

    public void onModule(ScriptModule module) {
        // Register each script that we defined in this plugin
        module.registerScript(SCRIPT_NAME, GetBeginScript.Factory.class);
    }
    
    public void onModule(AnalysisModule module) {
    	//module.addAnalyzer("new-analyzer", NewAnalyzerProvider.class);
    	module.addProcessor(new EllapsedTimeProcessor());
    }

}
