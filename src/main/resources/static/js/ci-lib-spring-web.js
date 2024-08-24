/*
 *  Copyright(c) 2019 sebastian koch/CI. All rights reserved.
 *  mailto: koch.sebastian@cook-industries.de
 *
 *  Created on : 29.07.2019
 *  Author     : <a href="mailto:koch.sebastian@cook-industries.de">sebastian koch</a>
 */
$(document).ready(function ()
{
    console.log("ci-lib-dev-build");

    /**
     * OnClick function
     */
    $("body").on("click", ".modal-overlay", function (event)
    {
        event.stopPropagation();
        if (event.target === this) {
            if ($(sprintf("#modal-overlay-%d", openModals)).attr("data-close-on-overlay") === "true") {
                closeModal();
            }
        }
    });

    hideGlobalLoader();
});

// === > global functions ==========================================================================
function GET(endpointUrl, dataToSend = {})
{
    return new Promise((resolve, reject) =>
    {
        fetch(endpointUrl,
        {
            method: 'GET'
        })
            .then(response =>
            {
                if (!response.ok) {
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }
                return response.json();
            })
            .then(json =>
            {
                handleResponse(json);
                return json;
            })
            .then(responseData =>
            {
                resolve(responseData);
            })
            .catch(error =>
            {
                reject(error);
            });
    });
}

function POST(endpointUrl, dataToSend = {})
{
    if(endpointUrl === undefined || endpointUrl === "")
    {
        throw new Error("POST URL cannot be undefined/empty");
    }

    return new Promise((resolve, reject) =>
    {
        fetch(endpointUrl, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(dataToSend),
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }
                return response.json();
            })
            .then(json => {
                handleResponse(json);
                return json;
            })
            .then(responseData => {
                resolve(responseData);
            })
            .catch(error => {
                reject(error);
            });
    });
}

/**
 * 
 * @param {*} id of form to grab values from
 * @param {*} url to call on backend
 */
function sendFromForm(id, url)
{
    showGlobalLoader("send form data");
    let data = extractValuesToSubmit(id);
    POST(url, data);
}

function handleResponse(response)
{
    triggerResponse(response.messages);

    switch (response.action)
    {
        case "NOTIFICATION":
            break;

        case "MODAL":
            openModal(response.modal);
            break;

        case "MODAL_CLOSE":
            closeModal();
            break;

        case "TAB_CLOSE":
            closeTab();
            break;

        case "GLOBAL_LOADER_SHOW":
            showGlobalLoader();
            break;

        case "GLOBAL_LOADER_HIDE":
            hideGlobalLoader();
            break;

        case "CONTENT":
            fillContent(response);
            break;

        case "REPLACE":
            replaceContent(msg);
            break;

        case "REMOVE":
            removeContent(msg);
            break;

        case "COMPOUND":
            for (let s in msg.results.list) {
                let data = msg.statements[s];

                handleResponse(data);
            }
            break;

        case "audio_control":
            audioControl(msg);
            break;

        case "LOADING_PROGRESS":
            changeGlobalLoaderText(msg.text);
            break;

        default:
            clientsideError(sprintf("could not recognice response action '%s'", response.action));
    }

    call(response.calls);
}

function call(calls)
{
    for (let func in calls)
    {
        let call = calls[func];
        let fn = window[call.functionName];

        if (typeof fn === "function") {
            fn();
        } else {
            clientsideError(sprintf("call to non existent function '%s'", call));
        }
    }
}
// === < global functions ==========================================================================
// === > global loader =============================================================================
/**
 * Shows the global loader overlay
 *
 * @param {type} text - text to be shown in loader if neccessary
 * @returns {undefined}
 */
function showGlobalLoader(text = "")
{
    if (text !== undefined)
    {
        $("#global-loader-text").html(text);
    }

    $("#global-loader-overlay").removeClass("hidden");
}

/**
 * Hides the global loader overlay
 *
 * @returns {undefined}
 */
function hideGlobalLoader()
{
    $("#global-loader-text").html("loading...");
    $("#global-loader-overlay").addClass("hidden");
}

function changeGlobalLoaderText(text)
{
    $("#global-loader-text").html(text);
}
// === < global loader =============================================================================
// === > notifications =============================================================================
/**
 * @param {type} messages
 * @param {type} btnName - label for the "ok" btn
 * @returns {undefined}
 */
