import { Client } from '/webjars/stomp__stompjs/esm6/index.js';
import { Logger } from '/js/ci-lib-logger.js';
import { Translations } from '/js/ci-lib-translations.js';

export {
  init,
  GET,
  POST,
  PUT,
  PATCH,
  DELETE,
  showGlobalLoader,
  hideGlobalLoader,
  requestModal,
  closeModal,
  submitFromModal,
  sendFromForm,
  dismissErrors,
  redirect,
  reload,
  openSite,
  FunctionRegistry,
};

/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 *
 * author: <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */

const version = "4.0.0";

const CLASS_HIDDEN = "hidden";

const LOGGER = Logger.createFor("ci-lib-spring-web");

const StompJs = { Client };

/**
 * Request settings applied to every call of the library (fetch and STOMP connect).
 */
const requestConfig = {
  credentials: "same-origin",
  headers: () => ({}),
};

/**
 * @param {object} [options]
 * @param {RequestCredentials} [options.credentials] - fetch credentials mode; use "include" if the API lives on another origin
 *   (the server then needs CORS with allowCredentials).
 * @param {object|function(): object} [options.headers] - headers added to every request and to the STOMP CONNECT frame, e.g.
 *   { Authorization: `Bearer ${token}` }. A function is evaluated per request/connect, so rotating tokens are picked up.
 */
function init(options = {}) {
  window.CILIB = this;

  if (options.credentials !== undefined) {
    requestConfig.credentials = options.credentials;
  }

  if (options.headers !== undefined) {
    requestConfig.headers = typeof options.headers === "function" ? options.headers : () => options.headers;
  }
}

