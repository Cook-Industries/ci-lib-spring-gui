package de.cookindustries.lib.spring.gui.hmi.mapper.json;

import java.util.List;
import java.util.Map;

public record ContainerParameters(String uid, List<String> classes, Map<String, String> attributes)
{

}
