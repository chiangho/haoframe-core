package haoframe.core.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CollectionUtils {

	
	@SuppressWarnings("unchecked")
	public static <T> List<T> arrayToList(T... a) {
		List<T> array = new ArrayList<>(a.length);
		Collections.addAll(array, a);
		return array;
	}

	
	
	
}