$(document).ready(function () {
  LOGGER.info("ci-lib-js: ", version);

  // load properties if neccessary
  var propertiesUrl = ($('#ui-properties-url').html() || "").trim();

  if (propertiesUrl) {
    LOGGER.debug("properties are requested from ", propertiesUrl);
    GET(propertiesUrl);
  } else {
    LOGGER.debug("no properties fetch url present, skip.");
  }

  // load translations if neccessary
  var translationUrl = ($('#ui-translations-url').html() || "").trim();

  if (translationUrl) {
    LOGGER.debug("translations are checked at ", translationUrl);
    Translations.init({ url: translationUrl, request: _request, locale: ($('#ui-locale').html() || "").trim() });
  } else {
    LOGGER.debug("no translation url present, skip.");
  }

  // close modal on overlay click
  $(document).on("click", ".modal-overlay", function (event) {
    if (event.target === this) {
      if ($(`#modal-overlay-${openModals} .modal-body`).attr("data-close-on-overlay") === "true") {
        closeModal();
      }
    }
  });

  // trigger button on enter key on input with data-on-enter-press attribute
  $(document).on('keydown', function (event) {
    if (event.key === 'Enter') {
      const $active = $(document.activeElement);

      const buttonId = $active.data('on-enter-press');
      if (buttonId && buttonId !== "") {
        const $button = $('#' + buttonId);
        if ($button.length) {
          event.preventDefault();
          $button.click();
        }
      }
    }
  });

  // prevent key presses when loader overlay is shown
  $(document).on('keydown', function (event) {
    const $overlay = $("#global-loader-overlay");

    if ($overlay.length && !$overlay.hasClass("hidden")) {
      event.preventDefault();

      if (event.key === 'F5') {
        if (event.shiftKey) {
          reload();
        } else {
          clientsideError(Translations.translate("ui-f5-blocked"));
        }
      }
    }
  });

  // change color of button on form change
  $(document).on('input change', '[data-connected-btn] input, [data-connected-btn] select, [data-connected-btn] textarea', function () {
    var $container = $(this).closest('[data-connected-btn]');
    var btnId = $container.data('connected-btn');

    updateButton(btnId);
  });

  // keep the value display and the track fill of a slider in line with its thumb
  $(document).on('input', 'input.form-slider-input', function () {
    updateSlider(this);
  });

  // native pattern validation only reports on a form submit, which the lib never triggers
  $(document).on('input change', 'input[data-value-type="TEXTFIELD"][pattern]', function () {
    checkPattern(this);
  });

  // jump to the value of a clicked label on a custom value slider
  $(document).on('click', '.input-slider-label', function () {
    setSliderValue(this.closest('.input-slider').querySelector('input.form-slider-input'), $(this).index());
  });

  // jump to the clicked position on the scale
  $(document).on('click', '.input-slider-ticks', function (e) {
    const slider = this.closest('.input-slider').querySelector('input.form-slider-input');
    const rect = this.getBoundingClientRect();
    const ratio = Math.min(Math.max((e.clientX - rect.left) / rect.width, 0), 1);
    const min = parseFloat(slider.min);

    setSliderValue(slider, min + ratio * (parseFloat(slider.max) - min));
  });

  // trigger hover on input field icons
  $(document).on('mouseenter mouseleave', '.tooltip-container', function (e) {
    const $tooltip = $(this).find('.tooltip-text');

    if (e.type === 'mouseenter') {
      const rect = this.getBoundingClientRect();

      $tooltip.css({
        position: 'fixed',
        left: rect.left + rect.width / 2 + 'px',
        top: rect.top - $tooltip.outerHeight() - 5 + 'px',
      });

      $tooltip.addClass('tooltip-visible');
    } else {
      $tooltip.removeClass('tooltip-visible');
      $tooltip.css({ position: '', left: '', top: '' });
    }
  });

  // trigger info fetch for input fields
  $(document).on("click", "[data-fetch-input-info-url]", function () {
    const url = $(this).attr("data-fetch-input-info-url");

    if (url) {
      GET(url);
    }
  });

  // trigger tooltip on hover
  $(document).on("mouseenter", "[data-tooltip]", function (e) {
    const text = $(this).data("tooltip");
    const tooltip = $("<div class='on-hover-tooltip'></div>").text(text);

    $("body").append(tooltip);

    tooltip.css({
      "z-index": 1000 + 200 * openModals
    });

    const updatePosition = (ev) => {
      const vw = $(window).width();
      const vh = $(window).height();
      const cx = vw / 2;
      const cy = vh / 2;
      const offset = 12;

      let top, left;

      // Horizontal: left or right of cursor based on viewport center
      left = ev.clientX < cx
        ? ev.clientX + offset
        : ev.clientX - tooltip.outerWidth() - offset;

      // Vertical: above or below cursor
      top = ev.clientY < cy
        ? ev.clientY + offset
        : ev.clientY - tooltip.outerHeight() - offset;

      // Clamp inside viewport
      left = Math.max(5, Math.min(left, vw - tooltip.outerWidth() - 5));
      top = Math.max(5, Math.min(top, vh - tooltip.outerHeight() - 5));

      tooltip.css({ top, left });
    };

    updatePosition(e);
    tooltip.fadeIn(150);

    $(this).on("mousemove.on-hover-tooltip", updatePosition);

  }).on("mouseleave", "[data-tooltip]", function () {
    $(".on-hover-tooltip").remove();
    $(this).off("mousemove.on-hover-tooltip");
  });

  // close modal on esc button press
  $(document).on("keydown", function (e) {
    if (e.key === "Escape") {
      CILIB.closeModal();
    }
  });

  // toggle dropdown on burger click
  $(document).on("click", ".burger-menu", function (e) {
    e.stopPropagation();

    $(".burger-dropdown").removeClass("show");

    const $dropdown = $(this).children(".burger-dropdown");
    const rect = this.getBoundingClientRect();
    const dropdownWidth = $dropdown.outerWidth();
    const dropdownHeight = $dropdown.outerHeight();
    const viewportWidth = $(window).width();
    const viewportHeight = $(window).height();

    let top, left;

    if (rect.top + rect.height + dropdownHeight < viewportHeight) {
      top = rect.bottom;
    } else {
      top = rect.top - dropdownHeight;
    }

    if (rect.left + dropdownWidth < viewportWidth) {
      left = rect.left;
    } else if (rect.right - dropdownWidth > 0) {
      left = rect.right - dropdownWidth;
    } else {
      left = (viewportWidth - dropdownWidth) / 2;
    }

    $dropdown.css({ top: top + "px", left: left + "px" });
    $dropdown.toggleClass("show");
  });

  // handle item click
  $(document).on("click", ".burger-item", function () {
    var url = $(this).data("burger-url");
    var method = $(this).data("burger-method");

    if (url) {
      $(".burger-dropdown").removeClass("show");
      showGlobalLoader();

      switch (method) {
        case "POST":
          POST(url);
          break;

        case "GET":
          GET(url);
          break;

        case "DELETE":
          DELETE(url);
          break;

        case "PATCH":
          PATCH(url);
          break;

        case "PUT":
          PUT(url);
          break;

        default:
          LOGGER.error("BurgerItem method invalid.", { method });
      }
    }
  });

  // close dropdown when clicking outside
  $(document).on("click", function (e) {
    $(".burger-dropdown").removeClass("show");
  });

  // --> register functions
  FunctionRegistry._registerInternal("GET", (url) => {
    GET(url);
  });

  FunctionRegistry._registerInternal("POST", (url, args = {}) => {
    POST(url, args);
  });

  FunctionRegistry._registerInternal("showGlobalLoader", (text) => {
    showGlobalLoader(text);
  });

  FunctionRegistry._registerInternal("hideGlobalLoader", () => {
    hideGlobalLoader();
  });

  FunctionRegistry._registerInternal("requestModal", (url, args = {}) => {
    requestModal(url, args);
  });

  FunctionRegistry._registerInternal("closeModal", () => {
    closeModal();
  });

  FunctionRegistry._registerInternal("submitFromModal", () => {
    submitFromModal();
  });

  FunctionRegistry._registerInternal("sendFromForm", (id, url) => {
    sendFromForm(id, url);
  });

  FunctionRegistry._registerInternal("resetForm", (id) => {
    resetForm(id);
  });

  FunctionRegistry._registerInternal("redirect", (url) => {
    redirect(url);
  });

  FunctionRegistry._registerInternal("reload", () => {
    reload();
  });

  FunctionRegistry._registerInternal("dismissErrors", () => {
    dismissErrors();
  });

  FunctionRegistry._registerInternal("resetButton", (id) => {
    resetButton(id);
  });

  FunctionRegistry._registerInternal("openSite", (url) => {
    openSite(url);
  });

  FunctionRegistry._registerInternal("noop", () => {
    LOGGER.warn("a call to the NoOp function occured. This is not normal and should be investigated.");
  });

  FunctionRegistry._registerInternal("registerTagInput", (settings) => {
    registerTagInput(settings);
  });

  hideGlobalLoader();
});

// === > global functions ==================================================================================================================
const SAFE_METHODS = new Set(["GET", "HEAD", "OPTIONS", "TRACE"]);

/**
 * CSRF header for state changing requests. Supports Spring Security's cookie repository (XSRF-TOKEN cookie -> X-XSRF-TOKEN) and the
 * _csrf / _csrf_header meta tags rendered by server side templates. Empty if the application uses neither.
 */
function _csrfHeaders(method) {
  if (SAFE_METHODS.has(method)) {
    return {};
  }

  const token = document.querySelector('meta[name="_csrf"]');

  if (token && token.content) {
    const header = document.querySelector('meta[name="_csrf_header"]');

    return { [(header && header.content) || "X-CSRF-TOKEN"]: token.content };
  }

  const cookie = document.cookie.split("; ").find((entry) => entry.startsWith("XSRF-TOKEN="));

  return cookie ? { "X-XSRF-TOKEN": decodeURIComponent(cookie.substring("XSRF-TOKEN=".length)) } : {};
}

