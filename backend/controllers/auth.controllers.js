const { setErr, setOk, Errors, user_datas, id } = require("./utils");

const passwords = new Map()
passwords.set("fin", "123456")
passwords.set("julia", "123456")
passwords.set("well", "123456")

const sessions = new Map()
sessions.set("fin", "fin")
sessions.set("julia", "julia")


const signIn = (ctx) => {
    const body = ctx.request.body;
    const password = body.password.toString();
    const username = body.username;

    if (passwords.get(username) == password) {
        const crypto = require('crypto');

        // 生成更随机的 token
        const token = crypto.randomBytes(16).toString('hex');
        sessions.forEach((v, k, m) => { if (v == username) { m.delete(k) } });
        sessions.set(token, username);
        setOk(ctx, { token });
    } else if (passwords.get(username) == undefined)
        setErr(ctx, "no such user");
    else
        setErr(ctx, "wrong password");

};

const signUp = (ctx) => {
    const body = ctx.request.body;
    const password = body.password.toString();
    const username = body.username;
    const avatar = body.avatar;

    if (passwords.has(username))
        setErr(ctx, "user exists");
    else {
        passwords.set(username, password);
        user_datas.set(username, { tweets: [], fans: [username], follows: [username], avatar: avatar ? avatar : null });
        signIn(ctx);
    }
};

const verify = (ctx) => {
    const token = ctx.request.body.token;
    if (token == undefined) {
        setErr(ctx);
        return;
    }
    try {
        const username = auth(token);
        setOk(ctx, { username });
    } catch (error) {
        setErr(ctx, "token invalid", 401);
    }
}

const auth = (token) => {
    const username = sessions.get(token);
    if (username) {
        return username
    } else throw (Errors.TokenInvalid);
};


module.exports = {
    auth,
    signIn,
    signUp,
    verify,
};
