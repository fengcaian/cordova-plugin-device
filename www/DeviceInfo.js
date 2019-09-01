var exec = require('cordova/exec');

exports.getDeviceInfo = function (arg0, success, error) {
    exec(success, error, 'DeviceInfo', 'getDeviceInfo', [arg0]);
};
exports.getImei = function (arg0, success, error) {
    exec(success, error, 'DeviceInfo', 'getImei', [arg0]);
};
