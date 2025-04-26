package shadersmodcore.api;

public interface WorldAccessor {
    default long getWorldTime() {
        return 0;
    }
}
