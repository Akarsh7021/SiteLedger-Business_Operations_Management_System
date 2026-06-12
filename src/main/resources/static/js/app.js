const SQUARE_FOOT_RATE = 0.50; // Change this value later if the SFT rate changes.
const GST_RATE = 0.05;
const CHART_COLORS = ["#875a7b", "#00a09d", "#f59e0b", "#4776c4", "#ef476f", "#6b7280"];
const BC_LOCATION_CENTER = {lat: 49.1913, lon: -122.8490}; // Surrey, BC
const BC_LOCATION_RADIUS_KM = 45;
const BC_LOCATION_VIEWBOX = "-123.45,49.60,-122.20,48.78";
let openModalCount = 0;

const applyTheme = (theme) => {
    document.documentElement.dataset.theme = theme;
    localStorage.setItem("payroll-theme", theme);
    document.querySelectorAll("[data-theme-label]").forEach((label) => {
        label.textContent = theme === "dark" ? "Dark" : "Light";
    });
};

applyTheme(localStorage.getItem("payroll-theme") || "light");

document.querySelectorAll("[data-theme-toggle]").forEach((button) => {
    button.addEventListener("click", () => {
        const nextTheme = document.documentElement.dataset.theme === "dark" ? "light" : "dark";
        applyTheme(nextTheme);
        drawDashboardCharts();
    });
});

const closeModal = (modal) => {
    if (!modal) {
        return;
    }
    const wasOpen = modal.dataset.dynamic === "true" || !modal.hidden;
    if (modal.dataset.dynamic === "true") {
        modal.remove();
    } else {
        modal.hidden = true;
    }
    if (wasOpen) {
        unlockPageScroll();
    }
};

const lockPageScroll = () => {
    openModalCount += 1;
    document.body.classList.add("modal-open");
};

const unlockPageScroll = () => {
    openModalCount = Math.max(0, openModalCount - 1);
    if (openModalCount === 0 && !document.querySelector(".modal-backdrop:not([hidden])")) {
        document.body.classList.remove("modal-open");
    }
};

const makeModalDraggable = (modal) => {
    const panel = modal.querySelector(".modal-panel");
    const header = modal.querySelector(".modal-header");
    if (!panel || !header || panel.dataset.draggable === "true") {
        return;
    }
    panel.dataset.draggable = "true";
    header.classList.add("modal-drag-handle");

    header.addEventListener("pointerdown", (event) => {
        if (event.target.closest("button, a, input, select, textarea")) {
            return;
        }
        const rect = panel.getBoundingClientRect();
        const offsetX = event.clientX - rect.left;
        const offsetY = event.clientY - rect.top;
        panel.style.position = "fixed";
        panel.style.left = `${rect.left}px`;
        panel.style.top = `${rect.top}px`;
        panel.style.width = `${rect.width}px`;
        panel.style.margin = "0";
        panel.setPointerCapture(event.pointerId);

        const move = (moveEvent) => {
            const nextLeft = Math.min(Math.max(8, moveEvent.clientX - offsetX), window.innerWidth - rect.width - 8);
            const nextTop = Math.min(Math.max(8, moveEvent.clientY - offsetY), window.innerHeight - rect.height - 8);
            panel.style.left = `${nextLeft}px`;
            panel.style.top = `${nextTop}px`;
        };
        const stop = () => {
            panel.removeEventListener("pointermove", move);
            panel.removeEventListener("pointerup", stop);
            panel.removeEventListener("pointercancel", stop);
        };
        panel.addEventListener("pointermove", move);
        panel.addEventListener("pointerup", stop);
        panel.addEventListener("pointercancel", stop);
    });
};

const prepareModal = (modal) => {
    if (!modal || modal.dataset.modalPrepared === "true") {
        return;
    }
    modal.dataset.modalPrepared = "true";
    const headerClose = modal.querySelector(".modal-header .modal-close");
    if (headerClose) {
        headerClose.classList.add("modal-x-button");
        headerClose.textContent = "×";
        headerClose.setAttribute("aria-label", "Close");
        headerClose.setAttribute("title", "Close");
    }
    makeModalDraggable(modal);
    modal.addEventListener("click", (event) => {
        if (event.target === modal || event.target.classList.contains("modal-close")) {
            closeModal(modal);
        }
    });
};

