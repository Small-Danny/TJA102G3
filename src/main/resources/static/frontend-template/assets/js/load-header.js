// /frontend-template/assets/js/load-header.js
(function () {
  const container = document.getElementById('header');
  if (!container) return;

  fetch('/frontend-template/header.txt?v=' + Date.now(), {
    cache: 'no-store',
    credentials: 'same-origin'
  })
    .then(r => r.text())
    .then(html => {
      container.innerHTML = html;

      // 重要：把片段中的 <script> 重新建立，讓它們實際執行
      const scripts = container.querySelectorAll('script');
      scripts.forEach(old => {
        const s = document.createElement('script');

        // 複製所有屬性（src、type、defer、async…）
        for (const { name, value } of old.attributes) {
          s.setAttribute(name, value);
        }

        if (old.src) {
          // 外部 script：直接掛上 <head>（或 <body>）
          document.head.appendChild(s);
        } else {
          // 內嵌 script：把內容搬過去再插入
          s.text = old.text || old.textContent || '';
          document.head.appendChild(s);
        }

        old.remove();
      });

      // 若你希望讓外部頁面知道 header 載入完成，可發事件
      document.dispatchEvent(new Event('header:mounted'));
    })
    .catch(err => {
      console.error('載入 header 失敗：', err);
    });
})();
