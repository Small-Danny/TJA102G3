// /frontend-template/assets/js/profile.js

document.addEventListener('DOMContentLoaded', function () {

    // =================================================================
    // 核心啟動函數 (整個頁面的入口)
    // =================================================================
    async function initializeProfilePage() {
        try {
            // 呼叫現有API，一次性獲取所有需要的會員資料
            const profileResponse = await fetch('/api/users/profile', { credentials: 'include', cache: 'no-store' });
            if (!profileResponse.ok) {
                throw new Error('使用者未登入或 Session 過期');
            }
            const user = await profileResponse.json();

            // 成功獲取資料後，才開始填充頁面
            populatePoints(user); // 填入點數
            loadUserOrders();     // 載入訂單

        } catch (error) {
            console.error('初始化會員中心頁面失敗:', error.message);
            Swal.fire({
                title: '您尚未登入',
                text: '將跳轉至登入頁面...',
                icon: 'warning',
                showConfirmButton: false,
                timer: 1500
            }).then(() => {
                window.location.href = '/frontend-template/login.html';
            });
        }
    }

    // =================================================================
    // 功能函數區
    // =================================================================

    /**
     * 填充「我的點數」區塊
     */
    function populatePoints(user) {
        const pointsDisplay = document.getElementById('points-balance-display');
        if (pointsDisplay) {
            // toLocaleString() 可以幫數字加上千分位，例如 1250 -> 1,250
            pointsDisplay.textContent = user.pointsBalance.toLocaleString();
        }
    }

    /**
     * 非同步載入並渲染「訂單管理」區塊
     */
    async function loadUserOrders() {
        const orderListContainer = document.querySelector('#orders .order-list');
        if (!orderListContainer) return;

        orderListContainer.innerHTML = '<p class="text-center text-muted">正在載入您的訂單...</p>';

        try {
            const response = await fetch('/api/orders/my', { credentials: 'include' });
            if (!response.ok) throw new Error('無法獲取訂單');

            const orders = await response.json();
            orderListContainer.innerHTML = ''; // 清空載入提示

            if (!orders || orders.length === 0) {
                orderListContainer.innerHTML = '<p class="text-center text-muted">您目前沒有任何訂單記錄。</p>';
                return;
            }

            orders.forEach(order => {
                const itemsHtml = order.items && order.items.length > 0
                    ? `<ul class="list-group list-group-flush mt-2">
                        ${order.items.map(item => `
                            <li class="list-group-item d-flex justify-content-between align-items-center flex-wrap">
                                <span>${item.productName || 'N/A'}</span>
                                <div class="text-nowrap">
                                    <span class="me-3">數量: ${item.quantity}</span>
                                    <span>單價: $${item.buyPrice.toLocaleString()}</span>
                                </div>
                            </li>
                        `).join('')}
                   </ul>`
                    : '<p class="text-muted small mt-2">此訂單沒有明細資料。</p>';

                const orderHtml = `
                    <div class="order-item mb-4 border p-3 rounded bg-light">
                        <div class="d-flex justify-content-between align-items-center flex-wrap">
                           <h5 class="mb-1 me-3">訂單編號：${order.orderCode}</h5>
                           <span class="badge ${order.paymentStatus === 1 ? 'bg-success' : 'bg-warning'}">${order.paymentStatus === 1 ? '已付款' : '未付款'}</span>
                        </div>
                        <p class="mb-1 text-secondary">
                            日期：${new Date(order.orderDate).toLocaleDateString()} |
                            金額：NT$ ${order.totalPrice.toLocaleString()}
                        </p>
                        ${itemsHtml}
                    </div>`;
                orderListContainer.insertAdjacentHTML('beforeend', orderHtml);
            });

        } catch (error) {
            console.error('載入訂單失敗:', error);
            orderListContainer.innerHTML = '<p class="text-center text-danger">載入訂單時發生錯誤，請稍後再試。</p>';
        }
    }

    // =================================================================
    // 頁面啟動
    // =================================================================
    initializeProfilePage();
});