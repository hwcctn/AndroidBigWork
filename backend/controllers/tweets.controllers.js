const { auth } = require("./auth.controllers");
let { setOk, setErr, tweets, id, user_datas, connections } = require("./utils");


const pushTweet = (token, tweet) => {
    const user = auth(token);
    console.log(`id: ${id}`);
    tweets.push(tweet);
    id += 1;

    return id - 1;
}

pushTweet("fin", {
    date: Date.now(),
    title: "title",
    sender: "fin",
    content: ["content"],
    tags: ["tag1", "tag2"],
    images: ["img1", "img2"],
});

pushTweet("fin", {
    date: Date.now(),
    title: "title",
    sender: "fin",
    content: ["content"],
    tags: ["tag1", "tag2"],
    images: ["img1", "img2"],
});

pushTweet("fin", {
    date: Date.now(),
    title: "title",
    sender: "fin",
    content: ["content"],
    tags: ["tag1", "tag2"],
    images: ["img1", "img2"],
});

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
            console.log(tweets)

            pushUpdate(tweet);
        } else {
            setErr(ctx, "Unauthorized", 400);
            return;
        }

        setOk(ctx, id);
    } catch (e) {
        console.log(e);
        setErr(ctx, "unknown error");
    }

}

// push update to all the connections
const pushUpdate = (tweet) => {
    const fans = user_datas.get(tweet.sender).fans;
    console.log(`connections: `, connections)
    console.log(`fans: `, fans)
    fans.forEach(fan => {
        const conn = connections.get(fan);
        if (!conn) return;
        try {
            conn.send(JSON.stringify(tweet));
            console.log(`pushed to ${fan}`);
        } catch (e) {
            console.log(e);
            connections.delete(fan);
        }
    });
}

const getTweetById = (ctx) => {
    const id = ctx.request.params.id;
    if (id == undefined) {
        setErr(ctx, "bad request", 400);
        return;
    }
    const data = tweets[id];
    if (data)
        setOk(ctx, data);
    else
        setErr(ctx, `no such tweet: ${id}`)
}


// get 10 highest viewd post 
const getHotTweets = (ctx) => {
    let result = [];

    tweets.forEach((v, i) => {
        result.push({ id: i, tweet: v });
    });

    result.sort((x, y) => {
        if (x > y) return 1
        else if (x < y) return -1
        else 0;
    });
    const tmp = result.slice(0, Math.min(result.length, 10));
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
            const tweet = tweets[tweet_id];
            console.log(tweets);
            console.log(`tweet_id: ${tweet_id}`);
            console.log(`tweet: ${tweet}`);
            if (tweet) {
                tweet.viewd += 1;
            } else {
                setErr(ctx, "Invalid tweet id", 400);
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
    getTweetById,
    getHotTweets,
}