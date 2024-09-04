const Router = require("koa-router");
const router = new Router();
const { signIn, signUp, verify } = require("./controllers/auth.controllers");
const { newTweet, getTweets, getHotTweets } = require("./controllers/tweets.controllers");

router.post("/api/v1/user/sign_in", signIn);
router.post("/api/v1/user/sign_up", signUp);
router.get("/api/v1/user/verify", verify);
router.post("/api/v1/tweet/new", newTweet);
router.get("/api/v1/tweet/of/:username", getTweets);


module.exports = router;
