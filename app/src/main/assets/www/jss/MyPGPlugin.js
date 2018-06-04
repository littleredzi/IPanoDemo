/**
 * 构造方法
 */
function MyPGPlugin() {
};
/**
 * 字符串相加
 *
 * @param str1, str2
 */
MyPGPlugin.prototype.addStr = function(successCallback, failureCallback, str1, str2) {
    PhoneGap.exec(successCallback, failureCallback, "MyPGPlugin", "addStr", [str1, str2]);
};
/**
 * 加载MyPlugin对象
 */
PhoneGap.addConstructor(function() {
                        if(!window.plugins)
                        {
                        window.plugins = {};
                        }
                        window.plugins.myPGPlugin = new MyPGPlugin();
                        });