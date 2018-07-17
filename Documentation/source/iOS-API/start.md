# __Get Started__

This guide contains a quick start to add and manage indoor navigation.

## __Prepare project__

If you want to access map in your Cocoa project it's necessary to create a proper building configuration on frontend server.
Ones you create `Complex --> Building --> Floor` and upload the appropriate map, it's necessary to add a proper scale and publish map on the server.
You have to check ID of the floor you want to show in the app and address of the frontend server.

<div class="note">      
   Note: The entire process is presented in the javascript-api documentation.
</div>

## __Installation__

___CocoaPods___

[CocoaPods](https://cocoapods.org) is a dependency manager for Cocoa projects. You can install it with the following command:
```bash
$ gem install cocoapods
```
To integrate IndoorNavi into your project using CocoaPods, add dependency in your `Podfile`:
```ruby
source 'https://github.com/CocoaPods/Specs.git'
platform :ios, '10.0'
use_frameworks!

target '<Your Target Name>' do
    pod 'IndoorNavi', '~> 1.0.0'
end
```
Then, install framework running the following command:
```bash
$ pod install
```

___Carthage___

[Carthage](https://github.com/Carthage/Carthage) is a decentralized dependency manager that builds your dependencies and provides you with binary frameworks.

You can install Carthage with [Homebrew](https://brew.sh/) using the following commands:

```bash
$ brew update
$ brew install carthage
```

To integrate IndoorNavi into your Xcode project using Carthage, add dependency in your `Cartfile`:

```
github "IndoorNavi/IndoorNavi" ~> 1.0.0
```

Run `carthage update` to build the framework and drag the built `IndoorNavi.framework` into your project.

___Manually___

Apart from using aforementioned dependency managers, you can integrate IndoorNavi into your project manually. Just download the zip file and drag `IndoorNavi.framework` into your project.

## __Usage__

### __Get an API key__

TODO

### __Add a map__

When you want to use `IndoorNavi.framework`, first thing to do is to create and initialize an instance of `INMap`. You can do it either by adding to the Storyboard and creating an outlet or you can set it up fully in code. The code below is an example of a simple application using `IndoorNavi.framework`, where INMap view is initialized and added in code.

___Swift___
```swift
import UIKit
import IndoorNavi

// Replace YourViewController with the name of your View Controller
class YourViewController: UIViewController {

  override func loadView() {
    // Create an INMap
    var map = INMap(frame: mapFrame, targetHost: "http://mybuilding.com", apiKey: "apiKey")
    // Load map with floor specified by floorID
    map.load(floorID)
    // Set map as View Controller's view
    view = map
  }
}
```

___Objective-C___
```objc
#import "YourViewController.h"
@import IndoorNavi;

// Replace YourViewController with the name of your View Controller
@implementation YourViewController

- (void)loadView {
  // Create an INMap
  INMap* map = [[INMap alloc] initWithFrame:mapFrame targetHost:@"http://mybuilding.com" apiKey:@"apiKey"];
  // Load map with floor specified by floorID
  [map load:floorID];
  // Set map as View Controller's view
  self.view = map;
}

@end
```