document.querySelectorAll(".modal-backdrop").forEach(prepareModal);

document.querySelectorAll("[data-modal-target]").forEach((button) => {
    button.addEventListener("click", () => {
        const modal = document.getElementById(button.dataset.modalTarget);
        prepareModal(modal);
        modal.hidden = false;
        lockPageScroll();
    });
});

const ensureDeleteConfirmModal = () => {
    let modal = document.getElementById("delete-confirm-modal");
    if (modal) {
        return modal;
    }
    modal = document.createElement("div");
    modal.id = "delete-confirm-modal";
    modal.className = "modal-backdrop";
    modal.hidden = true;
    modal.innerHTML = `
        <section class="modal-panel" role="dialog" aria-modal="true" aria-labelledby="delete-confirm-title">
            <div class="modal-header">
                <div>
                    <p class="eyebrow">Confirm Delete</p>
                    <h2 id="delete-confirm-title">Delete this record?</h2>
                </div>
                <button type="button" class="secondary-button modal-close">Close</button>
            </div>
            <p class="delete-confirm-message">This action will be recorded in Delete History.</p>
            <div class="modal-actions">
                <button type="button" class="danger-button delete-confirm-submit">Delete</button>
                <button type="button" class="secondary-button modal-close">Cancel</button>
            </div>
        </section>
    `;
    document.body.appendChild(modal);
    prepareModal(modal);
    modal.addEventListener("click", (event) => {
        if (event.target === modal || event.target.classList.contains("modal-close")) {
            closeModal(modal);
            modal.pendingForm = null;
        }
    });
    modal.querySelector(".delete-confirm-submit").addEventListener("click", () => {
        if (modal.pendingForm) {
            modal.pendingForm.submit();
        }
    });
    return modal;
};

document.querySelectorAll("form[data-confirm-delete]").forEach((form) => {
    form.addEventListener("submit", (event) => {
        event.preventDefault();
        const modal = ensureDeleteConfirmModal();
        modal.pendingForm = form;
        modal.querySelector(".delete-confirm-message").textContent =
                form.dataset.deleteMessage || "Delete this record? This action will be recorded in Delete History.";
        modal.hidden = false;
        lockPageScroll();
    });
});

const quickCreateEndpoint = (type) => `/api/quick-create/${type}`;

const selectOptions = (select) => Array.from(select.options)
        .filter((option) => option.value)
        .map((option) => ({value: option.value, label: option.textContent.trim()}));

const setEnhancedSelectValue = (select, input, option) => {
    let existing = Array.from(select.options).find((item) => item.value === String(option.value));
    if (!existing) {
        existing = new Option(option.label, option.value, true, true);
        select.add(existing);
    }
    select.value = String(option.value);
    input.value = option.label;
    select.dispatchEvent(new Event("change", {bubbles: true}));
};

const quickCreate = async (select, input, name) => {
    const type = select.dataset.quickType;
    if (select.dataset.serviceSelect === "true") {
        const serviceName = name.trim().slice(0, 40);
        if (serviceName) {
            setEnhancedSelectValue(select, input, {value: serviceName, label: serviceName});
        }
        return;
    }
    if (!type) {
        return;
    }
    const form = select.closest("form");
    const contractorSelect = form ? form.querySelector("select[name='contractorId']") : null;
    const response = await fetch(quickCreateEndpoint(type), {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({
            name,
            contractorId: contractorSelect ? contractorSelect.value : ""
        })
    });
    if (!response.ok) {
        return;
    }
    const option = await response.json();
    setEnhancedSelectValue(select, input, {value: option.id, label: option.name});
};

