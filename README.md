# cordova-plugin-device
cordova plugin to get device information for android


#Usage

克隆项目到本地，在一个cordova项目路径下安装即可：
<code>cordova plugin add d:/cordova-plugin-device</code>

在js文件中使用：
```javascript
document.addEventListener('deviceready', () => {
  window.cordova.plugins.deviceInfo.getDeviceInfo('', (result) => {
    console.log(result);
  }, (err) => {
    console.log(err);
  });
}, false);
```
