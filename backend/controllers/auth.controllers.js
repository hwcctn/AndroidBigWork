const { setErr, setOk, Errors, user_datas, id } = require("./utils");

const passwords = new Map()
passwords.set("fin", "123456")
passwords.set("julia", "123456")

const sessions = new Map()
sessions.set("fin", "fin")
sessions.set("julia", "julia")


const signIn = (ctx) => {
    const body = ctx.request.body;
    console.log(ctx.request);
    const password = body.password.toString();
    const username = body.username;

    if (passwords.get(username) == password) {
        const token = Date.now().toString();
        sessions.forEach((v, k, m) => { if (v == username) { m.delete(k) } });
        sessions.set(token, username);
        setOk(ctx, { token });
        console.log(sessions)
    } else if (passwords.get(username) == undefined)
        setErr(ctx, "no such user");

    else
        setErr(ctx, "wrong password");

};

const signUp = (ctx) => {
    const body = ctx.request.body;
    console.log(body);
    const password = body.password.toString();
    const username = body.username;
    const avatar = body.avatar;

    if (passwords.has(username))
        setErr(ctx, "user exists");
    else {
        passwords.set(username, password);
        userdata.set(username, { tweets: [], fans: [], follows: [] });
        signIn(ctx);
    }
};

const verify = (ctx) => {
    const token = ctx.params.token;
    if (token == undefined) {
        setErr(ctx);
        return;
    }
    try {
        const username = auth(token);
        setOk(ctx, { username });
    } catch (error) {
        setErr(ctx);
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