const openCustomerCreateModal = (select, input, initialName = "") => {
    const modal = document.createElement("div");
    modal.className = "modal-backdrop";
    modal.dataset.dynamic = "true";
    modal.innerHTML = `
        <section class="modal-panel wide-modal" role="dialog" aria-modal="true">
            <div class="modal-header">
                <div>
                    <p class="eyebrow">Customer</p>
                    <h2>Add Customer</h2>
                </div>
                <button type="button" class="secondary-button modal-close">Close</button>
            </div>
            <form class="form-stack quick-customer-form">
                <label>
                    Name
                    <input type="text" name="name" maxlength="120" required>
                </label>
                <div class="form-grid">
                    <label>
                        Number
                        <input type="text" name="phoneNumber" maxlength="30">
                    </label>
                    <label>
                        Builder or Owner
                        <select name="customerType" required>
                            <option value="BUILDER">BUILDER</option>
                            <option value="OWNER">OWNER</option>
                        </select>
                    </label>
                </div>
                <label>
                    Billing Name
                    <input type="text" name="billingName" maxlength="120">
                </label>
                <label>
                    Address
                    <textarea name="address" rows="3" maxlength="255"></textarea>
                </label>
                <div class="form-grid">
                    <label>
                        Amount Paid To Date
                        <input type="number" name="amountPaidToDate" min="0" step="0.01" value="0" required>
                    </label>
                    <label>
                        Amount Owed
                        <input type="number" name="amountUnpaid" min="0" step="0.01" value="0" required>
                    </label>
                </div>
                <div class="modal-actions">
                    <button type="submit" class="primary-button">Save Customer</button>
                    <button type="button" class="secondary-button modal-close">Close</button>
                </div>
            </form>
        </section>
    `;
    const form = modal.querySelector(".quick-customer-form");
    form.elements.name.value = initialName;
    form.addEventListener("submit", async (event) => {
        event.preventDefault();
        const body = Object.fromEntries(new FormData(form).entries());
        const response = await fetch(quickCreateEndpoint("customers"), {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(body)
        });
        if (!response.ok) {
            return;
        }
        const option = await response.json();
        setEnhancedSelectValue(select, input, {value: option.id, label: option.name});
        closeModal(modal);
    });
    document.body.appendChild(modal);
    prepareModal(modal);
    lockPageScroll();
    form.elements.name.focus();
};

const openServiceCreateModal = (select, input, initialName = "") => {
    const modal = document.createElement("div");
    modal.className = "modal-backdrop";
    modal.dataset.dynamic = "true";
    modal.innerHTML = `
        <section class="modal-panel" role="dialog" aria-modal="true">
            <div class="modal-header">
                <div>
                    <p class="eyebrow">Type of Service</p>
                    <h2>Add Service</h2>
                </div>
                <button type="button" class="secondary-button modal-close">Close</button>
            </div>
            <form class="form-stack quick-service-form">
                <label>
                    Service Name
                    <input type="text" name="name" maxlength="40" required>
                </label>
                <div class="modal-actions">
                    <button type="submit" class="primary-button">Save Service</button>
                    <button type="button" class="secondary-button modal-close">Close</button>
                </div>
            </form>
        </section>
    `;
    const form = modal.querySelector(".quick-service-form");
    form.elements.name.value = initialName;
    form.addEventListener("submit", (event) => {
        event.preventDefault();
        const serviceName = form.elements.name.value.trim().slice(0, 40);
        if (!serviceName) {
            form.elements.name.focus();
            return;
        }
        setEnhancedSelectValue(select, input, {value: serviceName, label: serviceName});
        closeModal(modal);
    });
    document.body.appendChild(modal);
    prepareModal(modal);
    lockPageScroll();
    form.elements.name.focus();
};

const openSearchMoreModal = (select, input) => {
    const modal = document.createElement("div");
    modal.className = "modal-backdrop";
    modal.dataset.dynamic = "true";
    const options = selectOptions(select);
    modal.innerHTML = `
        <section class="modal-panel wide-modal" role="dialog" aria-modal="true">
            <div class="modal-header">
                <div>
                    <p class="eyebrow">Search More</p>
                    <h2>${select.closest("label")?.childNodes[0]?.textContent?.trim() || "Records"}</h2>
                </div>
                <button type="button" class="secondary-button modal-close">Close</button>
            </div>
            <input type="search" class="odoo-modal-search" placeholder="Search...">
            <div class="odoo-modal-list"></div>
            <div class="modal-actions">
                <button type="button" class="primary-button odoo-modal-new">New</button>
                <button type="button" class="secondary-button modal-close">Close</button>
            </div>
        </section>
    `;
    const list = modal.querySelector(".odoo-modal-list");
    const search = modal.querySelector(".odoo-modal-search");
    const render = () => {
        const query = search.value.trim().toLowerCase();
        list.innerHTML = "";
        options
                .filter((option) => option.label.toLowerCase().includes(query))
                .forEach((option) => {
                    const button = document.createElement("button");
                    button.type = "button";
                    button.className = "odoo-modal-row";
                    button.textContent = option.label;
                    button.addEventListener("click", () => {
                        setEnhancedSelectValue(select, input, option);
                        closeModal(modal);
                    });
                    list.appendChild(button);
                });
    };
    modal.addEventListener("click", (event) => {
        if (event.target === modal || event.target.classList.contains("modal-close")) {
            closeModal(modal);
        }
    });
    modal.querySelector(".odoo-modal-new").addEventListener("click", async () => {
        const name = search.value.trim();
        if (select.dataset.serviceSelect === "true") {
            closeModal(modal);
            openServiceCreateModal(select, input, name);
        } else if (select.dataset.quickType === "customers") {
            closeModal(modal);
            openCustomerCreateModal(select, input, name);
        } else if (name) {
            await quickCreate(select, input, name);
            closeModal(modal);
        } else {
            search.focus();
        }
    });
    search.addEventListener("input", render);
    document.body.appendChild(modal);
    prepareModal(modal);
    lockPageScroll();
    render();
    search.focus();
};

