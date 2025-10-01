<header class="header-style-one">
  <div class="container">
    <div class="row">
      <div class="desktop-nav" id="stickyHeader">
        <div class="container">
          <div class="row">
            <div class="col-lg-12">
              <div class="d-flex-all justify-content-between">
                
                <div class="header-logo">
                  <a href="/frontend-template/index.html">
                    <figure><img src="/frontend-template/assets/images/logo-2.svg" alt="TibaFit Logo"></figure>
                  </a>
                </div>

                <div class="nav-bar">
                  <ul>
                    <li><a href="/frontend-template/plan.html">運動計畫</a></li>
                    <li class="menu-item-has-children"><a href="/dMain/fd/workoutPlan_main">數據圖表</a>
                      <ul class="sub-menu">
                        <li><a href="/analytics">數據圖表</a></li>
                        <li><a href="/frontend-template/tasks.html">我的任務</a></li>
                      </ul>
                    </li>
                    <li><a href="/frontend-template/forum.html">討論區</a></li>
                    <li><a href="/shop/products">商城</a></li>
                  </ul>
                </div>

                <div class="extras">
                  <div id="auth-links" style="white-space: nowrap;">
                    <a href="/frontend-template/login.html" class="login">登入 / 註冊</a>
                  </div>
                  <a href="javascript:void(0)" id="open-cart" class="shopping-bag white-icon" aria-label="開啟購物車">
                    <svg viewBox="0 0 512 512" xmlns="http://www.w3.org/2000/svg" aria-hidden="true"><path d="m452 120h-60.946c-7.945-67.478-65.477-120-135.054-120s-127.109 52.522-135.054 120h-60.946c-11.046 0-20 8.954-20 20v352c0 11.046 8.954 20 20 20h392c11.046 0 20-8.954 20-20v-352c0-11.046-8.954-20-20-20zm-196-80c47.484 0 87.019 34.655 94.659 80h-189.318c7.64-45.345 47.175-80 94.659-80zm176 432h-352v-312h40v60c0 11.046 8.954 20 20 20s20-8.954 20-20v-60h192v60c0 11.046 8.954 20 20 20s20-8.954 20-20v-60h40z"/></svg>
                    <span class="cart-count" id="cart-count"></span>
                  </a>
                  <a href="javascript:void(0)" id="mobile-menu" class="menu-start">
                     <svg id="ham-menu" viewBox="0 0 100 100" width="26" height="26"><path class="line line1" d="M 20,29 H 80"></path><path class="line line2" d="M 20,50 H 80"></path><path class="line line3" d="M 20,71 H 80"></path></svg>
                  </a>
                </div>

              </div>
            </div>
          </div>
        </div>
        
        <div class="mobile-nav" id="mobile-nav">
          </div>
      </div>
    </div>
</header>

<div id="cart-lightbox" class="cart-overlay clearfix">
  <div class="white_content">
    <a href="javascript:;" class="lightbox-close cart-close" aria-label="關閉">✕</a>
    <div class="cart-popup">
      <h3 class="cart-titlen">購物車</h3>
      <div id="cart-items-container">
        </div>
      <div class="cart-btn">
        <div class="cart-total d-flex align-items-center justify-content-between">
          <span>總計：</span><span>NT$ 0</span>
        </div>
        <div class="cart-btns d-flex align-items-center justify-content-between mt-3">
          <a class="btn btn-outline-secondary" href="/frontend-template/cart.html">查看購物車</a>
          <a class="btn btn-primary" href="/frontend-template/cart_order.html">結帳</a>
        </div>
      </div>
    </div>
  </div>
</div>