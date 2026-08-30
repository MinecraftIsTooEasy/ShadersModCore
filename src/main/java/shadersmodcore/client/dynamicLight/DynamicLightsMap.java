package shadersmodcore.client.dynamicLight;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamicLightsMap {
    private final Map<Integer, DynamicLight> map = new HashMap<>();
    /**
     * Copy-on-write snapshot so block light queries do not contend with the
     * entity update lock. The map itself remains guarded by the caller's lock.
     */
    private volatile List<DynamicLight> list = Collections.emptyList();

    public DynamicLight put(int id, DynamicLight dynamicLight) {
        boolean hadKey = this.map.containsKey(id);
        DynamicLight dynamiclight = this.map.put(id, dynamicLight);
        if (!hadKey || dynamiclight != dynamicLight) {
            this.list = Collections.unmodifiableList(new ArrayList<>(this.map.values()));
        }
        return dynamiclight;
    }

    public DynamicLight get(int id) {
        return this.map.get(id);
    }

    public int size() {
        return this.map.size();
    }

    public DynamicLight remove(int id) {
        boolean hadKey = this.map.containsKey(id);
        DynamicLight dynamiclight = this.map.remove(id);
        if (hadKey) {
            this.list = this.map.isEmpty()
                ? Collections.emptyList()
                : Collections.unmodifiableList(new ArrayList<>(this.map.values()));
        }

        return dynamiclight;
    }

    public void clear() {
        this.map.clear();
        this.list = Collections.emptyList();
    }

    public List<DynamicLight> valueList() {
        return this.list;
    }
}
