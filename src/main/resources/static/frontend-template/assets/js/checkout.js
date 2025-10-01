// /frontend-template/assets/js/checkout.js
// 結帳頁：讀取 cartSummary（含使用點數）、渲染訂單、建立訂單、跳金流（只顯示紅字，不用 alert）

document.addEventListener('DOMContentLoaded', async () => {
	const $ = (sel) => document.querySelector(sel);

	const form = $('#checkout-form');
	if (!form) return;

	const recipientName = $('#recipient_name');
	const recipientPhone = $('#recipient_phone');
	const recipientAddress = $('#recipient_address');
	const orderItemsBox = $('#order-items');

	const totalAmountEl = $('#total-amount');
	const usedPointsEl = $('#used-points');
	const orderAmountEl = $('#order-amount');
	const grandTotalEl = $('#grand-total');

	const linePayBtn = $('#linePayBtn');
	const creditCardBtn = $('#creditCardBtn');

	const money = (n) => `NT$${Number(n || 0).toLocaleString('zh-TW')}`;

	// ====== 全域錯誤顯示（頁面上方紅框） ======
	function showError(message) {
		orderItemsBox.innerHTML = `<div class="alert alert-danger">${message}</div>`;
	}

	// ====== 表單欄位錯誤（下方紅字 + 紅框） ======
	const getErrorBox = (inputEl) => {
		if (inputEl && inputEl.nextElementSibling && inputEl.nextElementSibling.classList.contains('error')) {
			return inputEl.nextElementSibling;
		}
		return null;
	};

	const setFieldError = (inputEl, message) => {
		const box = getErrorBox(inputEl);
		if (box) box.textContent = message || '';
		if (message) {
			inputEl.classList.add('is-invalid');
			inputEl.setAttribute('aria-invalid', 'true');
		} else {
			inputEl.classList.remove('is-invalid');
			inputEl.removeAttribute('aria-invalid');
		}
	};

	const clearAllErrors = () => {
		[recipientName, recipientPhone, recipientAddress].forEach((el) => setFieldError(el, ''));
	};

	// 即時驗證（輸入/離焦時就更新）
	[recipientName, recipientPhone, recipientAddress].forEach((el) => {
		el.addEventListener('input', () => validateFields());
		el.addEventListener('blur', () => validateFields());
	});

	function validateFields() {
		let ok = true;
		clearAllErrors();

		const name = recipientName.value.trim();
		const namePattern = /[\p{L}\p{M}\s.\-]{2,10}/u; // 對應 input 的 pattern
		if (name.length < 2 || !namePattern.test(name)) {
			setFieldError(recipientName, '收貨人姓名需 2–10 字（中英文字，可含空白/.-）');
			ok = false;
		}

		const phone = recipientPhone.value.trim();
		const phonePattern = /^(09\d{8}|0\d{1,2}-?\d{7,8})$/;
		if (!phonePattern.test(phone)) {
			setFieldError(recipientPhone, '請輸入正確的手機或市話（09xxxxxxxx 或 0x-xxxxxxx）');
			ok = false;
		}

		const addr = recipientAddress.value.trim();
		if (addr.length < 6 || addr.length > 120) {
			setFieldError(recipientAddress, '收貨人地址需 6–120 字');
			ok = false;
		}

		// 聚焦到第一個錯誤欄位（無彈窗）
		if (!ok) {
			const firstInvalid =
				[recipientName, recipientPhone, recipientAddress].find((el) => el.classList.contains('is-invalid'));
			firstInvalid?.focus();
		}
		return ok;
	}

	// ====== 資料取得 ======
	async function getCheckoutSummary() {
		try {
			const saved = sessionStorage.getItem('cartSummary');
			if (saved) {
				const s = JSON.parse(saved);
				if (s && Array.isArray(s.items) && typeof s.subtotal === 'number') {
					if (typeof s.usedPoints !== 'number') s.usedPoints = 0;
					return s;
				}
			}
			const resp = await apiFetch('/api/cart/summary');
			if (!resp.ok) throw new Error(`無法獲取購物車資訊（${resp.status}）`);
			const data = await resp.json();
			const summary = {
				items: data.items || [],
				subtotal: Number(data.totalAmount || 0),
				usedPoints: 0
			};
			sessionStorage.setItem('cartSummary', JSON.stringify(summary));
			return summary;
		} catch (e) {
			console.error(e);
			showError('載入購物車時發生錯誤，請稍後再試');
			return null;
		}
	}

	// ====== 畫面渲染 ======
	function render(summary) {
		orderItemsBox.innerHTML = '';
		(summary.items || []).forEach((item) => {
			const imgSrc = item.productPicture
				? `/frontend-template/assets/img/${item.productPicture}`
				: '/frontend-template/assets/images/default-product.png';

			const row = document.createElement('div');
			row.className = 'd-flex justify-content-between align-items-center mb-3';
			row.innerHTML = `
        <img src="${imgSrc}" alt="${item.productName}" class="img-fluid rounded"
             style="width:60px;height:60px;object-fit:cover"
             onerror="this.src='/frontend-template/assets/images/default-product.png'">
        <span class="flex-grow-1 mx-3">${item.productName}</span>
        <span>${item.quantity} × ${money(item.unitPrice)}</span>
        <span class="fw-bold" style="width:110px;text-align:right;">${money(item.subtotal)}</span>
      `;
			orderItemsBox.appendChild(row);
		});

		const subtotal = Number(summary.subtotal || 0);
		const usedPoints = Math.max(0, Number(summary.usedPoints || 0));
		const orderAmt = Math.max(0, subtotal - usedPoints);

		totalAmountEl.textContent = money(subtotal);
		usedPointsEl.textContent = `-${money(usedPoints).replace('NT$', 'NT$')}`;
		orderAmountEl.textContent = money(orderAmt);
		grandTotalEl.textContent = money(orderAmt);

		// 回寫，以便送單
		sessionStorage.setItem('cartSummary', JSON.stringify({
			items: summary.items,
			subtotal,
			usedPoints
		}));
	}

	// ====== 建立訂單 ======
	async function createOrder() {
		if (!validateFields()) return null;

		const saved = sessionStorage.getItem('cartSummary');
		if (!saved) {
			showError('購物車資訊遺失，請回上一頁重新操作');
			return null;
		}
		const summary = JSON.parse(saved);

		const usedPoints = Math.max(0, Number(summary.usedPoints || 0));
		const orderData = {
			recipientName: recipientName.value.trim(),
			recipientPhone: recipientPhone.value.trim(),
			recipientAddress: recipientAddress.value.trim(),
			usedPoints,
			items: (summary.items || []).map(it => ({
				productId: it.productId,
				quantity: it.quantity
			}))
		};

		try {
			const resp = await apiFetch('/api/checkout', {
				method: 'POST',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify(orderData)
			});

			if (!resp.ok) {
				// 把後端的欄位錯誤映射到紅字（若有）
				const txt = await resp.text().catch(() => '');
				try {
					const err = JSON.parse(txt);
					if (err && err.errors) {
						if (err.errors.recipientName) setFieldError(recipientName, err.errors.recipientName);
						if (err.errors.recipientPhone) setFieldError(recipientPhone, err.errors.recipientPhone);
						if (err.errors.recipientAddress) setFieldError(recipientAddress, err.errors.recipientAddress);
					} else {
						showError(err?.message || '建立訂單失敗，請稍後再試');
					}
				} catch {
					showError('建立訂單失敗，請稍後再試');
				}
				return null;
			}

			clearAllErrors();
			return await resp.json();
		} catch (e) {
			console.error(e);
			showError('建立訂單時發生錯誤，請稍後再試');
			return null;
		}
	}

	// ====== 主流程 ======
	const summary = await getCheckoutSummary();
	if (!summary || !summary.items || summary.items.length === 0) {
		showError('您的購物車是空的，無法進行結帳');
		if (linePayBtn) linePayBtn.disabled = true;
		if (creditCardBtn) creditCardBtn.disabled = true;
		return;
	}

	render(summary);
	linePayBtn.disabled = false;
	creditCardBtn.disabled = false;

	// ====== 信用卡 / ECPay ======
	creditCardBtn.addEventListener('click', async () => {
		creditCardBtn.disabled = true;
		const oldHtml = creditCardBtn.innerHTML;
		creditCardBtn.innerHTML = '<span class="spinner-border spinner-border-sm"></span>&nbsp;處理中...';

		const order = await createOrder();
		if (order && order.orderId) {
			sessionStorage.removeItem('cartSummary');
			location.href = `/payment/ecpay?orderId=${order.orderId}`;
		} else {
			creditCardBtn.disabled = false;
			creditCardBtn.innerHTML = oldHtml;
		}
	});

	// ====== LINE Pay ======
	linePayBtn.addEventListener('click', async () => {
		linePayBtn.disabled = true;
		const oldHtml = linePayBtn.innerHTML;
		linePayBtn.innerHTML = '<span class="spinner-border spinner-border-sm"></span>&nbsp;處理中...';

		const order = await createOrder();
		if (order && order.orderId) {
			try {
				const resp = await apiFetch('/api/line-pay/request', {
					method: 'POST',
					headers: { 'Content-Type': 'application/json' },
					body: JSON.stringify({ orderId: order.orderId })
				});
				if (!resp.ok) {
					showError('無法取得 LINE Pay 付款連結，請稍後再試');
					linePayBtn.disabled = false;
					linePayBtn.innerHTML = oldHtml;
					return;
				}
				const data = await resp.json();
				if (data.paymentUrl) {
					sessionStorage.removeItem('cartSummary');
					location.href = data.paymentUrl;
				} else {
					showError('LINE Pay 回應中未包含付款連結');
					linePayBtn.disabled = false;
					linePayBtn.innerHTML = oldHtml;
				}
			} catch (e) {
				console.error(e);
				showError('LINE Pay 處理失敗，請稍後再試');
				linePayBtn.disabled = false;
				linePayBtn.innerHTML = oldHtml;
			}
		} else {
			linePayBtn.disabled = false;
			linePayBtn.innerHTML = oldHtml;
		}
	});
});
