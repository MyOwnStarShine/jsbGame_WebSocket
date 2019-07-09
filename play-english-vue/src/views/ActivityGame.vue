<template xmlns="http://www.w3.org/1999/html">
  <div id="home">
    <div id="game" align="center">
      <span v-if="!can_chu">请稍后片刻，正在匹配此轮对手......</span>
      <div id="bt" v-if="can_chu">请选择出:
        <button @click="jian_dao()">✂️</button>
        <button @click="shi_tou()">🔨</button>
        <button @click="bu()">🖐️</button>
      </div>
      <textarea id="result" style="border:1px solid #000;width: 500px;height: 400px" align="center"
                v-model="result"></textarea>
    </div>
  </div>
</template>

<script>
  import axios from "axios"

  export default {
    name: "ActivityGame",
    data() {
      return {
        roomId: '',
        userId: '',
        web_sock: null,
        result: '',
        userWhat: '',
        my_status: 0,
        can_chu: false,
      }
    },
    created() {
    },
    mounted() {//省略了谁是P1和P2的构造
      this.userId = this.$route.params.userId;
      this.rival = this.$route.params.rival;
      this.roomId = this.$route.params.roomId;
      this.initWebSocket();
    },
    methods: {
      initWebSocket() {//初始化weosocket
        const ws_uri = "ws://127.0.0.1:8088/jsb_game/" + this.userId;
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
        if (event.data === '2') {
          this.result += "玩家已经都进入房间......准备开始比赛吧" + "\r\n";
        } else if (event.data === '3') {
          this.can_chu = true;
        } else if (event.data === '4') {//对手正常离开房间
          this.result += "对面玩家已经离开房间......" + "\r\n";
        } else if (event.data === '5') {//对手逃跑或者中途断开
          this.result += "对面玩家已经逃走......" + "\r\n";
          this.get_result();
        } else {
          let obj = JSON.parse(event.data);
          if (obj.p1 === parseInt(this.userId)) {
            this.result += "第 " + obj.round + " 轮:  " + this.parseMsg2ImgStr(obj.p1Pk) + "  vs  " + this.parseMsg2ImgStr(obj.p2Pk) + "\r\n";
          } else {
            this.result += "第 " + obj.round + " 轮:  " + this.parseMsg2ImgStr(obj.p2Pk) + "  vs  " + this.parseMsg2ImgStr(obj.p1Pk) + "\r\n";
          }
          this.can_chu = true;
          if (obj.round % 3 == 0) {
            this.get_result();
          }
        }
      },
      onclose() {  //关闭
        console.log('断开连接: ' + this.web_sock.readyState);
      },
      get_result() {
        axios.get("http://127.0.0.1:8088/jsb_activity/get_result?user_id=" + this.userId).then(response => {
          if (response.data.code !== 1) {
            alert(response.data.msg);
          }
          if (response.data.data.winner === -1) {
            this.result += "你们都向往和平！🕊🕊🕊🕊🕊🕊🕊\r\n";
          } else if (response.data.data.winner === parseInt(this.userId)) {
            this.result += "恭喜你，你赢喽！👏👏👏👏👏👏👏\r\n";
          } else {
            this.result += "不对啊，你居然落败，继续加油!🌹🌹🌹🌹🌹🌹\r\n";
          }
        }).catch(error => {
          alert('服务器目前维护中，获取结果失败，请稍后重试......');
        });
      },
      jian_dao() {
        this.userWhat = 'jiandao';
        this.pk();
      },
      shi_tou() {
        this.userWhat = 'shitou';
        this.pk();
      },
      bu() {
        this.userWhat = 'bu';
        this.pk();
      },
      parseMsg2ImgStr(msg) {
        if (msg === 'jiandao') {
          return '✂️';
        } else if (msg === 'shitou') {
          return '🔨';
        }
        return '🖐';
      },
      pk() {
        axios.post("http://127.0.0.1:8088/jsb_activity/pk_what?user_id=" + this.userId + "&what=" + this.userWhat).then(response => {
          if (response.data.code !== 1) {
            alert(response.data.msg);
          }
        }).catch(error => {
          alert('服务器目前维护中，游戏PK失败，请稍后重试......');
        });
        this.my_status = 3;
      },
    },
  }
</script>

<style scoped>
</style>
