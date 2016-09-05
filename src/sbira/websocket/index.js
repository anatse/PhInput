let wsUri
    // let wsProtocol = (document.location.protocol=='http:')? 'ws://' : 'wss://';
let wsProtocol = 'wss://';
if (document.location.host.indexOf('localhost') >= 0) {
    wsUri = wsProtocol + "localhost:8080/";
} else {
    wsUri = wsProtocol + document.location.host + "/";
}

let websocket = new WebSocket(wsUri);

let onOpen = [];
let onMessage = [];
let onError = [];
let onClose = [];

export function addOnOpen(callback) {
    onOpen.push(callback);
    return {
        remove: () => {
            const index = onOpen.indexOf(callback)
            onOpen.splice(index,  1);
            return onOpen;
        }
    }
}
export function addOnMessage(callback) {
    onMessage.push(callback);
    return {
        remove: () => {
            const index = onMessage.indexOf(callback)
            onMessage.splice(index, 1);
            return onMessage;
        }
    }
}
export function addOnError(callback) {
    onError.push(callback);
    return {
        remove: () => {
            const index = onError.indexOf(callback)
            onError.splice(index, 1);
            return onError;
        }
    }
}
export function addOnClose(callback) {
    onClose.push(callback);
    return {
        remove: () => {
            const index = onClose.indexOf(callback)
            onClose.splice(index, 1);
            return onClose;
        }
    }
}
export function initWebSocket() {
    websocket.onopen = function(evt) {
        onOpen.forEach((callback) => {
            callback(evt);
        })
    };
    websocket.onclose = function(evt) {
        onClose.forEach((callback) => {
            callback(evt);
        })
    };
    websocket.onmessage = function(evt) {
        onMessage.forEach((callback) => {
            callback(evt);
        })
    };
    websocket.onerror = function(evt) {
        onError.forEach((callback) => {
            callback(evt);
        })
    };
}

// function onOpenDef(evt) {
//     console.log("Websocket: CONNECTED");
// }
// function onCloseDef(evt) {
//     console.log("Websocket: DISCONNECTED");
//     console.log (evt);
// }
// function onMessageDef(evt) {
//     console.log("Websocket: RESPONSE: ", evt.data);
// //        websocket.close();
// }
// function onErrorDef(evt) {
//     console.log("Websocket: ERROR: ", evt.data);
// }
//
// addOnOpen(onOpenDef);
// addOnClose(onCloseDef);
// addOnMessage(onMessageDef);
// addOnError(onErrorDef);

export function doSend(message) {
    console.log("Websocket: SEND: ", message);
    websocket.send(message);
}

export default {
    init: initWebSocket,
    addOnOpen: addOnOpen,
    addOnClose: addOnClose,
    addOnMessage: addOnMessage,
    addOnError: addOnError
}
