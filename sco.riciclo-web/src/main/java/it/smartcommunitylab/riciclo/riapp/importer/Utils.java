package it.smartcommunitylab.riciclo.riapp.importer;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class Utils {
	private static ObjectMapper fullMapper = new ObjectMapper();
	static {
		Utils.fullMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		Utils.fullMapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
		Utils.fullMapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
		Utils.fullMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		Utils.fullMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
	}
	
	public static JsonNode readJsonFromReader(Reader reader) throws JsonProcessingException, IOException {
		return Utils.fullMapper.readTree(reader);
	}
	
	/**
	 * Read list of JSON objects from the specified file
	 * 
	 * @param f
	 * @param cls
	 * @return
	 * @throws IOException
	 */
	public static <T> List<T> readJSONListFromInputStream(InputStream in, Class<T> cls)
			throws IOException {
		List<Object> list = Utils.fullMapper.readValue(in, new TypeReference<List<?>>() {
		});
		List<T> result = new ArrayList<T>();
		for (Object o : list) {
			result.add(Utils.fullMapper.convertValue(o, cls));
		}
		return result;
	}

	/**
	 * Convert input object to the instance of the specified class
	 * 
	 * @param in
	 * @param cls
	 * @return
	 */
	public static <T> T toObject(Object in, Class<T> cls) {
		return Utils.fullMapper.convertValue(in, cls);
	}

	public static <T> T toObject(JsonNode in, Class<T> cls) throws JsonProcessingException {
		return Utils.fullMapper.treeToValue(in, cls);
	}
	
	/**
	 * Convert JSON array string to the list of objects of the specified class
	 * 
	 * @param in
	 * @param cls
	 * @return
	 */
	public static <T> List<T> toObjectList(Object in, Class<T> cls) {
		List<Object> list = Utils.fullMapper.convertValue(in, new TypeReference<List<?>>() {
		});
		List<T> result = new ArrayList<T>();
		for (Object o : list) {
			result.add(Utils.fullMapper.convertValue(o, cls));
		}
		return result;
	}

	public static String generateUUID() {
		return UUID.randomUUID().toString();
	}
	
	public static String capitalize(String input) {
		String output = input.substring(0, 1).toUpperCase() + input.substring(1);
		return output;
	}

	public static boolean isNotEmpty(String value) {
		boolean result = false;
		if ((value != null) && (!value.isEmpty())) {
			result = true;
		}
		return result;
	}
	
	public static boolean isEmpty(String value) {
		boolean result = true;
		if ((value != null) && (!value.isEmpty())) {
			result = false;
		}
		return result;
	}

}
