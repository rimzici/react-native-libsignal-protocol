using ReactNative.Bridge;
using System;
using System.Collections.Generic;
using Windows.ApplicationModel.Core;
using Windows.UI.Core;

namespace Libsignal.Protocol.RNLibsignalProtocol
{
    /// <summary>
    /// A module that allows JS to share data.
    /// </summary>
    class RNLibsignalProtocolModule : NativeModuleBase
    {
        /// <summary>
        /// Instantiates the <see cref="RNLibsignalProtocolModule"/>.
        /// </summary>
        internal RNLibsignalProtocolModule()
        {

        }

        /// <summary>
        /// The name of the native module.
        /// </summary>
        public override string Name
        {
            get
            {
                return "RNLibsignalProtocol";
            }
        }
    }
}
