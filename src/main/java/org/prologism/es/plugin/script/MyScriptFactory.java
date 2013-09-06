package org.prologism.es.plugin.script;

import java.util.Map;

import org.elasticsearch.ElasticSearchIllegalArgumentException;
import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.xcontent.support.XContentMapValues;
import org.elasticsearch.script.ExecutableScript;
import org.elasticsearch.script.NativeScriptFactory;

/**
 * Native scripts are build using factories that are registered in the
 * {@link org.prologism.es.plugin.MyPluginRegister#onModule(org.elasticsearch.script.ScriptModule)}
 * method when plugin is loaded.
 */
public class MyScriptFactory implements NativeScriptFactory {

    /**
     * This method is called for every search on every shard.
     *
     * @param params list of script parameters passed with the query
     * @return new native script
     */
    @Override
    public ExecutableScript newScript(@Nullable Map<String, Object> params) {
        // Example of a mandatory string parameter
        // The XContentMapValues helper class can be used to simplify parameter parsing
        String fieldName = params == null ? null : XContentMapValues.nodeStringValue(params.get("field"), null);
        if (fieldName == null) {
            throw new ElasticSearchIllegalArgumentException("Missing the field parameter");
        }

        // Example of an optional integer  parameter
        String endFieldValue = XContentMapValues.nodeStringValue(params.get("end_value"), "END");
        return new MyScript(fieldName,endFieldValue);
    }
}
