// assets/js/checkout.js
// 功能：
// 1) 讀 /api/cart/{userId}/summary 顯示購物明細與總計
// 2) 從上一頁取 usedPoints（sessionStorage.usedPoints）
// 3) 表單前端驗證（HTML5）＋ 將後端 ProblemDetail.errors 顯示在對應欄位
// 4) 送出 POST /api/checkout 建立訂單，成功後導向 pay.html

(function() {
	'use strict';

	// ====== 可調整區 ======
	const API_CART_SUMMARY = (uid) => `/api/cart/${uid}/summary`;
	const API_CHECKOUT = '/api/checkout';
	const PLACEHOLDER_IMG = '/assets/images/placeholder.png'; // summary 未帶圖時顯示
	const DEFAULT_USER_ID = 1; // 萬一沒登入，最後保底用
	// =====================

	// 從上一頁存的資料取 userId / usedPoints（cart.js 會寫 localStorage.uid + sessionStorage.usedPoints）
	const userId = Number(localStorage.getItem('uid') || DEFAULT_USER_ID);
	const usedPoints = Math.max(0, Number(sessionStorage.getItem('usedPoints') || '0') || 0);

	// DOM 節點
	const $form = document.getElementById('checkout-form');
	const $items = document.getElementById('order-items');
	const $total = document.getElementById('total-amount');
	const $used = document.getElementById('used-points');
	const $order = document.getElementById('order-amount');
	const $grand = document.getElementById('grand-total');
	const $checkoutBtns = document.querySelectorAll('.checkout-btn');

	// 欄位名稱對照：後端 errors 的 key（camelCase） -> 表單 input 的 name（snake_case）
	const FIELD_MAP = {
		recipientName: 'recipient_name',
		recipientPhone: 'recipient_phone',
		recipientAddress: 'recipient_address',
		usedPoints: null,
		userId: null
	};

	// 格式化貨幣
	const currency = (n) => new Intl.NumberFormat('zh-TW', { style: 'currency', currency: 'TWD' }).format(n);

	// 安全字串
	const esc = (s) => String(s).replace(/[&<>"']/g, m => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[m]));

	// 切換結帳按鈕可用狀態
	function setCheckoutDisabled(disabled) {
		$checkoutBtns.forEach(b => b.disabled = !!disabled);
	}

	// 顯示後端欄位錯誤在對應欄位下（加上 .is-invalid 並插入錯誤訊息）
	function showServerErrors(form, errors) {
		// 清除舊的
		form.querySelectorAll('.field-error').forEach(el => el.remove());
		['recipient_name', 'recipient_phone', 'recipient_address'].forEach(n => {
			if (form[n]) form[n].classList.remove('is-invalid');
		});

		if (!errors) return;

		Object.entries(errors).forEach(([serverField, msg]) => {
			const inputName = FIELD_MAP[serverField];
			if (!inputName) return; // 沒對應表單欄位就略過（例如 usedPoints 超扣類訊息，可額外 alert）
			const input = form[inputName];
			if (!input) return;

			input.classList.add('is-invalid');

			const hint = document.createElement('div');
			hint.className = 'field-error';
			hint.style.cssText = 'color:#d33;font-size:12px;margin-top:4px;';
			hint.textContent = msg;
			input.insertAdjacentElement('afterend', hint);
		});

		// 聚焦第一個錯欄位
		const firstKey = Object.keys(errors)[0];
		const inputName = FIELD_MAP[firstKey];
		if (inputName && form[inputName]) {
			form[inputName].focus();
			form[inputName].scrollIntoView({ behavior: 'smooth', block: 'center' });
		}
	}

	// 載入購物車摘要
	async function loadSummary() {
		const res = await fetch(API_CART_SUMMARY(userId), { cache: 'no-store' });
		if (!res.ok) throw new Error('載入購物車摘要失敗');
		const data = await res.json(); // { items:[{productName,unitPrice,quantity,subtotal,...}], totalAmount }

		renderItems(data.items || []);
		renderTotals(Number(data.totalAmount || 0), usedPoints);
	}

	// 渲染右側商品
	function renderItems(items) {
		if (!items.length) {
			$items.innerHTML = `<p class="text-muted">購物車是空的</p>`;
			setCheckoutDisabled(true);
			return;
		}
		setCheckoutDisabled(false);

		$items.innerHTML = items.map(it => `
      <div class="order-item">
        <img src="${esc(it.imageUrl || PLACEHOLDER_IMG)}" alt="">
        <div style="flex:1;">
          <div class="name">${esc(it.productName)}</div>
          <div>單價：${currency(it.unitPrice)}　×　${it.quantity}</div>
        </div>
        <div class="price">${currency(it.subtotal)}</div>
      </div>
    `).join('');
	}

	// 渲染總計（扣點不超扣）
	function renderTotals(totalAmount, usedPts) {
		const safeUsed = Math.max(0, Math.min(usedPts, totalAmount));
		const orderAmount = totalAmount;        // 未扣點金額（目前沒有運費/手續費）
		const grand = totalAmount - safeUsed;   // 扣點後實付

		$total.textContent = currency(totalAmount);
		$used.textContent = `-${currency(safeUsed)}`;
		$order.textContent = currency(orderAmount);
		$grand.textContent = currency(grand);
	}

	// 表單提交：前端驗證 -> 呼叫後端建單 -> 導到 pay.html
	if ($form) {
		$form.addEventListener('submit', async (e) => {
			// 空購物車就擋
			if ([...$checkoutBtns].some(b => b.disabled)) {
				e.preventDefault();
				alert('購物車為空，無法結帳');
				return;
			}

			// 先交給 HTML5 規則
			if (!$form.checkValidity()) {
				e.preventDefault();
				['recipient_name', 'recipient_phone', 'recipient_address'].forEach(n => {
					const el = $form[n]; if (el && !el.checkValidity()) el.classList.add('is-invalid');
				});
				const firstBad = $form.querySelector('.is-invalid');
				if (firstBad) firstBad.scrollIntoView({ behavior: 'smooth', block: 'center' });
				return;
			}

			// ✅ 真的送後端建單
			e.preventDefault();
			const btn = e.submitter || $checkoutBtns[0];
			btn.disabled = true;

			// 收集資料（你的 DTO 欄位名是 camelCase）
			const body = {
				userId: userId,
				recipientName: ($form.recipient_name.value || '').trim(),
				recipientPhone: ($form.recipient_phone.value || '').trim(),
				recipientAddress: ($form.recipient_address.value || '').trim(),
				usedPoints: usedPoints
			};

			try {
				const res = await fetch(API_CHECKOUT, {
					method: 'POST',
					headers: { 'Content-Type': 'application/json' },
					body: JSON.stringify(body)
				});

				if (!res.ok) {
					const pd = await res.json().catch(() => ({}));
					// 後端 GlobalExceptionHandler：{ title, detail, errors: {field: message} }
					if (pd && pd.errors) {
						showServerErrors($form, pd.errors);
					} else {
						alert(pd.detail || '建立訂單失敗，請稍後再試');
					}
					return;
				}

				const order = await res.json(); // OrdersDTO
				// 保存必要資訊，去付款頁
				sessionStorage.setItem('orderId', String(order.orderId));
				sessionStorage.setItem('orderCode', order.orderCode || '');
				location.href = 'pay.html';
			} catch (err) {
				console.error(err);
				alert('網路異常，請稍後再試');
			} finally {
				btn.disabled = false;
			}
		});

		// 使用者輸入時，移除 is-invalid 樣式
		['recipient_name', 'recipient_phone', 'recipient_address'].forEach(n => {
			const el = $form[n];
			if (el) el.addEventListener('input', () => el.classList.remove('is-invalid'));
		});
	}

	// 進頁就載入摘要
	loadSummary().catch(err => {
		console.error(err);
		$items.innerHTML = `<p class="text-danger">載入失敗，請稍後再試</p>`;
		setCheckoutDisabled(true);
	});
})();
