const Router = require("koa-router");
const router = new Router();
const { signIn, signUp, verify } = require("./controllers/auth.controllers");
const { getFans,
    getFollows,
    subscribe,
    unsubscribe,
    getTweetsOf,
} = require("./controllers/user_data.controllers");
const { newTweet, beClicked, getHotTweets, getTweetById } = require("./controllers/tweets.controllers");

router.post("/api/v1/user/sign_in", signIn);
router.post("/api/v1/user/sign_up", signUp);
router.post("/api/v1/user/verify", verify);

router.get("/api/v1/user/fans/of/:username", getFans);
router.get("/api/v1/user/follows/of/:username", getFollows);
router.post("/api/v1/user/subs", subscribe);
router.post("/api/v1/user/unsubs", unsubscribe);

router.post("/api/v1/tweet/new", newTweet);
router.get("/api/v1/tweet/of/:username", getTweetsOf);
router.get("/api/v1/tweet/id/:id", getTweetById);
router.get("/api/v1/tweet/clicked/:id", beClicked);
router.get("/api/v1/tweet/hot", getHotTweets);


module.exports = router;
