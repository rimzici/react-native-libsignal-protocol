
# react-native-libsignal-protocol

## Getting started

`$ npm install react-native-libsignal-protocol --save`  
or  
`$ yarn add react-native-libsignal-protocol`

### Mostly automatic installation

`$ react-native link react-native-libsignal-protocol`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-libsignal-protocol` and add `RNLibsignalProtocol.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNLibsignalProtocol.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.reactlibrary.RNLibsignalProtocolPackage;` to the imports at the top of the file
  - Add `new RNLibsignalProtocolPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-libsignal-protocol'
  	project(':react-native-libsignal-protocol').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-libsignal-protocol/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-libsignal-protocol')
  	```

#### Windows
[Read it! :D](https://github.com/ReactWindows/react-native)

1. In Visual Studio add the `RNLibsignalProtocol.sln` in `node_modules/react-native-libsignal-protocol/windows/RNLibsignalProtocol.sln` folder to their solution, reference from their app.
2. Open up your `MainPage.cs` app
  - Add `using Libsignal.Protocol.RNLibsignalProtocol;` to the usings at the top of the file
  - Add `new RNLibsignalProtocolPackage()` to the `List<IReactPackage>` returned by the `Packages` method


## Usage
```javascript
import RNLibsignalProtocol from 'react-native-libsignal-protocol';

// TODO: What to do with the module?
RNLibsignalProtocol;
```
  
