const { setErr, setOk, Errors } = require("./utils");

const users = new Map()
users.set("fin", "123456")

const sessions = new Map()
sessions.set("eeeeee", "fin")


const signIn = (ctx) => {
    const body = ctx.request.body;
    const password = body.password.toString();
    const username = body.username;

    if (users.get(username) == password) {
        const token = Date.now().toString();
        sessions.forEach((v, k, m) => { if (v == username) { m.delete(k) } });
        sessions.set(token, username);
        setOk(ctx, { token });
        console.log(sessions)
    } else if (users.get(username) == undefined)
        setErr(ctx, "no such user");

    else
        setErr(ctx, "wrong password");

};

const signUp = (ctx) => {
    const body = ctx.request.body;
    console.log(body);
    const password = body.password.toString();
    const username = body.username;

    if (users.has(username))
        setErr(ctx, "user exists");
    else {
        users.set(username, password);
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
    if (username) { return username } else throw (Errors.TokenInvalid);
};


module.exports = {
    auth,
    signIn,
    signUp,
    verify,
};