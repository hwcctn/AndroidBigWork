const { auth } = require('./auth.controllers');
const { setErr, setOk, user_datas, connections } = require('./utils');


const getFans = (ctx) => {
    const username = ctx.params.username;
    if (username == undefined) {
        setErr(ctx, "required parameter: username");
        return;
    }
    const data = user_datas.get(username);
    if (data)
        setOk(ctx, data.fans);
    else
        setErr(ctx, `no such user: ${username}`)
}

const getTweetsOf = (ctx) => {
    const username = ctx.params.username;
    if (username == undefined) {
        setErr(ctx, "required parameter: username");
        return;
    }
    const data = user_datas.get(username);
    if (data)
        setOk(ctx, data.tweets);
    else
        setErr(ctx, `no such user: ${username}`)
}

const getFollows = (ctx) => {
    const username = ctx.params.username;
    if (username == undefined) {
        setErr(ctx, "required parameter: username");
        return;
    }
    const data = user_datas.get(username);
    if (data)
        setOk(ctx, data.follows);
    else
        setErr(ctx, `no such user: ${username}`)
}

const getAvatar = (ctx) => {
    const username = ctx.params.username;
    if (username == undefined) {
        setErr(ctx, "required parameter: username");
        return;
    }
    const data = user_datas.get(username);
    if (data)
        setOk(ctx, data.avatar);
    else
        setErr(ctx, `avatar of ${username} no found`);
}

const subscribe = (ctx) => {
    try {
        const username = auth(ctx.headers.token);
        const username_target = ctx.request.body.target;
        if (username_target == undefined) {
            setErr(ctx, "bad request", 400);
            return;
        }
        {
            const data = user_datas.get(username_target);
            if (data)
                data.fans.push(username);
            else {
                setErr(ctx, `no such user: ${username}`)
                return;
            }
        }
        {
            const data = user_datas.get(username);
            data.follows.push(username_target);
        }
        setOk(ctx);
    } catch (err) {
        setErr(ctx, err, 401);
    }
}

const unsubscribe = (ctx) => {
    try {
        const username = auth(ctx.headers.token);
        const username_target = ctx.request.body.target;
        if (username_target == undefined) {
            setErr(ctx, "bad request", 400);
            return;
        }
        {
            const data = user_datas.get(username_target);
            if (data)
                data.fans.splice(data.fans.indexOf(username), 1);
            else {
                setErr(ctx, `no such user: ${username}`);
                return;
            }
        }
        {
            const data = user_datas.get(username);
            data.follows.splice(data.follows.indexOf(username_target), 1);
        }
        setOk(ctx);
    } catch (err) {
        setErr(ctx, err, 401);
    }
}

const listenUpdate = (ctx) => {
    try {
        connections.set(auth(ctx.headers.token), ctx.websocket);
    } catch (err) {
        setErr(ctx, "Unauthorized", 401);
    }
};

module.exports = {
    listenUpdate,
    getFans,
    getTweetsOf,
    getFollows,
    subscribe,
    unsubscribe,
    getAvatar,
}