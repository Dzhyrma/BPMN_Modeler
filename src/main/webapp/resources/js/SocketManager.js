var socket = new WebSocket("ws://0.0.0.0:8081/");
socket.onopen = function () {
  console.log("���������� ���������");
};
socket.onclose = function () {
  console.log ("���������� ���������");
};
socket.onmessage = function (event) {
  console.log ("������ ��������� � �����������:", event.data);
};