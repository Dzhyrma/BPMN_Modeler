var socket = new WebSocket("ws://0.0.0.0:8081/");
socket.onopen = function () {
  console.log("Соединение открылось");
};
socket.onclose = function () {
  console.log ("Соединение закрылось");
};
socket.onmessage = function (event) {
  console.log ("Пришло сообщение с содержанием:", event.data);
};