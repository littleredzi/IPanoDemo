//调用IOS方法插件
var MyIOSPlugin = {
    /**
     * 调用IOS方法
     * @param method 要调用IOS插件的方法名
     * @param parameter 参数[数组]
     * @param success 成功回调
     * @param fail 失败回调
     * @returns {*}
     */
nativeFunction: function(method, parameter, success, fail) {
 
    return Cordova.exec(success, fail, "MyPlugin", method, parameter);
}
//nativeFunction:function(method,parameter,success,fail){
//    return Cordova.exec(success,fail, "MyPlugin",method,parameter);
//}
};