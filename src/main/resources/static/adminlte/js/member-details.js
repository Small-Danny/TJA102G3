// /adminlte/dist/js/member-details.js

$(function () {
    // 從當前頁面的 URL 中取得 userId
    const pathParts = window.location.pathname.split('/');
    const userId = pathParts[pathParts.length - 1];

    if (!userId || isNaN(userId)) {
        console.error('無法從 URL 獲取有效的使用者 ID');
        $('#order-list-container').html('<p class="text-danger">錯誤：無法識別使用者 ID。</p>');
        return;
    }

    // 載入該使用者的訂單紀錄
    loadUserOrders(userId);

    /**
     * 根據 userId 載入並渲染訂單紀錄
     */
    async function loadUserOrders(userId) {
        const orderListContainer = $('#order-list-container');
        if (!orderListContainer.length) return;

        orderListContainer.html('<p class="text-muted text-center">正在載入訂單紀錄...</p>');

        try {
            // ▼▼▼【修改點 1：API 路徑】▼▼▼
            // 將原本呼叫 /api/admin/users/{userId}/orders 的路徑，
            // 改成呼叫現有的、使用查詢參數的 API
            const response = await fetch(`/api/admin/orders?userId=${userId}`);
            // ▲▲▲【修改結束】▲▲▲

            if (!response.ok) {
                throw new Error(`伺服器錯誤: ${response.statusText}`);
            }

            // ▼▼▼【修改點 2：解析 Page 物件】▼▼▼
            // 後端回傳的是 Page 物件，訂單列表在 content 屬性裡
            const pageData = await response.json();
            const orders = pageData.content;
            // ▲▲▲【修改結束】▲▲▲

            orderListContainer.empty();

            if (!orders || orders.length === 0) {
                orderListContainer.html('<p class="text-muted text-center">該會員沒有任何訂單記錄。</p>');
                return;
            }

            orders.forEach(order => {
                const itemsHtml = order.items && order.items.length > 0
                    ? `<table class="table table-sm table-hover mt-2">
            <tbody>
            ${order.items.map(item => `
                <tr>
                    <td>${item.productName || 'N/A'}</td>
                    <td class="text-end">x ${item.quantity}</td>
                    <td class="text-end" style="width: 120px;">$${item.buyPrice.toLocaleString()}</td>
                </tr>
            `).join('')}
            </tbody>
           </table>`
                    : '<p class="text-muted small mt-2">此訂單沒有明細資料。</p>';

                const orderHtml = `
        <div class="order-item mb-3">
            <div class="d-flex justify-content-between align-items-center">
               <h6 class="mb-1 font-weight-bold text-primary">${order.orderCode}</h6>
               <span class="badge ${order.paymentStatus === 1 ? 'badge-success' : 'badge-warning'}">${order.paymentStatus === 1 ? '已付款' : '未付款'}</span>
            </div>
            <p class="mb-1 text-secondary small">
                ${new Date(order.orderDate).toLocaleDateString()}
                <span class="mx-2">|</span>
                總金額：NT$ ${order.totalPrice.toLocaleString()}
            </p>
            ${itemsHtml}
            <hr class="mt-2 mb-0">
        </div>`;
                orderListContainer.append(orderHtml);
            });

        } catch (error) {
            console.error('載入訂單失敗:', error);
            orderListContainer.html('<p class="text-danger text-center">載入訂單時發生錯誤。</p>');
        }
    }
});