/**
 * fetch() with the library wide request settings. Headers of the call win over configured ones, which win over the CSRF header.
 */
function _request(url, options = {}) {
  const method = (options.method || "GET").toUpperCase();

  return fetch(url, {
    credentials: requestConfig.credentials,
    ...options,
    headers: { ..._csrfHeaders(method), ...requestConfig.headers(), ...options.headers },
  });
}

/**
 * Fetch data from a endpoint.
 * 
 * @param {string} method 
 * @param {string} endpointUrl 
 * @param {object} dataToSend 
 */
function _fetchHttp(method, endpointUrl) {
  if (endpointUrl === undefined || endpointUrl === "") {
    throw new Error(`"${method} URL cannot be undefined/empty"`);
  }

  return _request(endpointUrl, {
    method: method,
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
      }

      return response.json();
    })
    .then((json) => {
      handleResponse(json);

      return json;
    })
    .catch((error) => {
      hideGlobalLoader();
      clientsideError(error.message);

      throw error;
    });
}

/**
 * Send data asynchronous as JSON payload.
 * 
 * @param {string} method 
 * @param {string} endpointUrl 
 * @param {object} dataToSend 
 */
function _fetchHttpWithPayload(method, endpointUrl, dataToSend = {}) {
  if (endpointUrl === undefined || endpointUrl === "") {
    throw new Error(`"${method} URL cannot be undefined/empty"`);
  }

  return _request(endpointUrl, {
    method: method,
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(dataToSend),
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
      }
      return response.json();
    })
    .then((json) => {
      handleResponse(json);

      return json;
    })
    .catch((error) => {
      hideGlobalLoader();
      clientsideError(error.message);

      throw error;
    });
}

/**
 *
 * @param {String} endpointUrl
 */
function GET(endpointUrl) {
  _fetchHttp("GET", endpointUrl);
}

/**
 *
 * @param {String} endpointUrl
 */
function DELETE(endpointUrl) {
  _fetchHttp("DELETE", endpointUrl);
}

/**
 *
 * @param {String} endpointUrl
 * @param {*} dataToSend
 */
function POST(endpointUrl, dataToSend = {}) {
  _fetchHttpWithPayload("POST", endpointUrl, dataToSend);
}

/**
 *
 * @param {String} endpointUrl
 * @param {*} dataToSend
 */
function PUT(endpointUrl, dataToSend = {}) {
  _fetchHttpWithPayload("PUT", endpointUrl, dataToSend);
}

/**
 *
 * @param {String} endpointUrl
 * @param {*} dataToSend
 */
function PATCH(endpointUrl, dataToSend = {}) {
  _fetchHttpWithPayload("PATCH", endpointUrl, dataToSend);
}

/**
 * Explicit function for form submit so that we can send data and files
 *
 * @param {String} endpointUrl
 * @param {FormData} formData
 */
function POSTFormData(endpointUrl, formData = {}) {
  if (endpointUrl === undefined || endpointUrl === "") {
    throw new Error(`"POST URL cannot be undefined/empty"`);
  }

  return _request(endpointUrl, {
    method: "POST",
    body: formData,
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
      }

      return response.json();
    })
    .then((json) => {
      handleResponse(json);

      return json;
    })
    .catch((error) => {
      hideGlobalLoader();
      clientsideError(error.message);

      throw error;
    });
}

/**
 * Extract and send data from a form to an url
 *
 * @param {String} id of form to grab values from
 * @param {String} url to call on backend
 */
function sendFromForm(id, url) {
  showGlobalLoader("collect data");
  resetMarker();

  const formData = extractValuesToSubmit(id);

  changeGlobalLoaderText("send data");
  POSTFormData(url, formData);
}

function handleResponse(response) {
  handleMessages(response.messages);

  switch (response.action) {
    case "NOTIFICATION": {
      // do nothing since this is handled by handleMessages()
      break;
    }

    case "PROPERTIES": {
      registerProperties(response.properties);
      initWebSocketsFromConfig(response.properties);
      break;
    }

    case "MODAL": {
      openModal(response);
      break;
    }

    case "CONTENT": {
      contentResponse(response);
      break;
    }

    case "PROGRESS": {
      updateProgress(response);
      break;
    }

    case "COMPOUND": {
      for (const data of response.responses) {
        handleResponse(data);
      }
      break;
    }

    case "FETCH_TAGS": {
      tagifyWhitelists.set(response.inputId, response.results);

      const tagify = tagifyInstances.get(response.inputId);
      if (tagify) {
        tagify.whitelist = response.results;
      }
      break;
    }

    case "FETCH_TAGS_RESULT": {
      let tagInputId = response.inputId;
      let inputValue = response.originalInputValue;
      let result = response.results;
      let tagify = tagifyInstances.get(tagInputId);

      tagify.settings.whitelist = result.concat(tagify.value || []);
      tagify.loading(false).dropdown.show(inputValue);
      break;
    }

    case "REDIRECT": {
      redirect(response.url);
      break;
    }

    case "OBJECT": {
      FunctionRegistry.call(response.function, response.object);
      break;
    }

    case "FILE_UPLOAD": {
      startFileUpload(response);
      break;
    }
  }

  call(response.calls);
}

function call(calls) {
  calls.forEach((call) => {
    FunctionRegistry.call(call.name, ...call.args);
  });
}

/**
 * Reload the current site with a new url.
 * 
 * @param {*} url to load
 */
function redirect(url) {
  window.location.href = url;
}

function reload() {
  location.reload();
}

function openSite(url) {
  const fullUrl = new URL(url, window.location.href).href;
  window.open(fullUrl, '_blank');
}

function registerProperties(properties) {
  LOGGER.debug("properties received", properties)
}

