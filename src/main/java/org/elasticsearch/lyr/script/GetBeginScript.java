package org.elasticsearch.lyr.script;

import java.util.Map;

import org.elasticsearch.ElasticSearchIllegalArgumentException;
import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.xcontent.support.XContentMapValues;
import org.elasticsearch.script.AbstractSearchScript;
import org.elasticsearch.script.ExecutableScript;
import org.elasticsearch.script.NativeScriptFactory;

/**
 * Implementation of the native script that checks that the field exists and contains a prime number.
 * <p/>
 * The native script has to implement {@link org.elasticsearch.script.SearchScript} interface. But the
 * {@link org.elasticsearch.script.AbstractSearchScript} class can be used to simplify the implementation.
 */
public class GetBeginScript extends AbstractSearchScript {

    /**
     * Native scripts are build using factories that are registered in the
     * {@link org.elasticsearch.lyr.plugin.CalculateElapsedTime#onModule(org.elasticsearch.script.ScriptModule)}
     * method when plugin is loaded.
     */
    public static class Factory implements NativeScriptFactory {

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
            String beginFieldValue = XContentMapValues.nodeStringValue(params.get("begin_value"), "BEGIN");
            String dateFieldName = XContentMapValues.nodeStringValue(params.get("begin_value"), "axe_date");
            return new GetBeginScript(fieldName, beginFieldValue,endFieldValue,dateFieldName);
        }
    }

    private final String fieldName;
    private final String beginFieldValue;
    private final String dateFieldName;
    private final String endFieldValue;

    /**
     * Factory creates this script on every
     *
     * @param fieldName the name of the field that should be checked
     * @param endFieldValue the required certainty for the number to be prime
     */
    private GetBeginScript(String fieldName, String beginFieldValue,String endFieldValue,String dateFieldName) {
        this.fieldName = fieldName;
        this.beginFieldValue = beginFieldValue;
        this.endFieldValue = endFieldValue;
        this.dateFieldName = dateFieldName;
    }

    @Override
    public Object run() {
        // First we get field using doc lookup
    	if(doc().containsKey(fieldName)){
    		String field = doc().field(fieldName).getStringValue();
            // Check if field exists
            if (field != null && field.equals(endFieldValue)) {
            	doc().field(dateFieldName).stringValue();
                try {
                    // get the last occurennce of being corresponding of this end event
                	//doc.get(fieldName);
                } catch (NumberFormatException ex) {
                    return false;
                }
            }
    	}

        return false;
    }
}
