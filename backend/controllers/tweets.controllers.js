const { auth, Errors } = require("./auth.controllers")

const tweets = new Map()

/*
Tweet {
    id,
    date,
    title,
    content,
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
        pushTweet(token, content);
        ctx.body = {};
        ctx.status = 201;
    } catch (e) {
        console.log(`fuck you ${e}`);
        ctx.status = 500;
    }
    console.log(tweets)
}


module.exports = {
    newTweet
}