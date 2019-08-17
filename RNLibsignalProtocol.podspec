
Pod::Spec.new do |s|
  s.name         = "RNLibsignalProtocol"
  s.version      = "1.0.0"
  s.summary      = "RNLibsignalProtocol"
  s.description  = <<-DESC
                  RNLibsignalProtocol
                   DESC
  s.homepage     = "https://github.com/telldus/react-native-libsignal-protocol.git"
  s.license      = "MIT"
  # s.license      = { :type => "MIT", :file => "FILE_LICENSE" }
  s.author             = { "author" => "rimnesh.fernandez@telldus.com" }
  s.platform     = :ios, "7.0"
  s.source       = { :git => "https://github.com/telldus/react-native-libsignal-protocol.git", :tag => "master" }
  s.source_files  = "ios/*.{h,m}"
  s.requires_arc = true


  s.dependency "React"
  s.dependency "LibSignalProtocolSwift"
  #s.dependency "others"

end

  