package lib.kalu.mediaplayer.buried;


import java.io.Serializable;

import lib.kalu.mediaplayer.bean.args.StartArgs;
import lib.kalu.mediaplayer.bean.info.PlayInfo;
import lib.kalu.mediaplayer.bean.type.PlayerType;

public interface PlayBuried extends Serializable {

    void onCall(@PlayerType.BuriedType.Value int buriedType,
                StartArgs startArgs,
                PlayInfo playInfo);
}