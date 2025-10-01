package com.tibafit.service.product;

import com.tibafit.model.cart.ProductVO;
import org.springframework.stereotype.Service;

import com.tibafit.repository.product.ProductRepository;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

@Service
public class ProductService {

	private final ProductRepository repo;

	private static final Set<String> BASE_SIZES = Set.of("S", "M", "L", "XL");

	// 允許「純數字」或「數字+單位」，單位可省略；大小寫皆可
	private static final Pattern NUMERIC_SIZE = Pattern.compile("(?i)^\\s*(\\d+(?:\\.\\d+)?)(?:\\s*(ML|L|G|KG))?\\s*$");

	private static final Pattern PURE_NUMBER = Pattern.compile("^\\s*(\\d+(?:\\.\\d+)?)\\s*$");

	public ProductService(ProductRepository repo) {
		this.repo = repo;
	}

	public void add(ProductVO v) {
		fillDefaults(v); 
		repo.save(v);
	}

	public void update(ProductVO v) {
		fillDefaults(v);
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
		return NUMERIC_SIZE.matcher(u).matches(); // 單位可省略後就能吃到 "500"
	}
	
	 /* === 這段是新增的：統一補值與校正（避免 NULL / 負數） === */
	private void fillDefaults(ProductVO v) {
	    // reserved_stock / stock_quantity → NULL or 負數 → 0
	    if (v.getReservedStock() == null || v.getReservedStock() < 0) v.setReservedStock(0);
	    if (v.getStockQuantity() == null || v.getStockQuantity() < 0) v.setStockQuantity(0);

	    // **新增：reserved ≤ stock**
	    if (v.getReservedStock() > v.getStockQuantity()) {
	        v.setReservedStock(v.getStockQuantity());
	    }

	    // product_status 預設上架 1（依專案規則）
	    if (v.getProductStatus() == null) v.setProductStatus(1);

	    // 價格為 null 或負數 → 0
	    if (v.getProductPrice() == null || v.getProductPrice() < 0) {
	        v.setProductPrice(0);
	    }

	    // 去除名稱/代碼空白
	    if (v.getProductName() != null) v.setProductName(v.getProductName().trim());
	    if (v.getProductCode() != null) v.setProductCode(v.getProductCode().trim());

	    // 空字串圖片 → 設為 null，讓前端走預設圖
	    if (v.getProductPicture() != null && v.getProductPicture().isBlank()) {
	        v.setProductPicture(null);
	    }
	}

	/**
	 * 依商品型別決定「沒有單位的數字」時要補的預設單位 0=衣服裝備 -> 不補 1=容器 -> ML 2=蛋白粉 -> G
	 */
	public String defaultUnitFor(ProductVO p) {
		if (p == null)
			return null;
		Integer t = p.getProductType();
		if (t == null)
			return null;
		return switch (t) {
		case 1 -> "ML";
		case 2 -> "G";
		default -> null; // 0 或其它：不補單位
		};
	}

	/** 取得 SKU 中的尺寸（數值尺寸優先；若數值沒帶單位就補 fallbackUnit），沒有就回 null */
	public String sizeOf(String code, String fallbackUnit) {
		if (code == null)
			return null;

		String firstLetterSize = null; // S/M/L/XL
		String firstNumericSize = null; // 500 / 500ML / 1KG

		for (String raw : code.split("-")) {
			if (raw == null || raw.isBlank())
				continue;
			String t = raw.trim();

			// 1) 優先找數值尺寸
			var m = NUMERIC_SIZE.matcher(t);
			if (m.matches()) {
				double value = Double.parseDouble(m.group(1));
				String unit = m.group(2); // 可能為 null

				String u = (unit != null) ? unit.toUpperCase()
						: (fallbackUnit != null ? fallbackUnit.toUpperCase() : null);

				if ("G".equals(u)) {
					if (value >= 1000.0) {
						double kg = value / 1000.0;
						firstNumericSize = cleanNumber(kg) + "KG";
					} else {
						firstNumericSize = cleanNumber(value) + "G";
					}
				} else if ("ML".equals(u)) {
					if (value >= 1000.0) {
						double l = value / 1000.0;
						firstNumericSize = cleanNumber(l) + "L";
					} else {
						firstNumericSize = cleanNumber(value) + "ML";
					}
				} else if (u != null) {
					firstNumericSize = cleanNumber(value) + u;
				} else {
					firstNumericSize = cleanNumber(value);
				}
				break; // 數值優先，找到就用它
			}

			// 2) 其次找字母尺碼
			String u = t.toUpperCase();
			if (BASE_SIZES.contains(u) && firstLetterSize == null) {
				firstLetterSize = u;
			}
		}
		return (firstNumericSize != null) ? firstNumericSize : firstLetterSize;
	}

