package lib.kalu.mediaplayer.proxy;


import java.io.Serializable;

import lib.kalu.mediaplayer.bean.args.StartArgs;

public interface ProxyBuried extends Serializable {

    void onCall(String name, StartArgs startArgs, long position, long duration);
}