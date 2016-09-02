let wsUri
if (document.location.host.indexOf('localhost')>=0){
  wsUri = "wss://localhost:8080/";
}else {
  wsUri = "wss://" + document.location.host + "/";
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
      return onOpen.splice(index,index+1);
    }
  }
}
export function addOnMessage(callback) {
  onMessage.push(callback);
  return {
    remove: () => {
      const index = onMessage.indexOf(callback)
      return onMessage.splice(index,index+1);
    }
  }
}
export function addOnError(callback) {
  onError.push(callback);
  return {
    remove: () => {
      const index = onError.indexOf(callback)
      return onError.splice(index,index+1);
    }
  }
}
export function addOnClose(callback) {
  onClose.push(callback);
  return {
    remove: () => {
      const index = onClose.indexOf(callback)
      return onClose.splice(index,index+1);
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
  init : initWebSocket,
  addOnOpen: addOnOpen,
  addOnClose: addOnClose,
  addOnMessage: addOnMessage,
  addOnError: addOnError
}