	/* 舊版 API（相容） */
	public String sizeOf(String code) {
		return sizeOf(code, null);
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

		if (BASE_SIZES.contains(s)) {
			return switch (s) {
			case "S" -> 1;
			case "M" -> 2;
			case "L" -> 3;
			default -> 4; // XL
			};
		}

		var m = NUMERIC_SIZE.matcher(s);
		if (m.matches()) {
			double v = Double.parseDouble(m.group(1));
			String unit = m.group(2);
			if (unit == null)
				return v;
			return switch (unit.toUpperCase()) {
			case "ML" -> v;
			case "L" -> v * 1000.0;
			case "G" -> v;
			case "KG" -> v * 1000.0;
			default -> v;
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

	/** 將 size 文本標準化（去空白、大寫） */
	private String normalizeSize(String s) {
		return (s == null) ? null : s.trim().toUpperCase();
	}

	/** 回傳同款的 size -> productId 對照表（按尺寸大小排序） */
	public Map<String, Integer> findSiblingSizeMap(Integer productId) {
		ProductVO me = getOne(productId);
		if (me == null)
			return Map.of();

		String code = me.getProductCode();
		String key = keyWithoutSize(code);
		String prefix = firstToken(code);

		List<ProductVO> candidates = repo.findByCodeStartingWith(prefix + "-");

		List<ProductVO> siblings = candidates.stream().filter(p -> key.equals(keyWithoutSize(p.getProductCode())))
				.filter(p -> sizeOf(p.getProductCode()) != null).sorted(sizeComparator()).toList();

		Map<String, Integer> map = new LinkedHashMap<>();
		for (ProductVO p : siblings) {
			String sz = normalizeSize(sizeOf(p.getProductCode()));
			if (sz != null)
				map.putIfAbsent(sz, p.getProductId());
		}

		String mySize = normalizeSize(sizeOf(me.getProductCode()));
		if (mySize != null)
			map.putIfAbsent(mySize, me.getProductId());

		return map;
	}

	/** 由 productId + size（不分大小寫）找到同款中該尺寸的 productId */
	public Optional<Integer> findSiblingIdBySize(Integer productId, String size) {
		Map<String, Integer> map = findSiblingSizeMap(productId);
		if (map.isEmpty() || size == null)
			return Optional.empty();
		return Optional.ofNullable(map.get(normalizeSize(size)));
	}

	/** 尺寸排序：支援 S/M/L/XL 與數值單位（500ml、1L、250g、2kg） */
	public List<String> sortSizes(Collection<String> sizes) {
		if (sizes == null || sizes.isEmpty())
			return List.of();
		return sizes.stream().filter(Objects::nonNull).map(this::normalizeSize).distinct()
				.sorted(Comparator.comparingDouble(this::sizeWeight).thenComparing(Comparator.naturalOrder()))
				.collect(Collectors.toList());
	}

	/** 決定詳情頁的「目前尺寸」 */
	public String resolveCurrentSize(ProductVO product, String sizeQry, Map<String, Integer> sizeToId) {
		if (sizeToId == null || sizeToId.isEmpty())
			return null;

		String q = normalizeSize(sizeQry);
		if (q != null && sizeToId.containsKey(q))
			return q;

		String fromSelf = normalizeSize(sizeOf(product.getProductCode()));
		if (fromSelf != null && sizeToId.containsKey(fromSelf))
			return fromSelf;

		return sizeToId.keySet().iterator().next();
	}

	// 將1000g 改成 1kg
	private static String cleanNumber(double v) {
		BigDecimal bd = BigDecimal.valueOf(v).stripTrailingZeros();
		return bd.toPlainString();
	}

	// ====== A 方案：列表只顯示「上架」商品 ======

	private boolean isOnShelf(ProductVO p) {
		return p != null && Objects.equals(p.getProductStatus(), 1);
	}

	/**
	 * 依「同款」群組（keyWithoutSize）挑一個代表 ProductVO。 代表只從「上架」變體中挑；若整組都下架 → 直接不出現在列表。
	 * 代表挑選邏輯：尺寸權重較小者（S < M < L < XL；500ML < 1L…）。
	 */
	public List<ProductVO> collapseVariants(List<ProductVO> input) {
		if (input == null || input.isEmpty())
			return List.of();

		// 先依同款分組
		Map<String, List<ProductVO>> groups = new LinkedHashMap<>();
		for (ProductVO p : input) {
			String code = p.getProductCode();
			String key = keyWithoutSize(code);
			if (key == null)
				key = code; // 取不到就用自己
			groups.computeIfAbsent(key, k -> new ArrayList<>()).add(p);
		}

		List<ProductVO> result = new ArrayList<>();

		for (List<ProductVO> group : groups.values()) {
			// 只看上架的變體
			List<ProductVO> ups = group.stream().filter(this::isOnShelf).collect(Collectors.toList());

			if (ups.isEmpty()) {
				// 這個同款全下架 → 不加入列表
				continue;
			}

			// 從上架的變體挑一個代表（尺寸最小）
			ProductVO rep = ups.stream()
					.min(Comparator.comparingDouble(p -> sizeWeight(sizeOf(p.getProductCode(), defaultUnitFor(p)))))
					.orElse(ups.get(0));

			result.add(rep);
		}

		return result;
	}

	// 取得「全部商品」給列表頁（只顯示上架代表）
	public List<ProductVO> getAllCollapsed() {
		return collapseVariants(getAll());
	}

	// 搜尋結果（只顯示上架代表）
	public List<ProductVO> searchCollapsed(String keyword) {
		return collapseVariants(search(keyword));
	}

	// ====== 顯示名稱去除尺寸（僅顯示用，不動 DB） ======

	private static final Pattern NAME_SIZE_PAREN = Pattern
			.compile("(?iu)[（(]\\s*(?:XS|S|M|L|XL|XXL|\\d+(?:\\.\\d+)?\\s*(?:ML|L|G|KG))\\s*[)）]");
	private static final Pattern NAME_SIZE_LETTER_WITH_HAO = Pattern
			.compile("(?iu)\\b(?:XS|S|M|L|XL|XXL)\\s*(?:號|号)\\b");
	private static final Pattern NAME_SIZE_NUMERIC_UNIT = Pattern
			.compile("(?iu)\\b\\d+(?:\\.\\d+)?\\s*(?:ML|L|G|KG)\\b");
	private static final Pattern NAME_TRAIL_LETTER = Pattern.compile("(?iu)(?:-|\\s)+(?:XS|S|M|L|XL|XXL)\\b");
	private static final Pattern NAME_WS = Pattern.compile("\\s{2,}");

	/** 僅處理顯示：把名稱裡的尺寸字樣移除（S/M/L/XL、S號、(M)、500ML、1KG、- XL 等） */
	public String nameWithoutSize(String name) {
		if (name == null || name.isBlank())
			return name;

		String s = name;
		s = NAME_SIZE_PAREN.matcher(s).replaceAll("");
		s = NAME_SIZE_LETTER_WITH_HAO.matcher(s).replaceAll("");
		s = NAME_SIZE_NUMERIC_UNIT.matcher(s).replaceAll("");
		s = NAME_TRAIL_LETTER.matcher(s).replaceAll("");
		s = NAME_WS.matcher(s).replaceAll(" ").trim();
		return s;
	}
	
	/** 價格區間物件 */
	public static final class PriceRange {
	    public final Integer min;
	    public final Integer max;
	    public PriceRange(Integer min, Integer max) { this.min = min; this.max = max; }
	}

	/** 取得同款(不同尺寸)的價格區間（僅考慮上架商品）。若取不到，回 null。*/
	public PriceRange priceRangeFor(ProductVO rep) {
	    if (rep == null || rep.getProductCode() == null) return null;

	    // 找出同款的所有尺寸變體（你已經有的邏輯）
	    var variants = findSizeVariantsByCode(rep.getProductCode());
	    if (variants == null || variants.isEmpty()) {
	        // fallback：沒有尺寸變體，就用自己的價格
	        Integer p = rep.getProductPrice();
	        return (p == null) ? null : new PriceRange(p, p);
	    }

	    // 只計算「上架」且有價格的
	    var prices = variants.stream()
	            .filter(this::isOnShelf)
	            .map(ProductVO::getProductPrice)
	            .filter(Objects::nonNull)
	            .toList();

	    if (prices.isEmpty()) {
	        Integer p = rep.getProductPrice();
	        return (p == null) ? null : new PriceRange(p, p);
	    }

	    int min = prices.stream().min(Integer::compareTo).get();
	    int max = prices.stream().max(Integer::compareTo).get();
	    return new PriceRange(min, max);
	}

	/** 方便直接給 VO 呼叫 */
	public String displayNameWithoutSize(ProductVO p) {
		if (p == null)
			return "";
		return nameWithoutSize(p.getProductName());
	}
}
