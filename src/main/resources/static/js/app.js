const SQUARE_FOOT_RATE = 0.50; // Change this value later if the SFT rate changes.
const GST_RATE = 0.05;

document.querySelectorAll("[data-modal-target]").forEach((button) => {
    button.addEventListener("click", () => {
        document.getElementById(button.dataset.modalTarget).hidden = false;
    });
});

document.querySelectorAll(".modal-close").forEach((button) => {
    button.addEventListener("click", () => {
        button.closest(".modal-backdrop").hidden = true;
    });
});

document.querySelectorAll(".modal-backdrop").forEach((backdrop) => {
    backdrop.addEventListener("click", (event) => {
        if (event.target === backdrop) {
            backdrop.hidden = true;
        }
    });
});

document.querySelectorAll(".quote-form").forEach((form) => {
    const sftInput = form.querySelector(".quote-sft");
    const uomInput = form.querySelector(".quote-uom");
    const costInput = form.querySelector(".quote-cost");
    const gstInput = form.querySelector(".quote-gst");

    if (!sftInput || !uomInput || !costInput || !gstInput) {
        return;
    }

    const updateQuote = () => {
        const sft = Number.parseFloat(sftInput.value || "0");
        if (uomInput.value === "SFT") {
            costInput.value = (sft * SQUARE_FOOT_RATE).toFixed(2);
            costInput.readOnly = true;
        } else {
            costInput.readOnly = false;
        }
        const cost = Number.parseFloat(costInput.value || "0");
        gstInput.value = (cost * GST_RATE).toFixed(2);
    };

    sftInput.addEventListener("input", updateQuote);
    uomInput.addEventListener("change", updateQuote);
    costInput.addEventListener("input", updateQuote);
    updateQuote();
});

document.querySelectorAll(".location-search").forEach((input) => {
    const results = input.closest("label").querySelector(".location-results");
    let timeoutId;

    input.addEventListener("input", () => {
        clearTimeout(timeoutId);
        const query = input.value.trim();
        if (query.length < 3 || !results) {
            if (results) {
                results.innerHTML = "";
            }
            return;
        }

        timeoutId = setTimeout(async () => {
            try {
                const response = await fetch(`https://nominatim.openstreetmap.org/search?format=json&limit=5&q=${encodeURIComponent(query)}`);
                const locations = await response.json();
                results.innerHTML = "";
                locations.forEach((location) => {
                    const option = document.createElement("button");
                    option.type = "button";
                    option.className = "location-result";
                    option.textContent = location.display_name;
                    option.addEventListener("click", () => {
                        input.value = location.display_name;
                        results.innerHTML = "";
                    });
                    results.appendChild(option);
                });
            } catch {
                results.innerHTML = "";
            }
        }, 350);
    });
});
