fetch('/frontend-template/header.txt?v=' + Date.now(), { cache: 'no-store' })
    .then(r => r.text())
    .then(html => {
        document.getElementById('header').innerHTML = html;
    });