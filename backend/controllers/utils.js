function setErr(ctx, msg, errorCode = 500) {
    if (msg)
        ctx.body = { result: 1, msg };
    else
        ctx.body = { result: 1 };
    ctx.status = errorCode;
}

function setOk(ctx, content, code = 201) {
    if (content) {
        ctx.body = { reuslt: 0, content };
    } else {
        ctx.body = { result: 0 };
    }
    ctx.status = code;
}

const Errors = {
    TokenInvalid: 1,
}


const user_datas = new Map();

user_datas.set('fin', {
    fans: [],
    follows: ['julia'],
    // id of tweets
    tweets: [],
});

user_datas.set('julia', {
    fans: ['fin'],
    follows: [],
    // id of tweets
    tweets: [],
    avatar: null,
});

const tweets = new Map();
var id = 0;
/*
Tweet {
    date,
    title,
    sender,
    content: []string,
    viewd,
    tags: []string,
    images: []string,
}
*/

const connections = new Map();

module.exports = {
    connections,
    setErr,
    setOk,
    Errors,
    user_datas,
    tweets,
    id,
}