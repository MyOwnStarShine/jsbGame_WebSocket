import Vue from 'vue'
import Router from 'vue-router'
import Activity from "../views/Activity";
import ActivityGame from "../views/ActivityGame";

Vue.use(Router);

let routes = [{
  path: '/',
  component: Activity,
  name: 'Activity',
}, {
  path: '/activity/activityGame/:userId/:rival/:roomId',
  component: ActivityGame,
  name: 'ActivityGame',
}];

const router = new Router({
  routes
});

export default router;
