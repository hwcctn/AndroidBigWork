const { auth, Errors } = require("./auth.controllers");
const { setOk, setErr } = require("./utils");

const tweets = new Map()
var id = 0;
/*
Tweet {
    id,
    date,
    title,
    content,
    viewd,
    tags,
    images,
}
*/


const pushTweet = (token, tweet) => {
    const user = auth(token)
    const old = tweets.get(user)
    if (old) {
        old.push(tweet)
    } else {
        tweets.set(user, [tweet])
    }
}

const newTweet = (ctx) => {
    try {
        const token = ctx.request.headers.token;
        const content = ctx.request.body.content;
        const title = ctx.request.body.title;
        pushTweet(token, {
            content,
            id,
            date: Date.now(),
            title,
            viewd: 0,
            tags: [],
            images: [],
        });
        id += 1;
        setOk(ctx);
    } catch (e) {
        console.log(`fuck you ${e}`);
        setErr(ctx, "unknown error");
    }
    console.log(tweets)
}

const getTweets = (ctx) => {
    const username = ctx.params.username;
    console.log(ctx);
    if (username == undefined) {
        setErr(ctx, "required parameter: username");
        return;
    }
    const ts = tweets.get(username);
    if (ts)
        setOk(ctx, ts);
    else
        setOk(ctx, []);
}

const getHotTweets = (ctx) => {
    const page_size = ctx.params.size;
    const page_offset = ctx.params.offset;
}


module.exports = {
    newTweet,
    getTweets,
    getHotTweets,
}