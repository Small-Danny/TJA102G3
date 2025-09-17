function changeQty(delta) {
      const qtyInput = document.getElementById('qty');
      let value = parseInt(qtyInput.value) || 1;
      value = Math.max(1, value + delta);
      qtyInput.value = value;
    }
	
	document.addEventListener("DOMContentLoaded", () => {
	      const group = document.querySelector(".size-group");
	      const hidden = document.getElementById("selected_size");

	      if(group) {
	        group.addEventListener("click", e => {
	          if(e.target.classList.contains("size-chip")) {
	            group.querySelectorAll(".size-chip").forEach(c => {
	              c.classList.remove("is-selected");
	              c.setAttribute("aria-checked", "false");
	            });
	            e.target.classList.add("is-selected");
	            e.target.setAttribute("aria-checked", "true");
	            hidden.value = e.target.dataset.size;
	          }
	        });
	      }
	    });