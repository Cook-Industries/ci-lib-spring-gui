export { 
    showGlobalLoader, 
    hideGlobalLoader, 
    getModal, 
    GET, 
    POST, 
    POSTFormData, 
    sendFromForm, 
    submitFromModal, 
    registerFunction, 
    closeModal, 
    dismissErrors
};

/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */

const version = "";
const customFunctionMap = new Map();

const CLASS_HIDDEN = "hidden";

$(document).ready(function ()
{
    console.log("ci-lib-dev-build");

    /**
     * OnClick function
     */
    $("body").on("click", ".modal-overlay", function (event) {
        event.stopPropagation();
        if (event.target === this) {
            if ($(sprintf("#modal-overlay-%d", openModals)).attr("data-close-on-overlay") === "true") {
                closeModal();
            }
        }
    });

    window.dismissErrors = () => { dismissErrors(); };
    window.closeModal = () => { closeModal()};
    window.removeListSelectionItem = (uuid) => { removeListSelectionItem(uuid); };
    window.addListSelectionItem = (selectUuid, holderUuid) =>  { addListSelectionItem(selectUuid, holderUuid)};

    hideGlobalLoader();
});

// === > global functions ==========================================================================
/**
 * 
 * @param {String} endpointUrl
 * 
 * @returns a Promise
 */
