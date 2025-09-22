(function () {
	'use strict';

	// ====== 常數與 API 路徑設定 ======
	const API_CART_SUMMARY = (uid) => `/api/cart/${uid}/summary`;
	const API_CHECKOUT = '/api/checkout';
	const API_LINE_PAY_REQUEST = '/api/line-pay/request';
	
	const SUCCESS_URL = '/frontend-template/pay_success.html';
	const LOGIN_URL = '/frontend-template/login.html';
	const PLACEHOLDER_IMG = '/assets/images/placeholder.png';
	const DEFAULT_USER_ID = 1; // 預設使用者 ID

	// ====== 從儲存中讀取資料 ======
	const userId = Number(localStorage.getItem('uid') || DEFAULT_USER_ID);
	const usedPoints = Math.max(0, Number(sessionStorage.getItem('usedPoints') || '0') || 0);

	// ====== DOM 節點宣告 ======
	const $form = document.getElementById('checkout-form');
	const $items = document.getElementById('order-items');
	const $total = document.getElementById('total-amount');
	const $used = document.getElementById('used-points');
	const $order = document.getElementById('order-amount');
	const $grand = document.getElementById('grand-total');
	const $linePayBtn = document.getElementById('linePayBtn');
	const $creditCardBtn = document.getElementById('creditCardBtn');

	// ====== 輔助函式 ======
	const currency = (n) => `NT$${Number(n || 0).toLocaleString('en-US')}`;
	const esc = (s) => String(s).replace(/[&<>"']/g, m => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[m]));
	function setAllCheckoutButtonsDisabled(disabled) {
		if ($linePayBtn) $linePayBtn.disabled = disabled;
		if ($creditCardBtn) $creditCardBtn.disabled = disabled;
	}

	// ====== 核心功能 ======

	async function loadSummary() {
		try {
			const res = await apiFetch(API_CART_SUMMARY(userId));
			if (!res.ok) throw new Error('載入購物車摘要失敗');
			const data = await res.json();
			if (!data.items || data.items.length === 0) {
				$items.innerHTML = `<p class="text-muted">購物車是空的</p>`;
				setAllCheckoutButtonsDisabled(true);
				return;
			}
			$items.innerHTML = data.items.map(it => `
                <div class="order-item">
                    <img src="${esc(it.imageUrl || PLACEHOLDER_IMG)}" alt="${esc(it.productName)}">
                    <div style="flex:1;">
                        <div class="name">${esc(it.productName)}</div>
                        <div>單價：${currency(it.unitPrice)} × ${it.quantity}</div>
                    </div>
                    <div class="price">${currency(it.subtotal)}</div>
                </div>`).join('');
			const totalAmount = Number(data.totalAmount || 0);
			const safeUsed = Math.max(0, Math.min(usedPoints, totalAmount));
			const grand = totalAmount - safeUsed;
			$total.textContent = currency(totalAmount);
			$used.textContent = `-${currency(safeUsed)}`;
			$order.textContent = currency(totalAmount);
			$grand.textContent = currency(grand);
			setAllCheckoutButtonsDisabled(false);
		} catch (err) {
			console.error(err);
			$items.innerHTML = `<p class="text-danger">載入失敗，請稍後再試</p>`;
			setAllCheckoutButtonsDisabled(true);
		}
	}

async function handleCheckout(paymentMethod) {
		if (!$form.checkValidity()) {
			$form.reportValidity();
			return;
		}
		setAllCheckoutButtonsDisabled(true);

		try {
			// 步驟 1: 建立訂單 (所有付款方式共用)
			const createOrderResponse = await apiFetch(API_CHECKOUT, {
				method: 'POST',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify({
					userId: userId,
					recipientName: $form.recipient_name.value.trim(),
					recipientPhone: $form.recipient_phone.value.trim(),
					recipientAddress: $form.recipient_address.value.trim(),
					usedPoints: usedPoints
				})
			});

			if (createOrderResponse.status === 401) { /* ... 登入處理維持不變 ... */ }
			if (!createOrderResponse.ok) { /* ... 錯誤處理維持不變 ... */ }

			const newOrder = await createOrderResponse.json();
			const orderId = newOrder.orderId;
			console.log(`內部訂單建立成功，ID: ${orderId}，付款方式: ${paymentMethod}`);

			// 步驟 2: 根據付款方式，執行不同流程
			if (paymentMethod === 'LINE_PAY') {
                // LINE Pay 的流程維持原樣
				console.log(`正在為訂單 ${orderId} 請求 LINE Pay 連結...`);
				const linePayResponse = await apiFetch(API_LINE_PAY_REQUEST, { /* ... */ });
				if (!linePayResponse.ok) throw new Error('請求 LINE Pay 連結失敗');
				const linePayData = await linePayResponse.json();
				if (linePayData.paymentUrl) {
					window.location.href = linePayData.paymentUrl;
				} else {
					throw new Error('無法取得 LINE Pay 付款連結');
				}

			} else if (paymentMethod === 'CREDIT_CARD') {
				// ★★★ 核心修正點：不再 call API，而是直接導向我們的 Controller ★★★
                console.log(`訂單 ${orderId} 建立成功，準備重新導向至伺服器進行 ECPay 表單渲染...`);
                
                // 直接將瀏覽器導向到我們的後端 Controller，並帶上訂單 ID
                window.location.href = `/payment/ecpay?orderId=${orderId}`;
			}
		} catch (err) {
			console.error('結帳流程失敗:', err);
			alert('處理付款時發生錯誤，請稍後再試');
			setAllCheckoutButtonsDisabled(false); // 記得在出錯時也要解鎖按鈕
		}
	}

	// ====== 事件綁定 ======
	if ($linePayBtn) {
		$linePayBtn.addEventListener('click', () => handleCheckout('LINE_PAY'));
	}
	if ($creditCardBtn) {
		$creditCardBtn.addEventListener('click', () => handleCheckout('CREDIT_CARD'));
	}

	loadSummary();
})();