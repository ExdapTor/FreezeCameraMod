package category;

import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public class FREEZE {
    public static final KeyMapping.Category FREEZE =
            KeyMapping.Category.register(
                    Identifier.fromNamespaceAndPath(
                            "freezecamera",
                            "freeze"
                    )
            );
}
