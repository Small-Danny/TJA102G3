let csrfToken = null;
let csrfHeaderName = null;

/**
 * 初始化 CSRF Token（快取一次即可）
 */
async function initCsrfToken() {
	if (csrfToken && csrfHeaderName) {
		return; // 已經有了就不再打
	}

	const response = await fetch('/api/csrf-token', { credentials: 'include' });
	if (!response.ok) {
		throw new Error('無法取得 CSRF Token，請重新登入');
	}

	const data = await response.json();
	csrfToken = data.token;
	csrfHeaderName = data.headerName;
}

/**
 * 包裝 callAPI，讓它自動帶上 CSRF Token
 *
 * @param {string} url
 * @param {object} options
 */
async function csrfCallAPI(url, options = {}) {
	await initCsrfToken();

	// 合併 header，避免覆蓋原本設定
	options.headers = {
		...(options.headers || {}),
		[csrfHeaderName]: csrfToken
	};

	options.credentials = 'include';

	return callAPI(url, options); // 呼叫你 sport-api.js 的 callAPI
}

/**
 * 包裝 safeCallAPI，讓它自動帶上 CSRF Token
 *
 * @param {string} url
 * @param {object} options
 */
async function csrfSafeCallAPI(url, options = {}) {
	try {
		const data = await csrfCallAPI(url, options);
		return {
			retResult: true,
			retData: data,
			retMsg: 'csrfSafeCallAPI success'
		};
	} catch (err) {
		return {
			retResult: false,
			retData: null,
			retMsg: err.message || 'csrfSafeCallAPI error: 未知錯誤'
		};
	}
}
