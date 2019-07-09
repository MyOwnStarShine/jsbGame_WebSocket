<template>
  <div id="home">
    <textarea id="msg" v-model="msg" style="width: 600px; height: 500px"></textarea>
    <br/>
    <textarea id="my_msg" v-model="my_msg" style="width: 600px; height: 50px"></textarea>
    <br/>
    <button @click="send()">发送消息</button>
    <button @click="onclose()">关闭连接</button>
  </div>
</template>

<script>
  export default {
    name: "Home",
    data() {
      return {
        web_sock: null,
        userId: -1,
        msg: '',
        my_msg: '',
      }
    },
    created() {
      this.initWebSocket();
    },
    destroyed() {
      this.web_sock.close() //离开路由之后断开websocket连接
    },
    methods: {
      getUserId(m, n) {
        return Math.floor(Math.random() * (m - n) + n);
      },
      initWebSocket() {//初始化weosocket
        this.userId = this.getUserId(1, 1000);
        const ws_uri = "ws://127.0.0.1:8088/play/" + this.userId;
        this.web_sock = new WebSocket(ws_uri);
        this.web_sock.onmessage = this.onmessage;
        this.web_sock.onopen = this.onopen;
        this.web_sock.onerror = this.onerror;
        this.web_sock.onclose = this.onclose;
      },
      onopen() { //连接建立之后执行send方法发送数据
        console.log('建立连接成功: ' + this.web_sock.readyState);
      },
      onerror() {//连接建立失败重连
        console.log('出现错误:' + this.web_sock.readyState);
      },
      onmessage(event) { //数据接收
        console.log('收到消息: ' + event.data);
        var time = new Date().getTime();
        this.msg += (time + "  " + this.userId + "\r\n");
        this.msg += (event.data + "\r\n");
      },
      send() {//数据发送
        this.web_sock.send(this.my_msg);
      },
      onclose() {  //关闭
        console.log('断开连接: ' + this.web_sock.readyState);
      },
    },
  }
</script>

<style scoped>

</style>
