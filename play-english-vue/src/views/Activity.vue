<template>
  <div id="activity">
    输入您的ID<input type="text" v-model="userId"/>
    输入活动ID<input type="text" v-model="activityId"/>
    <button @click="in_activity" v-if="!is_in">进入活动</button>
    <button @click="out_activity" v-if="is_in">退出活动</button>
    <div id="players">
      <ul>
        <li v-for="(player) in players">
          <el-tag type="success">{{ player }}</el-tag>
        </li>
      </ul>
    </div>
    <div id="round1WinPlayers" v-if="round>=1">
      <span>第 1 轮获胜者为:</span>
      <ul>
        <li v-for="(player) in round1WinPlayers">
          <el-tag type="success">{{ player }}</el-tag>
        </li>
      </ul>
    </div>
    <div id="round2WinPlayers" v-if="round>=2">
      <span>第 2 轮获胜者为:</span>
      <ul>
        <li v-for="(player) in round2WinPlayers">
          <el-tag type="success">{{ player }}</el-tag>
        </li>
      </ul>
    </div>
  </div>
</template>
<script>
  export default {
    name: "Activity",
    data() {
      return {
        is_in: false,
        players: [],
        round1WinPlayers: [],//此处方便存储
        round2WinPlayers: [],
        userId: '',
        activityId: '',
        round: 0,
      }
    },
    methods: {
      initWebSocket() {//初始化weosocket
        const ws_uri = "ws://127.0.0.1:8088/jsb_activity/" + this.userId + "/" + this.activityId;
        this.web_sock = new WebSocket(ws_uri);
        this.web_sock.onmessage = this.onmessage;
        this.web_sock.onopen = this.onopen;
        this.web_sock.onerror = this.onerror;
        this.web_sock.onclose = this.onclose;
      },
      onopen() { //连接建立之后执行send方法发送数据
        console.log('建立连接成功: ' + this.web_sock.readyState);
        this.is_in = true;
      },
      onerror() {//连接建立失败重连
        console.log('出现错误:' + this.web_sock.readyState);
      },
      onmessage(event) { //数据接收
        console.log('收到消息: ' + event.data);
        let obj = JSON.parse(event.data);
        switch (obj.type) {
          case 1://准备消息
            if (!obj.canPlay) {
              alert(obj.msg);
              this.out_activity();
              return;
            }
            this.players = obj.players;
            break;
          case 2://开始比赛
            if (this.round == 1 && this.round1WinPlayers.indexOf(obj.userId) <= -1) {
              return;
            }
            let data = this.$router.resolve({
              name: 'ActivityGame',
              params: {
                userId: obj.userId,
                rival: obj.rival,
                roomId: obj.roomId,
              },
            });
            window.open(data.href, '_blank');
            break;
          case 3://每轮结果消息
            this.round = obj.round;
            if (this.round == 1) {
              this.round1WinPlayers = obj.winPlayers;
            } else if (this.round == 2) {
              this.round2WinPlayers = obj.winPlayers;
            }
            if (this.round >= 2) {
              return;
            }
            break;
          default:
            break;
        }
      },
      onclose() {  //关闭
        console.log('断开连接: ' + this.web_sock.readyState);
      },
      in_activity() {
        this.initWebSocket();
      },
      out_activity() {
        this.is_in = false;
        this.onclose();
      },
    },
  }
</script>

<style scoped>
</style>
