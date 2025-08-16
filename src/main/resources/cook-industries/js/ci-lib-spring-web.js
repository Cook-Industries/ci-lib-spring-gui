export {
  init,
  GET,
  POST,
  showGlobalLoader,
  hideGlobalLoader,
  requestModal,
  closeModal,
  submitFromModal,
  sendFromForm,
  dismissErrors,
  redirect,
  reload,
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

const version = "3.0.0";

const CLASS_HIDDEN = "hidden";

function init() {
  window.CILIB = this;
}

$(document).ready(function () {
  console.log("ci-lib-js: ", version);

  $(document).on("click", ".modal-overlay", function (event) {
    event.stopPropagation();

    if (event.target === this) {
      if ($(`#modal-overlay-${openModals} .modal-body`).attr("data-close-on-overlay") === "true") {
        closeModal();
      }
    }
  });

  $(document).on('input change', '[data-connected-btn] input, [data-connected-btn] select, [data-connected-btn] textarea', function () {
    var $container = $(this).closest('[data-connected-btn]');
    var btnId = $container.data('connected-btn');

    updateButton(btnId);
  });

  $(document).on('mouseenter mouseleave', '.tooltip-container', function (e) {
    $(this).find('.tooltip-text').toggleClass('tooltip-visible', e.type === 'mouseenter');
  });


  $(document).on("keydown", function (e) {
    if (e.key === "Escape") {
      CILIB.closeModal();
    }
  });

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

  FunctionRegistry._registerInternal("noop", () => {
    console.log(
      "a call to the NoOp function occured. This is not normal and should be investigated."
    );
  });

  FunctionRegistry._registerInternal("registerTagInput", (id, fetchUrl, searchUrl, enforceWhitelist) => {
    registerTagInput(id, fetchUrl, searchUrl, enforceWhitelist);
  });

  hideGlobalLoader();
});

// === > global functions ==========================================================================
/**
 *
 * @param {String} endpointUrl
 *
 * @returns a Promise
 */
function GET(endpointUrl) {
  if (endpointUrl === undefined || endpointUrl === "") {
    throw new Error("GET URL cannot be undefined/empty");
  }

  return new Promise((resolve, reject) => {
    fetch(endpointUrl, {
      method: "GET",
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
      .then((responseData) => {
        resolve(responseData);
      })
      .catch((error) => {
        hideGlobalLoader();
        clientsideError(error.message);
        reject(error);
      });
  });
}

/**
 *
 * @param {String} endpointUrl
 * @param {*} dataToSend
 *
 * @returns a Promise
 */
function POST(endpointUrl, dataToSend = {}) {
  if (endpointUrl === undefined || endpointUrl === "") {
    throw new Error("POST URL cannot be undefined/empty");
  }

  return new Promise((resolve, reject) => {
    fetch(endpointUrl, {
      method: "POST",
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
      .then((responseData) => {
        resolve(responseData);
      })
      .catch((error) => {
        hideGlobalLoader();
        clientsideError(error.message);
        reject(error);
      });
  });
}

/**
 * Explicit function for form submit so that we can send data and files
 *
 * @param {String} endpointUrl
 * @param {FormData} formData
 *
 * @returns a Promise
 */
function POSTFormData(endpointUrl, formData = {}) {
  if (endpointUrl === undefined || endpointUrl === "") {
    throw new Error("POST URL cannot be undefined/empty");
  }

  return new Promise((resolve, reject) => {
    fetch(endpointUrl, {
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
      .then((responseData) => {
        resolve(responseData);
      })
      .catch((error) => {
        hideGlobalLoader();
        clientsideError(error.message);
        reject(error);
      });
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
    case "NOTIFICATION":
      break;

    case "MODAL":
      openModal(response.contentHtml);
      break;

    case "CONTENT":
      fillContent(response);
      break;

    case "REMOVE":
      removeContent(response);
      break;

    case "LOADING_PROGRESS":
      changeGlobalLoaderText(response.text);
      break;

    case "COMPOUND":
      for (let s in response.results.list) {
        let data = response.statements[s];

        handleResponse(data);
      }
      break;

    case "FETCH_TAGS":
      tagifyWhitelists.set(response.inputId, response.results);
      break;

    case "FETCH_TAGS_RESULT":
      let tagInputId = response.inputId;
      let inputValue = response.originalInputValue;
      let result = response.results;
      let tagify = tagifyInstances.get(tagInputId);

      tagify.settings.whitelist = result.concat(tagify.value || []);
      tagify.loading(false).dropdown.show(inputValue);
      break;

    case "REDIRECT":
      redirect(response.url);
      break;
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
 *
 * @returns {undefined}
 */
function handleMessages(messages, btnName = "ok") {
  hideGlobalLoader();

  $(".error-marker").addClass(CLASS_HIDDEN);

  for (let m in messages) {
    let msg = messages[m];

    switch (msg.target) {
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
        console.log(`"unrecognized message type [${msg.target}]`);
    }
  }

  if (errorOverlayVisible) {
    $("#btn-error-text").html(btnName);
    $("#error-overlay").removeClass(CLASS_HIDDEN);
    $("#error-overlay").find(":button").focus();
    toggleBodyScroll();
  }
}

function getMsgClass(type) {
  let c;

  switch (type) {
    case "SUCCESS":
      c = "success";
      break;

    case "ERROR":
      c = "error";
      break;

    case "WARNING":
      c = "warning";
      break;

    default:
      c = "";
  }

  return c;
}

function handleModalMsg(msg) {
  let msgClass = getMsgClass(msg.type);
  $("#error-holder").append(`<div class="message-container alert ${msgClass}"><i class="material-icons">${msgClass}</i><div>${msg.msg}</div></div>`);
}

function handlePopupMsg(msg) {
  $("#popup-holder").append(`<div class="pop-up pop-up-fade-out"><div class="${getMsgClass(msg.type)}">${msg.msg}</div></div>`);
}

function handleMarkerMsg(msg) {
  $(`#error-marker-${msg.formId}-${msg.transferId}-${msg.markerCategory}-${msg.markerType}`).removeClass(CLASS_HIDDEN);
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
 * Function to submit directly from a modal if no other function is specified
 */
function submitFromModal() {
  showGlobalLoader("collect data");

  const modal = $(`#modal-overlay-${openModals}`);
  const formData = extractValuesToSubmit(modal.attr("data-extraction-id"));
  const url = modal.attr("data-server-target");

  resetMarker();
  changeGlobalLoaderText("send data");
  POSTFormData(url, formData);
}

/**
 * Opens a modal and fills it with input fields specified by the jserver response object
 *
 * @param {object} obj - the response object from jserver
 *
 * @returns {undefined}
 */
function openModal(modalHtml) {
  openModals++;

  $("#modal-container").append(`<div id="modal-overlay-${openModals}" class="modal-overlay"></div>`);

  $(`#modal-overlay-${openModals}`).append(modalHtml);

  $("#modal-container").removeClass(CLASS_HIDDEN);
  $(`#modal-overlay-${openModals}`).removeClass(CLASS_HIDDEN);
  $('#modal-container .modal-inlay').last().find('input:visible').first().focus();

  console.log("input:", $('#modal-container .modal-inlay').last().find('input:visible').first())

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
 *
 * @returns {undefined}
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
function fillContent(content) {
  const elementId = `#${content.elementId}`;
  if ($(content).length) {
    if (content.replace) {
      $(elementId).html("");
    }

    $(elementId).append(content.contentHtml);
  }
}

function removeContent(obj) {
  $(`#${obj.context}`).remove();
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

  for (const elem of $(`#${target} [data-submit-id="${target}"]`)) {
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

  // Remove any btn-* style
  $btn.removeClass(function (i, className) {
    return (className.match(/(^|\s)btn-\S+/g) || []).join(' ');
  });

  // Add your "changed" style
  $btn.addClass('btn btn-warning');

  // Mark button as having unsaved changes
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

function registerTagInput(id, fetchTagsUrl, searchTagsUrl, enforceWhitelist) {
  const inputElm = document.querySelector(`#${id}`);
  const initialValues = inputElm.value.trim().split(/\s*,\s*/);

  const tagify = new Tagify(inputElm, {
    enforceWhitelist: enforceWhitelist,
    whitelist: initialValues,
    inputId: id,
    searchTagsUrl: searchTagsUrl,
    dropdown: {
      classname: "color-blue",
      enabled: 0,
      maxItems: 10
    }
  });

  tagify
    .on("add", (e) => onAddTag(e, tagify))
    .on("input", (e) => onInput(e, tagify));

  tagifyInstances.set(id, tagify);
  tagifyWhitelists.set(id, initialValues);

  if (fetchTagsUrl !== "") {
    POST(fetchTagsUrl, { id: id });
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
