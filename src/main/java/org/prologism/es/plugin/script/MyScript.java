package org.prologism.es.plugin.script;

import org.elasticsearch.script.AbstractSearchScript;

/**
 * Implementation of the native script.
 * <p/>
 * The native script has to implement {@link org.elasticsearch.script.SearchScript} interface. But the
 * {@link org.elasticsearch.script.AbstractSearchScript} class can be used to simplify the implementation.
 */
class MyScript extends AbstractSearchScript {

    

    private final String fieldName;
    private final String endFieldValue;

    /**
     * Factory creates this script on every
     *
     * @param fieldName the name of the field that should be checked
     * @param endFieldValue the value of the field which disable the return of the doc
     */
    MyScript(String fieldName,String endFieldValue) {
        this.fieldName = fieldName;
        this.endFieldValue = endFieldValue;
    }

    @Override
    public Object run() {
        // First we get field using doc lookup
    	if(doc().containsKey(fieldName)){
    		String field = doc().field(fieldName).getStringValue();
            // Check if field exists
            if (field != null && field.equalsIgnoreCase(endFieldValue)) {
	            	//if the value is not equal to the value indicate 
	            	//return true to say this doc must be returned into the response of the request
                	return true;
            }
    	}

        return false;
    }
}
