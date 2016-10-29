
# react-native-authz

## Getting started

`$ npm install react-native-authz --save`

### Mostly automatic installation

`$ react-native link react-native-authz`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-authz` and add `RNAuthz.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNAuthz.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import br.com.helderfarias.authz.RNAuthzPackage;` to the imports at the top of the file
  - Add `new RNAuthzPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-authz'
  	project(':react-native-authz').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-authz/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-authz')
  	```


## Usage
```javascript
import RNAuthz from 'react-native-authz';

// TODO: What do with the module?
...
    componentDidMount() {
        RNAuthz.addEventListener('onDismiss', this.handleToken);
        RNAuthz.addEventListener('onShow', this.handleLogin);
    }

    componentWillUnmount() {
        RNAuthz.removeEventListener('onDismiss', this.handleToken);
        RNAuthz.removeEventListener('onShow', this.handleLogin);
    }

    handleLogin() {
        console.log('startup');
    }

    handleToken(e) {
        const token = e.url.split('#')[1].split('=')[1];

        console.log(token);
    }

    login() {
        RNAuthz.isAvailable().then(() => {
            RNAuthz.open("http://example/authz?callback=app://success").catch(error => console.log(error));
        });
    }
...
```