function toggleBodyScroll() {
  if (errorOverlayVisible || globalLoaderVisible || openModals > 0) {
    $("body").addClass("no-scroll");
  } else {
    $("body").removeClass("no-scroll");
  }
}
// === < global functions ==================================================================================================================
// === > function register =================================================================================================================
const FunctionRegistry = (function () {
  const internalFunctions = new Map();
  const externalFunctions = new Map();

  function registerInternal(name, fn) {
    if (typeof fn !== "function") {
      throw new Error("Must register a function.");
    }

    internalFunctions.set(name, fn);
  }

  return {
    // Public way for users to register their own functions
    registerUserFunction(name, fn) {
      if (internalFunctions.has(name)) {
        throw new Error(`Cannot overwrite internal function: ${name}`);
      }

      externalFunctions.set(name, fn);
    },

    hasFunction(name) {
      const fn = internalFunctions.get(name) || externalFunctions.get(name);

      return typeof fn === "function";
    },

    // Public function caller (calls either internal or user function)
    call(name, ...args) {
      const fn = internalFunctions.get(name) || externalFunctions.get(name);

      if (typeof fn !== "function") {
        LOGGER.warn(`Function '${name}' not found.`);
        return;
      }

      return fn(...args);
    },

    // List registered functions
    listFunctions() {
      return {
        internal: Array.from(internalFunctions.keys()),
        external: Array.from(externalFunctions.keys()),
      };
    },

    _registerInternal: registerInternal,
  };
})();
// === < function register =================================================================================================================
// === > content ===========================================================================================================================
function contentResponse(response) {
  const elementSelector = response.elementId ? `#${response.elementId}` : null;
  const parentSelector = response.parentId ? `#${response.parentId}` : null;

  const $element = elementSelector ? $(elementSelector) : $();
  const $parent = parentSelector ? $(parentSelector) : $();

  if ($(response).length) {
    switch (response.handling) {
      case "APPEND": if ($parent.length) {
        $parent.append(response.htmlRep);
      }
        break;

      case "PREPEND":
        if ($parent.length) {
          $parent.prepend(response.htmlRep);
        }
        break;

      case "REPLACE":
        if ($element.length) {
          $element.replaceWith(response.htmlRep);
        }
        break;

      case "UPCERT":
        if ($element.length) {
          $element.replaceWith(response.htmlRep);
        } else {
          $parent.append(response.htmlRep);
        }
        break;

      case "UPCERT_PREPEND":
        if ($element.length) {
          $element.replaceWith(response.htmlRep);
        } else {
          $parent.prepend(response.htmlRep);
        }
        break;

      case "DELETE":
        if ($element.length) {
          $element.remove();
        }
        break;

      default:
        LOGGER.error(`"unrecognized content response type [${response.handling}]`);
    }
  }
}

function updateProgress(response) {
  const $elem = $(`#global-loader-overlay`);
  const $bar = $elem.find(".loadbar");
  const $text = $elem.find(".loader-text");

  if (!$bar.length) {
    return;
  }

  $bar
    .data("progress", response.progress)
    .css("--v", response.progress);

  $text.text(response.text);
}

function resetProgress() {
  const $elem = $(`#global-loader-overlay`);
  const $bar = $elem.find(".loadbar");
  const $text = $elem.find(".loader-text");

  if (!$bar.length) {
    return;
  }

  $bar
    .data("progress", 0)
    .css("--v", 0);

  $text.text("loading");
}
// === < content ===========================================================================================================================
// === > global loader =====================================================================================================================
var globalLoaderVisible = false;
/**
 * Shows the global loader overlay
 *
 * @param {type} text - text to be shown in loader if neccessary
 *
 * @returns {undefined}
 */
function showGlobalLoader(text = "loading...") {
  globalLoaderVisible = true;

  $("#global-loader-text").text(text);
  $("#global-loader-overlay").removeClass(CLASS_HIDDEN);

  toggleBodyScroll();
}

/**
 * Hides the global loader overlay
 *
 * @returns {undefined}
 */
function hideGlobalLoader() {
  $("#global-loader-text").text("loading...");
  $("#global-loader-overlay").addClass(CLASS_HIDDEN);
  globalLoaderVisible = false;

  resetProgress();

  toggleBodyScroll();
}

function changeGlobalLoaderText(text) {
  $("#global-loader-text").text(text);
}
// === < global loader =====================================================================================================================
// === > notifications =====================================================================================================================
var errorOverlayVisible = false;

/**
 * @param {type} messages
 * @param {type} btnName - label for the "ok" btn
 */
function handleMessages(messages, btnName = "ok") {
  for (let m in messages) {
    let msg = messages[m];
    let target = msg.target;

    switch (target) {
      case "MODAL":
        errorOverlayVisible = true;
        handleModalMsg(msg);
        break;

      case "POP_UP":
        handlePopupMsg(msg);
        break;

      case "MARKER":
        handleMarkerMsg(msg);
        break;

      default:
        LOGGER.error("unrecognized message target.", { target });
    }
  }

  if (errorOverlayVisible) {
    $("#btn-error-text").html(btnName);
    $("#error-overlay").removeClass(CLASS_HIDDEN);
    $("#error-overlay").find(":button").focus();
    toggleBodyScroll();
  }
}

function handleModalMsg(msg) {
  $("#error-holder").append(
    $("<div>")
      .addClass(`message-container alert ${msg.type.toLowerCase()}`)
      .append($("<i>").addClass(`bi ${msg.icon}`))
      .append($("<div>").text(msg.msg))
  );
}

function handlePopupMsg(msg) {
  $("#popup-holder").append(
    $("<div>")
      .addClass("pop-up pop-up-fade-out")
      .append($("<div>").addClass(msg.type.toLowerCase()).text(msg.msg))
  );
}

function handleMarkerMsg(msg) {
  $(`#${msg.uid}-text`).text(msg.text);
  $(`#${msg.uid}`).removeClass(CLASS_HIDDEN);
}

