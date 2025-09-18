package com.tibafit.service.product;

import org.springframework.stereotype.Service;

import com.tibafit.model.product.ProductVO;
import com.tibafit.repository.product.ProductRepository;
import com.tibafit.repository.product.ProductVariantRow;

import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

@Service
public class ProductService {

	private final ProductRepository repo;

	private static final Set<String> BASE_SIZES = Set.of("S", "M", "L", "XL");

	private static final Pattern NUMERIC_SIZE = Pattern.compile("(?i)^\\s*(\\d+(?:\\.\\d+)?)\\s*(ML|L|G|KG)\\s*$");

	public ProductService(ProductRepository repo) {
		this.repo = repo;
	}

	public void add(ProductVO v) {
		repo.save(v);
	}

	public void update(ProductVO v) {
		repo.save(v);
	}

	public void delete(Integer id) {
		repo.deleteById(id);
	}

	public ProductVO getOne(Integer id) {
		return repo.findById(id).orElse(null);
	}

	public ProductVO findByCode(String code) {
		return repo.findByCode(code).orElse(null);
	}

	public List<ProductVO> findByType(Integer type) {
		return repo.findByTypeOrderById(type);
	}

	public List<ProductVO> getAll() {
		return repo.findAllOrderById();
	}

	public List<ProductVO> search(String keyword) {
		return repo.searchByKeywordOrderById(keyword);
	}

	/* 判斷一個 token 是否為尺寸 */
	private boolean isSizeToken(String t) {
		if (t == null)
			return false;
		String u = t.trim().toUpperCase();
		if (BASE_SIZES.contains(u))
			return true;
		return NUMERIC_SIZE.matcher(u).matches();
	}

	/* 取得 SKU 中的尺寸（第一個符合 isSizeToken 的 token），沒有就回 null */
	public String sizeOf(String code) {
		if (code == null)
			return null;
		for (String t : code.split("-")) {
			if (isSizeToken(t))
				return t.trim().toUpperCase();
		}
		return null;
	}

	/* 拿掉尺寸 token 後的 key，用來判斷同款 */
	public String keyWithoutSize(String code) {
		if (code == null)
			return null;
		String[] tokens = code.split("-");
		List<String> kept = new ArrayList<>();
		for (String t : tokens)
			if (!isSizeToken(t))
				kept.add(t);
		return String.join("-", kept);
	}

	/* 統一成排序用的數值：S/M/L/XL -> 1..4；數值單位換算到 base 單位 */
	private double sizeWeight(String sizeToken) {
		if (sizeToken == null)
			return Double.MAX_VALUE;
		String s = sizeToken.trim().toUpperCase();
		// 服飾
		if (BASE_SIZES.contains(s)) {
			return switch (s) {
			case "S" -> 1;
			case "M" -> 2;
			case "L" -> 3;
			default -> 4;
			};
		}
		// 數值單位
		var m = NUMERIC_SIZE.matcher(s);
		if (m.matches()) {
			double v = Double.parseDouble(m.group(1));
			String unit = m.group(2).toUpperCase();
			// 換算到 base：體積用 ML、重量用 G（只是為了排序，不影響顯示）
			return switch (unit) {
			case "ML" -> v;
			case "L" -> v * 1000.0;
			case "G" -> v;
			case "KG" -> v * 1000.0;
			default -> v; // fallback
			};
		}
		return Double.MAX_VALUE;
	}

	private Comparator<ProductVO> sizeComparator() {
		return Comparator.comparingDouble(p -> sizeWeight(sizeOf(p.getProductCode())));
	}

	private String firstToken(String code) {
		if (code == null)
			return null;
		int i = code.indexOf('-');
		return (i > 0) ? code.substring(0, i) : code;
	}

	/** 找同款所有尺寸變體（同一個 keyWithoutSize），並照尺寸大小排序 */
	public List<ProductVO> findSizeVariantsByCode(String code) {
		if (code == null)
			return List.of();
		String key = keyWithoutSize(code);
		String prefix = firstToken(code);
		List<ProductVO> candidates = repo.findByCodeStartingWith(prefix + "-");

		return candidates.stream().filter(p -> key.equals(keyWithoutSize(p.getProductCode())))
				.filter(p -> sizeOf(p.getProductCode()) != null).sorted(sizeComparator()).toList();
	}

}
