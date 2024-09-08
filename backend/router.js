const Router = require("koa-router");
const router = new Router();
const { signIn, signUp, verify } = require("./controllers/auth.controllers");
const { getFans,
    getFollows,
    subscribe,
    unsubscribe,
    getTweets,
    listenUpdate,
} = require("./controllers/user_data.controllers");
const { newTweet, beClicked } = require("./controllers/tweets.controllers");

router.post("/api/v1/user/sign_in", signIn);
router.post("/api/v1/user/sign_up", signUp);
router.get("/api/v1/user/verify", verify);

router.get("/api/v1/user/fans/of/:username", getFans);
router.get("/api/v1/user/follows/of/:username", getFollows);
router.post("/api/v1/user/subs", subscribe);
router.all("/api/v1/user/listen", listenUpdate);
router.post("/api/v1/user/unsubs", unsubscribe);

router.post("/api/v1/tweet/new", newTweet);
router.get("/api/v1/tweet/of/:username", getTweets);
router.get("/api/v1/tweet/clicked", beClicked);


module.exports = router;
