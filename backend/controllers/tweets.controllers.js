const { auth } = require("./auth.controllers");
const { setOk, setErr, tweets, id, user_datas, connections } = require("./utils");


const pushTweet = (token, tweet) => {
    const user = auth(token);
    tweets.set(id, tweet);
    id += 1;

    return id - 1;
}

const newTweet = (ctx) => {
    try {
        const token = ctx.request.headers.token;
        const content = ctx.request.body.content;
        const tags = ctx.request.body.tags;
        const images = ctx.request.body.images;
        const title = ctx.request.body.title;
        const sender = auth(token);

        if (!token) {
            setErr(ctx, "Unauthorized", 401);
            return;
        }

        if (content && title && tags && images) {
            const tweet = {
                date: Date.now(),
                title,
                sender,
                content,
                tags,
                images,
            };
            pushTweet(token, tweet);
            pushUpdate(tweet);
        } else {
            setErr(ctx, "Unauthorized", 400);
            return;
        }

        setOk(ctx, id);
    } catch (e) {
        console.log(`fuck you ${e}`);
        setErr(ctx, "unknown error");
    }

    console.log(tweets)
}

// push update to all the connections
const pushUpdate = (tweet) => {
    const fans = user_datas.get(tweet.sender).fans;
    fans.forEach(fan => {
        const conn = connections.get(fan);
        conn.send(tweet);
    });
}

// get 10 highest viewd post 
const getHotTweets = (ctx) => {
    let result = [];

    tweets.forEach((v, k) => {
        result.push({ id: k, tweet: v });
    });

    tweets.sort((x, y) => {
        if (x > y) return 1
        else if (x < y) return -1
        else 0;
    });
    const tmp = tweets.slice(0, Math.min(result.length, 10));
    setOk(ctx, tmp);
}

// a tweet is clicked, viewd += 1
const beClicked = (ctx) => {
    try {
        const token = ctx.request.headers.token;
        const tweet_id = ctx.request.params.id;
        if (!tweet_id) {
            setErr(ctx, "Require parameter: id", 400);
        }
        if (token) {
            const sender = auth(token);
            const tweet = tweets.get(tweet_id);
            if (tweet) {
                tweet.viewd += 1;
            } else {
                setErr(ctx, "Invalid token", 400);
                return;
            }
        } else {
            setErr(ctx, "Unauthorized", 401);
            return;
        }
    } catch (e) {
        console.log(e);
        setErr(ctx, "Unauthorized", 401);
    }
}


module.exports = {
    beClicked,
    newTweet,
    getHotTweets,
}