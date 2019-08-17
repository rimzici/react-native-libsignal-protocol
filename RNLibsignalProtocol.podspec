
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
  s.platform     = :ios, "9.0"
  s.source       = { :git => "https://github.com/telldus/react-native-libsignal-protocol.git", :tag => "ios" }
  s.source_files  = "ios/*.{h,m,swift}"
  s.requires_arc = true


  s.dependency "React"
  #s.dependency "others"

end

  