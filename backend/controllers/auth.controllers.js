const users = new Map()
users.set("fin", "123456")

const sessions = new Map()
sessions.set("eeeeee", "fin")


const signIn = (ctx) => {
    const body = ctx.request.body;
    console.log(body);
    const password = body.password.toString();
    const username = body.username;

    if (users.get(username) == password) {
        const token = Date.now().toString();
        sessions.forEach((v, k, m) => { if (v == username) { m.delete(k) } });
        sessions.set(token, username);
        ctx.body = { token };
        ctx.status = 201;
        console.log(sessions)
    }
    else
        ctx.status = 500;
};

const signUp = (ctx) => {
    const body = ctx.request.body;
    console.log(body);
    const password = body.password.toString();
    const username = body.username;

    if (users.get(username) == password) {
        ctx.body = { msg: "user exits" }
        ctx.status = 500;
    }
    else {
        users.set(username, password);
        signIn(ctx);
    }
};

const auth = (token) => {
    const username = sessions.get(token);
    if (username) { return username } else throw ("invalid token");
};

const Errors = {
    TokenInvalid: 1,
}

module.exports = {
    auth,
    signIn,
    signUp
};