package org.cuckoo.universal.utils.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * Request工具类
 */
public class RequestUtils {
	
	public static final String Range_Param_dateStart = "dateStart";
	public static final String Range_Param_dateEnd = "dateEnd";

	public static Map<String, Object> getRangeParams(HttpServletRequest request) {
		Map<String, Object> rangeParams = new HashMap<>();
		rangeParams.put(Range_Param_dateStart, request.getParameter(Range_Param_dateStart));
		rangeParams.put(Range_Param_dateEnd, request.getParameter(Range_Param_dateEnd));
		return rangeParams;
	}
}
