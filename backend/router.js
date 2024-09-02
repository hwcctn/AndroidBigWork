const Router = require("koa-router");
const router = new Router();
const { getEvents, addEvent } = require("./controllers/events.controllers");
const { signIn, signUp } = require("./controllers/auth.controllers");
const { newTweet } = require("./controllers/tweets.controllers");

router.get("/events_list", getEvents);
router.post("/add_event", addEvent);
router.post("/sign_in", signIn);
router.post("/sign_up", signUp);
router.post("/tweet/new", newTweet);


module.exports = router;