document.querySelectorAll("select.odoo-select").forEach((select) => {
    const previewLimit = Number.parseInt(select.dataset.previewLimit || "5", 10);
    const wrapper = document.createElement("div");
    wrapper.className = "odoo-select";
    const input = document.createElement("input");
    input.type = "text";
    input.className = "odoo-select-input";
    input.placeholder = "Search...";
    const menu = document.createElement("div");
    menu.className = "odoo-select-menu";
    select.after(wrapper);
    wrapper.append(input, menu);
    select.classList.add("native-select-hidden");
    const selected = select.selectedOptions[0];
    if (selected && selected.value) {
        input.value = selected.textContent.trim();
    }

    const render = () => {
        const query = input.value.trim();
        const lowerQuery = query.toLowerCase();
        const matchedOptions = selectOptions(select)
                .filter((option) => option.label.toLowerCase().includes(lowerQuery));
        const options = matchedOptions.slice(0, query ? 8 : previewLimit);
        menu.innerHTML = "";
        options.forEach((option) => {
            const button = document.createElement("button");
            button.type = "button";
            button.className = "odoo-select-option";
            button.textContent = option.label;
            button.addEventListener("mousedown", (event) => {
                event.preventDefault();
                setEnhancedSelectValue(select, input, option);
                menu.hidden = true;
            });
            menu.appendChild(button);
        });
        if (select.dataset.searchMore === "true") {
            const searchMore = document.createElement("button");
            searchMore.type = "button";
            searchMore.className = "odoo-select-option odoo-select-command";
            searchMore.textContent = "Search more...";
            searchMore.addEventListener("mousedown", (event) => {
                event.preventDefault();
                menu.hidden = true;
                openSearchMoreModal(select, input);
            });
            menu.appendChild(searchMore);
        }
        const hasExact = selectOptions(select).some((option) => option.label.toLowerCase() === lowerQuery);
        if (query && !hasExact) {
            const add = document.createElement("button");
            add.type = "button";
            add.className = "odoo-select-option odoo-select-add";
            add.textContent = `Add '${query}'`;
            add.addEventListener("mousedown", async (event) => {
                event.preventDefault();
                await quickCreate(select, input, query);
                menu.hidden = true;
            });
            menu.appendChild(add);
        }
        menu.hidden = false;
    };

    input.addEventListener("focus", render);
    input.addEventListener("input", render);
    input.addEventListener("blur", () => {
        setTimeout(() => {
            menu.hidden = true;
        }, 160);
    });
});

