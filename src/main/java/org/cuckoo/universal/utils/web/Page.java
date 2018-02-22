package org.cuckoo.universal.utils.web;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分页组件
 * @author cuckoo20100401
 * @param <T> 实体对象
 * @version 1.0
 */
public class Page<T> implements Serializable {
	
	private static final long serialVersionUID = 5965246754215069653L;
	
	private int pageNo;					//当前页
	private int pageSize;				//每页显示结果数
	private List<T> rows;				//结果集
	private long totalRows;				//总记录数
	
	/**
	 * 构造方法
	 */
	public Page() {}

	/**
	 * 构造方法
	 * @param pageNo
	 * @param pageSize
	 * @param rows
	 * @param totalRows
	 */
	public Page(int pageNo, int pageSize, List<T> rows, long totalRows) {
		this.pageNo = pageNo;
		this.pageSize = pageSize;
		this.rows = rows;
		this.totalRows = totalRows;
	}
	
	/**
	 * 获取JEasyUI格式数据
	 * @return
	 */
	public Map<String, Object> getJEasyUIData(){
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("total", this.totalRows);
		data.put("rows", this.rows);
		return data;
	}
	
	// 属性访问器
	public int getPageNo() {
		return pageNo;
	}
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public List<T> getRows() {
		return rows;
	}
	public void setRows(List<T> rows) {
		this.rows = rows;
	}
	public long getTotalRows() {
		return totalRows;
	}
	public void setTotalRows(long totalRows) {
		this.totalRows = totalRows;
	}
}
