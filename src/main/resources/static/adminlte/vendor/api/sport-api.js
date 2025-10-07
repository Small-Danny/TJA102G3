// 預設 alert
const globalApiHandler = {
	onParameterError: (msg) => alert(msg)
};

// TODO: 記得改回!!!
// const BASE_API_URL = window.location.origin;
const BASE_API_URL = 'http://localhost:8080';

async function callAPI(url, options = {}) {
	const res = await fetch(url, {
		// method: 'POST',
		// headers: {
		// 	'Content-Type': 'application/json'
		// },
		// body: JSON.stringify(payload ?? {}),
		...options
	});

	if (!res.ok) {
		throw new Error('callAPI error: HTTP 錯誤: ' + res.status);
	}

	let data;
	try {
		// 後端回 void ，可能沒 body，先用 text() 判斷
		const text = await res.text();
		if (!text) {
			// 回傳 null，表示後端沒內容
			return null;
		}
		data = JSON.parse(text);
	} catch (err) {
		throw new Error('callAPI error: 回傳格式錯誤，非 JSON');
	}

	// 接受後端回傳 null JSON
	if (data === null) {
		return null;
	}
	if (typeof data !== 'object') {
		throw new Error('callAPI error: 回傳資料異常，非物件格式');
	}

	if (!('returnCode' in data) || !('returnMsg' in data)) {
		throw new Error('callAPI error: 回傳格式不符合 ApiResponseDTO');
	}

	if (data.returnCode === 'P500') {
		// alert or toast
		if (globalApiHandler.onParameterError) {
			globalApiHandler.onParameterError(data.returnMsg);
		}
		throw new Error(data.returnMsg || 'callAPI error: P500錯誤');
	}

	if (data.returnCode !== '000' && data.returnCode !== 'P500') {
		throw new Error(data.returnMsg || 'callAPI error: 未知錯誤');
	}

	return data.hasOwnProperty('returnData') ? data.returnData : null;
}

async function safeCallAPI(url, options = {}) {
	try {
		const data = await callAPI(url, options);
		return {
			retResult: true,
			retData: data,
			retMsg: 'safeCallAPI success'
		};
	} catch (err) {
		return {
			retResult: false,
			retData: null,
			retMsg: err.message || 'safeCallAPI error: 未知錯誤'
		};
	}
}