document.querySelectorAll(".quote-form").forEach((form) => {
    const sftInput = form.querySelector(".quote-sft");
    const uomInput = form.querySelector(".quote-uom");
    const costInput = form.querySelector(".quote-cost");
    const gstInput = form.querySelector(".quote-gst");
    const serviceTypeInput = form.querySelector(".service-type-select");
    const sftField = form.querySelector(".sft-field");
    const uomField = form.querySelector(".uom-field");

    if (!sftInput || !uomInput || !costInput || !gstInput) {
        return;
    }

    const updateQuote = () => {
        const isDeepCleanup = serviceTypeInput && serviceTypeInput.value === "DEEP_FULL_SERVICE_CLEANUP";
        if (sftField && uomField) {
            sftField.hidden = !isDeepCleanup;
            uomField.hidden = !isDeepCleanup;
        }
        if (!isDeepCleanup) {
            sftInput.value = sftInput.value || "0";
            sftInput.required = false;
            uomInput.value = "LSM";
            costInput.readOnly = false;
        } else {
            sftInput.required = true;
        }
        const sft = Number.parseFloat(sftInput.value || "0");
        if (isDeepCleanup && uomInput.value === "SFT") {
            costInput.value = (sft * SQUARE_FOOT_RATE).toFixed(2);
            costInput.readOnly = true;
        } else if (isDeepCleanup) {
            costInput.readOnly = false;
        }
        const cost = Number.parseFloat(costInput.value || "0");
        gstInput.value = (cost * GST_RATE).toFixed(2);
    };

    sftInput.addEventListener("input", updateQuote);
    uomInput.addEventListener("change", updateQuote);
    costInput.addEventListener("input", updateQuote);
    if (serviceTypeInput) {
        serviceTypeInput.addEventListener("change", updateQuote);
    }
    updateQuote();
});

const distanceKm = (first, second) => {
    const earthRadiusKm = 6371;
    const toRadians = (value) => value * Math.PI / 180;
    const latDelta = toRadians(second.lat - first.lat);
    const lonDelta = toRadians(second.lon - first.lon);
    const firstLat = toRadians(first.lat);
    const secondLat = toRadians(second.lat);
    const a = Math.sin(latDelta / 2) ** 2
            + Math.cos(firstLat) * Math.cos(secondLat) * Math.sin(lonDelta / 2) ** 2;
    return earthRadiusKm * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
};

const renderLocationOptions = (input, results, locations, source = "") => {
    results.innerHTML = "";
    locations.forEach((location) => {
        const label = typeof location === "string" ? location : location.display_name;
        const option = document.createElement("button");
        option.type = "button";
        option.className = "location-result";
        option.textContent = label;
        option.addEventListener("click", () => {
            input.value = label;
            results.innerHTML = "";
        });
        results.appendChild(option);
    });
    if (source === "google" && locations.length) {
        const poweredBy = document.createElement("div");
        poweredBy.className = "location-attribution";
        poweredBy.textContent = "Powered by Google";
        results.appendChild(poweredBy);
    }
};

const searchGoogleLocations = async (query) => {
    const response = await fetch(`/api/locations/search?query=${encodeURIComponent(query)}`);
    if (!response.ok) {
        return {source: "fallback", locations: []};
    }
    return response.json();
};

const searchOpenStreetMapLocations = async (query) => {
    const params = new URLSearchParams({
        format: "json",
        limit: "8",
        countrycodes: "ca",
        bounded: "1",
        viewbox: BC_LOCATION_VIEWBOX,
        addressdetails: "1",
        q: `${query}, British Columbia, Canada`
    });
    const response = await fetch(`https://nominatim.openstreetmap.org/search?${params.toString()}`);
    return (await response.json()).filter((location) => {
        const point = {
            lat: Number.parseFloat(location.lat),
            lon: Number.parseFloat(location.lon)
        };
        return Number.isFinite(point.lat)
                && Number.isFinite(point.lon)
                && distanceKm(BC_LOCATION_CENTER, point) <= BC_LOCATION_RADIUS_KM;
    }).slice(0, 8);
};

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
                const googleResult = await searchGoogleLocations(query);
                if (googleResult.locations.length) {
                    renderLocationOptions(input, results, googleResult.locations, googleResult.source);
                    return;
                }
                renderLocationOptions(input, results, await searchOpenStreetMapLocations(query));
            } catch {
                results.innerHTML = "";
            }
        }, 350);
    });
});

const dashboardChartState = new Map();
const cssColor = (name) => getComputedStyle(document.documentElement).getPropertyValue(name).trim();

const parseDatasetList = (value) => {
    if (!value) {
        return [];
    }
    return value
        .replace(/^\[/, "")
        .replace(/\]$/, "")
        .split(",")
        .map((item) => item.trim())
        .filter(Boolean);
};

const parseNumberList = (value) => parseDatasetList(value).map((item) => Number.parseFloat(item) || 0);