function resetMarker() {
  $(".error-marker").addClass(CLASS_HIDDEN);
}

/**
 *
 * @param {String} msg  - message to be shown -> musst be set
 *
 * @return {undefined}
 */
function clientsideError(msg) {
  LOGGER.error("client error.", { msg });

  handleMessages([{ msg: msg, target: "MODAL", type: "ERROR" }]);
}

/**
 * Clears the error messages and closes the error overlay
 *
 * @returns {undefined}
 */
function dismissErrors() {
  $("#error-overlay").addClass(CLASS_HIDDEN);
  $("#error-holder").html("");

  errorOverlayVisible = false;

  toggleBodyScroll();
}
// === < notifications =====================================================================================================================
// === > modal =============================================================================================================================
let openModals = 0;

function requestModal(url, args = {}) {
  showGlobalLoader("load modal");

  POST(url, args);
}

/**
 * Function to submit directly from a modal
 */
function submitFromModal() {
  showGlobalLoader("collect data");

  const modal = $(`#modal-overlay-${openModals}`);
  const formId = modal.find('.form-container').first().attr('id');
  const url = modal.attr("data-server-target");

  if (formId === undefined || formId === "") {
    clientsideError("modal: no target id found");

    throw new Error("modal: no target id found (no form defined?)");
  }

  if (url === undefined || url === "") {
    clientsideError("modal: no target url defined");

    throw new Error("modal: no target url defined");
  }

  const formData = extractValuesToSubmit(formId);

  resetMarker();
  changeGlobalLoaderText("send data");
  POSTFormData(url, formData);
}

/**
 * Opens a modal and fills it with input fields specified by the jserver response object
 *
 * @param {object} obj - the response object from jserver
 */
function openModal(response) {
  openModals++;

  const modalId = `modal-overlay-${openModals}`;

  $("#modal-container").append(`<div id="${modalId}" class="modal-overlay" data-server-target="${response.modal.requestUrl}"></div>`);

  $(`#${modalId}`).append(response.contentHtml);

  $("#modal-container").removeClass(CLASS_HIDDEN);
  $(`#${modalId}`).removeClass(CLASS_HIDDEN);
  $(`#${modalId}`).css("z-index", 10000 + 100 * openModals);
  $(`#${modalId}`)
    .last()
    .find(':input:visible')
    .first()
    .focus();

  hideGlobalLoader();

  toggleBodyScroll();
}

function radioClick(event) {
  event.stopPropagation();
  $(event.currentTarget).find(".slider").click();
}

/**
 * Hides the modal overlay and clears its remains
 */
function closeModal() {
  if (openModals <= 0) {
    return;
  }

  if ($(`#modal-overlay-${openModals}`).attr("data-lock-set") === "1") {
    $(`#modal-overlay-${openModals}`).attr("data-lock-set", "0");
    releaseLock();
  }

  $(`#modal-overlay-${openModals}`).remove();
  openModals--;

  toggleBodyScroll();
}
// === < modal =============================================================================================================================
// === > form ==============================================================================================================================
function resetForm(formId) {
  $(`#${formId}`)
    .find('input, textarea, select')
    .filter(':enabled')
    .each(function () {
      const $el = $(this);

      if ($el.is(':checkbox, :radio')) {
        $el.prop('checked', false);
      } else {
        $el.val('');
      }
    });
}
/**
 * Extracts values from fields specified by an identifier as an object to be send back to the jserver
 *
 * @param {string} target - id of the context to be extracted from
 *
 * @returns {FormData} containing the extracted values
 */
function extractValuesToSubmit(target) {
  showGlobalLoader("extract form fields");
  const formData = new FormData();

  formData.append("__form_id", target);

  changeGlobalLoaderText(`extract: ${target}`);

  for (const elem of $(`#${target}`).find(`[data-submit-id="${target}"]`)) {
    if ($(elem).attr("data-submit-as") === "") {
      // no submit id set so ignore input
    } else {
      const id = $(elem).attr("data-submit-as");

      switch ($(elem).attr("data-value-type")) {
        case "INPUT":
        case "SELECT":
        case "TEXTAREA":
        case "TEXTFIELD":
        case "DATE":
        case "PASSWORD":
        case "NUMBER":
        case "HIDDEN":
        case "TAG":
          formData.append(id, $(elem).val());
          break;

        case "SLIDER": {
          const label = $(elem).closest(".input-slider").find(".input-slider-label").eq(parseInt($(elem).val(), 10));

          formData.append(id, label.length > 0 ? label.text() : $(elem).val());
          break;
        }

        case "CURRENCY":
          break;

        case "DIV":
          formData.append(id, $(elem).html());
          break;

        case "CHECKBOX":
          if ($(elem).prop("checked")) {
            formData.append(id, "true");
          } else {
            formData.append(id, "false")
          }
          break;

        case "SWITCH":
          formData.append(id, $(elem).is(":checked"));
          break;

        case "RADIO":
          formData.append(id, $(elem).find('input[type="radio"]:checked').val());
          break;

        case "FILE":
          const fileList = $(elem)[0].files;
          files.set(id, Array.from(fileList));
          formData.append(id, JSON.stringify(Array.from(fileList).map(f => (
            { name: f.name, size: f.size, type: f.type }
          ))));
          break;

        default:
          clientsideError(`field value from [${$(elem).attr("data-submit-as")}] type [${$(elem).attr("data-value-type")}] couldn't be fetched`);
      }
    }
  }

  return formData;
}

/**
 * Shows the error marker of a text input while its value violates its pattern, hides it again once it matches.
 *
 * @param {HTMLInputElement} input - a text input with a pattern attribute
 */
function checkPattern(input) {
  const marker = document.getElementById(`input-icon-${input.dataset.submitId}-error-${input.dataset.submitAs}`);

  if (marker === null) {
    return;
  }

  if (input.validity.patternMismatch) {
    $(marker).find(".input-icon-text").text(Translations.translate("ui-input-pattern-mismatch", input.validationMessage, input.pattern));
    marker.classList.remove(CLASS_HIDDEN);
  } else {
    marker.classList.add(CLASS_HIDDEN);
  }
}

