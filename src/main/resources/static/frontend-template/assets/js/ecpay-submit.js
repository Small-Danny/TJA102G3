document.addEventListener('DOMContentLoaded', function() {
    const ecpayForm = document.getElementById('ecpay-form');
    if (ecpayForm) {
        ecpayForm.submit(); // 觸發表單提交到綠界支付頁面
    } else {
        console.error('未找到綠界支付表單，無法自動提交');
    }
});