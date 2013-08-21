es-plugin
=========

Basic Template for elasticsearch plugin developpement


You can run the junit test :
EllapsedTimeProcessorTest.testCallAnalyzer and make a breakpoint at the end of this method.
After this it's possible to make some curl request to the elasticsearch server.
For example, you can make this 3 different request wich return the same document. 
It is interesting because in this 3 request, we used the same field which be analyzed,indexed
in 3 different way :
- The simple/original-ES way :
curl -XGET "http://localhost:9200/test/line/_search?q=partial.partial_parsed:dekpo"

- The way used a not_analyzed field :
curl -XGET "http://localhost:9200/test/line/_search?q=partial:dekpo%20ede"

- The last used a custom developped tokenFilter, wich concatenat the field with a parametrable separaot
(for this example the separator is "" but it could be as you want) :
curl -XGET "http://localhost:9200/test/line/_search?q=partial.partialnos:dekpoede"
