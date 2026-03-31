package lib.kalu.mediaplayer.proxy;


import java.io.Serializable;

import lib.kalu.mediaplayer.bean.args.StartArgs;
import lib.kalu.mediaplayer.bean.type.PlayerType;

public interface ProxyBuried extends Serializable {

    void onCall(@PlayerType.BuriedType.Value int buriedType,
                StartArgs startArgs,
                long position,
                long duration,
                float speed);
}