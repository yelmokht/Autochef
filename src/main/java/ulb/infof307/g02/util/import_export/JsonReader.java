package ulb.infof307.g02.util.import_export;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonReader {

    private static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper();

    private JsonReader() {}

    public static void configure() {
        DEFAULT_MAPPER.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
    }

    public static ObjectMapper getDefaultMapper() {
        return DEFAULT_MAPPER;
    }

}
