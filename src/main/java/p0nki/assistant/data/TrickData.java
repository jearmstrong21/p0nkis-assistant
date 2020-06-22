package p0nki.assistant.data;

import p0nki.assistant.lib.data.PerGuildDataCache;
import p0nki.assistant.lib.data.ReadWriteData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrickData extends ReadWriteData {

    public static final PerGuildDataCache<TrickData> CACHE = new PerGuildDataCache<>("trick", TrickData::new);
    private final List<Trick> tricks = new ArrayList<>();
    private boolean enabled = true;

    private TrickData(String dir) {
        super(dir);
    }

    public List<Trick> getTricks() {
        return Collections.unmodifiableList(tricks);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Trick fromName(String name) {
        for (Trick trick : tricks) {
            if (trick.getName().equals(name)) return trick;
        }
        throw new UnsupportedOperationException();
    }

    public boolean hasName(String name) {
        return tricks.stream().anyMatch(trick -> trick.getName().equals(name));
    }

    public void add(Trick trick) {
        if (hasName(trick.getName())) throw new UnsupportedOperationException();
        tricks.add(trick);
        write();
    }

    public void update(String name, Trick newValue) {
        for (int i = 0; i < tricks.size(); i++) {
            if (tricks.get(i).getName().equals(name)) {
                tricks.set(i, newValue);
                write();
                return;
            }
        }
        throw new UnsupportedOperationException();
    }

    public void remove(String name) {
        for (int i = 0; i < tricks.size(); i++) {
            if (tricks.get(i).getName().equals(name)) {
                tricks.remove(i);
                write();
                return;
            }
        }
        throw new UnsupportedOperationException();
    }

}
