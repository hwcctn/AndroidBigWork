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

module.exports = {
    setErr,
    setOk,
    Errors,
}