function updateButton(btnId) {
  var $btn = $('#' + btnId);

  $btn.removeClass(function (i, className) {
    return (className.match(/(^|\s)btn-\S+/g) || []).join(' ');
  });

  $btn.addClass('btn btn-warning');

  $btn.attr('data-pending-change', 'true');
}

function resetButton(btnId) {
  var $btn = $('#' + btnId);

  $btn.removeClass(function (i, className) {
    return (className.match(/(^|\s)btn-\S+/g) || []).join(' ');
  });

  $btn.addClass('btn btn-primary');
  $btn.removeAttr('data-pending-change');
}
// === < form ==============================================================================================================================
// === > file upload =======================================================================================================================
/**
 * Files selected in the forms that were submitted, by the id they are submitted as. Filled by extractValuesToSubmit() and consumed by
 * startFileUpload().
 */
const files = new Map();

/**
 * Id of the progress element for uploads: the element that has the .loadbar and .loader-text children, the same kind of id the server
 * sends in a ProgressResponse (see updateProgress()).
 */
const UPLOAD_PROGRESS_ID = "global-loader-overlay";

/**
 * Single handler that owns the loader text while uploads run. The part uploaders only send it a success message; it counts over all
 * running uploads and renders at most once per frame, however many parts finish.
 */
const UploadProgress = (function () {
  const jobs = new Set();
  let total = 0;
  let done = 0;
  let renderScheduled = false;
  let warned = false;

  function schedule() {
    if (renderScheduled) {
      return;
    }

    renderScheduled = true;

    const later = typeof requestAnimationFrame === "function" ? requestAnimationFrame : (fn) => setTimeout(fn, 16);

    later(render);
  }

  function render() {
    renderScheduled = false;

    if (jobs.size === 0) {
      return;
    }

    if (!warned && !$(`#${UPLOAD_PROGRESS_ID}`).find(".loadbar").length) {
      warned = true;

      LOGGER.warn("upload progress element has no .loadbar, progress will not be visible.", { elementId: UPLOAD_PROGRESS_ID });
    }

    // same shape as the server's ProgressResponse, so uploads and server-side progress take the same path
    handleResponse({
      action: "PROGRESS",
      elementId: UPLOAD_PROGRESS_ID,
      text: `uploaded parts ${done}/${total}`,
      progress: total === 0 ? 100 : Math.floor((done / total) * 100),
      messages: [],
      calls: [],
    });
  }

  return {
    /** an upload starts; its parts count towards the shared total */
    add(job) {
      jobs.add(job);
      total += job.totalParts;

      schedule();
    },

    /** success message of the part uploader */
    partUploaded(job) {
      if (!jobs.has(job)) {
        return;
      }

      job.doneParts++;
      done++;

      schedule();
    },

    /** an upload ended; a failed one leaves the count, a finished one stays in it so the bar never jumps back */
    remove(job) {
      if (!jobs.delete(job)) {
        return;
      }

      if (job.failed) {
        total -= job.totalParts;
        done -= job.doneParts;
      }

      if (jobs.size === 0) {
        total = 0;
        done = 0;
      }
    },

    idle() {
      return jobs.size === 0;
    },
  };
})();

/**
 * Decides which part is uploaded next, over all running uploads of the page. The server's limit for parallel uploads is per site, so it is
 * shared: the strictest maxParallelUploadsGlobal of the running uploads is the cap, and every file may use up to its own
 * maxParallelUploadsPerFile. Slots are handed out round robin, so the maximum is reached and no upload starves another.
 */
const UploadScheduler = (function () {
  const jobs = new Set();
  let inFlight = 0;

  function cap() {
    let min = Infinity;

    for (const job of jobs) {
      min = Math.min(min, job.policy.maxParallelUploadsGlobal);
    }

    return min;
  }

  function pump() {
    const max = cap();
    let started = true;

    while (started) {
      started = false;

      for (const job of jobs) {
        if (job.failed) {
          continue;
        }

        for (const entry of job.entries) {
          if (inFlight >= max) {
            return;
          }

          if (entry.nextPart <= entry.totalParts && entry.inFlight < job.policy.maxParallelUploadsPerFile) {
            start(job, entry, entry.nextPart++);
            started = true;
          }
        }
      }
    }
  }

  function start(job, entry, partNumber) {
    inFlight++;
    entry.inFlight++;

    _uploadPart(job, entry, partNumber).then(
      () => {
        inFlight--;
        entry.inFlight--;
        entry.done++;

        UploadProgress.partUploaded(job);
        _uploadMaybeFinish(job, entry);
        pump();
      },
      (error) => {
        inFlight--;
        entry.inFlight--;

        _uploadFailJob(job, error);
        pump();
      }
    );
  }

  return {
    add(job) {
      jobs.add(job);
      pump();
    },

    remove(job) {
      jobs.delete(job);
      pump();
    },
  };
})();

/**
 * Uploads the files of a submitted form in parts, as instructed by the server. The server controls part length, parallelism and retries
 * through the policy. A failed part is retried by the policy; if it still fails, the whole upload is aborted on the server. The page is
 * blocked, several uploads may run at the same time and share the parallelism limit.
 *
 * Endpoints below response.uploadUrl:
 *   PUT    /push?batchUuid&fileUuid&partNumber   body: the bytes of the part, numbered from 1
 *   POST   /finish?batchUuid&fileUuid            after the last part of a file
 *   POST   /complete?batchUuid                   after all files, answers with the final response
 *   DELETE /abort?batchUuid                      on failure
 *
 * @param {object} response - StartFileUploadResponse
 *
 * @returns {Promise} settles when the upload is complete or aborted; never rejects
 */
