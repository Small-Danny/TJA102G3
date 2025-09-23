(function () {
	'use strict';

	// ====== 常數與 API 路徑設定 ======
	const API_CART_SUMMARY = (uid) => `/api/cart/${uid}/summary`;
	const API_CHECKOUT = '/api/checkout';
	const API_LINE_PAY_REQUEST = '/api/line-pay/request';

	const SUCCESS_URL = '/frontend-template/pay_success.html';
	const LOGIN_URL = '/frontend-template/login.html';
	const PLACEHOLDER_IMG = '/frontend-template/assets/images/placeholder.png';;
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
		// ✨✨✨ 在這裡加入最關鍵的登入檢查 ✨✨✨
		const isLoggedIn = localStorage.getItem('uid'); // 檢查 localStorage 是否有 uid

		if (!isLoggedIn) {
			alert('請先登入會員，再進行結帳。');

			// 為了更好的使用者體驗，我們可以記住使用者原本想去結帳
			// 這樣登入成功後，就可以自動跳轉回來
			sessionStorage.setItem('redirectAfterLogin', window.location.href);

			window.location.href = LOGIN_URL; // LOGIN_URL 變數已在檔案開頭定義
			return; // 中斷後續所有結帳流程
		}
		// 檢查表單驗證
		if (!$form.checkValidity()) {
			$form.reportValidity();
			return;
		}
		setAllCheckoutButtonsDisabled(true);

		try {
			// ✨✨✨ 關鍵還原點：這一段是您目前版本缺少的 ✨✨✨
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

			// 檢查建立訂單的回應
			if (createOrderResponse.status === 401) {
				alert('您尚未登入，將跳轉至登入頁面');
				window.location.href = LOGIN_URL;
				return;
			}
			if (!createOrderResponse.ok) {
				throw new Error('建立訂單失敗');
			}
			// ✨✨✨ 關鍵還原點結束 ✨✨✨

			const newOrder = await createOrderResponse.json();
			const orderId = newOrder.orderId;
			console.log(`內部訂單建立成功，ID: ${orderId}，付款方式: ${paymentMethod}`);

			// 步驟 2: 根據付款方式，執行不同流程
			if (paymentMethod === 'LINE_PAY') {
				console.log(`正在為訂單 ${orderId} 請求 LINE Pay 連結...`);

				const linePayResponse = await apiFetch(API_LINE_PAY_REQUEST, {
					method: 'POST',
					headers: { 'Content-Type': 'application/json' },
					body: JSON.stringify({ orderId: orderId })
				});

				if (!linePayResponse.ok) {
					const errorData = await linePayResponse.json();
					throw new Error(`請求 LINE Pay 連結失敗: ${errorData.message || linePayResponse.statusText}`);
				}

				const linePayData = await linePayResponse.json();
				if (linePayData.paymentUrl) {
					window.location.href = linePayData.paymentUrl;
				} else {
					throw new Error('無法取得 LINE Pay 付款連結');
				}

			} else if (paymentMethod === 'CREDIT_CARD') {
				console.log(`訂單 ${orderId} 建立成功，準備重新導向至伺服器進行 ECPay 表單渲染...`);
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