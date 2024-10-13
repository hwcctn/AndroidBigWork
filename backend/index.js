const Koa = require("koa");
const parser = require("koa-bodyparser");
const cors = require("@koa/cors");
const router = require("./router");

const websockify = require("koa-websocket");
const { listenUpdate } = require("./controllers/user_data.controllers");

const App = websockify(new Koa());
const port = 8001;

const Router = require("koa-router");
const ws_router = new Router();
ws_router.all('/api/v1/user/listen', listenUpdate);


App.use(parser())
    .use(cors())
    .use(router.routes())

App.ws.use(ws_router.routes()).use(ws_router.allowedMethods());

App.listen(port, () => {
    console.log(`🚀 Server listening http://0.0.0.0:${port}/ 🚀`);
});
