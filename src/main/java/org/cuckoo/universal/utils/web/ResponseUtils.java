package org.cuckoo.universal.utils.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

/**
 * 服务器响应工具类
 */
public class ResponseUtils {
	
	/**
	 * 创建响应消息
	 * @param success
	 * @param message
	 * @return
	 */
	public static Map<String, Object> createMsg(boolean success, String message){
		Map<String, Object> msg = new HashMap<String, Object>();
		msg.put("success", success);
		msg.put("message", message);
		return msg;
	}
	
	/**
	 * 创建响应消息
	 * @param success
	 * @param code
	 * @param message
	 * @return
	 */
	public static Map<String, Object> createMsg(boolean success, Integer code, String message){
		Map<String, Object> msg = new HashMap<String, Object>();
		msg.put("success", success);
		msg.put("code", code);
		msg.put("message", message);
		return msg;
	}
	
	/**
	 * 创建响应消息
	 * @param keys
	 * @param values
	 * @return
	 */
	public static Map<String, Object> createMsg(String[] keys, Object[] values){
		Map<String, Object> msg = new HashMap<String, Object>();
		for(int i=0;i<keys.length;i++){
			msg.put(keys[i], values[i]);
		}
		return msg;
	}
	
	/**
	 * 向前台输出JSON
	 * @param json
	 * @param response
	 * @throws IOException
	 */
	public static void writeJson(String json, HttpServletResponse response) throws IOException {
		response.setContentType("application/json");
		response.setContentLength(json.getBytes("utf-8").length);
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
		response.getWriter().flush();
	}
	
	/**
	 * 向前台输出TEXT
	 * @param text
	 * @param response
	 * @throws IOException
	 */
	public static void writeText(String text, HttpServletResponse response) throws IOException {
		response.setContentType("application/text");
		response.setContentLength(text.getBytes("utf-8").length);
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(text);
		response.getWriter().flush();
	}
	
	/**
	 * 向前台输出XML
	 * @param xml
	 * @param response
	 * @throws IOException
	 */
	public static void writeXml(String xml, HttpServletResponse response) throws IOException {
		response.setContentType("application/xml");
		response.setContentLength(xml.getBytes("utf-8").length);
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(xml);
		response.getWriter().flush();
	}
	
	/**
	 * 向前台输出HTML
	 * @param html
	 * @param response
	 * @throws IOException
	 */
	public static void writeHtml(String html, HttpServletResponse response) throws IOException {
		response.setContentType("text/html;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(html);
		response.getWriter().flush();
	}
}
