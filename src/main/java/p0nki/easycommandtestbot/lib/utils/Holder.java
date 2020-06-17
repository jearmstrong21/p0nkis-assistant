package p0nki.easycommandtestbot.lib.utils;

import net.dv8tion.jda.api.JDA;
import p0nki.easycommand.CommandDispatcher;
import p0nki.easycommandtestbot.lib.EasyListener;

public interface Holder {

    default JDA jda() {
        return EasyListener.INSTANCE.getJda();
    }

    default CommandDispatcher dispatcher() {
        return EasyListener.INSTANCE.getDispatcher();
    }

}
