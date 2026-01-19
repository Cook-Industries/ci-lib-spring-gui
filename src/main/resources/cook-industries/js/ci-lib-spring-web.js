import { Client } from '/webjars/stomp__stompjs/esm6/index.js';

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
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 *
 * author: <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */

const version = "3.4.0";

const CLASS_HIDDEN = "hidden";

const StompJs = { Client };

function init() {
  window.CILIB = this;
}

$(document).ready(function () {
  console.log("ci-lib-js: ", version);

  // load properties if neccessary
  var propertiesUrl = $('#ui-properties-url').html().trim();
  if (propertiesUrl) {
    console.log("properties are requested from ", propertiesUrl);
    GET(propertiesUrl);
  } else {
    console.log("no properties fetch url present, skip.");
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
    }
  });

  // change color of button on form change
  $(document).on('input change', '[data-connected-btn] input, [data-connected-btn] select, [data-connected-btn] textarea', function () {
    var $container = $(this).closest('[data-connected-btn]');
    var btnId = $container.data('connected-btn');

    updateButton(btnId);
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
          console.error("BurgerItem method invalid.", { method });
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
    console.log("a call to the NoOp function occured. This is not normal and should be investigated.");
  });

  FunctionRegistry._registerInternal("registerTagInput", (settings) => {
    registerTagInput(settings);
  });

  hideGlobalLoader();
});

// === > global functions ==========================================================================
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

  return fetch(endpointUrl, {
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

  return fetch(endpointUrl, {
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
    throw new Error(`"${method} URL cannot be undefined/empty"`);
  }

  return fetch(endpointUrl, {
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
      for (let s in response.results.list) {
        let data = response.statements[s];

        handleResponse(data);
      }
      break;
    }

    case "FETCH_TAGS": {
      tagifyWhitelists.set(response.inputId, response.results);

      let tagify = tagifyInstances.get(response.inputId);
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
  console.log("properties received", properties)
}
// === < global functions ==========================================================================
// === > function register =========================================================================
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
        console.warn(`Function '${name}' not found.`);
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
// === < function register =========================================================================
// === > global loader =============================================================================
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

  $("#global-loader-text").html(text);
  $("#global-loader-overlay").removeClass(CLASS_HIDDEN);

  toggleBodyScroll();
}

/**
 * Hides the global loader overlay
 *
 * @returns {undefined}
 */
function hideGlobalLoader() {
  $("#global-loader-text").html("loading...");
  $("#global-loader-overlay").addClass(CLASS_HIDDEN);
  globalLoaderVisible = false;

  toggleBodyScroll();
}

function changeGlobalLoaderText(text) {
  $("#global-loader-text").html(text);
}
// === < global loader =============================================================================
// === > notifications =============================================================================
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
        console.error("unrecognized message target.", { target });
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
  $("#error-holder").append(`<div class="message-container alert ${msg.type.toLowerCase()}"><i class="bi ${msg.icon}"></i><div>${msg.msg}</div></div>`);
}

function handlePopupMsg(msg) {
  $("#popup-holder").append(`<div class="pop-up pop-up-fade-out"><div class="${msg.type.toLowerCase()}">${msg.msg}</div></div>`);
}

function handleMarkerMsg(msg) {
  $(`#${msg.uid}-text`).html(msg.text);
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
  console.error("client error.", { msg });

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
// === < notifications =============================================================================
// === > modal =====================================================================================
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

  $("#modal-container").append(`<div id="modal-overlay-${openModals}" class="modal-overlay" data-server-target="${response.modal.requestUrl}"></div>`);

  $(`#modal-overlay-${openModals}`).append(response.contentHtml);

  $("#modal-container").removeClass(CLASS_HIDDEN);
  $(`#modal-overlay-${openModals}`).removeClass(CLASS_HIDDEN);
  $(`#modal-overlay-${openModals}`).css("z-index", 1000 + 100 * openModals);
  $('#modal-container .modal-inlay').last().find('input:visible').first().focus();

  $("body").addClass("no-scroll");

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
  if ($(`#modal-overlay-${openModals}`).attr("data-lock-set") === "1") {
    $(`#modal-overlay-${openModals}`).attr("data-lock-set", "0");
    releaseLock();
  }

  $(`#modal-overlay-${openModals}`).remove();
  openModals--;

  toggleBodyScroll();
}
// === < modal =====================================================================================
// === > form ======================================================================================
function contentResponse(response) {
  const elementId = `#${response.elementId}`;

  if ($(response).length) {
    switch (response.handling) {
      case "APPEND":
        $(elementId).append(response.contentHtml);
        break;

      case "PREPEND":
        $(elementId).prepend(response.contentHtml);
        break;

      case "REPLACE":
        $(elementId).replaceWith(response.contentHtml);
        break;

      case "DELETE":
        $(elementId).remove();
        break;

      default:
        console.log(`"unrecognized content response type [${response.handling}]`);
    }
  }
}

function updateProgress(response) {
  const $elem = $(`#${response.elementId}`);
  const $bar = $elem.children(".loadbar");
  const $text = $elem.children(".loader-text");

  if (!$bar.length) return;

  $bar
    .data("progress", response.progress)
    .css("--v", response.progress);

  $text.html(response.text);
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
          const files = $(elem)[0].files;
          for (var x = 0; x < files.length; x++) {
            formData.append(id, files[x]);
          }
          break;

        default:
          clientsideError(`field value from [${$(elem).attr("data-submit-as")}] type [${$(elem).attr("data-value-type")}] couldn't be fetched`);
      }
    }
  }

  return formData;
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
// === < form ======================================================================================
// === > tags ======================================================================================
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
// === < tags ======================================================================================
function toggleBodyScroll() {
  if (errorOverlayVisible || globalLoaderVisible || openModals > 0) {
    $("body").addClass("no-scroll");
  } else {
    $("body").removeClass("no-scroll");
  }
}
// === > websocket =================================================================================
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
      debug: () => { },

      onConnect: frame => {
        console.log(`Connected [${name}]`);

        connections.set(name, client);

        for (const { destination } of subscriptions) {
          client.subscribe(destination, msg => handleResponse(JSON.parse(msg.body)));
        }

        if (FunctionRegistry.hasFunction('onWSConnect')) {
          FunctionRegistry.call('onWSConnect', name);
        }
      },

      onStompError: error => {
        console.error(`Broker error [${name}]`, error);

        if (FunctionRegistry.hasFunction('onWSError')) {
          FunctionRegistry.call('onWSError', name, error);
        }
      },

      onWebSocketError: error => {
        console.error(`WebSocket error [${name}]`, error);
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

      console.log(`Disconnected [${name}]`);
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
      console.warn('Invalid websocket entry, skipping:', wsDef);

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