function startFileUpload(response) {
  const job = {
    batchUuid: response.batchUuid,
    base: response.uploadUrl.replace(/\/+$/, ""),
    policy: response.uploadPolicy,
    controller: new AbortController(),
    fieldIds: [...new Set(response.fieldDescribtors.map((descriptor) => descriptor.fieldId))],
    entries: [],
    totalParts: 0,
    doneParts: 0,
    finishedFiles: 0,
    failed: false,
    error: null,
    resolve: null,
    reject: null,
  };

  return Promise.resolve()
    .then(() => {
      job.entries = _uploadBindFiles(response.fieldDescribtors, job.policy.partLength);
      job.totalParts = job.entries.reduce((sum, entry) => sum + entry.totalParts, 0);

      job.fieldIds.forEach((id) => files.delete(id));
    })
    .then(
      () =>
        new Promise((resolve, reject) => {
          job.resolve = resolve;
          job.reject = reject;

          UploadProgress.add(job);
          UploadScheduler.add(job);

          // files without content have no parts that could trigger the finish
          job.entries.forEach((entry) => _uploadMaybeFinish(job, entry));

          // no files at all: nothing triggers the finish, the upload is complete as it starts
          if (job.entries.length === 0) {
            job.resolve();
          }
        })
    )
    .then(() => _uploadComplete(job))
    .then(
      () => _uploadFinalize(job),
      (error) => _uploadAbort(job, error)
    );
}

/**
 * Matches the files announced by the server with the files selected by the user. The descriptors come back in the order they were sent,
 * but the match is checked by name and size, so a changed selection cannot upload the wrong file.
 */
function _uploadBindFiles(descriptors, partLength) {
  const used = new Set();

  return descriptors.map((descriptor) => {
    const candidates = files.get(descriptor.fieldId) || [];
    const index = candidates.findIndex(
      (file, i) => !used.has(`${descriptor.fieldId}:${i}`) && file.name === descriptor.filename && file.size === descriptor.expectedLength
    );

    if (index < 0) {
      throw new Error(`selected file does not match the announced one: ${descriptor.filename}`);
    }

    used.add(`${descriptor.fieldId}:${index}`);

    const file = candidates[index];

    return {
      uuid: descriptor.uuid,
      file: file,
      totalParts: Math.ceil(file.size / partLength),
      nextPart: 1,
      inFlight: 0,
      done: 0,
      finishing: false,
    };
  });
}

function _uploadPart(job, entry, partNumber) {
  const partLength = job.policy.partLength;
  const start = (partNumber - 1) * partLength;
  const blob = entry.file.slice(start, Math.min(start + partLength, entry.file.size));
  const url = _uploadUrl(job, "push", { fileUuid: entry.uuid, partNumber: partNumber });

  return _uploadRetry(job, () => _uploadFetch("PUT", url, blob, job.controller.signal));
}

/**
 * Closes a file on the server as soon as all its parts are in, and resolves the job after the last file.
 */
function _uploadMaybeFinish(job, entry) {
  if (job.failed || entry.finishing || entry.done < entry.totalParts) {
    return;
  }

  entry.finishing = true;

  _uploadRetry(job, () => _uploadFetch("POST", _uploadUrl(job, "finish", { fileUuid: entry.uuid }), undefined, job.controller.signal)).then(
    () => {
      job.finishedFiles++;

      if (job.finishedFiles === job.entries.length) {
        job.resolve();
      }
    },
    (error) => _uploadFailJob(job, error)
  );
}

/**
 * Not retried: after a lost answer the server has either completed the upload or refuses a second try.
 */
function _uploadComplete(job) {
  return _uploadFetch("POST", _uploadUrl(job, "complete", {}))
    .then((response) => response.json())
    .then((json) => handleResponse(json));
}

function _uploadFailJob(job, error) {
  if (job.failed) {
    return;
  }

  job.failed = true;
  job.error = error;
  job.controller.abort();
  job.reject(error);
}

function _uploadFinalize(job) {
  UploadScheduler.remove(job);
  UploadProgress.remove(job);
}

/**
 * Stops everything of this upload that is still running, discards it on the server and tells the user. Other uploads go on.
 */
function _uploadAbort(job, error) {
  const message = (error && error.message) || String(error);

  LOGGER.error("file upload failed.", { batchUuid: job.batchUuid, message });

  job.failed = true;
  job.error = error;
  job.controller.abort();
  job.fieldIds.forEach((id) => files.delete(id));

  return _request(_uploadUrl(job, "abort", {}), { method: "DELETE" })
    .catch((abortError) => LOGGER.warn("abort request failed, the server cleans up on expiry.", { batchUuid: job.batchUuid, abortError }))
    .then(() => {
      _uploadFinalize(job);

      if (UploadProgress.idle()) {
        hideGlobalLoader();
      }

      clientsideError(`file upload failed: ${message}`);
    });
}

/**
 * Runs an operation again after a delay if it failed in a way that can heal: network error, 5xx, 408 or 429.
 */
function _uploadRetry(job, operation, attempt = 0) {
  if (job.failed) {
    return Promise.reject(job.error);
  }

  return operation()
    .catch((error) => {
      if (!error.retryable || attempt >= job.policy.maxRetries) {
        throw error;
      }

      const policy = job.policy;
      const delay = Math.min(policy.retryMaxDelayMillis, policy.retryBaseDelayMillis * Math.pow(2, attempt)) * (0.5 + Math.random() * 0.5);

      return new Promise((resolve) => setTimeout(resolve, delay)).then(() => _uploadRetry(job, operation, attempt + 1));
    });
}

