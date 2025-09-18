// assets/js/pay.js
// 從 sessionStorage 取 orderId / orderCode / localStorage 取 uid
// 送 POST /api/payments/mock（你的假金流）
// 成功 → 導到 pay_success.html；失敗 → 顯示錯誤或停在此頁

(function() {
	'use strict';

	const API_MOCK_PAY = '/api/payments/mock';

	const userId = Number(localStorage.getItem('uid') || '1');
	const orderId = Number(sessionStorage.getItem('orderId') || '0');
	const orderCode = sessionStorage.getItem('orderCode') || `#${orderId}`;

	document.getElementById('orderCode').textContent = orderCode;

	const $form = document.getElementById('payForm');
	const $msg = document.getElementById('msg');

	$form.addEventListener('submit', async (e) => {
		e.preventDefault();

		// 簡單前端驗證
		let bad = false;
		[...$form.elements].forEach(el => {
			if (el.hasAttribute('required') && !el.value.trim()) { el.classList.add('is-invalid'); bad = true; }
			else el.classList.remove('is-invalid');
		});
		if (bad) return;

		try {
			const res = await fetch(API_MOCK_PAY, {
				method: 'POST',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify({ userId, orderId, success: true }) // 想測試失敗就改成 false
			});
			if (!res.ok) { $msg.textContent = '付款失敗（伺服器錯誤）'; return; }
			const order = await res.json();
			if (order.paymentStatus === 1) {
				location.href = 'pay_success.html';
			} else {
				$msg.textContent = '付款未成功，請重試';
			}
		} catch (err) {
			console.error(err);
			$msg.textContent = '網路異常，請稍後再試';
		}
	});
})();
