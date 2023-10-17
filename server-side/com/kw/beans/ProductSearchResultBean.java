package com.kw.beans;

import java.util.List;

public class ProductSearchResultBean {
	private List<?> search_results;
	private List<?> suggested_results;
	public List<?> getSearch_results() {
		return search_results;
	}
	public void setSearch_results(List<?> search_results) {
		this.search_results = search_results;
	}
	public List<?> getSuggested_results() {
		return suggested_results;
	}
	public void setSuggested_results(List<?> suggested_results) {
		this.suggested_results = suggested_results;
	}
	
	
}