function _uploadFetch(method, url, body, signal) {
  return _request(url, {
    method: method,
    body: body,
    signal: signal,
    headers: body ? { "Content-Type": "application/octet-stream" } : undefined,
  }).then(
    (response) => {
      if (!response.ok) {
        const error = new Error(`HTTP error! Status: ${response.status}`);

        error.retryable = response.status >= 500 || response.status === 408 || response.status === 429;

        throw error;
      }

      return response;
    },
    (error) => {
      error.retryable = error.name !== "AbortError";

      throw error;
    }
  );
}

function _uploadUrl(job, path, params) {
  return `${job.base}/${path}?${new URLSearchParams({ batchUuid: job.batchUuid, ...params })}`;
}
// === < file upload =======================================================================================================================
// === > slider ============================================================================================================================
// the browser rounds the value to the slider's step
function setSliderValue(slider, value) {
  slider.value = value;
  slider.dispatchEvent(new Event('input', { bubbles: true }));
  slider.dispatchEvent(new Event('change', { bubbles: true }));
}

function updateSlider(slider) {
  const min = parseFloat(slider.min);
  const max = parseFloat(slider.max);
  const ratio = max > min ? Math.min(Math.max((parseFloat(slider.value) - min) / (max - min), 0), 1) : 0;
  const field = slider.closest(".input-slider");

  field.style.setProperty("--ci-slider-ratio", ratio);

  const labels = field.querySelectorAll(".input-slider-label");

  if (labels.length > 0) {
    const index = Math.round(parseFloat(slider.value));

    labels.forEach((label, i) => label.classList.toggle("input-slider-label-active", i === index));
    slider.setAttribute("aria-valuetext", labels[index].textContent);
  } else {
    field.querySelector(".input-slider-value").textContent = slider.value;
  }
}
// === < slider ============================================================================================================================
// === > tags ==============================================================================================================================
const tagifyInstances = new Map();
const tagifyWhitelists = new Map();

function registerTagInput(settings) {
  const inputElm = document.querySelector(`#${settings.id}`);
  const initialValues = inputElm.value.trim().split(/\s*,\s*/);

  const tagify = new Tagify(inputElm, {
    enforceWhitelist: settings.enforceWhitelist,
    whitelist: initialValues,
    inputId: settings.id,
    searchTagsUrl: settings.searchTagsUrl,
    maxTags: settings.maxTags,
    dropdown: {
      classname: "color-blue",
      enabled: 1,
      maxItems: 10
    },
    templates: {
      dropdownItemNoMatch: () =>
        `<div class="tagify__dropdown__item no-match" tabindex="0" role="option">❌</div>`
    }

  });

  tagify
    .on("add", (e) => onAddTag(e, tagify))
    .on("input", (e) => onInput(e, tagify));

  tagifyInstances.set(settings.id, tagify);

  if (settings.fetchWhitelistUrl !== "") {
    POST(settings.fetchWhitelistUrl, { id: settings.id });
  }
}

function onAddTag(e, tagify) {
  tagify.off("add", onAddTag);
}

function onInput(e, tagify) {
  if (tagify.settings.searchTagsUrl !== "") {
    tagify.whitelist = tagifyWhitelists.get(tagify.settings.inputId);
    tagify.loading(true);

    POST(tagify.settings.searchTagsUrl, {
      id: tagify.settings.inputId,
      input: e.detail.value,
    });
  }
}
// === < tags ==============================================================================================================================
// === > websocket =========================================================================================================================
const WebSocketManager = (function () {
  const connections = new Map();

  function createConnection({
    name,
    endpoint,
    headers = {},
    subscriptions = [],
    reconnectInterval = 5000
  }) {
    if (!name) throw new Error('WS Connection name is required');
    if (connections.has(name)) throw new Error(`WS Connection ${name} already exists`);
    if (!endpoint) throw new Error('WS Connection endpoint is required');

    const client = new StompJs.Client({
      brokerURL: endpoint,
      reconnectDelay: reconnectInterval,
      connectHeaders: headers,
      // evaluated on every (re)connect so that rotated tokens are used; a STOMP session is authenticated once by the CONNECT frame
      beforeConnect: (c) => {
        c.connectHeaders = { ..._csrfHeaders("CONNECT"), ...requestConfig.headers(), ...headers };
      },
      debug: () => { },

      onConnect: frame => {
        LOGGER.debug(`Connected [${name}]`);

        connections.set(name, client);

        for (const { destination } of subscriptions) {
          client.subscribe(destination, msg => handleResponse(JSON.parse(msg.body)));
        }

        if (FunctionRegistry.hasFunction('onWSConnect')) {
          FunctionRegistry.call('onWSConnect', name);
        }
      },

      onStompError: error => {
        LOGGER.error(`Broker error [${name}]`, error);

        if (FunctionRegistry.hasFunction('onWSError')) {
          FunctionRegistry.call('onWSError', name, error);
        }
      },

      onWebSocketError: error => {
        LOGGER.error(`WebSocket error [${name}]`, error);
      }
    });

    client.activate();

    return client;
  }

  function send(name, destination, body) {
    const c = connections.get(name);

    if (!c) throw new Error(`No connection named ${name}`);

    c.publish({ destination, body: JSON.stringify(body) });
  }

  function disconnect(name) {
    const c = connections.get(name);

    if (c) {
      c.deactivate();
      connections.delete(name);

      LOGGER.debug(`Disconnected [${name}]`);
    }
  }

  return { createConnection, send, disconnect };
})();

function initWebSocketsFromConfig(config) {
  if (!config || !config.websockets || !Array.isArray(config.websockets)) {
    throw new Error('Invalid config structure for WS creation');
  }

  for (const wsDef of config.websockets) {
    const { name, url, destinations, reconnectInterval } = wsDef;

    if (!name || !url || !Array.isArray(destinations)) {
      LOGGER.warn('Invalid websocket entry, skipping:', wsDef);

      continue;
    }

    WebSocketManager.createConnection({
      name: name,
      endpoint: url,
      subscriptions: destinations.map(d => ({ destination: d })),
      reconnectInterval: reconnectInterval
    });
  }
}
