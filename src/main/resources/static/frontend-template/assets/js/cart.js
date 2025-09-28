// cart.js (最終強健版)

/**
 * 全域可用的函式，用於將商品加入購物車
 * (此函式在 productdetail.js 與 productlist.js 中被呼叫)
 */
async function addItemToCart(productId, quantity = 1) {
    try {
        const response = await apiFetch('/api/cart/items', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ 
                productId: Number(productId), 
                quantity: Number(quantity) 
            })
        });

        if (!response.ok) {
            const errData = await response.json();
            throw new Error(errData.message || '加入購物車失敗');
        }
        
        document.dispatchEvent(new Event('cart:changed')); 
        return await response.json();

    } catch (err) {
        console.error('addItemToCart 失敗:', err);
        throw err; 
    }
}