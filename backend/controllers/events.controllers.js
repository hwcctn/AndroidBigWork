const events_db = [];

const getEvents = (ctx) => {
    ctx.body = events_db;
    ctx.status = 200;
};

const addEvent = (ctx) => {
    console.log(ctx.request)
    events_db.push(ctx.request.body);
    ctx.body = "Events created!";
    ctx.status = 201;
};

module.exports = {
    getEvents,
    addEvent,
};