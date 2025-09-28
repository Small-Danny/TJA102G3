// checkout.js (最終修正版 - 2.0)
document.addEventListener('DOMContentLoaded', async () => {
    const form = document.getElementById('checkout-form');
    if (!form) return;

    const recipientName = document.getElementById('recipient_name');
    const recipientPhone = document.getElementById('recipient_phone');
    const recipientAddress = document.getElementById('recipient_address');
    const orderItemsContainer = document.getElementById('order-items');
    const totalAmountEl = document.getElementById('total-amount');
    const usedPointsEl = document.getElementById('used-points');
    const orderAmountEl = document.getElementById('order-amount');
    const grandTotalEl = document.getElementById('grand-total');
    const linePayBtn = document.getElementById('linePayBtn');
    const creditCardBtn = document.getElementById('creditCardBtn');

    function showError(message) {
        orderItemsContainer.innerHTML = `<div class="alert alert-danger">${message}</div>`;
        if (linePayBtn) linePayBtn.disabled = true;
        if (creditCardBtn) creditCardBtn.disabled = true;
    }

    // ★★★ 這是新增的核心函式 ★★★
    // 在頁面載入時，主動從後端 API 獲取最新的購物車完整資訊
    async function fetchAndRenderCheckoutPage() {
        try {
            console.log("checkout.js: 正在從 /api/cart/summary 獲取最新購物車資訊...");
            const response = await apiFetch('/api/cart/summary');

            if (!response.ok) {
                if (response.status === 401) {
                    showError('您尚未登入，請先登入後再結帳。');
                    return null;
                }
                throw new Error('無法獲取購物車資訊');
            }

            const summary = await response.json();

            if (!summary || !summary.items || summary.items.length === 0) {
                showError('您的購物車是空的，無法進行結帳。');
                return null;
            }

            console.log("checkout.js: 成功獲取購物車資訊", summary);

            sessionStorage.setItem('cartSummary', JSON.stringify(summary));
            render(summary);
            return summary;

        } catch (error) {
            console.error('獲取結帳資訊失敗:', error);
            showError(`載入購物車時發生錯誤: ${error.message}`);
            return null;
        }
    }

    function render(summary) {
        orderItemsContainer.innerHTML = '';
        summary.items.forEach(item => {
            const itemHtml = `
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <img src="${item.productPicture || '/frontend-template/assets/img/images/default-product.png'}" alt="${item.productName}" class="img-fluid rounded" style="width: 60px;">
                    <span class="flex-grow-1 mx-3">${item.productName}</span>
                    <span>${item.quantity} x ${item.unitPrice.toLocaleString()}</span>
                    <span class="font-weight-bold" style="width: 100px; text-align: right;">$${item.subtotal.toLocaleString()}</span>
                </div>
            `;
            orderItemsContainer.insertAdjacentHTML('beforeend', itemHtml);
        });

        totalAmountEl.textContent = summary.totalAmount.toLocaleString();
        orderAmountEl.textContent = summary.totalAmount.toLocaleString();
        grandTotalEl.textContent = summary.totalAmount.toLocaleString();
    }

    async function createOrder() {
        // ★★★ 在函式開頭加上這段檢查 ★★★
        if (recipientName.value.trim().length < 2) {
            alert('收貨人姓名不得少於 2 個字');
            return null; // 中斷執行
        }
        if (recipientAddress.value.trim().length < 6) {
            alert('收貨人地址不得少於 6 個字');
            return null; // 中斷執行
        }
        const summaryJson = sessionStorage.getItem('cartSummary');
        if (!summaryJson) {
            alert('購物車資訊遺失，請重試');
            return null;
        }

        const summary = JSON.parse(summaryJson);
        const orderData = {
            recipientName: recipientName.value,
            recipientPhone: recipientPhone.value,
            recipientAddress: recipientAddress.value,
            usedPoints: usedPointsEl.value ? parseInt(usedPointsEl.value, 10) : 0,
            items: summary.items.map(item => ({
                productId: item.productId,
                quantity: item.quantity
            }))
        };

        try {
            const response = await apiFetch('/api/checkout', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(orderData)
            });

            if (!response.ok) {
                if (response.status === 401) {
                    alert('您需要登入才能結帳，將為您導向登入頁面。');
                    window.location.href = `/frontend-template/login.html?redirect=${encodeURIComponent(window.location.href)}`;
                    return null;
                }
                const errData = await response.json();
                throw new Error(errData.message || '建立訂單失敗');
            }
            return await response.json();
        } catch (error) {
            console.error('建立訂單時發生錯誤:', error);
            alert(`建立訂單時發生錯誤: ${error.message}`);
            return null;
        }
    }

    // ===== 主執行流程 =====
    const summary = await fetchAndRenderCheckoutPage();

    if (summary) {
        // 啟用按鈕
        linePayBtn.disabled = false;
        creditCardBtn.disabled = false;

        // 【信用卡/ECPay 按鈕事件】
        creditCardBtn.addEventListener('click', async (e) => {
            creditCardBtn.disabled = true;
            creditCardBtn.innerHTML = '<span class="spinner-border spinner-border-sm"></span> 處理中...';

            const order = await createOrder();
            if (order && order.orderId) {
                sessionStorage.removeItem('cartSummary');
                window.location.href = `/payment/ecpay?orderId=${order.orderId}`;
            } else {
                creditCardBtn.disabled = false;
                creditCardBtn.innerHTML = '信用卡 / ECPay';
            }
        });

        // 【LINE Pay 按鈕事件】
        linePayBtn.addEventListener('click', async (e) => {
            linePayBtn.disabled = true;
            linePayBtn.innerHTML = '<span class="spinner-border spinner-border-sm"></span> 處理中...';

            const order = await createOrder();
            if (order && order.orderId) {
                try {
                    const response = await apiFetch('/api/line-pay/request', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({ orderId: order.orderId })
                    });

                    if (!response.ok) throw new Error('無法取得 LINE Pay 付款連結');

                    const data = await response.json();
                    if (data.paymentUrl) {
                        sessionStorage.removeItem('cartSummary');
                        window.location.href = data.paymentUrl;
                    } else {
                        throw new Error('LINE Pay 回應中未包含付款連結');
                    }
                } catch (error) {
                    alert('LINE Pay 處理失敗: ' + error.message);
                    linePayBtn.disabled = false;
                    linePayBtn.innerHTML = 'LINE Pay 付款';
                }
            } else {
                linePayBtn.disabled = false;
                linePayBtn.innerHTML = 'LINE Pay 付款';
            }
        });
    } else {
        // 購物車是空的，禁用按鈕
        linePayBtn.disabled = true;
        creditCardBtn.disabled = true;
    }
    // ★★★ 在這裡補上遺失的右大括號和分號 ★★★
});