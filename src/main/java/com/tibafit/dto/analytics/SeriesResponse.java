package com.tibafit.dto.analytics;

import java.util.List;

public class SeriesResponse {
	private List<String> labels;
	private List<Number> data;
	private String unit;
	private Number total;
	private String metric;
	private String range;
	

	public SeriesResponse() {
	}

	public SeriesResponse(List<String> labels, List<? extends Number> data, String unit, Number total, String metric,
			String range) {
		this.labels = labels;
		this.data = (List<Number>) data;
		this.unit = unit;
		this.total = total;
		this.metric = metric;
		this.range = range;
	}

	public List<String> getLabels() {
		return labels;
	}

	public List<Number> getData() {
		return data;
	}

	public String getUnit() {
		return unit;
	}

	public Number getTotal() {
		return total;
	}

	public String getMetric() {
		return metric;
	}

	public String getRange() {
		return range;
	}
}