const prepareCanvas = (canvas) => {
    const context = canvas.getContext("2d");
    const ratio = window.devicePixelRatio || 1;
    const width = Math.max(240, Math.floor(canvas.clientWidth || canvas.parentElement.clientWidth));
    const height = Math.max(210, Math.floor(canvas.clientHeight || Number.parseInt(canvas.getAttribute("height"), 10) || 260));
    canvas.width = width * ratio;
    canvas.height = height * ratio;
    context.setTransform(ratio, 0, 0, ratio, 0, 0);
    context.clearRect(0, 0, width, height);
    return {context, width, height};
};

const chartState = (canvas) => {
    if (!dashboardChartState.has(canvas.id)) {
        dashboardChartState.set(canvas.id, {
            hidden: new Set(),
            hoverIndex: null,
            points: [],
            segments: [],
            tooltip: null
        });
    }
    return dashboardChartState.get(canvas.id);
};

const ensureTooltip = (canvas) => {
    const state = chartState(canvas);
    if (!state.tooltip) {
        state.tooltip = document.createElement("div");
        state.tooltip.className = "chart-tooltip";
        canvas.closest(".chart-shell").appendChild(state.tooltip);
    }
    return state.tooltip;
};

const hideTooltip = (canvas) => {
    const tooltip = chartState(canvas).tooltip;
    if (tooltip) {
        tooltip.classList.remove("is-visible");
    }
};

const showTooltip = (canvas, x, y, html) => {
    const tooltip = ensureTooltip(canvas);
    if (tooltip.dataset.content !== html) {
        tooltip.innerHTML = html;
        tooltip.dataset.content = html;
    }
    tooltip.style.left = `${x}px`;
    tooltip.style.top = `${y}px`;
    tooltip.classList.add("is-visible");
};

const drawNoData = (context, width, height) => {
    context.fillStyle = cssColor("--muted");
    context.font = "700 14px Roboto, sans-serif";
    context.textAlign = "center";
    context.fillText("No data yet", width / 2, height / 2);
};

const moneyCompact = (value) => new Intl.NumberFormat("en-CA", {
    notation: "compact",
    maximumFractionDigits: 1
}).format(value);

const moneyFull = (value) => new Intl.NumberFormat("en-CA", {
    style: "currency",
    currency: "CAD",
    maximumFractionDigits: 0
}).format(value);

const numberFull = (value) => new Intl.NumberFormat("en-CA", {
    maximumFractionDigits: 1
}).format(value);

const formatChartValue = (value, valueLabel) => valueLabel === "Jobs" ? numberFull(value) : moneyFull(value);

const shortMonthLabel = (label) => label.replace(/([A-Za-z]{3})\w*\s+(\d{4})/, "$1 '$2").replace("'20", "'");

