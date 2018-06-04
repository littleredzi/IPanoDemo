cordova.define('cordova/plugin_list', function(require, exports, module) {
module.exports = [
    {
        "file": "plugins/cordova-plugin-whitelist/whitelist.js",
        "id": "cordova-plugin-whitelist.whitelist",
        "runs": true
    },
    
    {
        "file": "plugins/cordova-plugin-inappbrowser/www/inappbrowser.js",
        "id": "cordova-plugin-inappbrowser.inappbrowser",
        "clobbers": [
            "cordova.InAppBrowser.open",
            "window.open"
        ]
    },
    

    {
    "file": "plugins/cordova-plugin-weixin/www/weixin.js", //注意这里的路径修改为你的weixin.js所在的位置
    "id": "com.phonegap.plugins.weixin.WeiXin",
    "clobbers": [
        "navigator.weixin"
    ]
	}
    
    



];
module.exports.metadata = 
// TOP OF METADATA
{
    "cordova-plugin-whitelist": "1.0.1-dev",
     "cordova-plugin-inappbrowser": "1.0.1-dev",
    "com.phonegap.plugins.weixin": "0.5.2"
}
// BOTTOM OF METADATA
});