//立即执行的js
(function() {
    //获取contextPath
    var contextPath = getContextPath();
    //获取basePath
    var basePath = getBasePath();
    //将获取到contextPath和basePath分别赋值给window对象的g_contextPath属性和g_basePath属性
    window.g_contextPath = contextPath;
    window.g_basePath = basePath;
})();


function getBasePath() {
    var curWwwPath = window.document.location.href;
    var pathName = window.document.location.pathname;
    var pos = curWwwPath.indexOf(pathName);
    var localhostPath = curWwwPath.substring(0, pos);
    var projectName = pathName.substring(0, pathName.substr(1).indexOf('/') + 1);
    return (localhostPath + projectName);
}

function getContextPath() {
    return window.document.location.pathname.substring(0, window.document.location.pathname.indexOf('\/', 1));
};