const drawProfitTrend = () => {
    const canvas = document.getElementById("profitTrendChart");
    if (!canvas) {
        return;
    }
    const state = chartState(canvas);
    const labels = parseDatasetList(canvas.dataset.labels);
    const revenue = parseNumberList(canvas.dataset.revenue);
    const payroll = parseNumberList(canvas.dataset.payroll);
    const profit = parseNumberList(canvas.dataset.profit);
    const {context, width, height} = prepareCanvas(canvas);
    state.points = [];
    if (!labels.length) {
        drawNoData(context, width, height);
        return;
    }

    const padding = {top: 16, right: 18, bottom: width < 520 ? 46 : 34, left: 56};
    const chartWidth = Math.max(120, width - padding.left - padding.right);
    const chartHeight = Math.max(110, height - padding.top - padding.bottom);
    const maxValue = Math.max(1, ...revenue, ...payroll, ...profit.map((value) => Math.abs(value)));
    const xStep = chartWidth / Math.max(labels.length - 1, 1);
    const y = (value) => padding.top + chartHeight - (Math.max(value, 0) / maxValue) * chartHeight;

    context.strokeStyle = cssColor("--canvas-grid");
    context.lineWidth = 1;
    context.fillStyle = cssColor("--muted");
    context.font = "700 11px Roboto, sans-serif";
    context.textAlign = "right";
    for (let index = 0; index <= 4; index++) {
        const gridY = padding.top + (chartHeight / 4) * index;
        context.beginPath();
        context.moveTo(padding.left, gridY);
        context.lineTo(width - padding.right, gridY);
        context.stroke();
        context.fillText(moneyCompact(maxValue - (maxValue / 4) * index), padding.left - 8, gridY + 4);
    }

    labels.forEach((label, index) => {
        const x = padding.left + xStep * index;
        const displayLabel = shortMonthLabel(label);
        context.fillStyle = cssColor("--muted");
        context.font = "700 11px Roboto, sans-serif";
        if (chartWidth / labels.length < 68) {
            context.save();
            context.translate(x, height - 10);
            context.rotate(-0.45);
            context.textAlign = "right";
            context.fillText(displayLabel, 0, 0);
            context.restore();
        } else {
            context.textAlign = "center";
            context.fillText(displayLabel, x, height - 12);
        }
    });

    const series = [
        {name: "Revenue", values: revenue, color: CHART_COLORS[1]},
        {name: "Payroll", values: payroll, color: CHART_COLORS[2]},
        {name: "Profit", values: profit, color: CHART_COLORS[0]}
    ];

    series.forEach((item) => {
        context.strokeStyle = item.color;
        context.lineWidth = 3;
        context.beginPath();
        item.values.forEach((value, index) => {
            const x = padding.left + xStep * index;
            const pointY = y(value);
            if (index === 0) {
                context.moveTo(x, pointY);
            } else {
                context.lineTo(x, pointY);
            }
        });
        context.stroke();
    });

    labels.forEach((label, index) => {
        const x = padding.left + xStep * index;
        const values = series.map((item) => ({...item, value: item.values[index] || 0, y: y(item.values[index] || 0)}));
        state.points.push({label, x, values});
        values.forEach((point) => {
            const isHover = state.hoverIndex === index;
            context.fillStyle = point.color;
            context.beginPath();
            context.arc(x, point.y, isHover ? 5 : 4, 0, Math.PI * 2);
            context.fill();
        });
    });

    bindTrendEvents(canvas);
};

const renderPieLegend = (canvas, labels) => {
    const shell = canvas.closest(".chart-shell");
    const state = chartState(canvas);
    let legend = shell.querySelector(".chart-legend");
    if (!legend) {
        legend = document.createElement("div");
        legend.className = "chart-legend";
        shell.insertBefore(legend, canvas);
    }
    legend.innerHTML = "";
    labels.forEach((label, index) => {
        const button = document.createElement("button");
        button.type = "button";
        button.className = state.hidden.has(index) ? "is-disabled" : "";
        button.innerHTML = `<span class="chart-legend-swatch" style="background:${CHART_COLORS[index % CHART_COLORS.length]}"></span>${label}`;
        button.addEventListener("click", () => {
            if (state.hidden.has(index)) {
                state.hidden.delete(index);
            } else {
                state.hidden.add(index);
            }
            drawPieChart(canvas.id);
        });
        legend.appendChild(button);
    });
};

const drawPieChart = (canvasId) => {
    const canvas = document.getElementById(canvasId);
    if (!canvas) {
        return;
    }
    const state = chartState(canvas);
    const labels = parseDatasetList(canvas.dataset.labels);
    const values = parseNumberList(canvas.dataset.values);
    renderPieLegend(canvas, labels);
    const {context, width, height} = prepareCanvas(canvas);
    const activeValues = values.map((value, index) => state.hidden.has(index) ? 0 : value);
    const total = activeValues.reduce((sum, value) => sum + value, 0);
    state.segments = [];
    if (!labels.length || total <= 0) {
        drawNoData(context, width, height);
        return;
    }

    const radius = Math.min(width, height) * 0.34;
    const centerX = width / 2;
    const centerY = height / 2;
    let startAngle = -Math.PI / 2;

    activeValues.forEach((value, index) => {
        if (value <= 0) {
            return;
        }
        const angle = (value / total) * Math.PI * 2;
        const endAngle = startAngle + angle;
        const isHover = state.hoverIndex === index;
        const dimOtherSlices = state.hoverIndex !== null && !isHover;
        const drawRadius = isHover ? radius + 8 : radius;
        context.globalAlpha = dimOtherSlices ? 0.46 : 1;
        context.fillStyle = CHART_COLORS[index % CHART_COLORS.length];
        context.beginPath();
        context.moveTo(centerX, centerY);
        context.arc(centerX, centerY, drawRadius, startAngle, endAngle);
        context.closePath();
        context.fill();
        context.globalAlpha = 1;
        context.strokeStyle = cssColor("--panel");
        context.lineWidth = 2;
        context.stroke();
        state.segments.push({
            index,
            label: labels[index],
            value,
            color: CHART_COLORS[index % CHART_COLORS.length],
            startAngle,
            endAngle,
            centerX,
            centerY,
            radius: drawRadius
        });
        startAngle = endAngle;
    });

    bindPieEvents(canvas);
};

