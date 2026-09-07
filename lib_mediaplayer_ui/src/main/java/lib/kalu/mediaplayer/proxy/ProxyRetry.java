package lib.kalu.mediaplayer.proxy;

import java.io.Serializable;

import lib.kalu.mediaplayer.bean.args.StartArgs;

public interface ProxyRetry extends Serializable {

    default StartArgs formatRetry(StartArgs startArgs) {
        return startArgs;
    }
}
