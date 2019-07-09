<template xmlns="http://www.w3.org/1999/html">
  <div id="home">
    输入您的ID<input type="text" v-model="userId"/>
    <br/>
    然后您可以:
    <br/>
    ☺-------------------
    <button @click="new_room">创建房间</button>
    <br/>
    您还可以:
    <br/>
    ☺-------------------房间号码<input type="text" v-model="roomId"/>
    <button @click="in_room">进入房间</button>
    <br/><br/><br/>
    ☺-------------------点击开始
    <button @click="start()" v-if="can_start">→</button>
    <div id="game" align="center">
      <div id="bt" v-if="can_chu">请选择出:
        <button @click="jian_dao()">✂️</button>
        <button @click="shi_tou()">🔨</button>
        <button @click="bu()">🖐️</button>
      </div>
      <textarea id="result" style="border:1px solid #000;width: 500px;height: 400px" align="center"
                v-model="result"></textarea>
      <div>
        ☺-------------------最后如果你点击
        <button @click="leave_room()" v-if="can_leave">退出房间️</button>
      </div>
    </div>
  </div>
</template>

<script>
  import axios from "axios"

  export default {
    name: "Game",
    data() {
      return {
        roomId: '',
        roomPassword: '',
        userId: '',
        web_sock: null,
        result: '',
        can_chu: false,
        can_start: false,
        can_leave: false,
        userWhat: '',
        my_status: 0,
      }
    },
    created() {
    },
    methods: {
        initWebSocket() {//初始化weosocket
          const ws_uri = "ws://127.0.0.1:8088/jsb/" + this.userId;
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
            this.can_start = true;
          } else if (event.data === '3') {
            this.can_chu = true;
          } else if (event.data === '4') {//对手正常离开房间
            this.result += "对面玩家已经离开房间......" + "\r\n";
            this.can_start = false;
            this.can_chu = false;
          } else if (event.data === '5') {//对手逃跑或者中途断开
            this.result += "对面玩家已经逃走......" + "\r\n";
            this.get_result();
            this.can_start = false;
            this.can_chu = false;
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
      in_room() {
        axios.post("http://127.0.0.1:8088/jsb/in_home?user_id=" + this.userId + "&room_id=" + this.roomId).then(response => {
          if (response.data.code !== 1) {
            alert(response.data.msg);
            return;
          }
          if (response.data.data === false) {
            alert("抱歉,进入房间失败，请稍后重试......");
            return;
          }
          this.initWebSocket();
        }).catch(error => {
          alert('服务器目前维护中，房间创建失败，请稍后重试......');
        });
        this.can_leave = true;
        this.my_status = 1;
      },
      new_room() {
        axios.post("http://127.0.0.1:8088/jsb/new_home?user_id=" + this.userId).then(response => {
          if (response.data.code !== 1) {
            alert(response.data.msg);
            return;
          }
          this.roomId = response.data.data;
          this.result += "你已经进入房间......请等待玩家2进入......\r\n";
          this.initWebSocket();
        }).catch(error => {
          alert('服务器目前维护中，房间创建失败，请稍后重试......');
        });
        this.can_leave = true;
        this.my_status = 1;
      },
      leave_room() {
        this.web_sock.close();
        this.can_chu = false;
        this.can_start = false;
        this.can_leave = false;
        this.result = "";
        this.roomId = "";
      },
      get_result() {
        axios.get("http://127.0.0.1:8088/jsb/get_result?user_id=" + this.userId).then(response => {
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
        this.can_chu = false;
        this.can_start = true;
        this.can_leave = true;
      },
      start() {
        axios.post("http://127.0.0.1:8088/jsb/start_game?user_id=" + this.userId).then(response => {
          if (response.data.code !== 1) {
            alert(response.data.msg);
            return;
          }
          this.result = "即将开始比赛......请稍后\r\n";
          this.can_start = false;
          this.can_leave = false;
          this.my_status = 2;
        }).catch(error => {
          alert('服务器目前维护中，游戏开始失败，请稍后重试......');
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
        axios.post("http://127.0.0.1:8088/jsb/pk_what?user_id=" + this.userId + "&what=" + this.userWhat).then(response => {
          if (response.data.code !== 1) {
            alert(response.data.msg);
          }
        }).catch(error => {
          alert('服务器目前维护中，游戏PK失败，请稍后重试......');
        });
        this.can_chu = false;
        this.can_leave = false;
        this.my_status = 3;
      },
    },
  }
</script>

<style scoped>
</style>