const relativePointer = (canvas, event) => {
    const rect = canvas.getBoundingClientRect();
    return {
        x: event.clientX - rect.left,
        y: event.clientY - rect.top
    };
};

const bindPieEvents = (canvas) => {
    if (canvas.dataset.boundPie === "true") {
        return;
    }
    canvas.dataset.boundPie = "true";
    canvas.addEventListener("mousemove", (event) => {
        const state = chartState(canvas);
        const pointer = relativePointer(canvas, event);
        const segment = state.segments.find((item) => {
            const dx = pointer.x - item.centerX;
            const dy = pointer.y - item.centerY;
            const distance = Math.sqrt(dx * dx + dy * dy);
            let angle = Math.atan2(dy, dx);
            if (angle < -Math.PI / 2) {
                angle += Math.PI * 2;
            }
            return distance <= item.radius && angle >= item.startAngle && angle <= item.endAngle;
        });
        const nextIndex = segment ? segment.index : null;
        if (state.hoverIndex !== nextIndex) {
            state.hoverIndex = nextIndex;
            drawPieChart(canvas.id);
        }
        if (segment) {
            const percent = (segment.value / state.segments.reduce((sum, item) => sum + item.value, 0)) * 100;
            const valueLabel = canvas.dataset.valueLabel || "Value";
            const anchorAngle = segment.startAngle + ((segment.endAngle - segment.startAngle) / 2);
            const anchorRadius = segment.radius * 0.72;
            showTooltip(canvas, segment.centerX + Math.cos(anchorAngle) * anchorRadius, segment.centerY + Math.sin(anchorAngle) * anchorRadius, `
                <div>${segment.label}</div>
                <div class="chart-tooltip-row">
                    <span class="chart-tooltip-swatch" style="background:${segment.color}"></span>
                    <span>${valueLabel}: ${formatChartValue(segment.value, valueLabel)} (${percent.toFixed(1)}%)</span>
                </div>
            `);
        } else {
            hideTooltip(canvas);
        }
    });
    canvas.addEventListener("mouseleave", () => {
        const state = chartState(canvas);
        state.hoverIndex = null;
        hideTooltip(canvas);
        drawPieChart(canvas.id);
    });
};

const bindTrendEvents = (canvas) => {
    if (canvas.dataset.boundTrend === "true") {
        return;
    }
    canvas.dataset.boundTrend = "true";
    canvas.addEventListener("mousemove", (event) => {
        const state = chartState(canvas);
        const pointer = relativePointer(canvas, event);
        const nearest = state.points
                .map((point, index) => ({...point, index, distance: Math.abs(point.x - pointer.x)}))
                .sort((first, second) => first.distance - second.distance)[0];
        if (!nearest || nearest.distance > 36) {
            state.hoverIndex = null;
            hideTooltip(canvas);
            drawProfitTrend();
            return;
        }
        if (state.hoverIndex !== nearest.index) {
            state.hoverIndex = nearest.index;
            drawProfitTrend();
        }
        showTooltip(canvas, nearest.x, Math.min(...nearest.values.map((point) => point.y)), `
            <div>${nearest.label}</div>
            ${nearest.values.map((point) => `
                <div class="chart-tooltip-row">
                    <span class="chart-tooltip-swatch" style="background:${point.color}"></span>
                    <span>${point.name}: ${moneyFull(point.value)}</span>
                </div>
            `).join("")}
        `);
    });
    canvas.addEventListener("mouseleave", () => {
        const state = chartState(canvas);
        state.hoverIndex = null;
        hideTooltip(canvas);
        drawProfitTrend();
    });
};

const drawDashboardCharts = () => {
    drawProfitTrend();
    drawPieChart("serviceMixChart");
    drawPieChart("jobStatusChart");
    drawPieChart("payrollStatusChart");
};

drawDashboardCharts();
window.addEventListener("resize", drawDashboardCharts);
