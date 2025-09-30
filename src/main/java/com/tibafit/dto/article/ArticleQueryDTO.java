package com.tibafit.dto.article;

import java.io.Serializable;

public class ArticleQueryDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String keyWords;// 關鍵字
	private Integer type;// 文章分類
	private Integer pageNum;// 第?頁
	private Integer pageSize;// 一頁幾條文章

	public String getKeyWords() {
		return keyWords;
	}

	public void setKeyWords(String keyWords) {
		this.keyWords = keyWords;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getPageNum() {
		return pageNum;
	}

	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public ArticleQueryDTO(String keyWords, Integer type, Integer pageNum, Integer pageSize) {
		super();
		this.keyWords = keyWords;
		this.type = type;
		this.pageNum = pageNum;
		this.pageSize = pageSize;
	}

	public ArticleQueryDTO() {
		super();
	}

}
