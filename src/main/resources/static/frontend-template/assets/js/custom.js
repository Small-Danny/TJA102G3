/*-----------------------------------------------------------------------------------
  Template: Gym On (Stable custom.js)
  Notes:
    - All plugins/DOM guarded (won't throw if missing)
    - No Nzoom dependency
    - jQuery required and must be loaded before this file
-----------------------------------------------------------------------------------*/
(function ($) {
  "use strict";

  // ---------- Helpers ----------
  const hasEl     = (sel) => $(sel).length > 0;
  const safe      = (sel) => (hasEl(sel) ? $(sel) : null);
  const hasPlugin = (name) => !!($.fn && $.fn[name]);

  const addClass    = (sel, cls) => { const $el = safe(sel); if ($el) $el.addClass(cls); };
  const removeClass = (sel, cls) => { const $el = safe(sel); if ($el) $el.removeClass(cls); };
  const toggleClass = (sel, cls) => { const $el = safe(sel); if ($el) $el.toggleClass(cls); };

  $(function () {
    /* 01 marquee_text */
    if (hasPlugin("marquee") && hasEl(".marquee_text")) {
      try {
        $(".marquee_text").marquee({
          direction: "left",
          duration: 20000,
          gap: 50,
          delayBeforeStart: 0,
          duplicated: true,
          startVisible: true
        });
      } catch (e) { console.warn("marquee skipped:", e); }
    }

    /* 02 Counter Style One */
    if (hasPlugin("counterUp") && hasEl(".counter")) {
      $(".counter").counterUp({ delay: 10, time: 1000 });
    }

    /* 03 Team Slider */
    if (hasPlugin("owlCarousel") && hasEl(".team-slider.owl-carousel")) {
      $(".team-slider.owl-carousel").owlCarousel({
        loop: true,
        nav: true,
        navText: [
          "<i class='fa-solid fa-arrow-left-long'></i>",
          "<i class='fa-solid fa-arrow-right-long'></i>"
        ],
        dots: false,
        touchDrag: false,
        mouseDrag: false,
        margin: 10,
        navContainer: ".team-slider-nav",
        responsive: { 0:{items:1}, 756:{items:2}, 992:{items:3}, 1200:{items:3} }
      });
    }

    /* 04 Featured Slider One */
    if (hasPlugin("owlCarousel") && hasEl(".f-slider-one.owl-carousel")) {
      $(".f-slider-one.owl-carousel").owlCarousel({
        items: 1, loop: true, margin: 0, stagePadding: 0, dots: true,
        autoplay: true, animateOut: "fadeOut", touchDrag: false, mouseDrag: false
      });
    }

    /* 05 f-slider-three */
    if (hasPlugin("owlCarousel") && hasEl(".f-slider-three")) {
      $(".f-slider-three").owlCarousel({
        items: 1, loop: true, margin: 0, stagePadding: 0,
        dots: true, autoplay: true, smartSpeed: 2000
      });
    }

    /* 06 myslider */
    if (hasPlugin("owlCarousel") && hasEl(".myslider")) {
      $(".myslider").owlCarousel({
        items: 1, loop: true, dots: true, smartSpeed: 1000,
        dotsData: true, nav: false, autoplay: true, mouseDrag: false
      });
    }

    /* 07 Client Slider */
    if (hasPlugin("owlCarousel") && hasEl(".client-slider.owl-carousel")) {
      $(".client-slider.owl-carousel").owlCarousel({
        items: 5, autoplay: true, autoplayTimeout: 3000, autoplayHoverPause: false, dots: false,
        responsive: { 0:{items:1}, 600:{items:2}, 800:{items:3}, 1000:{items:5} }
      });
    }

    /* 08 Client Review Slider */
    if (hasPlugin("owlCarousel") && hasEl(".client-review-slider.owl-carousel")) {
      $(".client-review-slider.owl-carousel").owlCarousel({
        items: 1, autoplay: true, autoplayTimeout: 3000, autoplayHoverPause: false, dots: true
      });
    }

    /* 09 c-slider */
    if (hasPlugin("owlCarousel") && hasEl(".c-slider.owl-carousel")) {
      $(".c-slider.owl-carousel").owlCarousel({
        loop: true, items: 1, dots: false, autoplay: true,
        autoplayTimeout: 3000, autoplayHoverPause: false, nav: true,
        navText: [
          "<i class='fa-solid fa-arrow-left-long'></i>",
          "<i class='fa-solid fa-arrow-right-long'></i>"
        ],
        responsive: { 0:{nav:false}, 768:{nav:true} }
      });
    }

    /* 10 blog-slider */
    if (hasPlugin("owlCarousel") && hasEl(".blog-slider.owl-carousel")) {
      $(".blog-slider.owl-carousel").owlCarousel({
        items: 3, center: true, loop: true, margin: 12, dots: true,
        autoplay: true, autoplayTimeout: 3000, autoplayHoverPause: false,
        responsive: { 0:{items:1}, 768:{center:false, items:2}, 1000:{items:3} }
      });
    }

    /* 11 Nice Select */
    if (hasPlugin("niceSelect") && hasEl("select")) {
      $("select").niceSelect();
    }

    /* 12 P-Slider */
    if (hasPlugin("owlCarousel") && hasEl(".p-slider.owl-carousel")) {
      $(".p-slider.owl-carousel").owlCarousel({
        items: 3, loop: true, center: true, dots: true,
        autoplay: true, autoplayTimeout: 3000, autoplayHoverPause: false,
        responsive: { 0:{items:1}, 768:{center:false, items:2}, 1100:{items:3} }
      });
    }

    /* 13 Project Detail Slider */
    if (hasPlugin("owlCarousel") && hasEl(".p-d-slider.owl-carousel")) {
      $(".p-d-slider.owl-carousel").owlCarousel({ items: 1, dots: true });
    }

    /* 14 c-data */
    if (hasEl(".contact-us .c-data ul li") && hasEl(".c-cards .card")) {
      $(".contact-us .c-data ul li").on("click", function () {
        $(".contact-us .c-data a").removeClass("active");
        $(this).children("a").addClass("active");
        const idx = $(this).index() + 1; // nth-child is 1-based
        $(".c-cards .card").removeClass("active");
        $(`.c-cards .card:nth-child(${idx})`).addClass("active");
      });
    }

    /* 15 Products List Grid */
    if (hasEl(".shop-filter")) {
      $(".shop-filter a.list").on("click", function () {
        $(".p-slider").removeClass("grid").addClass("list");
      });
      $(".shop-filter a.grid").on("click", function () {
        $(".p-slider").removeClass("list").addClass("grid");
      });
    }

    /* 16 wwb-ul li hover */
    if (hasEl(".wwb-ul li")) {
      $(".wwb-ul li").hover(function () {
        $(".wwb-ul li").removeClass("active");
        $(this).addClass("active");
      });
    }

    // /* 17 mobile-nav */
    // if (hasEl(".mobile-nav .menu-item-has-children")) {
    //   $(".mobile-nav .menu-item-has-children").on("click", function (e) {
    //     $(this).toggleClass("active");
    //     e.stopPropagation();
    //   });
    // }

    // /* 18 #mobile-menu */
    // if (hasEl("#mobile-menu")) {
    //   $("#mobile-menu").on("click", function () {
    //     $(this).toggleClass("open");
    //     toggleClass("#mobile-nav", "open");
    //   });
    // }

    // /* 19 #desktop-menu */
    // if (hasEl("#desktop-menu")) {
    //   $("#desktop-menu").on("click", function () {
    //     $(this).toggleClass("open");
    //     toggleClass(".desktop-menu", "open");
    //   });
    // }

    // /* 20 #res-cross */
    // if (hasEl("#res-cross")) {
    //   $("#res-cross").on("click", function () {
    //     removeClass("#mobile-nav", "open");
    //     removeClass("#mobile-menu", "open");
    //   });
    // }

    /* 21 li-pd-imgs（無 Nzoom 依賴） */
    if (hasEl(".li-pd-imgs")) {
      $(".li-pd-imgs").on("click", function () {
        $(".li-pd-imgs.nav-active").removeClass("nav-active");
        $(this).addClass("nav-active");
        const src = $(this).find("img").attr("src");
        if (src) {
          if (hasEl("#NZoomContainer img")) $("#NZoomContainer img").attr("src", src);
          else if (hasEl("#main-image"))     $("#main-image").attr("src", src);
        }
      });
    }

    /* 22 Cart Popup */
	/*
	if (hasEl(".white_content")) {
	  $(".white_content").stop(true).animate({ opacity: 0, width: 0, right: -10000 });
	  if (hasEl("#close")) $("#close").on("click", function () {
	    $(".white_content").stop(true).animate({ opacity: 0, width: 0, right: -1000 });
	  });
	  if (hasEl("#show")) $("#show").on("click", function () {
	    $(".white_content").stop(true).animate({ opacity: 1, right: 0 });
	  });
	}
	*/


    /* 23 Sticky Header（安全版） */
    (function () {
      const header = document.getElementById("stickyHeader");
      if (!header) return;
      let prev = 0;
      window.addEventListener("scroll", () => {
        const y = window.scrollY;
        if (prev < y && y > 100) {
          header.classList.remove("slideDown");
          header.classList.add("slideUp");
        } else if (y < 100) {
          header.classList.remove("slideDown");
          header.classList.remove("slideUp");
        } else if (prev > y) {
          header.classList.remove("slideUp");
          header.classList.add("slideDown");
        }
        prev = y;
      });
    })();

    /* Tabs（.tabs-box） */
    if (hasEl(".tabs-box")) {
      $(".tabs-box .tab-buttons .tab-btn").on("click", function (e) {
        e.preventDefault();
        const targetSel = $(this).attr("data-tab");
        const $target = $(targetSel);
        if (!$target.length || $target.is(":visible")) return;

        const $box = $target.parents(".tabs-box");
        $box.find(".tab-buttons .tab-btn").removeClass("active-btn");
        $(this).addClass("active-btn");
        $box.find(".tabs-content .tab").fadeOut(0).removeClass("active-tab");
        $target.fadeIn(300).addClass("active-tab");
      });
    }

    /* 25. days time */
    if (hasEl("#days")) {
      (function () {
        const second = 1000, minute = second * 60, hour = minute * 60, day = hour * 24;
        let today = new Date(),
            dd = String(today.getDate()).padStart(2, "0"),
            mm = String(today.getMonth() + 1).padStart(2, "0"),
            yyyy = today.getFullYear(),
            nextYear = yyyy + 1,
            dayMonth = "9/21/",
            birthday = dayMonth + yyyy;

        today = mm + "/" + dd + "/" + yyyy;
        if (today > birthday) birthday = dayMonth + nextYear;

        const countDown = new Date(birthday).getTime();
        const timer = setInterval(function () {
          const now = new Date().getTime();
          const distance = countDown - now;

          $("#days").text(Math.floor(distance / day));
          $("#hours").text(Math.floor((distance % day) / hour));
          $("#minutes").text(Math.floor((distance % hour) / minute));
          $("#seconds").text(Math.floor((distance % minute) / second));

          if (distance < 0) {
            $("#headline").text("event");
            $("#countdown").hide();
            $("#content").show();
            clearInterval(timer);
          }
        }, 1000);
      })();
    }

    /* 26 scrollTop Percentage + reveal */
    if (hasEl("#scroll-percentage")) {
      const scrollPercentage = () => {
        const doc = document.documentElement;
        const top = doc.scrollTop;
        const height = doc.scrollHeight - doc.clientHeight;
        const val = Math.round((top / height) * 100);
        const $wrap = $("#scroll-percentage");

        $wrap.css("background", `conic-gradient(#fff ${val}%, #411111 ${val}%)`);
        $wrap.toggleClass("active", top > 100);

        if (val < 99) $("#scroll-percentage-value").text(`${val}%`);
        else $("#scroll-percentage-value").html('<i class="fa-solid fa-arrow-up-long"></i>');
      };
      window.addEventListener("scroll", scrollPercentage);
      window.addEventListener("load", scrollPercentage);
      $("#scroll-percentage").on("click", function () {
        document.documentElement.scrollTo({ top: 0, behavior: "smooth" });
      });
    }

    if (hasEl(".reveal1")) {
      const reveal1 = () => {
        document.querySelectorAll(".reveal1").forEach((el) => {
          const elementTop = el.getBoundingClientRect().top;
          if (elementTop < window.innerHeight - 150) el.classList.add("active");
        });
      };
      window.addEventListener("scroll", reveal1);
      reveal1();
    }
  });

  /* 24 Preloader（保持不變；確保不會卡白屏） */
  $(window).on("load", function () {
    $("body").addClass("page-loaded");
  });

})(jQuery);