package net.fununity.clashofclans.values;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * This class holds method to get all coc value enums.
 * @see ResourceTypes
 * @see PlayerValues
 * @see ICoCValue
 * @author Niko
 * @since 1.0.2
 */
public class CoCValues {

    private CoCValues() {
        throw new UnsupportedOperationException("This class is an utility class");
    }

    private static List<ICoCValue> ALL_VALUES = null;

    /**
     * Returns all values from {@link ResourceTypes} and {@link PlayerValues}.
     * @return List<ICoCValue> - A list of all available {@link ICoCValue}.
     * @since 1.0.2
     */
    public static List<ICoCValue> getAllValues() {
        if (ALL_VALUES == null) {
            List<ICoCValue> list = new ArrayList<>();
            list.addAll(Arrays.asList(ResourceTypes.values()));
            list.addAll(Arrays.asList(PlayerValues.values()));
            ALL_VALUES = List.copyOf(list);
        }
        return ALL_VALUES;
    }

    /**
     * Returns all values the player can see with the given town hall.
     * @param townHallLevel int - the town hall level
     * @return List<ICoCValue> - all coc values the player can see
     * @since 1.0.2
     */
    public static List<ICoCValue> canReachWithTownHall(int townHallLevel) {
        List<ICoCValue> list = new ArrayList<>(List.of(PlayerValues.values()));
        list.addAll(ResourceTypes.canReachWithTownHall(townHallLevel));
        return List.copyOf(list);
    }

    /**
     * Returns all values which are stole-able.
     * @return List<ICoCValue> - all coc values a player can stole.
     * @since 1.0.2
     */
    public static List<ICoCValue> stoleAbleResource() {
        return CoCValues.getAllValues().stream().filter(c -> c != PlayerValues.GEMS && c != PlayerValues.XP).sorted(Comparator.comparing(ICoCValue::name)).toList();
    }

    /**
     * Get the ICoCValue from the given name.
     * @param name String - name to get the instance from.
     * @return ICoCValue - the {@link ICoCValue}.
     * @since 1.0.2
     */
    public static ICoCValue getFromName(String name) {
        return getAllValues().stream().filter(n -> n.name().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
