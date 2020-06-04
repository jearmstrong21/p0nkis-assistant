package p0nki.p0nkisassistant.data;

import p0nki.p0nkisassistant.utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TricksConfig {

    public static TricksConfig CACHE = Utils.deserialize("tricks", TricksConfig.class);

    public List<Trick> tricks = new ArrayList<>();

    public static class Source {
        public String code;
        public boolean isLisp;

        public Source(String code, boolean isLisp) {
            this.code = code;
            this.isLisp = isLisp;
        }
    }

    public static class Owner {
        public String owner;
        public String guild;
        public boolean isGlobal;

        public Owner(String owner, String guild, boolean isGlobal) {
            this.owner = owner;
            this.guild = guild;
            this.isGlobal = isGlobal;
        }
    }

    public static class Trick {
        public String name;
        public Owner owner;
        public Source source;
        public Date created;
        public Date modified;

        public Trick(String name, Owner owner, Source source, Date created, Date modified) {
            this.name = name;
            this.owner = owner;
            this.source = source;
            this.created = created;
            this.modified = modified;
        }
    }

}
