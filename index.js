import { NativeModules, NativeAppEventEmitter } from 'react-native';

const { RNAuthz } = NativeModules;

class RNAuthzModule {

    static open(url) {
        return new Promise((resolve, reject) => {
            RNAuthz.openURL(url, (error) => {
                if (error) {
                    return reject(error);
                }
                resolve(true);
            });
        });
    }

    static dismiss() {
        RNAuthz.dismiss();
    }

    static isAvailable() {
        return new Promise((resolve, reject) => {
            RNAuthz.isAvailable((error) => {
                if (error) {
                    return reject(error);
                }
                resolve(true);
            });
        });
    }

    static addEventListener(eventName, listener) {
        if (eventName == 'onShow') {
            NativeAppEventEmitter.addListener('InAppBrowserTabOnShow', listener);
            return;
        }

        if (eventName == 'onDismiss') {
            NativeAppEventEmitter.addListener('InAppBrowserTabOnDismiss', listener);
            return;
        }
    }

    static removeEventListener(eventName, listener) {
        if (eventName == 'onShow') {
            NativeAppEventEmitter.removeListener('InAppBrowserTabOnShow', listener);
            return;
        }

        if (eventName == 'onDismiss') {
            NativeAppEventEmitter.removeListener('InAppBrowserTabOnDismiss', listener);
            return;
        }
    }

}

export default RNAuthzModule;