function triggerResponse(messages, btnName = "")
{
    hideGlobalLoader();

    let needOverlay = false;

    $(".error-icon").addClass("hidden");

    for (let m in messages) {
        let msg = messages[m];

        switch (msg.target)
        {
            case "MODAL":
                needOverlay = true;
                handleModalMsg(msg);
                break;

            case "POP_UP":
                handlePopupMsg(msg);
                break;

            case "HIGHLIGHT":
                handleHighlightMsg(msg);
                break;

            default:
                console.log("unrecognized message type");
        }
    }

    if (needOverlay)
    {
        $("#btn-error-text").html(btnName);
        $("#error-overlay").removeClass("hidden");
        $("#error-overlay").find(':button').focus();
    }
}

function getMsgClass(type)
{
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

function handleModalMsg(msg)
{
    let c = getMsgClass(msg.type);
    $("#error-holder").append(sprintf('<div class="message-container %s"><i class="material-icons">%s</i><div>%s</div></div>', c, c, msg.msg));
}

function handlePopupMsg(msg)
{
    $("#popup-holder").append(sprintf('<div class="pop-up pop-up-fade-out"><div class="%s">%s</div></div>', getMsgClass(msg.type), msg.msg));
}

function handleHighlightMsg(msg)
{

}

/**
 *
 * @param {String} type - type of error
 * @param {String} msg  - message to be shown -> musst be set
 * @param {String} func - function name (without '()') to be called on button click
 *                          -> can be set, default 'dismissErrors'
 * @param {String} btn  - text on button -> can be set, default: 'dismiss'
 *
 * @return {undefined}
 */
function clientsideError(msg, btn = "dismiss")
{
    triggerResponse(msg, btn);
}

/**
 * Clears the error objects and closes the error popup
 *
 * @returns {undefined}
 */
function dismissErrors()
{
    $("#error-overlay").addClass("hidden");
    $("#error-holder").html("");
}
// === < notifications =============================================================================
// === > modal =====================================================================================
let openModals = 0;

function getModal(url)
{
    showGlobalLoader();

    GET(url);
}


/**
 * Function to submit directly from a modal if no other function is specified
 */
function submitFromModal()
{
    showGlobalLoader();

    let modal = $(sprintf("#modal-overlay-%d", openModals));
    let data = extractValuesToSubmit(modal.attr("data-extraction-id"));
    let url = modal.attr("data-server-target");

    POST(url, data);
}



function submitLeftBtnPress()
{
    let modal = $(sprintf("#modal-overlay-%d", openModals));
    let arr = {
        btn_left: true,
        i_uuid: modal.attr("data-extraction-id")
    };

    POST(modal.attr("data-request-url"), arr);
}

function submitMiddleBtnPress()
{
    let modal = $(sprintf("#modal-overlay-%d", openModals));
    let arr = {
        btn_middle: true,
        i_uuid: modal.attr("data-extraction-id")
    };

    POST(modal.attr("data-request-url"), arr);
}

function submitRightBtnPress()
{
    let modal = $(sprintf("#modal-overlay-%d", openModals));
    let arr = {
        btn_right: true,
        i_uuid: modal.attr("data-extraction-id")
    };

    POST(modal.attr("data-request-url"), arr);
}

function submitAbort()
{
    let modal = $(sprintf("#modal-overlay-%d", openModals));
    let arr = {
        aborted: true,
        i_uuid: modal.attr("data-extraction-id")
    };

    POST(modal.attr("data-request-url"), arr);
}

/**
 * Opens a modal and fills it with input fields specified by the jserver response object
 *
 * @param {object} obj - the response object from jserver
 *
 * @returns {undefined}
 */
function openModal(modal)
{
    openModals++;

    // -> data attributes
    let data = "";
    for (let att in modal.dataAttributes) {
        let a = modal.dataAttributes[att];
        data += sprintf(" data-%s=\"%s\"", att, a);
    }
    // <-

    $("#modal-container").append(sprintf('<div id="modal-overlay-%d" class="modal-overlay"%s><div id="modal-inlay-%d"class="modal-inlay"><div id="modal-name-%d" class="modal-name text-bold">Modal</div><div id="modal-input-%d" class="modal-input flex-vertical"></div><div id="modal-buttons-%d" class="modal-buttons flex-horizontal flex-center"></div></div></div>', openModals, data, openModals, openModals, openModals, openModals, openModals));

    $(sprintf("#modal-name-%d", openModals)).html(modal.name);
    $(sprintf("#modal-overlay-%d", openModals)).attr("data-extraction-id", modal.identifier);
    $(sprintf("#modal-overlay-%d", openModals)).attr("data-server-target", modal.requestUrl);
    $(sprintf("#modal-overlay-%d", openModals)).attr("data-close-on-overlay", modal.closeOnOverlayClick);
    $(sprintf("#modal-overlay-%d", openModals)).css("z-index", openModals + 100);

    if (modal.hideBtn === true) {
        $(sprintf("#modal-buttons-%d", openModals)).addClass("hidden");
    }
    // <-
    $(sprintf("#modal-input-%d", openModals)).append(modal.contentHtml);

    $(sprintf("#modal-container")).removeClass("hidden")
    $(sprintf("#modal-overlay-%d", openModals)).removeClass("hidden");
    $(sprintf("#modal-inlay-%d :input", openModals)).first().focus();

    hideGlobalLoader();
}

function modalShowBtn()
{
    $(sprintf("#modal-overlay-%d", openModals)).attr("data-close-on-overlay", "0");
    $(sprintf("#modal-buttons-%d", openModals)).removeClass("hidden");
}

function radioClick(event)
{
    event.stopPropagation();
    $(event.currentTarget).find(".slider").click();
}

/**
 * Hides the modal overlay and clears its remains
 *
 * @returns {undefined}
 */
function closeModal() {
    if ($(sprintf("#modal-overlay-%d", openModals)).attr("data-lock-set") === "1")
    {
        $(sprintf("#modal-overlay-%d", openModals)).attr("data-lock-set", "0");
        releaseLock();
    }

    $(sprintf("#modal-overlay-%d", openModals)).remove();
    openModals--;
}
// === < modal =====================================================================================
// === > site ======================================================================================
function fillContent(content)
{
    let elementId = sprintf("#%s", content.elementId);
    if ($(content).length)
    {
        if (content.replace)
        {
            $(elementId).html("");
        }

        $(elementId).append(content.contentHtml);
    }
}

function replaceContent(obj)
{
    let contentIdentifier = obj.context;

    $(sprintf("#%s", contentIdentifier)).replaceWith(write(obj.element));
}

function removeContent(obj)
{
    let contentIdentifier = obj.context;

    $(sprintf("#%s", contentIdentifier)).remove();
}

/**
 * Extracts values from fields specified by an identifier as an object to be send back to the jserver
 *
 * @param {string} target   - id of the context to be extracted from
 *
 * @returns {object} containing the extracted values and the standard message for the jserver
 */
function extractValuesToSubmit(target)
{
    let arg = {};

    $(sprintf('[data-submit-id="%s"]', target)).each(function ()
    {
        let arr = new Array();

        if ($(this).attr("data-submit-as") === "")
        {
            // no submit id set so ignore input
        } else {
            switch ($(this).attr("data-value-type")) {
                case "INPUT":
                case "SELECT":
                case "TEXTAREA":
                case "TEXTFIELD":
                case "DATE":
                case "PASSWORD":
                    arg[$(this).attr("data-submit-as")] = $(this).val();
                    break;
                case "DIV":
                    arg[$(this).attr("data-submit-as")] = $(this).html();
                    break;
                case "CHECKBOX":
                    $(this).find('input[type="checkbox"]:checked').each(function () {
                        arr.push($(this).val());
                    });
                    arg[$(this).attr("data-submit-as")] = arr;
                    break;
                case "SWITCH":
                    arg[$(this).attr("data-submit-as")] = $(this).is(':checked');
                    break;
                case "RADIO":
                    arg[$(this).attr("data-submit-as")] = $(this).find('input[type="radio"]:checked').val();
                    break;

                default:
                    clientsideError("ERR_READ_FAIL", sprintf("a field value couldn't be fetched (field: %s)", $(this).attr("data-submit-as")));
            }
        }
    });

    console.log(arg);
    return arg;
}
// === < site ======================================================================================