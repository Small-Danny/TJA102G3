/* global Chart */
(function () {
  'use strict';
  console.log('[analytics] script loaded');

  const API_BASE = `${window.location.origin}/api/analytics`;

  // ------- 狀態（初始化後會用 select 的實際值覆蓋） -------
  const state = {
    userId: 1,
    metric: 'workout-time',   // workout-time | tasks | calories
    range: 'today',           // today | week | month
    type: 'bar',              // bar | line
    unitMap: { 'workout-time': '分鐘', 'calories': '大卡', 'tasks': '次' }
  };

  const labelI18n = {
    metric(v){ return ({'workout-time':'運動時長','calories':'消耗熱量','tasks':'任務數'})[v] || v; },
    range(v){ return ({today:'今天', week:'近一週', month:'近一月'})[v] || v; }
  };

  // today -> 00:00；其他 range 原樣
  function fmtLabel(label) {
    const s = String(label);
    if (state.range === 'today') {
      const t = s.indexOf('T');
      if (t > 0 && s.length >= t + 6 && s[t+3] === ':') return s.slice(t + 1, t + 6); // HH:mm
      const n = Number(s);
      if (!Number.isNaN(n) && n >= 0 && n <= 23) return `${String(n).padStart(2,'0')}:00`;
    }
    return s;
  }

  // ============ Chart ============
  let chart = null;

  function updateTitle(el) {
    if (!el) return;
    el.textContent = `${labelI18n.metric(state.metric)}｜${labelI18n.range(state.range)}｜${state.type === 'line' ? '折線圖' : '長條圖'}`;
  }

  function buildChart(canvasEl, labels, values, unit) {
    if (!window.Chart) {
      console.error('[analytics] Chart.js 未載入');
      return;
    }
    const dispLabels = (labels || []).map(fmtLabel);

    const options = {
      responsive: true,
      maintainAspectRatio: false,
      interaction: { mode: 'index', intersect: false },
      animation: false,
      scales: { y: { beginAtZero: true, title: { display: true, text: unit } } }
    };

    const dataset = { label: labelI18n.metric(state.metric), data: values, borderWidth: 2 };

    chart = new Chart(canvasEl, {
      type: state.type,             // 只允許 bar / line
      data: { labels: dispLabels, datasets: [dataset] },
      options
    });
  }

  function ensureChart(canvasEl, labels, values, unit) {
    if (!canvasEl) return;

    // 保險：若外部塞了非法型別（例如 pie），強制回 bar
    if (state.type !== 'bar' && state.type !== 'line') state.type = 'bar';

    const typeMismatch = chart && chart.config && chart.config.type !== state.type;
    if (!chart || typeMismatch) {
      if (chart) { try { chart.destroy(); } catch (e) { /* ignore */ } chart = null; }
      buildChart(canvasEl, labels, values, unit);
      return;
    }
    chart.data.labels = (labels || []).map(fmtLabel);
    chart.data.datasets[0].data = values;
    chart.update();
  }

  function renderSummary(summaryWrap, totalEl, labels, values, unit, total){
    if (summaryWrap) {
      const html = labels.map((d,i) =>
        `<li class="d-flex justify-content-between"><span>${fmtLabel(d)}</span><span>${values[i] ?? 0} ${unit}</span></li>`
      ).join('');
      summaryWrap.innerHTML = html;
    }
    if (totalEl) totalEl.textContent = `${total ?? (values?.reduce((a,b)=>a+(+b||0),0))} ${unit}`;
  }

  // ============ 載入資料 ============
  let currentReq = 0;
  async function loadSeries(chartTitleEl, canvasEl, summaryWrapEl, totalEl){
    const reqId = ++currentReq;
    const params = new URLSearchParams({ metric: state.metric, range: state.range, userId: String(state.userId) });
    const url = `${API_BASE}/series?${params.toString()}`;
    console.log('[analytics] fetch', url);

    try {
      const res = await fetch(url, { credentials: 'same-origin' });
      const json = await res.json();
      if (reqId !== currentReq) return;

      const { labels = [], data = [], unit = state.unitMap[state.metric] || '', total } = json;
      const values = (data || []).map(x => Number(x) || 0);

      updateTitle(chartTitleEl);
      ensureChart(canvasEl, labels, values, unit);
      renderSummary(summaryWrapEl, totalEl, labels, values, unit, total);
    } catch (err) {
      console.error('[analytics] fetch failed', err);
    }
  }

  // ============ DOM Ready ============
  function onReady(fn){
    if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', fn, { once: true });
    else fn();
  }

  onReady(() => {
    console.log('[analytics] DOM ready');

    const chartTitleEl   = document.getElementById('chartTitle');
    const metricSelectEl = document.getElementById('metricSelect');
    const chartTypeEl    = document.getElementById('chartTypeSelect');
    const rangeTabEls    = document.querySelectorAll('#rangeTabs .nav-link');
    const summaryWrapEl  = document.getElementById('summaryList');
    const totalEl        = document.getElementById('totalValue');
    const canvasEl       = document.getElementById('mainChart');

    // 讀取初始值
    if (metricSelectEl && metricSelectEl.value) state.metric = metricSelectEl.value;

    // —— 僅允許 bar/line，移除其他選項（例如 pie） —— //
    const allowedTypes = new Set(['bar','line']);
    if (chartTypeEl) {
      Array.from(chartTypeEl.options || []).forEach(opt => {
        if (!allowedTypes.has(opt.value)) opt.remove();
      });
      if (!allowedTypes.has(chartTypeEl.value)) chartTypeEl.value = 'bar';
      state.type = chartTypeEl.value || 'bar';
    }

    console.log('[analytics] detected selects:', {
      metricSelect: metricSelectEl ? metricSelectEl.tagName : null,
      chartTypeSelect: chartTypeEl ? chartTypeEl.tagName : null,
      type: state.type
    });

    // —— 原生 select 的 change —— //
    if (metricSelectEl) {
      metricSelectEl.addEventListener('change', () => {
        state.metric = metricSelectEl.value || 'workout-time';
        console.log('[analytics] metric ->', state.metric);
        if (chart) { chart.destroy(); chart = null; }
        loadSeries(chartTitleEl, canvasEl, summaryWrapEl, totalEl);
      });
    }
    if (chartTypeEl) {
      chartTypeEl.addEventListener('change', () => {
        state.type = (chartTypeEl.value === 'line' || chartTypeEl.value === 'bar') ? chartTypeEl.value : 'bar';
        if (chartTypeEl.value !== state.type) chartTypeEl.value = state.type;
        console.log('[analytics] chart type ->', state.type);
        if (chart) { chart.destroy(); chart = null; }
        loadSeries(chartTitleEl, canvasEl, summaryWrapEl, totalEl);
      });
    }

    // —— 期間 tabs —— //
    if (rangeTabEls && rangeTabEls.length) {
      rangeTabEls.forEach(a => {
        a.addEventListener('click', (e) => {
          e.preventDefault();
          const r = a.dataset.range;
          if (!r) return;
          state.range = r;
          rangeTabEls.forEach(x => x.classList.remove('active'));
          a.classList.add('active');
          console.log('[analytics] range ->', state.range);
          if (chart) { chart.destroy(); chart = null; }
          loadSeries(chartTitleEl, canvasEl, summaryWrapEl, totalEl);
        });
      });
    }

    // —— 後備 1：capturing change（插件若有觸發 change 也會收到） —— //
    document.addEventListener('change', (e) => {
      const t = e.target;
      if (!t) return;
      if (t.id === 'metricSelect') {
        state.metric = t.value || 'workout-time';
        console.log('[analytics][capture] metric ->', state.metric);
        if (chart) { chart.destroy(); chart = null; }
        loadSeries(chartTitleEl, canvasEl, summaryWrapEl, totalEl);
      }
      if (t.id === 'chartTypeSelect') {
        state.type = (t.value === 'line' || t.value === 'bar') ? t.value : 'bar';
        if (t.value !== state.type) t.value = state.type;
        console.log('[analytics][capture] chart type ->', state.type);
        if (chart) { chart.destroy(); chart = null; }
        loadSeries(chartTitleEl, canvasEl, summaryWrapEl, totalEl);
      }
    }, true);

    // —— 後備 2：Nice Select 橋接（點 .nice-select 的選項） —— //
    document.addEventListener('click', (e) => {
      const opt = e.target.closest('.nice-select .option');
      if (!opt) return;

      // 若使用者點到「圓餅圖」，直接忽略
      const raw = (opt.dataset && (opt.dataset.value || opt.dataset.chartType)) || (opt.textContent || '');
      if (/^(pie|圓餅圖)$/i.test(raw.trim())) {
        console.log('[analytics] pie disabled');
        e.preventDefault();
        return;
      }

      // nice-select: <select ... style="display:none"></select><div class="nice-select">...</div>
      const nice = opt.closest('.nice-select');
      const sel = nice && nice.previousElementSibling && nice.previousElementSibling.tagName === 'SELECT'
        ? nice.previousElementSibling
        : null;
      if (!sel) return;

      setTimeout(() => {
        if (sel.id === 'metricSelect') {
          state.metric = sel.value || 'workout-time';
          console.log('[analytics][nice-select] metric ->', state.metric);
          if (chart) { chart.destroy(); chart = null; }
          loadSeries(chartTitleEl, canvasEl, summaryWrapEl, totalEl);
        } else if (sel.id === 'chartTypeSelect') {
          state.type = (sel.value === 'line' || sel.value === 'bar') ? sel.value : 'bar';
          if (sel.value !== state.type) sel.value = state.type;
          console.log('[analytics][nice-select] chart type ->', state.type);
          if (chart) { chart.destroy(); chart = null; }
          loadSeries(chartTitleEl, canvasEl, summaryWrapEl, totalEl);
        }
      }, 0);
    });

    // —— 後備 3：jQuery 事件（若插件用 jQuery 觸發 change） —— //
    if (window.jQuery) {
      jQuery(document).on('change', '#metricSelect', function(){
        state.metric = this.value || 'workout-time';
        console.log('[analytics][jquery] metric ->', state.metric);
        if (chart) { chart.destroy(); chart = null; }
        loadSeries(chartTitleEl, canvasEl, summaryWrapEl, totalEl);
      });
      jQuery(document).on('change', '#chartTypeSelect', function(){
        state.type = (this.value === 'line' || this.value === 'bar') ? this.value : 'bar';
        if (this.value !== state.type) this.value = state.type;
        console.log('[analytics][jquery] chart type ->', state.type);
        if (chart) { chart.destroy(); chart = null; }
        loadSeries(chartTitleEl, canvasEl, summaryWrapEl, totalEl);
      });
    }

    // 最初載入
    loadSeries(chartTitleEl, canvasEl, summaryWrapEl, totalEl);
  });
})();
