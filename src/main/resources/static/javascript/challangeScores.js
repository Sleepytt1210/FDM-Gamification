const local = window.localStorage;

$(function () {
    const url = window.location.href;
    const urlSplit = url.match(/scenario\/(\d+)\/(\d+)/);
    const cid = urlSplit[1];
    const cidString = 'c' + cid;

    local.getItem(cidString);
});