function GET(endpointUrl)
{
    return new Promise((resolve, reject) => {
        fetch(endpointUrl,
            {
                method: 'GET'
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
 * @param {String} endpointUrl 
 * @param {*} dataToSend 
 * 
 * @returns a Promise
 */
function POST(endpointUrl, dataToSend = {})
{
    if (endpointUrl === undefined || endpointUrl === "") {
        throw new Error("POST URL cannot be undefined/empty");
    }

    return new Promise((resolve, reject) => {
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
 * @param {String} endpointUrl 
 * @param {FormData} formData 
 * 
 * @returns a Promise
 */
function POSTFormData(endpointUrl, formData = {})
{
    if (endpointUrl === undefined || endpointUrl === "") {
        throw new Error("POST URL cannot be undefined/empty");
    }
    console.log(formData);

    return new Promise((resolve, reject) => {
        fetch(endpointUrl, {
            method: 'POST',
            body: formData
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
 * @param {String} id of form to grab values from
 * @param {String} url to call on backend
 */
function sendFromForm(id, url)
{
    showGlobalLoader("collect data");
    const formData = extractValuesToSubmit(id);
    changeGlobalLoaderText("send data");
    POSTFormData(url, formData);
}

function handleResponse(response)
{
    triggerResponse(response.messages);

    switch (response.action) {
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
            replaceContent(response);
            break;

        case "REMOVE":
            removeContent(response);
            break;

        case "COMPOUND":
            for (let s in response.results.list) {
                let data = response.statements[s];

                handleResponse(data);
            }
            break;

        case "audio_control":
            audioControl(response);
            break;

        case "LOADING_PROGRESS":
            changeGlobalLoaderText(response.text);
            break;

        default:
            tryCustomFunction(response);
    }

    call(response.calls);
}

function tryCustomFunction(response)
{
    const key = response.action;

    if (customFunctionMap.has(key)) {
        const func = customFunctionMap.get(key);

        return func(response);
    }
    else {
        clientsideError(sprintf("could not recognice response action [%s]", key));
    }
}

function registerFunction(key, func)
{
    customFunctionMap.set(key, func);
}

function call(calls)
{
    for (let func in calls) {
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
 * 
 * @returns {undefined}
 */
function showGlobalLoader(text = "")
{
    if (text !== undefined) {
        $("#global-loader-text").html(text);
    }

    $("#global-loader-overlay").removeClass(CLASS_HIDDEN);
}

/**
 * Hides the global loader overlay
 *
 * @returns {undefined}
 */
function hideGlobalLoader()
{
    $("#global-loader-text").html("loading...");
    $("#global-loader-overlay").addClass(CLASS_HIDDEN);
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
 * 
 * @returns {undefined}
 */
function triggerResponse(messages, btnName = "")
{
    hideGlobalLoader();

    let needOverlay = false;

    $(".error-marker").addClass(CLASS_HIDDEN);

    for (let m in messages) {
        let msg = messages[m];

        switch (msg.target) {
            case "MODAL":
                needOverlay = true;
                handleModalMsg(msg);
                break;

            case "POP_UP":
                handlePopupMsg(msg);
                break;

            case "MARKER":
                handleMarkerMsg(msg);
                break;

            default:
                console.log(sprintf("unrecognized message type [%s]", msg.target));
        }
    }

    if (needOverlay) {
        $("#btn-error-text").html(btnName);
        $("#error-overlay").removeClass(CLASS_HIDDEN);
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

function handleMarkerMsg(msg)
{
    console.log(msg, sprintf("#error-marker-%s-%s-%s", msg.formId, msg.fieldId, msg.type));

    $(sprintf("#error-marker-%s-%s-%s", msg.formId, msg.fieldId, msg.type)).removeClass(CLASS_HIDDEN);
}

/**
 *
 * @param {String} msg  - message to be shown -> musst be set
 * @param {String} btn  - text on button -> can be set, default: 'dismiss'
 *
 * @return {undefined}
 */
function clientsideError(msg, btn = "dismiss")
{
    triggerResponse({ "msg": msg, "target": "POP_UP" }, btn);
}

/**
 * Clears the error objects and closes the error popup
 *
 * @returns {undefined}
 */
function dismissErrors()
{
    $("#error-overlay").addClass(CLASS_HIDDEN);
    $("#error-holder").html("");
}
// === < notifications =============================================================================
// === > modal =====================================================================================
let openModals = 0;

function getModal(url)
{
    showGlobalLoader("load modal");

    GET(url);
}

/**
 * Function to submit directly from a modal if no other function is specified
 */
function submitFromModal()
{
    showGlobalLoader("collect data");

    let modal = $(sprintf("#modal-overlay-%d", openModals));
    let formData = extractValuesToSubmit(modal.attr("data-extraction-id"));
    let url = modal.attr("data-server-target");

    changeGlobalLoaderText("send data");
    POSTFormData(url, formData);
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
        $(sprintf("#modal-buttons-%d", openModals)).addClass(CLASS_HIDDEN);
    }
    // <-
    $(sprintf("#modal-input-%d", openModals)).append(modal.contentHtml);

    $(sprintf("#modal-container")).removeClass(CLASS_HIDDEN)
    $(sprintf("#modal-overlay-%d", openModals)).removeClass(CLASS_HIDDEN);
    $(sprintf("#modal-inlay-%d :input", openModals)).first().focus();

    hideGlobalLoader();
}

function modalShowBtn()
{
    $(sprintf("#modal-overlay-%d", openModals)).attr("data-close-on-overlay", "0");
    $(sprintf("#modal-buttons-%d", openModals)).removeClass(CLASS_HIDDEN);
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
function closeModal()
{
    if ($(sprintf("#modal-overlay-%d", openModals)).attr("data-lock-set") === "1") {
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
    if ($(content).length) {
        if (content.replace) {
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
 * @param {string} target - id of the context to be extracted from
 *
 * @returns {FormData} containing the extracted values
 */
function extractValuesToSubmit(target)
{
    showGlobalLoader("extract form fields");
    const  formData = new FormData();

    formData.append("__form_id", target);

    for (const elem of $(sprintf('[data-submit-id="%s"]', target))) {
        if ($(elem).attr("data-submit-as") === "") {
            // no submit id set so ignore input
        } else {
            changeGlobalLoaderText("extract: " + target);

            const id = $(elem).attr("data-submit-as");

            switch ($(elem).attr("data-value-type")) {
                case "INPUT":
                case "SELECT":
                case "TEXTAREA":
                case "TEXTFIELD":
                case "DATE":
                case "PASSWORD":
                case "NUMBER":
                    formData.append(id, $(elem).val());
                    break;

                case "CURRENCY":
                    break;

                case "DIV":
                    formData.append(id, $(elem).html());
                    break;

                case "CHECKBOX":
                    let checkArr = [];
                    $(elem).find('input[type="checkbox"]:checked').each(function () {
                        checkArr.push($(elem).val());
                    });
                    val = checkArr;
                    formData.append(id, checkArr);
                    break;

                case "SWITCH":                    
                    formData.append(id, $(elem).is(':checked'));
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

                case "LIST":
                    let listArr = [];
                    $(elem).find(".form-list-selection-item").each(function () {
                        listArr.push($(this).attr("data-value"));
                    });
                    formData.append(id, listArr);
                    break;

                default:
                    clientsideError("ERR_READ_FAIL", sprintf("a field value couldn't be fetched (field: %s)", $(elem).attr("data-submit-as")));
            }
        }
    }

    console.log(formData);
    return formData;
}

async function extractFiles(files)
{
    const fileArray = [];
    const promises = [];

    if (files.length > 0) {
        for (const file of files) {
            const fileData = await read_file(file);
            fileArray.push(fileData);
        }
    }

    return fileArray;
}

function read_file(file)
{
    return new Promise((resolve, reject) => {
        var fr = new FileReader();
        fr.fileName = file.name;
        fr.fileType = file.type;
        fr.onload = (event) => {
            resolve({
                fileName: event.target.fileName,
                fileType: event.target.fileType,
                fileContent: event.target.result.replace('data:', '').replace(/^.+,/, '')
            });
        };
        fr.onerror = (error) => {
            console.log("error reading file");
            reject(error);
        }

        fr.readAsDataURL(file);
    });
}
// === < site ======================================================================================
// === > form ======================================================================================
function addListSelectionItem(inputUuid, holderUuid)
{
    const itemName = $(sprintf('#%s option:selected', inputUuid)).text();
    const itemValue = $(sprintf('#%s', inputUuid)).val();
    const isMultiple = $(sprintf('#%s', inputUuid)).data("multiple");
    const newUuid = generateUUID();

    if(isMultiple || $(sprintf('#%s', holderUuid)).find(sprintf('[data-value="%s"]', itemValue)).length === 0) {
        $(sprintf('#%s', holderUuid)).append(sprintf('<div id="%s"><div class="form-list-selection-item" data-value="%s"><p>%s</p><button onclick="removeListSelectionItem(\'%s\')" class="btn btn-primary">X</button></div></div>', newUuid, itemValue, itemName, newUuid));
    }
}

function removeListSelectionItem(uuid)
{
    $(sprintf('#%s', uuid)).remove();
}
// === < form ======================================================================================
function generateUUID() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        const r = Math.random() * 16 | 0, v = c === 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}