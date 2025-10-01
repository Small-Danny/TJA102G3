(function() {
	const stockHint = document.getElementById('stockHint');
	const hidPid = document.getElementById('variantProductId');
	const hidSize = document.getElementById('selectedSize');
	const btnCart = document.getElementById('btnAddToCart');
	const skuText = document.getElementById('skuText');
	const priceText = document.getElementById('priceText');
	const qtyInput = document.getElementById('qty'); // 數量輸入框控制

	// 追蹤目前可買庫存（由後端 /api/stock 回傳）
	let currentStock = null;

	// 先取出庫存 API
	const STOCK_API = stockHint ? (stockHint.dataset.stockUrl || '/shop/api/stock') : '/shop/api/stock';

	// 初始按鈕狀態：沒有 pid 就先停用
	if (btnCart && (!btnCart.dataset.id || btnCart.dataset.id.trim() === '')) {
		btnCart.setAttribute('aria-disabled', 'true');
		btnCart.classList.add('disabled');
	}
	if (qtyInput) qtyInput.disabled = !btnCart || btnCart.classList.contains('disabled');

	function setHint(text, isError) {
		if (!stockHint) return;
		stockHint.textContent = text;
		stockHint.classList.toggle('text-danger', !!isError);
		stockHint.classList.toggle('text-secondary', !isError);
	}

	// 價格格式化
	function setPrice(val) {
		if (!priceText) return;
		if (val == null || val === '') return;
		const n = Number(val);
		priceText.textContent = Number.isFinite(n) ? n.toLocaleString('zh-TW') : String(val);
	}

	// 解析數量為正整數（至少 1）
	function parseQty(v) {
		const n = Number(v);
		if (!Number.isFinite(n) || n < 1) return 1;
		return Math.floor(n);
	}

	// 設定/檢查數量上限，回傳是否有效（未超過）
	function enforceQtyLimit() {
		if (!qtyInput) return true;

		let q = parseQty(qtyInput.value);
		qtyInput.value = q;

		if (currentStock == null) {
			// 還沒取得庫存：不強制，但維持 >=1
			return true;
		}

		if (currentStock <= 0) {
			// 無庫存：數量鎖住為 1 並停用購買
			qtyInput.value = 1;
			qtyInput.disabled = true;
			if (btnCart) { btnCart.setAttribute('aria-disabled', 'true'); btnCart.classList.add('disabled'); }
			return false;
		}

		// 有庫存：設定 max，若超過就壓回並提示
		qtyInput.max = String(currentStock);
		if (q > currentStock) {
			qtyInput.value = currentStock;
			setHint(`超過庫存上限，已自動調整為 ${currentStock} 件`, true);
			return false;
		}
		return true;
	}

	// 一鍵停用/啟用購買 UI（數量＋加入購物車）
	function disablePurchase(msg) {
		currentStock = 0;
		setHint(msg || '此尺寸已下架或無庫存', true);
		if (qtyInput) { qtyInput.value = 1; qtyInput.disabled = true; qtyInput.removeAttribute('max'); }
		if (btnCart) { btnCart.setAttribute('aria-disabled', 'true'); btnCart.classList.add('disabled'); }
	}
	function enablePurchase(stock) {
		currentStock = Number(stock);
		if (!Number.isFinite(currentStock) || currentStock < 0) currentStock = 0;

		if (currentStock > 0) {
			setHint(`庫存：${currentStock} 件`, false);
			if (qtyInput) {
				qtyInput.disabled = false;
				qtyInput.max = String(currentStock);
				// 初次開放時若數量超過庫存，壓回
				enforceQtyLimit();
			}
			if (btnCart) { btnCart.removeAttribute('aria-disabled'); btnCart.classList.remove('disabled'); }
		} else {
			disablePurchase('目前缺貨');
		}
	}

	// 判斷/標記下架尺寸（無 pid 或 aria-disabled=true）
	function markDisabledChip(chip) {
		if (!chip) return;
		chip.classList.add('disabled', 'opacity-50');
		chip.setAttribute('aria-disabled', 'true');
		chip.removeAttribute('aria-selected');
		chip.classList.remove('active');
	}
	function isChipDisabled(chip) {
		if (!chip) return true;
		const pid = (chip.dataset.pid || '').trim().toLowerCase();
		const ariaDisabled = chip.getAttribute('aria-disabled') === 'true';
		return ariaDisabled || pid === '' || pid === '0' || pid === 'null' || pid === 'undefined';
	}

	// 初始掃描：沒有 pid 或 aria-disabled=true → 灰掉 & 不可點
	document.querySelectorAll('#sizeGroup .size-chip').forEach(chip => {
		if (isChipDisabled(chip)) {
			markDisabledChip(chip);
		}
	});

	// 控制請求並避免亂序
	let inflightCtrl = null;

	async function loadStock(pid) {
		// 無 pid：代表此尺寸未上架或未選
		if (!pid) {
			disablePurchase('此尺寸已下架或無庫存');
			return;
		}

		// 取消上一個請求
		if (inflightCtrl) inflightCtrl.abort();
		inflightCtrl = new AbortController();

		setHint('載入庫存中…', false);

		try {
			const r = await fetch(`${STOCK_API}?productId=${encodeURIComponent(pid)}`, {
				credentials: 'same-origin',
				signal: inflightCtrl.signal,
				headers: { 'Accept': 'application/json' }
			});

			const ctype = r.headers.get('content-type') || '';
			const isJson = ctype.toLowerCase().includes('application/json');
			if (!isJson) throw new Error('Non-JSON response');

			const data = await r.json();
			if (!r.ok) throw new Error('HTTP ' + r.status);

			// 預期結構 { ok:true, stock:number }
			if (data && data.ok === true && typeof data.stock === 'number') {
				enablePurchase(data.stock);
			} else {
				const stock = Number(data?.stock ?? data?.quantity ?? NaN);
				if (!Number.isNaN(stock)) enablePurchase(stock);
				else throw new Error('Invalid payload');
			}
		} catch (err) {
			if (err.name === 'AbortError') return; // 使用者又選了其他尺寸
			disablePurchase('無法取得庫存，請稍後再試');
		}
	}

	// 文件層級代理：支援 button/div/span 都可當 size-chip
	document.addEventListener('click', (e) => {
		const chip = e.target.closest('.size-chip');
		if (!chip) return;

		// 如果這顆是下架（沒有 pid / 被標示 aria-disabled / .disabled），就直接擋下
		if (isChipDisabled(chip)) {
			e.preventDefault();
			return;
		}

		e.preventDefault();

		// 只在同一個 size-group 內切換
		const group = chip.closest('#sizeGroup');
		if (group) {
			group.querySelectorAll('.size-chip.active').forEach(el => {
				el.classList.remove('active');
				el.setAttribute('aria-selected', 'false');
			});
			chip.classList.add('active');
			chip.setAttribute('aria-selected', 'true');
		}

		const size = chip.dataset.size || '';
		let pid = chip.dataset.pid || '';
		const sku = chip.dataset.sku || '';
		const price = chip.dataset.price || '';

		// 正規化 pid（保留空字串；有值時轉成純數字字串）
		if (pid) {
			const n = Number(pid);
			pid = Number.isFinite(n) ? String(n) : String(pid);
		}

		if (hidSize) hidSize.value = size;
		if (hidPid) hidPid.value = pid;

		if (btnCart) {
			if (pid) btnCart.setAttribute('data-id', pid);
			else btnCart.removeAttribute('data-id');
		}
		if (skuText && sku) skuText.textContent = sku;

		setPrice(price);
		loadStock(pid);
	}, true);

	// 監聽數量輸入：避免超過庫存
	if (qtyInput) {
		qtyInput.addEventListener('input', () => {
			enforceQtyLimit();
		});
		qtyInput.addEventListener('blur', () => {
			enforceQtyLimit();
		});
	}

	// 首次載入：盡量選可賣的尺寸（有 pid 的 active；否則第一個可賣的；都沒有就停用）
	(function init() {
		const chips = Array.from(document.querySelectorAll('#sizeGroup .size-chip'));
		// 若預設 active 是下架，移除並提示
		const presetActive = document.querySelector('#sizeGroup .size-chip.active');
		if (presetActive && isChipDisabled(presetActive)) {
			markDisabledChip(presetActive);
			disablePurchase('此尺寸已下架或無庫存');
		}

		const active = document.querySelector('#sizeGroup .size-chip.active');
		const candidate = (active && (active.dataset.pid || '').trim() && !isChipDisabled(active))
			? active
			: chips.find(c => (c.dataset.pid || '').trim() && !isChipDisabled(c));

		if (candidate) {
			candidate.setAttribute('aria-selected', 'true');
			const pid = candidate.dataset.pid || '';
			const sku = candidate.dataset.sku || '';
			const price = candidate.dataset.price || '';
			if (skuText && sku) skuText.textContent = sku;
			setPrice(price);
			loadStock(pid);
		} else {
			disablePurchase('此商品已下架或無庫存');
		}
	})();

	// 「加入購物車」按鈕
	if (btnCart) {
		btnCart.addEventListener('click', async function(event) {
			event.preventDefault();

			// 任一停用狀態都直接提示
			if (this.classList.contains('disabled') || this.getAttribute('aria-disabled') === 'true' || (qtyInput && qtyInput.disabled)) {
				setHint('請先選擇有庫存的尺寸', true);
				return;
			}

			// 檢查數量是否超過庫存
			if (!enforceQtyLimit()) {
				// 已在 enforceQtyLimit 內提示
				return;
			}

			const productId = this.dataset.id;
			const quantity = qtyInput ? parseQty(qtyInput.value) : 1;

			if (!productId) {
				console.error('找不到商品 Variant ID，無法加入購物車。');
				setHint('無法加入購物車，請重新選擇尺寸', true);
				return;
			}

			try {
				if (typeof addItemToCart !== 'function') {
					throw new Error('購物車功能未正確載入。');
				}
				await addItemToCart(productId, quantity);

				Swal.fire({
					icon: 'success',
					title: '成功加入購物車！',
					showConfirmButton: false,
					timer: 1500
				});
			} catch (error) {
				Swal.fire({
					icon: 'error',
					title: '加入失敗',
					text: error.message || '請稍後再試'
				});
			}
		});
	}

	// 再保險一次：若模板端已標 aria-disabled 或無 pid，強制灰掉
	document.querySelectorAll('#sizeGroup .size-chip').forEach(chip => {
		if (isChipDisabled(chip)) {
			markDisabledChip(chip);
		}
	});
})();
